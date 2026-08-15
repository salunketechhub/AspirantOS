package com.aspirantos.dto;

public record DatabaseHealthResponse(
        String status,
        String database,
        String message
) {
    public static DatabaseHealthResponse up() {
        return new DatabaseHealthResponse("UP", "PostgreSQL", "Database connection successful");
    }

    public static DatabaseHealthResponse down(String errorMessage) {
        return new DatabaseHealthResponse("DOWN", "PostgreSQL", "Database connection failed: " + errorMessage);
    }
}
