package com.gerov.storedprocedurelight.service;

import com.gerov.storedprocedurelight.dto.EmployeeAttributesDto;
import com.gerov.storedprocedurelight.storedprocedure.H2StoredProcedureBuilder;
import com.gerov.storedprocedurelight.transformer.ClobTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Map;

@Service
@Profile("h2")
public class H2EmployeeService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public H2EmployeeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Fetches employee details via GET_EMP_DETAILS.
     * Uses IN param + column transformers on the returned ResultSet.
     */
    public Map<String, Object> getEmployeeDetails(Long empId) {
        return new H2StoredProcedureBuilder(jdbcTemplate, "GET_EMP_DETAILS")
                .inParam  ("in_emp_id",      Types.NUMERIC, empId)
                .transform("out_name",       v -> v != null ? v.toString().trim() : null)
                .transform("out_salary",     v -> ((Number) v).doubleValue())
                .transform("out_attributes", new ClobTransformer<>(EmployeeAttributesDto.class))
                .execute("employee");
    }

    /**
     * Applies a percentage raise to an employee's salary via APPLY_RAISE.
     * Uses two IN params; the returned ResultSet columns are transformed.
     */
    public Map<String, Object> applyRaise(Long empId, Double raisePct) {
        return new H2StoredProcedureBuilder(jdbcTemplate, "APPLY_RAISE")
                .inParam  ("in_emp_id",      Types.NUMERIC, empId)
                .inParam  ("in_raise_pct",   Types.NUMERIC, raisePct)
                .transform("out_name",       v -> v != null ? v.toString().trim() : null)
                .transform("out_salary",     v -> ((Number) v).doubleValue())
                .transform("out_attributes", new ClobTransformer<>(EmployeeAttributesDto.class))
                .execute("raise_result");
    }
}
