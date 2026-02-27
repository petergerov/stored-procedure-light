package com.gerov.storedprocedurelight.controller;

import com.gerov.storedprocedurelight.dto.EmployeeAttributesDto;
import com.gerov.storedprocedurelight.service.H2EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Profile("h2")
@RestController
@RequestMapping("/h2/employees")
public class H2EmployeeController {

    @Autowired
    private H2EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployee(@PathVariable Long id) {
        Map<String, Object> result = employeeService.getEmployeeDetails(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name",       result.get("out_name"));
        response.put("salary",     result.get("out_salary"));
        response.put("attributes", (EmployeeAttributesDto) result.get("out_attributes"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/raise/{pct}")
    public ResponseEntity<Map<String, Object>> applyRaise(
            @PathVariable Long id,
            @PathVariable Double pct) {
        Map<String, Object> result = employeeService.applyRaise(id, pct);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name",       result.get("out_name"));
        response.put("newSalary",  result.get("out_salary"));
        response.put("attributes", (EmployeeAttributesDto) result.get("out_attributes"));
        return ResponseEntity.ok(response);
    }
}
