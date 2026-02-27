package com.jp.storedprocedurelight.controller;

import com.jp.storedprocedurelight.dto.EmployeeAttributesDto;
import com.jp.storedprocedurelight.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/oracle/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

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
        response.put("newSalary",  result.get("inout_salary"));
        response.put("attributes", (EmployeeAttributesDto) result.get("out_attributes"));
        return ResponseEntity.ok(response);
    }
}
