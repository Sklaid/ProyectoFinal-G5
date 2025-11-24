package com.techcorp.devops.e2e.config;

/**
 * Configuration class for test data and constants.
 */
public class TestConfig {
    
    // Test user credentials
    public static final String TEST_USERNAME = "admin";
    public static final String TEST_PASSWORD = "admin123";
    
    // Test employee data
    public static final String TEST_EMPLOYEE_FIRST_NAME = "John";
    public static final String TEST_EMPLOYEE_LAST_NAME = "Doe";
    public static final String TEST_EMPLOYEE_EMAIL = "john.doe@techcorp.com";
    public static final String TEST_EMPLOYEE_PHONE = "555-0123";
    
    // Updated employee data
    public static final String UPDATED_EMPLOYEE_FIRST_NAME = "Jane";
    public static final String UPDATED_EMPLOYEE_LAST_NAME = "Smith";
    public static final String UPDATED_EMPLOYEE_EMAIL = "jane.smith@techcorp.com";
    
    // Timeouts
    public static final int DEFAULT_TIMEOUT_SECONDS = 10;
    public static final int LONG_TIMEOUT_SECONDS = 30;
    
    private TestConfig() {
        // Private constructor to prevent instantiation
    }
}
