package com.gerov.storedprocedurelight.service;

import com.gerov.storedprocedurelight.builders.StoredProcedureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Map;

@Service
public class EmployeeService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getEmployeeDetails(Long empId) {
        return new StoredProcedureBuilder(jdbcTemplate, "GET_EMP_DETAILS")
                .inParam("p_emp_id", Types.NUMERIC, empId)
                .executeReturningResultSet("employee");
    }
}