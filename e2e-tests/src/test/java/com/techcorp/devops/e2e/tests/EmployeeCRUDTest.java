package com.techcorp.devops.e2e.tests;

import com.techcorp.devops.e2e.base.BaseTest;
import com.techcorp.devops.e2e.config.TestConfig;
import com.techcorp.devops.e2e.pages.EmployeeFormPage;
import com.techcorp.devops.e2e.pages.EmployeeListPage;
import com.techcorp.devops.e2e.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for Employee CRUD operations.
 * Tests creation, reading, updating, and deletion of employees.
 * 
 * Requirements: 10.2, 10.3, 10.4, 10.5
 */
public class EmployeeCRUDTest extends BaseTest {
    
    private String testEmployeeEmail;
    
    /**
     * Override setUp to login after driver initialization.
     */
    @Override
    @BeforeMethod
    public void setUp() {
        super.setUp(); // Initialize driver first
        
        // Now login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo(baseUrl);
        loginPage.login(TestConfig.TEST_USERNAME, TestConfig.TEST_PASSWORD);
        waitFor(2);
        
        // Generate unique email for this test run
        testEmployeeEmail = "test." + System.currentTimeMillis() + "@techcorp.com";
        
        System.out.println("✓ Logged in successfully for CRUD tests");
    }
    
    /**
     * Test viewing the employee list.
     * Validates: Requirements 10.2, 10.3
     */
    @Test(priority = 1, description = "Test viewing employee list")
    public void testViewEmployeeList() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        
        // Act
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        
        // Assert
        Assert.assertTrue(employeeListPage.isDisplayed(), 
            "Employee list page should be displayed");
        
        int employeeCount = employeeListPage.getEmployeeCount();
        Assert.assertTrue(employeeCount >= 0, 
            "Employee count should be non-negative");
        
        System.out.println("✓ View employee list test passed - Found " + employeeCount + " employees");
    }
    
    /**
     * Test creating a new employee.
     * Validates: Requirements 10.2, 10.3, 10.4
     */
    @Test(priority = 2, description = "Test employee creation flow")
    public void testCreateEmployee() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        EmployeeFormPage employeeFormPage = new EmployeeFormPage(driver);
        
        // Act - Navigate to employee list and click add
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        int initialCount = employeeListPage.getEmployeeCount();
        
        employeeListPage.clickAddEmployee();
        waitFor(1);
        
        // Verify form is displayed
        Assert.assertTrue(employeeFormPage.isDisplayed(), 
            "Employee form should be displayed");
        
        // Fill the form
        employeeFormPage.fillEmployeeForm(
            TestConfig.TEST_EMPLOYEE_FIRST_NAME,
            TestConfig.TEST_EMPLOYEE_LAST_NAME,
            testEmployeeEmail,
            TestConfig.TEST_EMPLOYEE_PHONE,
            "MALE",
            "IT",
            "MID",
            "2024-01-15"
        );
        
        // Submit the form
        employeeFormPage.clickSubmit();
        waitFor(5); // Wait for form submission
        
        // Debug: Check if there are any error messages
        System.out.println("DEBUG: Checking for error messages...");
        try {
            String pageSource = driver.getPageSource();
            if (pageSource.contains("error") || pageSource.contains("Error") || pageSource.contains("required")) {
                System.out.println("DEBUG: Page contains error-related text");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Could not check page source");
        }
        
        // Workaround: Navigate manually to employee list since auto-redirect may fail
        employeeListPage.navigateTo(baseUrl);
        
        // Assert - Should be back on employee list
        employeeListPage.waitForTableToLoad();
        
        // Debug: Print employee count
        int finalCount = employeeListPage.getEmployeeCount();
        System.out.println("DEBUG: Initial count: " + initialCount + ", Final count: " + finalCount);
        System.out.println("DEBUG: Looking for employee with email: " + testEmployeeEmail);
        
        // Verify employee was created
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(testEmployeeEmail), 
            "Newly created employee should be displayed in the list");
        
        Assert.assertEquals(finalCount, initialCount + 1, 
            "Employee count should increase by 1");
        
        System.out.println("✓ Create employee test passed - Created employee: " + testEmployeeEmail);
    }
    
    /**
     * Test updating an existing employee.
     * Validates: Requirements 10.2, 10.3, 10.4
     */
    @Test(priority = 3, description = "Test employee update flow")
    public void testUpdateEmployee() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        EmployeeFormPage employeeFormPage = new EmployeeFormPage(driver);
        String updateTestEmail = "update.test." + System.currentTimeMillis() + "@techcorp.com";
        
        // First, create an employee to update
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        employeeListPage.clickAddEmployee();
        waitFor(1);
        
        employeeFormPage.fillEmployeeForm(
            "Update",
            "Test",
            updateTestEmail,
            "555-1111",
            "FEMALE",
            "HR",
            "JUNIOR",
            "2024-03-01"
        );
        employeeFormPage.clickSubmit();
        waitFor(5);
        
        // Navigate back to employee list
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        
        // Verify employee was created
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(updateTestEmail), 
            "Employee should exist before update");
        
        // Act - Click edit button
        employeeListPage.clickEditEmployee(updateTestEmail);
        waitFor(1);
        
        // Verify form is displayed with existing data
        Assert.assertTrue(employeeFormPage.isDisplayed(), 
            "Employee form should be displayed for editing");
        
        Assert.assertEquals(employeeFormPage.getFirstNameValue(), 
            "Update",
            "First name should be pre-filled");
        
        // Update the employee
        employeeFormPage.enterFirstName(TestConfig.UPDATED_EMPLOYEE_FIRST_NAME);
        employeeFormPage.enterLastName(TestConfig.UPDATED_EMPLOYEE_LAST_NAME);
        
        // Submit the form
        employeeFormPage.clickSubmit();
        waitFor(5); // Wait for form submission
        
        // Navigate manually to employee list
        employeeListPage.navigateTo(baseUrl);
        
        // Assert - Should be back on employee list
        employeeListPage.waitForTableToLoad();
        
        // Verify employee still exists (email unchanged)
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(updateTestEmail), 
            "Updated employee should still be displayed in the list");
        
        System.out.println("✓ Update employee test passed - Updated employee: " + updateTestEmail);
    }
    
    /**
     * Test deleting an employee.
     * Validates: Requirements 10.2, 10.3, 10.4
     */
    @Test(priority = 4, description = "Test employee deletion flow")
    public void testDeleteEmployee() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        EmployeeFormPage employeeFormPage = new EmployeeFormPage(driver);
        String deleteTestEmail = "delete.test." + System.currentTimeMillis() + "@techcorp.com";
        
        // First, create an employee to delete
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        int initialCount = employeeListPage.getEmployeeCount();
        
        employeeListPage.clickAddEmployee();
        waitFor(1);
        
        employeeFormPage.fillEmployeeForm(
            "Delete",
            "Test",
            deleteTestEmail,
            "555-2222",
            "OTHER",
            "SALES",
            "LEAD",
            "2024-04-01"
        );
        employeeFormPage.clickSubmit();
        waitFor(5);
        
        // Navigate back to employee list
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        
        // Verify employee was created
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(deleteTestEmail), 
            "Employee should exist before deletion");
        
        // Act - Click delete button
        employeeListPage.clickDeleteEmployee(deleteTestEmail);
        waitFor(1);
        
        // Confirm deletion
        employeeListPage.confirmDelete();
        waitFor(2);
        
        // Assert - Employee should be removed
        employeeListPage.waitForTableToLoad();
        
        Assert.assertFalse(employeeListPage.isEmployeeDisplayed(deleteTestEmail), 
            "Deleted employee should not be displayed in the list");
        
        int finalCount = employeeListPage.getEmployeeCount();
        Assert.assertEquals(finalCount, initialCount, 
            "Employee count should return to initial value after create and delete");
        
        System.out.println("✓ Delete employee test passed - Deleted employee: " + deleteTestEmail);
    }
    
    /**
     * Test complete CRUD flow in sequence.
     * Validates: Requirements 10.2, 10.3, 10.4, 10.5
     */
    @Test(priority = 5, description = "Test complete CRUD flow")
    public void testCompleteCRUDFlow() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        EmployeeFormPage employeeFormPage = new EmployeeFormPage(driver);
        String flowTestEmail = "flow.test." + System.currentTimeMillis() + "@techcorp.com";
        
        // Act & Assert - CREATE
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        int initialCount = employeeListPage.getEmployeeCount();
        
        employeeListPage.clickAddEmployee();
        waitFor(1);
        
        employeeFormPage.fillEmployeeForm(
            "Flow",
            "Test",
            flowTestEmail,
            "555-9999",
            "FEMALE",
            "HR",
            "SENIOR",
            "2024-02-01"
        );
        employeeFormPage.clickSubmit();
        waitFor(5); // Wait for form submission
        
        // Workaround: Navigate manually to employee list
        employeeListPage.navigateTo(baseUrl);
        
        // Verify CREATE
        employeeListPage.waitForTableToLoad();
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(flowTestEmail), 
            "Employee should be created");
        
        // UPDATE
        employeeListPage.clickEditEmployee(flowTestEmail);
        waitFor(1);
        employeeFormPage.enterFirstName("Updated Flow");
        employeeFormPage.clickSubmit();
        waitFor(5); // Wait for form submission
        
        // Workaround: Navigate manually to employee list
        employeeListPage.navigateTo(baseUrl);
        
        // Verify UPDATE
        employeeListPage.waitForTableToLoad();
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(flowTestEmail), 
            "Employee should still exist after update");
        
        // DELETE
        employeeListPage.clickDeleteEmployee(flowTestEmail);
        waitFor(1);
        employeeListPage.confirmDelete();
        waitFor(2);
        
        // Verify DELETE
        employeeListPage.waitForTableToLoad();
        Assert.assertFalse(employeeListPage.isEmployeeDisplayed(flowTestEmail), 
            "Employee should be deleted");
        
        int finalCount = employeeListPage.getEmployeeCount();
        Assert.assertEquals(finalCount, initialCount, 
            "Employee count should return to initial value");
        
        System.out.println("✓ Complete CRUD flow test passed");
    }
}
