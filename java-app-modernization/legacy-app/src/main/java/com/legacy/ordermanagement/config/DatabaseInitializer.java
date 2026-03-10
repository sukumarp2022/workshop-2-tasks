package com.legacy.ordermanagement.config;

import com.legacy.ordermanagement.utils.DatabaseUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Legacy database initializer — runs SQL on startup.
 * No migration tool (Flyway/Liquibase).
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing database...");
        DatabaseUtil.initializeDatabase();
        System.out.println("Database initialization complete.");
    }
}
