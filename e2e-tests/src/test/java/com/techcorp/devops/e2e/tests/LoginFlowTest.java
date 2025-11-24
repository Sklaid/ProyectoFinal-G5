package com.techcorp.devops.e2e.tests;

import com.techcorp.devops.e2e.base.BaseTest;
import com.techcorp.devops.e2e.config.TestConfig;
import com.techcorp.devops.e2e.pages.EmployeeListPage;
import com.techcorp.devops.e2e.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for Login Flow functionality.
 * Tests the complete login flow including successful and failed login attempts.
 * 
 * Requirements: 10.2, 10.3
 */
public class LoginFlowTest extends BaseTest {
    
    /**
     * Test successful login with valid credentials.
     * Validates: Requirements 10.2, 10.3
     */
    @Test(priority = 1, description = "Test successful login with valid credentials")
    public void testSuccessfulLogin() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        
        // Act
        loginPage.navigateTo(baseUrl);
        Assert.assertTrue(loginPage.isDisplayed(), "Login page should be displayed");
        
        loginPage.login(TestConfig.TEST_USERNAME, TestConfig.TEST_PASSWORD);
        
        // Wait for navigation to complete
        waitFor(2);
        
        // Assert
        Assert.assertTrue(employeeListPage.isDisplayed(), 
            "Employee list page should be displayed after successful login");
        
        System.out.println("✓ Successful login test passed");
    }
    
    /**
     * Test failed login with invalid credentials.
     * Validates: Requirements 10.2, 10.3
     */
    @Test(priority = 2, description = "Test failed login with invalid credentials")
    public void testFailedLoginWithInvalidCredentials() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        String invalidUsername = "invaliduser";
        String invalidPassword = "wrongpassword";
        
        // Act
        loginPage.navigateTo(baseUrl);
        Assert.assertTrue(loginPage.isDisplayed(), "Login page should be displayed");
        
        loginPage.login(invalidUsername, invalidPassword);
        
        // Wait for error message
        waitFor(2);
        
        // Assert
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid credentials");
        
        // Verify still on login page
        Assert.assertTrue(loginPage.isDisplayed(), 
            "Should remain on login page after failed login");
        
        System.out.println("✓ Failed login test passed");
    }
    
    /**
     * Test login with empty credentials.
     * Validates: Requirements 10.2, 10.3
     */
    @Test(priority = 3, description = "Test login with empty credentials")
    public void testLoginWithEmptyCredentials() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        
        // Act
        loginPage.navigateTo(baseUrl);
        Assert.assertTrue(loginPage.isDisplayed(), "Login page should be displayed");
        
        loginPage.login("", "");
        
        // Wait for validation
        waitFor(1);
        
        // Assert - Should remain on login page (form validation should prevent submission)
        Assert.assertTrue(loginPage.isDisplayed(), 
            "Should remain on login page with empty credentials");
        
        System.out.println("✓ Empty credentials test passed");
    }
    
    /**
     * Test complete login flow from start to employee list.
     * Validates: Requirements 10.2, 10.3, 10.4
     */
    @Test(priority = 4, description = "Test complete login flow")
    public void testCompleteLoginFlow() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        
        // Act - Navigate to home (should redirect to login if not authenticated)
        navigateToHome();
        waitFor(1);
        
        // Verify redirected to login
        Assert.assertTrue(loginPage.isDisplayed() || driver.getCurrentUrl().contains("login"), 
            "Should be on login page or redirected to login");
        
        // Perform login
        if (!loginPage.isDisplayed()) {
            loginPage.navigateTo(baseUrl);
        }
        loginPage.login(TestConfig.TEST_USERNAME, TestConfig.TEST_PASSWORD);
        
        // Wait for navigation
        waitFor(2);
        
        // Assert - Should be on employee list page
        Assert.assertTrue(employeeListPage.isDisplayed(), 
            "Should be on employee list page after successful login");
        
        System.out.println("✓ Complete login flow test passed");
    }
}
