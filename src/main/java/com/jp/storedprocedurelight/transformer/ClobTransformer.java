package com.jp.storedprocedurelight.transformer;

import tools.jackson.databind.ObjectMapper;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Generic transformer that converts a CLOB value
 * (returned as either a {@link Clob} by Oracle or a {@link String} by H2)
 * into an instance of {@code T} by deserializing the JSON content.
 */
public class ClobTransformer<T> implements Function<Object, T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Class<T> targetType;

    public ClobTransformer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public T apply(Object value) {
        if (value == null) return null;
        String json = extractString(value);
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, targetType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize CLOB JSON into " + targetType.getSimpleName(), e);
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
