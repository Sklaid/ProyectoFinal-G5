package com.techcorp.devops.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for the Employee List Page.
 * Represents the employee list page with table and action buttons.
 */
public class EmployeeListPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Page elements
    // Using XPath to find button by text content since CSS doesn't support :contains()
    @FindBy(xpath = "//button[contains(text(), 'Create New') or contains(text(), 'Add')]")
    private WebElement addEmployeeButton;
    
    @FindBy(css = "table, .MuiTable-root")
    private WebElement employeeTable;
    
    @FindBy(css = "tbody tr, .MuiTableBody-root tr")
    private List<WebElement> employeeRows;
    
    @FindBy(css = "h1, h2, .page-title")
    private WebElement pageTitle;
    
    /**
     * Constructor initializes the page elements.
     * 
     * @param driver WebDriver instance
     */
    public EmployeeListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased from 10 to 20 seconds
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Navigates to the employee list page.
     * 
     * @param baseUrl Base URL of the application
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/employees");
    }
    
    /**
     * Checks if the employee list page is displayed.
     * 
     * @return true if page is displayed, false otherwise
     */
    public boolean isDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(employeeTable));
            return employeeTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Clicks the "Add Employee" button.
     */
    public void clickAddEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(addEmployeeButton));
        addEmployeeButton.click();
    }
    
    /**
     * Gets the number of employees in the table.
     * 
     * @return Number of employee rows
     */
    public int getEmployeeCount() {
        wait.until(ExpectedConditions.visibilityOf(employeeTable));
        return employeeRows.size();
    }
    
    /**
     * Checks if an employee with the given email exists in the table.
     * 
     * @param email Email to search for
     * @return true if employee exists, false otherwise
     */
    public boolean isEmployeeDisplayed(String email) {
        try {
            wait.until(ExpectedConditions.visibilityOf(employeeTable));
            WebElement employeeCell = driver.findElement(
                By.xpath("//td[contains(text(), '" + email + "')]")
            );
            return employeeCell.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Clicks the edit button for an employee with the given email.
     * 
     * @param email Email of the employee to edit
     */
    public void clickEditEmployee(String email) {
        try {
            wait.until(ExpectedConditions.visibilityOf(employeeTable));
            WebElement editButton = driver.findElement(
                By.xpath("//td[contains(text(), '" + email + "')]/ancestor::tr//button[contains(@aria-label, 'edit') or contains(., 'Edit')]")
            );
            wait.until(ExpectedConditions.elementToBeClickable(editButton));
            editButton.click();
        } catch (Exception e) {
            System.err.println("Failed to click edit button for employee: " + email);
            throw e;
        }
    }
    
    /**
     * Clicks the delete button for an employee with the given email.
     * 
     * @param email Email of the employee to delete
     */
    public void clickDeleteEmployee(String email) {
        try {
            wait.until(ExpectedConditions.visibilityOf(employeeTable));
            WebElement deleteButton = driver.findElement(
                By.xpath("//td[contains(text(), '" + email + "')]/ancestor::tr//button[contains(@aria-label, 'delete') or contains(., 'Delete')]")
            );
            wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
            deleteButton.click();
        } catch (Exception e) {
            System.err.println("Failed to click delete button for employee: " + email);
            throw e;
        }
    }
    
    /**
     * Confirms the delete action in the confirmation dialog.
     */
    public void confirmDelete() {
        try {
            WebElement confirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Confirm') or contains(text(), 'Delete') or contains(text(), 'Yes')]")
                )
            );
            confirmButton.click();
        } catch (Exception e) {
            System.err.println("Failed to confirm delete");
            throw e;
        }
    }
    
    /**
     * Gets the page title text.
     * 
     * @return Page title text
     */
    public String getPageTitle() {
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
        return pageTitle.getText();
    }
    
    /**
     * Waits for the employee table to load.
     */
    public void waitForTableToLoad() {
        wait.until(ExpectedConditions.visibilityOf(employeeTable));
    }
}
