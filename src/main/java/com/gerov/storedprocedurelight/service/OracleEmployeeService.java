package com.gerov.storedprocedurelight.service;

import com.gerov.storedprocedurelight.storedprocedure.OracleStoredProcedureBuilder;
import com.gerov.storedprocedurelight.transformer.AttributesTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Map;

@Service
public class OracleEmployeeService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OracleEmployeeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Fetches employee details via GET_EMP_DETAILS.
     *   in_emp_id  IN  – employee to look up
     *   out_name   OUT – employee name
     *   out_salary OUT – current salary (transformed to Double)
     */
    public Map<String, Object> getEmployeeDetails(Long empId) {
        return new OracleStoredProcedureBuilder(jdbcTemplate, "GET_EMP_DETAILS")
                .inParam ("in_emp_id",      Types.NUMERIC, empId)
                .outParam("out_name",       Types.VARCHAR)
                .outParam("out_salary",     Types.NUMERIC,  v -> ((Number) v).doubleValue())
                .outParam("out_attributes", Types.CLOB,     new AttributesTransformer())
                .execute();
    }

    /**
     * Applies a percentage raise to an employee's salary via APPLY_RAISE.
     *   in_emp_id    IN     – employee to look up
     *   in_raise_pct IN     – raise percentage
     *   inout_salary IN OUT – caller passes 0; procedure returns the computed new salary
     *   out_name     OUT    – employee name
     */
    public Map<String, Object> applyRaise(Long empId, Double raisePct) {
        return new OracleStoredProcedureBuilder(jdbcTemplate, "APPLY_RAISE")
                .inParam   ("in_emp_id",      Types.NUMERIC, empId)
                .inParam   ("in_raise_pct",   Types.NUMERIC, raisePct)
                .inOutParam("inout_salary",   Types.NUMERIC, 0, v -> ((Number) v).doubleValue())
                .outParam  ("out_name",       Types.VARCHAR)
                .outParam  ("out_attributes", Types.CLOB,    new AttributesTransformer())
                .execute();
    }
}