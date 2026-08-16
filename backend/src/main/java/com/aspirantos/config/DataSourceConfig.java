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

    @Value("${DB_HOST:#{null}}")
    private String dbHost;

    @Value("${DB_PORT:#{null}}")
    private String dbPort;

    @Value("${DB_NAME:#{null}}")
    private String dbName;

    @Value("${DB_USERNAME:#{null}}")
    private String dbUsername;

    @Value("${DB_PASSWORD:#{null}}")
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

        // Check if any variable contains a full connection string
        String fullUrl = System.getenv("DATABASE_URL");
        if (fullUrl == null || fullUrl.isBlank()) {
            fullUrl = System.getenv("DATABASE_PUBLIC_URL");
        }
        if (fullUrl == null || fullUrl.isBlank()) {
            fullUrl = System.getenv("SPRING_DATASOURCE_URL");
        }
        if ((fullUrl == null || fullUrl.isBlank()) && dbHost != null && (dbHost.contains("://") || dbHost.startsWith("jdbc:"))) {
            fullUrl = dbHost;
        }
        if ((fullUrl == null || fullUrl.isBlank()) && System.getenv("DB_HOST") != null && (System.getenv("DB_HOST").contains("://") || System.getenv("DB_HOST").startsWith("jdbc:"))) {
            fullUrl = System.getenv("DB_HOST");
        }

        String finalUrl = null;
        String finalUser = System.getenv("PGUSER") != null ? System.getenv("PGUSER") : (dbUsername != null ? dbUsername : "postgres");
        String finalPassword = System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : (dbPassword != null ? dbPassword : "2516");

        if (fullUrl != null && !fullUrl.isBlank()) {
            log.info("Detected full database connection string");
            if (fullUrl.startsWith("postgres://") || fullUrl.startsWith("postgresql://")) {
                try {
                    URI uri = URI.create(fullUrl);
                    String host = uri.getHost();
                    int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                    String path = uri.getPath();
                    if (path != null && path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    if (path == null || path.isBlank()) {
                        path = "railway";
                    }
                    if (uri.getUserInfo() != null) {
                        String[] userInfo = uri.getUserInfo().split(":", 2);
                        finalUser = userInfo[0];
                        if (userInfo.length > 1) {
                            finalPassword = userInfo[1];
                        }
                    }
                    boolean isInternal = host != null && (host.endsWith(".railway.internal") || host.equals("postgres") || host.equals("database") || host.equals("localhost") || host.equals("127.0.0.1"));
                    String sslParam = isInternal ? "sslmode=prefer" : "sslmode=require";
                    finalUrl = String.format("jdbc:postgresql://%s:%d/%s?%s", host, port, path, sslParam);
                } catch (Exception e) {
                    log.warn("Failed to parse URL as URI: {}", e.getMessage());
                    finalUrl = fullUrl.startsWith("jdbc:") ? fullUrl : "jdbc:" + fullUrl;
                }
            } else if (fullUrl.startsWith("jdbc:postgresql://")) {
                finalUrl = fullUrl;
            }
        }

        if (finalUrl == null) {
            String host = System.getenv("PGHOST") != null ? System.getenv("PGHOST") : (dbHost != null ? dbHost : "localhost");
            String port = System.getenv("PGPORT") != null ? System.getenv("PGPORT") : (dbPort != null ? dbPort : "5432");
            String db = System.getenv("PGDATABASE") != null ? System.getenv("PGDATABASE") : (dbName != null ? dbName : "aspirantos");
            boolean isLocal = "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "database".equalsIgnoreCase(host) || host.endsWith(".railway.internal");
            String sslParam = isLocal ? "sslmode=prefer" : "sslmode=require";
            finalUrl = String.format("jdbc:postgresql://%s:%s/%s?%s", host, port, db, sslParam);
        }

        log.info("Connecting PostgreSQL DataSource to: {}", finalUrl.replaceAll(":[^:@]+@", ":****@"));
        config.setJdbcUrl(finalUrl);
        config.setUsername(finalUser);
        config.setPassword(finalPassword);

        return new HikariDataSource(config);
    }
}
