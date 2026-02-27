package com.gerov.storedprocedurelight.storedprocedure;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;
import java.util.function.Function;

/**
 * Builder for stored procedures on Oracle databases.
 * Supports three parameter modes:
 * <ul>
 *   <li>{@link #inParam}    – pure input (IN)</li>
 *   <li>{@link #outParam}   – pure output (OUT)</li>
 *   <li>{@link #inOutParam} – input that is also returned as output (IN OUT)</li>
 * </ul>
 *
 * <pre>
 * new OracleStoredProcedureBuilder(jdbcTemplate, "ADJUST_SALARY")
 *     .inParam   ("p_emp_id",   Types.NUMERIC, empId)
 *     .inOutParam("p_salary",   Types.NUMERIC, currentSalary, v -> ((Number) v).doubleValue())
 *     .outParam  ("p_grade",    Types.VARCHAR)
 *     .execute();
 * </pre>
 */
public class OracleStoredProcedureBuilder extends StoredProcedureBuilder<OracleStoredProcedureBuilder> {

    public OracleStoredProcedureBuilder(JdbcTemplate jdbcTemplate, String procedureName) {
        super(jdbcTemplate, procedureName);
    }

    // ── Output parameters (OUT) ───────────────────────────────────────────────

    /** Declares a pure OUT parameter. */
    public OracleStoredProcedureBuilder outParam(String name, int sqlType) {
        declaredParams.put(name, new SqlOutParameter(name, sqlType));
        return this;
    }

    /** Declares a pure OUT parameter with an inline transformer applied to its value. */
    @SuppressWarnings("unchecked")
    public OracleStoredProcedureBuilder outParam(String name, int sqlType, Function<Object, ?> transformer) {
        declaredParams.put(name, new SqlOutParameter(name, sqlType));
        transformers.put(name, (Function<Object, Object>) transformer);
        return this;
    }

    // ── In-out parameters (IN OUT) ────────────────────────────────────────────

    /**
     * Declares an IN OUT parameter: passes {@code value} into the procedure
     * and reads the (possibly modified) value back out.
     */
    public OracleStoredProcedureBuilder inOutParam(String name, int sqlType, Object value) {
        declaredParams.put(name, new SqlInOutParameter(name, sqlType));
        inParams.addValue(name, value);
        return this;
    }

    /**
     * Declares an IN OUT parameter with an inline transformer applied to the
     * returned value.
     */
    @SuppressWarnings("unchecked")
    public OracleStoredProcedureBuilder inOutParam(String name, int sqlType, Object value, Function<Object, ?> transformer) {
        declaredParams.put(name, new SqlInOutParameter(name, sqlType));
        inParams.addValue(name, value);
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

        return applyTransformers(call.execute(inParams));
    }
}
