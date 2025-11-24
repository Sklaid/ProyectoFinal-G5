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
        waitFor(2);
        
        // Assert - Should be back on employee list
        employeeListPage.waitForTableToLoad();
        
        // Verify employee was created
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(testEmployeeEmail), 
            "Newly created employee should be displayed in the list");
        
        int finalCount = employeeListPage.getEmployeeCount();
        Assert.assertEquals(finalCount, initialCount + 1, 
            "Employee count should increase by 1");
        
        System.out.println("✓ Create employee test passed - Created employee: " + testEmployeeEmail);
    }
    
    /**
     * Test updating an existing employee.
     * Validates: Requirements 10.2, 10.3, 10.4
     */
    @Test(priority = 3, dependsOnMethods = "testCreateEmployee", 
          description = "Test employee update flow")
    public void testUpdateEmployee() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        EmployeeFormPage employeeFormPage = new EmployeeFormPage(driver);
        
        // Act - Navigate to employee list
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        
        // Verify employee exists
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(testEmployeeEmail), 
            "Employee should exist before update");
        
        // Click edit button
        employeeListPage.clickEditEmployee(testEmployeeEmail);
        waitFor(1);
        
        // Verify form is displayed with existing data
        Assert.assertTrue(employeeFormPage.isDisplayed(), 
            "Employee form should be displayed for editing");
        
        Assert.assertEquals(employeeFormPage.getFirstNameValue(), 
            TestConfig.TEST_EMPLOYEE_FIRST_NAME,
            "First name should be pre-filled");
        
        // Update the employee
        employeeFormPage.enterFirstName(TestConfig.UPDATED_EMPLOYEE_FIRST_NAME);
        employeeFormPage.enterLastName(TestConfig.UPDATED_EMPLOYEE_LAST_NAME);
        
        // Submit the form
        employeeFormPage.clickSubmit();
        waitFor(2);
        
        // Assert - Should be back on employee list
        employeeListPage.waitForTableToLoad();
        
        // Verify employee still exists (email unchanged)
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(testEmployeeEmail), 
            "Updated employee should still be displayed in the list");
        
        System.out.println("✓ Update employee test passed - Updated employee: " + testEmployeeEmail);
    }
    
    /**
     * Test deleting an employee.
     * Validates: Requirements 10.2, 10.3, 10.4
     */
    @Test(priority = 4, dependsOnMethods = "testUpdateEmployee", 
          description = "Test employee deletion flow")
    public void testDeleteEmployee() {
        // Arrange
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        
        // Act - Navigate to employee list
        employeeListPage.navigateTo(baseUrl);
        employeeListPage.waitForTableToLoad();
        
        int initialCount = employeeListPage.getEmployeeCount();
        
        // Verify employee exists
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(testEmployeeEmail), 
            "Employee should exist before deletion");
        
        // Click delete button
        employeeListPage.clickDeleteEmployee(testEmployeeEmail);
        waitFor(1);
        
        // Confirm deletion
        employeeListPage.confirmDelete();
        waitFor(2);
        
        // Assert - Employee should be removed
        employeeListPage.waitForTableToLoad();
        
        Assert.assertFalse(employeeListPage.isEmployeeDisplayed(testEmployeeEmail), 
            "Deleted employee should not be displayed in the list");
        
        int finalCount = employeeListPage.getEmployeeCount();
        Assert.assertEquals(finalCount, initialCount - 1, 
            "Employee count should decrease by 1");
        
        System.out.println("✓ Delete employee test passed - Deleted employee: " + testEmployeeEmail);
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
        waitFor(2);
        
        // Verify CREATE
        employeeListPage.waitForTableToLoad();
        Assert.assertTrue(employeeListPage.isEmployeeDisplayed(flowTestEmail), 
            "Employee should be created");
        
        // UPDATE
        employeeListPage.clickEditEmployee(flowTestEmail);
        waitFor(1);
        employeeFormPage.enterFirstName("Updated Flow");
        employeeFormPage.clickSubmit();
        waitFor(2);
        
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
