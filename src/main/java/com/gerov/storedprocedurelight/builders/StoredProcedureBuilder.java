package com.gerov.storedprocedurelight.builders;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.LinkedHashMap;
import java.util.Map;

public class StoredProcedureBuilder {

    private final JdbcTemplate jdbcTemplate;
    private final String procedureName;
    private final MapSqlParameterSource inParams = new MapSqlParameterSource();
    private final Map<String, SqlParameter> declaredParams = new LinkedHashMap<>();

    public StoredProcedureBuilder(JdbcTemplate jdbcTemplate, String procedureName) {
        this.jdbcTemplate = jdbcTemplate;
        this.procedureName = procedureName;
    }

    public StoredProcedureBuilder inParam(String name, int sqlType, Object value) {
        declaredParams.put(name, new SqlParameter(name, sqlType));
        inParams.addValue(name, value);
        return this;
    }

    public StoredProcedureBuilder outParam(String name, int sqlType) {
        declaredParams.put(name, new SqlOutParameter(name, sqlType));
        return this;
    }

    /** For databases with native OUT parameters (e.g. Oracle). */
    public Map<String, Object> execute() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName)
                .withoutProcedureColumnMetaDataAccess();
        call.declareParameters(declaredParams.values().toArray(new SqlParameter[0]));
        return call.execute(inParams);
    }

    /**
     * For H2 (and other databases) where the procedure returns a ResultSet
     * instead of OUT parameters.  The first row of the result set is returned
     * as a flat map keyed by column name.
     *
     * @param rsName logical name used internally by SimpleJdbcCall
     */
    public Map<String, Object> executeReturningResultSet(String rsName) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName)
                .withoutProcedureColumnMetaDataAccess()
                .returningResultSet(rsName, new ColumnMapRowMapper());
        call.declareParameters(declaredParams.values().toArray(new SqlParameter[0]));

        Map<String, Object> result = call.execute(inParams);

        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> rows =
                (java.util.List<Map<String, Object>>) result.get(rsName);

        return (rows != null && !rows.isEmpty()) ? rows.get(0) : Map.of();
    }
}