package com.techcorp.devops.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
    
    // Form input elements - Using labels since Material-UI generates dynamic IDs
    @FindBy(xpath = "//label[contains(text(), 'First Name')]/following-sibling::div//input | //input[@name='firstName']")
    private WebElement firstNameInput;
    
    @FindBy(xpath = "//label[contains(text(), 'Last Name')]/following-sibling::div//input | //input[@name='lastName']")
    private WebElement lastNameInput;
    
    @FindBy(xpath = "//label[contains(text(), 'Email')]/following-sibling::div//input | //input[@name='email']")
    private WebElement emailInput;
    
    @FindBy(xpath = "//label[contains(text(), 'Phone')]/following-sibling::div//input | //input[@name='phone']")
    private WebElement phoneInput;
    
    @FindBy(xpath = "//button[@type='submit' or contains(text(), 'Save') or contains(text(), 'Submit')]")
    private WebElement submitButton;
    
    @FindBy(xpath = "//button[contains(text(), 'Cancel')] | //a[contains(text(), 'Cancel')]")
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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased from 10 to 20 seconds
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
            // Material-UI radio buttons - use JavaScript to click
            WebElement genderInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='radio' and @value='" + gender + "']")
                )
            );
            
            // Use JavaScript Executor to click the radio button and trigger React events
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", genderInput);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", genderInput);
            
            System.out.println("DEBUG: Gender selected using JavaScript: " + gender);
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
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // Click on the department select/combobox - find by label
            WebElement departmentSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[contains(text(), 'Department')]/following-sibling::div//div[@role='combobox' or @role='button']")
                )
            );
            
            // Try regular click first (more reliable for dropdowns)
            departmentSelect.click();
            
            // Wait for the dropdown to open - Material-UI renders in a portal
            try {
                Thread.sleep(2000); // Increased wait time for dropdown to render
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // Select the option - Material-UI uses li elements with text content
            WebElement departmentOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[@role='option' and (contains(text(), '" + department + "') or @data-value='" + department + "')]")
                )
            );
            js.executeScript("arguments[0].click();", departmentOption);
            
            System.out.println("DEBUG: Department selected: " + department);
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
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // Click on the level select/combobox - find by label
            WebElement levelSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[contains(text(), 'Level')]/following-sibling::div//div[@role='combobox' or @role='button']")
                )
            );
            
            // Try regular click first (more reliable for dropdowns)
            levelSelect.click();
            
            // Wait for the dropdown to open - Material-UI renders in a portal
            try {
                Thread.sleep(2000); // Increased wait time for dropdown to render
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // Select the option - Material-UI uses li elements with text content
            // Convert level to proper case (e.g., "MID" -> "Mid")
            String displayLevel = level.substring(0, 1).toUpperCase() + level.substring(1).toLowerCase();
            WebElement levelOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[@role='option' and (contains(text(), '" + displayLevel + "') or @data-value='" + level + "')]")
                )
            );
            js.executeScript("arguments[0].click();", levelOption);
            
            System.out.println("DEBUG: Level selected: " + level);
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
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        for (String skill : skills) {
            try {
                // Material-UI checkboxes - find by label text
                WebElement skillCheckbox = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//label[.//span[text()='" + skill + "']]//input[@type='checkbox']")
                    )
                );
                
                // Use JavaScript Executor to click the checkbox and trigger React events
                js.executeScript("arguments[0].click();", skillCheckbox);
                js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", skillCheckbox);
                
                System.out.println("DEBUG: Skill selected using JavaScript: " + skill);
            } catch (Exception e) {
                System.err.println("Failed to select skill: " + skill);
                // Continue with other skills even if one fails
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
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            WebElement hireDateInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(text(), 'Hire Date')]/following-sibling::div//input | //input[@name='hireDate']")
                )
            );
            
            // Scroll to the element to ensure it's in view
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", hireDateInput);
            
            // Wait for scroll to complete
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // Focus on the input first
            js.executeScript("arguments[0].focus();", hireDateInput);
            
            // Use JavaScript to set the value and trigger React Hook Form updates
            // This creates a native input event that React Hook Form will recognize
            js.executeScript(
                "var input = arguments[0];" +
                "var value = arguments[1];" +
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                "nativeInputValueSetter.call(input, value);" +
                "var event = new Event('input', { bubbles: true });" +
                "input.dispatchEvent(event);",
                hireDateInput, hireDate
            );
            
            // Wait a moment for React to process
            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // Trigger change and blur events
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", hireDateInput);
            js.executeScript("arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));", hireDateInput);
            
            // Wait for React Hook Form to update
            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // Verify the value was set correctly
            String actualValue = (String) js.executeScript("return arguments[0].value;", hireDateInput);
            System.out.println("DEBUG: Hire date set to: " + hireDate + ", actual value: " + actualValue);
            
            // If value didn't stick, try the native approach again
            if (!hireDate.equals(actualValue)) {
                System.out.println("DEBUG: Date value didn't stick, retrying with native setter...");
                js.executeScript(
                    "var input = arguments[0];" +
                    "var value = arguments[1];" +
                    "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                    "nativeInputValueSetter.call(input, value);" +
                    "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                    "input.dispatchEvent(new Event('change', { bubbles: true }));",
                    hireDateInput, hireDate
                );
                
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                
                actualValue = (String) js.executeScript("return arguments[0].value;", hireDateInput);
                System.out.println("DEBUG: After retry, actual value: " + actualValue);
            }
        } catch (Exception e) {
            System.err.println("Failed to enter hire date: " + hireDate);
            throw e;
        }
    }
    
    /**
     * Clicks the submit button.
     */
    public void clickSubmit() {
        // Wait a moment to ensure all form state is settled
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        System.out.println("DEBUG: Clicking submit button...");
        
        // Verify hire date value before submitting
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            WebElement hireDateInput = driver.findElement(
                By.xpath("//label[contains(text(), 'Hire Date')]/following-sibling::div//input | //input[@name='hireDate']")
            );
            String dateValue = (String) js.executeScript("return arguments[0].value;", hireDateInput);
            System.out.println("DEBUG: Hire date value before submit: " + dateValue);
        } catch (Exception e) {
            System.out.println("DEBUG: Could not verify hire date before submit");
        }
        
        // Use JavaScript Executor for more reliable clicking
        js.executeScript("arguments[0].click();", submitButton);
        
        System.out.println("DEBUG: Submit button clicked using JavaScript");
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
        System.out.println("DEBUG: Filling first name...");
        enterFirstName(firstName);
        System.out.println("DEBUG: Filling last name...");
        enterLastName(lastName);
        System.out.println("DEBUG: Filling email...");
        enterEmail(email);
        System.out.println("DEBUG: Filling phone...");
        enterPhone(phone);
        System.out.println("DEBUG: Selecting gender: " + gender);
        selectGender(gender);
        System.out.println("DEBUG: Selecting department: " + department);
        selectDepartment(department);
        System.out.println("DEBUG: Selecting level: " + level);
        selectLevel(level);
        System.out.println("DEBUG: Selecting skills...");
        // Select at least one skill (required field)
        selectSkills("Java", "Spring Boot");
        
        // Wait for dropdowns to settle before entering date
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Enter hire date LAST to prevent it from being cleared by dropdown interactions
        System.out.println("DEBUG: Entering hire date...");
        enterHireDate(hireDate);
        System.out.println("DEBUG: Form filling complete");
        
        // Wait a moment for React to update the form state
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
