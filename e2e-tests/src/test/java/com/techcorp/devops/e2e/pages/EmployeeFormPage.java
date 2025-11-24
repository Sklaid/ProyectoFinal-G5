package com.techcorp.devops.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for the Employee Form Page.
 * Represents the employee creation/edit form with all input controls.
 */
public class EmployeeFormPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Form input elements
    @FindBy(id = "firstName")
    private WebElement firstNameInput;
    
    @FindBy(id = "lastName")
    private WebElement lastNameInput;
    
    @FindBy(id = "email")
    private WebElement emailInput;
    
    @FindBy(id = "phone")
    private WebElement phoneInput;
    
    @FindBy(css = "button[type='submit'], button:contains('Save'), button:contains('Submit')")
    private WebElement submitButton;
    
    @FindBy(css = "button:contains('Cancel'), a:contains('Cancel')")
    private WebElement cancelButton;
    
    @FindBy(css = ".success-message, .MuiAlert-standardSuccess")
    private WebElement successMessage;
    
    /**
     * Constructor initializes the page elements.
     * 
     * @param driver WebDriver instance
     */
    public EmployeeFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Navigates to the new employee form page.
     * 
     * @param baseUrl Base URL of the application
     */
    public void navigateToNewEmployeeForm(String baseUrl) {
        driver.get(baseUrl + "/employees/new");
    }
    
    /**
     * Navigates to the edit employee form page.
     * 
     * @param baseUrl Base URL of the application
     * @param employeeId ID of the employee to edit
     */
    public void navigateToEditEmployeeForm(String baseUrl, String employeeId) {
        driver.get(baseUrl + "/employees/" + employeeId + "/edit");
    }
    
    /**
     * Checks if the form page is displayed.
     * 
     * @return true if form is displayed, false otherwise
     */
    public boolean isDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(firstNameInput));
            return firstNameInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Enters first name.
     * 
     * @param firstName First name to enter
     */
    public void enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOf(firstNameInput));
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
    }
    
    /**
     * Enters last name.
     * 
     * @param lastName Last name to enter
     */
    public void enterLastName(String lastName) {
        wait.until(ExpectedConditions.visibilityOf(lastNameInput));
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
    }
    
    /**
     * Enters email.
     * 
     * @param email Email to enter
     */
    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);
    }
    
    /**
     * Enters phone number.
     * 
     * @param phone Phone number to enter
     */
    public void enterPhone(String phone) {
        wait.until(ExpectedConditions.visibilityOf(phoneInput));
        phoneInput.clear();
        phoneInput.sendKeys(phone);
    }
    
    /**
     * Selects gender using radio button.
     * 
     * @param gender Gender to select (MALE, FEMALE, OTHER)
     */
    public void selectGender(String gender) {
        try {
            WebElement genderRadio = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='radio' and @value='" + gender + "']")
                )
            );
            genderRadio.click();
        } catch (Exception e) {
            System.err.println("Failed to select gender: " + gender);
            throw e;
        }
    }
    
    /**
     * Selects department from combobox/select.
     * 
     * @param department Department to select (IT, HR, FINANCE, SALES)
     */
    public void selectDepartment(String department) {
        try {
            // Click on the department select/combobox
            WebElement departmentSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("department")
                )
            );
            departmentSelect.click();
            
            // Select the option
            WebElement departmentOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[@data-value='" + department + "' or contains(text(), '" + department + "')]")
                )
            );
            departmentOption.click();
        } catch (Exception e) {
            System.err.println("Failed to select department: " + department);
            throw e;
        }
    }
    
    /**
     * Selects level from combobox/select.
     * 
     * @param level Level to select (JUNIOR, MID, SENIOR, LEAD)
     */
    public void selectLevel(String level) {
        try {
            // Click on the level select/combobox
            WebElement levelSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("level")
                )
            );
            levelSelect.click();
            
            // Select the option
            WebElement levelOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[@data-value='" + level + "' or contains(text(), '" + level + "')]")
                )
            );
            levelOption.click();
        } catch (Exception e) {
            System.err.println("Failed to select level: " + level);
            throw e;
        }
    }
    
    /**
     * Selects skills using checkboxes.
     * 
     * @param skills Array of skills to select
     */
    public void selectSkills(String... skills) {
        for (String skill : skills) {
            try {
                WebElement skillCheckbox = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@type='checkbox' and @value='" + skill + "']")
                    )
                );
                if (!skillCheckbox.isSelected()) {
                    skillCheckbox.click();
                }
            } catch (Exception e) {
                System.err.println("Failed to select skill: " + skill);
            }
        }
    }
    
    /**
     * Enters hire date.
     * 
     * @param hireDate Hire date in format YYYY-MM-DD
     */
    public void enterHireDate(String hireDate) {
        try {
            WebElement hireDateInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("hireDate"))
            );
            hireDateInput.clear();
            hireDateInput.sendKeys(hireDate);
        } catch (Exception e) {
            System.err.println("Failed to enter hire date: " + hireDate);
            throw e;
        }
    }
    
    /**
     * Clicks the submit button.
     */
    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();
    }
    
    /**
     * Clicks the cancel button.
     */
    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        cancelButton.click();
    }
    
    /**
     * Fills the complete employee form with all required fields.
     * 
     * @param firstName First name
     * @param lastName Last name
     * @param email Email
     * @param phone Phone number
     * @param gender Gender
     * @param department Department
     * @param level Level
     * @param hireDate Hire date
     */
    public void fillEmployeeForm(String firstName, String lastName, String email, 
                                  String phone, String gender, String department, 
                                  String level, String hireDate) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterEmail(email);
        enterPhone(phone);
        selectGender(gender);
        selectDepartment(department);
        selectLevel(level);
        enterHireDate(hireDate);
    }
    
    /**
     * Checks if success message is displayed.
     * 
     * @return true if success message is displayed, false otherwise
     */
    public boolean isSuccessMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(successMessage));
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the current value of the first name field.
     * 
     * @return First name value
     */
    public String getFirstNameValue() {
        wait.until(ExpectedConditions.visibilityOf(firstNameInput));
        return firstNameInput.getAttribute("value");
    }
    
    /**
     * Gets the current value of the email field.
     * 
     * @return Email value
     */
    public String getEmailValue() {
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        return emailInput.getAttribute("value");
    }
}
