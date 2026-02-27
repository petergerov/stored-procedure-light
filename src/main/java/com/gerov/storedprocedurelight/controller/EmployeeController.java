package com.gerov.storedprocedurelight.controller;

import com.gerov.storedprocedurelight.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployee(@PathVariable Long id) {
        Map<String, Object> result = employeeService.getEmployeeDetails(id);

        return ResponseEntity.ok(Map.of(
                "name", result.get("p_name"),
                "salary", result.get("p_salary")
        ));
    }
}
