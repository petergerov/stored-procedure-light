package com.jp.storedprocedurelight;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Fluent builder for Oracle stored procedures.
 * Supports three parameter modes:
 * <ul>
 *   <li>{@link #inParam}    – pure input (IN)</li>
 *   <li>{@link #outParam}   – pure output (OUT)</li>
 *   <li>{@link #inOutParam} – input that is also returned as output (IN OUT)</li>
 * </ul>
 *
 * <pre>
 * new StoredProcedureBuilder(jdbcTemplate, "ADJUST_SALARY")
 *     .inParam   ("p_emp_id",  Types.NUMERIC, empId)
 *     .inOutParam("p_salary",  Types.NUMERIC, currentSalary, v -> ((Number) v).doubleValue())
 *     .outParam  ("p_grade",   Types.VARCHAR)
 *     .execute();
 * </pre>
 */
public class StoredProcedureBuilder {

    private final JdbcTemplate jdbcTemplate;
    private final String procedureName;
    private final MapSqlParameterSource inParams = new MapSqlParameterSource();
    private final Map<String, SqlParameter> declaredParams = new LinkedHashMap<>();
    private final Map<String, Function<Object, Object>> transformers = new LinkedHashMap<>();

    public StoredProcedureBuilder(JdbcTemplate jdbcTemplate, String procedureName) {
        this.jdbcTemplate = jdbcTemplate;
        this.procedureName = procedureName;
    }

    // ── Input parameters (IN) ─────────────────────────────────────────────────

    public StoredProcedureBuilder inParam(String name, int sqlType, Object value) {
        declaredParams.put(name, new SqlParameter(name, sqlType));
        inParams.addValue(name, value);
        return this;
    }

    // ── Output parameters (OUT) ───────────────────────────────────────────────

    public StoredProcedureBuilder outParam(String name, int sqlType) {
        declaredParams.put(name, new SqlOutParameter(name, sqlType));
        return this;
    }

    @SuppressWarnings("unchecked")
    public StoredProcedureBuilder outParam(String name, int sqlType, Function<Object, ?> transformer) {
        declaredParams.put(name, new SqlOutParameter(name, sqlType));
        transformers.put(name, (Function<Object, Object>) transformer);
        return this;
    }

    // ── In-out parameters (IN OUT) ────────────────────────────────────────────

    public StoredProcedureBuilder inOutParam(String name, int sqlType, Object value) {
        declaredParams.put(name, new SqlInOutParameter(name, sqlType));
        inParams.addValue(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public StoredProcedureBuilder inOutParam(String name, int sqlType, Object value, Function<Object, ?> transformer) {
        declaredParams.put(name, new SqlInOutParameter(name, sqlType));
        inParams.addValue(name, value);
        transformers.put(name, (Function<Object, Object>) transformer);
        return this;
    }

    // ── Transformers ──────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public StoredProcedureBuilder transform(String name, Function<Object, ?> transformer) {
        transformers.put(name, (Function<Object, Object>) transformer);
        return this;
    }

    // ── Execute ───────────────────────────────────────────────────────────────

    /**
     * Executes the procedure and returns a map of all OUT and IN OUT parameter values.
     * Transformers are applied before returning.
     */
    public Map<String, Object> execute() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName)
                .withoutProcedureColumnMetaDataAccess();

        call.declareParameters(declaredParams.values().toArray(new SqlParameter[0]));

        Map<String, Object> result = call.execute(inParams);
        if (transformers.isEmpty()) return result;

        Map<String, Object> out = new LinkedHashMap<>(result);
        transformers.forEach((key, fn) -> {
            if (out.containsKey(key)) out.put(key, fn.apply(out.get(key)));
        });
        return out;
    }
}
