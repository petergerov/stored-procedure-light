package com.gerov.storedprocedurelight.storedprocedure;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.List;
import java.util.Map;

/**
 * Builder for stored procedures on H2 databases.
 * H2 does not support native JDBC OUT parameters, so procedures must return
 * a ResultSet. The first row is unwrapped and returned as a flat map.
 *
 * <pre>
 * new H2StoredProcedureBuilder(jdbcTemplate, "GET_EMP_DETAILS")
 *     .inParam("p_emp_id", Types.NUMERIC, empId)
 *     .transform("p_name",   v -> v.toString().trim())
 *     .transform("p_salary", v -> ((Number) v).doubleValue())
 *     .execute("employee");
 * </pre>
 */
public class H2StoredProcedureBuilder extends StoredProcedureBuilder<H2StoredProcedureBuilder> {

    public H2StoredProcedureBuilder(JdbcTemplate jdbcTemplate, String procedureName) {
        super(jdbcTemplate, procedureName);
    }

    /**
     * Executes the procedure and returns the first row of the returned ResultSet
     * as a flat map keyed by column name. Transformers are applied before returning.
     *
     * @param rsName logical name used internally by {@link SimpleJdbcCall}
     */
    public Map<String, Object> execute(String rsName) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName)
                .withoutProcedureColumnMetaDataAccess()
                .returningResultSet(rsName, new ColumnMapRowMapper());
        call.declareParameters(declaredParams.values().toArray(new SqlParameter[0]));

        Map<String, Object> result = call.execute(inParams);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get(rsName);

        Map<String, Object> row = (rows != null && !rows.isEmpty()) ? rows.get(0) : Map.of();
        return applyTransformers(row);
    }
}