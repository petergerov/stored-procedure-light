package com.gerov.storedprocedurelight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAttributesDto {
    private String department;
    private String level;
    private List<String> skills;
}