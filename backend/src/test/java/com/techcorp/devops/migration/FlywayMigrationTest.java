package com.techcorp.devops.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Flyway database migrations.
 * Validates that migrations execute successfully and maintain database integrity.
 * 
 * Note: This test uses a separate profile "flyway-test" where Flyway is enabled,
 * unlike the standard "test" profile where Flyway is disabled.
 */
@SpringBootTest
@ActiveProfiles("flyway-test")
class FlywayMigrationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Test that migrations execute successfully on clean database.
     * Validates: Requirements 5.3
     */
    @Test
    void testMigrationsExecuteSuccessfully() {
        // Verify Flyway is configured
        assertNotNull(flyway, "Flyway should be configured");

        // Get migration info
        MigrationInfoService infoService = flyway.info();
        MigrationInfo[] migrations = infoService.all();

        // Verify migrations exist
        assertTrue(migrations.length > 0, "At least one migration should exist");

        // Verify all migrations are applied
        MigrationInfo[] applied = infoService.applied();
        assertTrue(applied.length > 0, "At least one migration should be applied");

        // Verify no pending migrations
        MigrationInfo[] pending = infoService.pending();
        assertEquals(0, pending.length, "No migrations should be pending after startup");
    }

    /**
     * Test that migration history is recorded correctly.
     * Validates: Requirements 5.3
     */
    @Test
    void testMigrationHistoryRecorded() {
        // Query flyway_schema_history table
        String sql = "SELECT version, description, type, script, checksum, installed_rank, success " +
                     "FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank";

        List<Map<String, Object>> history = jdbcTemplate.queryForList(sql);

        // Verify history is not empty
        assertFalse(history.isEmpty(), "Migration history should not be empty");

        // Verify all migrations succeeded
        for (Map<String, Object> record : history) {
            Boolean success = (Boolean) record.get("success");
            assertTrue(success, "Migration " + record.get("version") + " should have succeeded");
        }

        // Verify V1 migration exists
        boolean v1Exists = history.stream()
                .anyMatch(record -> record.get("version") != null && 
                                   record.get("version").toString().startsWith("1"));
        assertTrue(v1Exists, "V1 migration should be in history");

        // Verify V2 migration exists
        boolean v2Exists = history.stream()
                .anyMatch(record -> record.get("version") != null && 
                                   record.get("version").toString().startsWith("2"));
        assertTrue(v2Exists, "V2 migration should be in history");
    }

    /**
     * Test that users table was created correctly.
     * Validates: Requirements 5.1, 5.5
     */
    @Test
    void testUsersTableCreated() {
        // Verify users table exists
        String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables " +
                               "WHERE LOWER(table_name) = 'users'";
        Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
        assertTrue(tableCount >= 1, "Users table should exist");

        // Verify required columns exist
        String checkColumnsSql = "SELECT LOWER(column_name) as column_name FROM information_schema.columns " +
                                 "WHERE LOWER(table_name) = 'users' ORDER BY column_name";
        List<String> columns = jdbcTemplate.queryForList(checkColumnsSql, String.class);

        assertTrue(columns.contains("id"), "Users table should have id column");
        assertTrue(columns.contains("username"), "Users table should have username column");
        assertTrue(columns.contains("password"), "Users table should have password column");
        assertTrue(columns.contains("email"), "Users table should have email column");
        assertTrue(columns.contains("role"), "Users table should have role column");
        assertTrue(columns.contains("created_at"), "Users table should have created_at column");
        assertTrue(columns.contains("last_login"), "Users table should have last_login column");
        assertTrue(columns.contains("active"), "Users table should have active column");

        // Verify unique constraint on username exists
        String checkConstraintSql = "SELECT COUNT(*) FROM information_schema.table_constraints " +
                                    "WHERE LOWER(table_name) = 'users' AND constraint_type = 'UNIQUE'";
        Integer constraintCount = jdbcTemplate.queryForObject(checkConstraintSql, Integer.class);
        assertTrue(constraintCount > 0, "Unique constraint on username should exist");

        // Verify default admin user exists
        String checkAdminSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        Integer adminCount = jdbcTemplate.queryForObject(checkAdminSql, Integer.class);
        assertEquals(1, adminCount, "Default admin user should exist");
    }

    /**
     * Test that employees table was created correctly.
     * Validates: Requirements 5.1, 5.5
     */
    @Test
    void testEmployeesTableCreated() {
        // Verify employees table exists
        String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables " +
                               "WHERE LOWER(table_name) = 'employees'";
        Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
        assertTrue(tableCount >= 1, "Employees table should exist");

        // Verify required columns exist
        String checkColumnsSql = "SELECT LOWER(column_name) as column_name FROM information_schema.columns " +
                                 "WHERE LOWER(table_name) = 'employees' ORDER BY column_name";
        List<String> columns = jdbcTemplate.queryForList(checkColumnsSql, String.class);

        assertTrue(columns.contains("id"), "Employees table should have id column");
        assertTrue(columns.contains("first_name"), "Employees table should have first_name column");
        assertTrue(columns.contains("last_name"), "Employees table should have last_name column");
        assertTrue(columns.contains("email"), "Employees table should have email column");
        assertTrue(columns.contains("phone"), "Employees table should have phone column");
        assertTrue(columns.contains("gender"), "Employees table should have gender column");
        assertTrue(columns.contains("department"), "Employees table should have department column");
        assertTrue(columns.contains("level"), "Employees table should have level column");
        assertTrue(columns.contains("hire_date"), "Employees table should have hire_date column");

        // Verify sample data was inserted
        String checkDataSql = "SELECT COUNT(*) FROM employees";
        Integer dataCount = jdbcTemplate.queryForObject(checkDataSql, Integer.class);
        assertTrue(dataCount >= 3, "Sample employee data should exist");
    }

    /**
     * Test that employee_skills junction table was created correctly.
     * Validates: Requirements 5.1, 5.5
     */
    @Test
    void testEmployeeSkillsTableCreated() {
        // Verify employee_skills table exists
        String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables " +
                               "WHERE table_name = 'employee_skills'";
        Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
        assertEquals(1, tableCount, "Employee_skills table should exist");

        // Verify foreign key constraint exists
        String checkFkSql = "SELECT COUNT(*) FROM information_schema.table_constraints " +
                            "WHERE table_name = 'employee_skills' AND constraint_type = 'FOREIGN KEY'";
        Integer fkCount = jdbcTemplate.queryForObject(checkFkSql, Integer.class);
        assertTrue(fkCount > 0, "Foreign key constraint should exist on employee_skills");

        // Verify sample data was inserted
        String checkDataSql = "SELECT COUNT(*) FROM employee_skills";
        Integer dataCount = jdbcTemplate.queryForObject(checkDataSql, Integer.class);
        assertTrue(dataCount > 0, "Sample skills data should exist");
    }

    /**
     * Test validation of migration integrity.
     * Validates: Requirements 5.3
     */
    @Test
    void testMigrationValidation() {
        // Validate migrations
        MigrationInfoService infoService = flyway.info();

        // Verify current version
        MigrationInfo current = infoService.current();
        assertNotNull(current, "Current migration version should be set");
        assertTrue(current.getVersion().getVersion().compareTo("2") >= 0, 
                   "Current version should be at least V2");
        
        // Verify all applied migrations succeeded
        MigrationInfo[] applied = infoService.applied();
        for (MigrationInfo migration : applied) {
            assertTrue(migration.getState().isApplied(), 
                      "Migration " + migration.getVersion() + " should be successfully applied");
        }
    }

    /**
     * Test that check constraints are enforced.
     * Validates: Requirements 5.4
     */
    @Test
    void testCheckConstraintsEnforced() {
        // Test invalid role is rejected
        assertThrows(Exception.class, () -> {
            jdbcTemplate.update(
                "INSERT INTO users (username, password, email, role) " +
                "VALUES ('testuser', 'password', 'test@test.com', 'INVALID_ROLE')"
            );
        }, "Invalid role should be rejected by check constraint");

        // Test invalid gender is rejected
        assertThrows(Exception.class, () -> {
            jdbcTemplate.update(
                "INSERT INTO employees (first_name, last_name, email, gender, department, level, hire_date) " +
                "VALUES ('Test', 'User', 'test@test.com', 'INVALID', 'IT', 'JUNIOR', CURRENT_DATE)"
            );
        }, "Invalid gender should be rejected by check constraint");

        // Test invalid department is rejected
        assertThrows(Exception.class, () -> {
            jdbcTemplate.update(
                "INSERT INTO employees (first_name, last_name, email, gender, department, level, hire_date) " +
                "VALUES ('Test', 'User', 'test2@test.com', 'MALE', 'INVALID_DEPT', 'JUNIOR', CURRENT_DATE)"
            );
        }, "Invalid department should be rejected by check constraint");

        // Test invalid level is rejected
        assertThrows(Exception.class, () -> {
            jdbcTemplate.update(
                "INSERT INTO employees (first_name, last_name, email, gender, department, level, hire_date) " +
                "VALUES ('Test', 'User', 'test3@test.com', 'MALE', 'IT', 'INVALID_LEVEL', CURRENT_DATE)"
            );
        }, "Invalid level should be rejected by check constraint");
    }

    /**
     * Test that unique constraints are enforced.
     * Validates: Requirements 5.4
     */
    @Test
    void testUniqueConstraintsEnforced() {
        // Test duplicate username is rejected
        assertThrows(Exception.class, () -> {
            jdbcTemplate.update(
                "INSERT INTO users (username, password, email, role) " +
                "VALUES ('admin', 'password', 'another@test.com', 'USER')"
            );
        }, "Duplicate username should be rejected");

        // Test duplicate email in employees is rejected
        assertThrows(Exception.class, () -> {
            jdbcTemplate.update(
                "INSERT INTO employees (first_name, last_name, email, gender, department, level, hire_date) " +
                "VALUES ('Test', 'User', 'john.doe@techcorp.com', 'MALE', 'IT', 'JUNIOR', CURRENT_DATE)"
            );
        }, "Duplicate employee email should be rejected");
    }
}
