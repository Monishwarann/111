package com.hrm.hrms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Testing connection to database at {} with user '{}'...", dbUrl, dbUser);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            log.info("Successfully connected to MySQL Database!");
        } catch (Exception ex) {
            log.error("\n================================================================================" +
                      "\nDATABASE CONNECTION FAILURE:" +
                      "\nCould not connect to MySQL Server at localhost:3306." +
                      "\nError details: " + ex.getMessage() +
                      "\n" +
                      "\nPLEASE VERIFY:" +
                      "\n1. MySQL 8.x Server service ('MySQL80') is running on port 3306." +
                      "\n2. Database user '" + dbUser + "' has correct privileges." +
                      "\n3. If your MySQL password is not 'root', set environment variable DB_PASSWORD:" +
                      "\n   PowerShell: $env:DB_PASSWORD=\"your_actual_mysql_password\"" +
                      "\n   CMD:        set DB_PASSWORD=your_actual_mysql_password" +
                      "\n================================================================================\n");
        }

        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(dbUrl)
                .username(dbUser)
                .password(dbPassword)
                .build();
    }
}
