package com.gerov.storedprocedurelight.transformer;

import tools.jackson.databind.ObjectMapper;
import com.gerov.storedprocedurelight.dto.EmployeeAttributesDto;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Transformer that converts an {@code out_attributes} CLOB value
 * (returned as either a {@link java.sql.Clob} by Oracle or a {@link String} by H2)
 * into an {@link EmployeeAttributesDto}.
 */
public class AttributesTransformer implements Function<Object, EmployeeAttributesDto> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public EmployeeAttributesDto apply(Object value) {
        if (value == null) return null;
        String json = extractString(value);
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, EmployeeAttributesDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize employee attributes JSON", e);
        }
    }

    private static String extractString(Object value) {
        if (value instanceof Clob clob) {
            try {
                return clob.getSubString(1, (int) clob.length());
            } catch (SQLException e) {
                throw new RuntimeException("Failed to read CLOB value", e);
            }
        }
        return value.toString();
    }
}
