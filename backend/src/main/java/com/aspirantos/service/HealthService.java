package com.aspirantos.service;

import com.aspirantos.dto.DatabaseHealthResponse;
import com.aspirantos.dto.HealthResponse;

public interface HealthService {
    HealthResponse getApplicationHealth();
    DatabaseHealthResponse getDatabaseHealth();
}
