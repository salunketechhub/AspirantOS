package com.aspirantos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:#{null}}")
    private String rawDatasourceUrl;

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:5432}")
    private String dbPort;

    @Value("${DB_NAME:aspirantos}")
    private String dbName;

    @Value("${DB_USERNAME:postgres}")
    private String dbUsername;

    @Value("${DB_PASSWORD:2516}")
    private String dbPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setInitializationFailTimeout(-1);

        String envDatabaseUrl = System.getenv("DATABASE_URL");
        String finalUrl = null;
        String finalUser = dbUsername;
        String finalPassword = dbPassword;

        if (envDatabaseUrl != null && !envDatabaseUrl.isBlank()) {
            log.info("Detected DATABASE_URL environment variable from cloud provider");
            if (envDatabaseUrl.startsWith("postgres://") || envDatabaseUrl.startsWith("postgresql://")) {
                try {
                    URI uri = URI.create(envDatabaseUrl);
                    String host = uri.getHost();
                    int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                    String path = uri.getPath();
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    if (uri.getUserInfo() != null) {
                        String[] userInfo = uri.getUserInfo().split(":", 2);
                        finalUser = userInfo[0];
                        if (userInfo.length > 1) {
                            finalPassword = userInfo[1];
                        }
                    }
                    finalUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, path);
                } catch (Exception e) {
                    log.warn("Failed to parse DATABASE_URL as URI, using as-is: {}", e.getMessage());
                    finalUrl = envDatabaseUrl;
                }
            } else if (envDatabaseUrl.startsWith("jdbc:postgresql://")) {
                finalUrl = envDatabaseUrl;
            }
        }

        if (finalUrl == null) {
            boolean isLocal = "localhost".equalsIgnoreCase(dbHost) || "127.0.0.1".equals(dbHost) || "database".equalsIgnoreCase(dbHost);
            String sslParam = isLocal ? "sslmode=prefer" : "sslmode=require";
            finalUrl = String.format("jdbc:postgresql://%s:%s/%s?%s", dbHost, dbPort, dbName, sslParam);
        }

        log.info("Configuring PostgreSQL DataSource for host target: {}", finalUrl.replaceAll(":[^:@]+@", ":****@"));
        config.setJdbcUrl(finalUrl);
        config.setUsername(finalUser);
        config.setPassword(finalPassword);

        return new HikariDataSource(config);
    }
}
