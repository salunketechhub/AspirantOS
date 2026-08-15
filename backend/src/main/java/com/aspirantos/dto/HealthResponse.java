package com.aspirantos.dto;

public record HealthResponse(
        String status,
        String application,
        String message
) {
    public static HealthResponse up() {
        return new HealthResponse("UP", "AspirantOS", "Backend is running successfully");
    }
}
