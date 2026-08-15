package com.aspirantos.service;

import com.aspirantos.dto.DatabaseHealthResponse;
import com.aspirantos.dto.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class HealthServiceImpl implements HealthService {

    private static final Logger log = LoggerFactory.getLogger(HealthServiceImpl.class);
    private final DataSource dataSource;

    public HealthServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HealthResponse getApplicationHealth() {
        return HealthResponse.up();
    }

    @Override
    public DatabaseHealthResponse getDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("SELECT 1");
                }
                return DatabaseHealthResponse.up();
            } else {
                return DatabaseHealthResponse.down("Connection validation timed out");
            }
        } catch (SQLException ex) {
            log.warn("Database health check failed: {}", ex.getMessage());
            return DatabaseHealthResponse.down(ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error during database health check", ex);
            return DatabaseHealthResponse.down(ex.getMessage());
        }
    }
}
