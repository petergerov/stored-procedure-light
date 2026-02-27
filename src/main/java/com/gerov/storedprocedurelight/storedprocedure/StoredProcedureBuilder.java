package com.gerov.storedprocedurelight.storedprocedure;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Abstract base for stored-procedure builders.
 * Holds the fields and methods common to all database dialects:
 * input parameters and output transformers.
 *
 * @param <B> the concrete builder type (CRTP), enabling fluent chaining on subclasses
 */
public abstract class StoredProcedureBuilder<B extends StoredProcedureBuilder<B>> {

    protected final JdbcTemplate jdbcTemplate;
    protected final String procedureName;
    protected final MapSqlParameterSource inParams = new MapSqlParameterSource();
    protected final Map<String, SqlParameter> declaredParams = new LinkedHashMap<>();
    protected final Map<String, Function<Object, Object>> transformers = new LinkedHashMap<>();

    protected StoredProcedureBuilder(JdbcTemplate jdbcTemplate, String procedureName) {
        this.jdbcTemplate = jdbcTemplate;
        this.procedureName = procedureName;
    }

    @SuppressWarnings("unchecked")
    protected final B self() {
        return (B) this;
    }

    // ── Input parameters ──────────────────────────────────────────────────────

    public B inParam(String name, int sqlType, Object value) {
        declaredParams.put(name, new SqlParameter(name, sqlType));
        inParams.addValue(name, value);
        return self();
    }

    // ── Transformers ──────────────────────────────────────────────────────────

    /**
     * Registers a post-execution transformer for a named output value.
     * Can be used for OUT parameters or result-set columns.
     *
     * <pre>
     * .transform("p_name",   v -> v.toString().trim())
     * .transform("p_salary", v -> ((Number) v).doubleValue())
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public B transform(String name, Function<Object, ?> transformer) {
        transformers.put(name, (Function<Object, Object>) transformer);
        return self();
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    protected Map<String, Object> applyTransformers(Map<String, Object> result) {
        if (transformers.isEmpty()) {
            return result;
        }
        Map<String, Object> out = new LinkedHashMap<>(result);
        transformers.forEach((key, fn) -> {
            if (out.containsKey(key)) {
                out.put(key, fn.apply(out.get(key)));
            }
        });
        return out;
    }
}