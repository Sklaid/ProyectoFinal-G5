package com.techcorp.devops.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for the Login Page.
 * Represents the login page and provides methods to interact with it.
 */
public class LoginPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Page elements using @FindBy annotations
    @FindBy(id = "username")
    private WebElement usernameInput;
    
    @FindBy(id = "password")
    private WebElement passwordInput;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    @FindBy(css = ".MuiAlert-message, [role='alert']")
    private WebElement errorMessage;
    
    /**
     * Constructor initializes the page elements.
     * 
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Navigates to the login page.
     * 
     * @param baseUrl Base URL of the application
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/login");
    }
    
    /**
     * Enters username in the username field.
     * 
     * @param username Username to enter
     */
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }
    
    /**
     * Enters password in the password field.
     * 
     * @param password Password to enter
     */
    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }
    
    /**
     * Clicks the login button.
     */
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }
    
    /**
     * Performs complete login action.
     * 
     * @param username Username to login with
     * @param password Password to login with
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
    
    /**
     * Checks if error message is displayed.
     * 
     * @return true if error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the error message text.
     * 
     * @return Error message text
     */
    public String getErrorMessageText() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }
    
    /**
     * Checks if the login page is displayed.
     * 
     * @return true if login page is displayed, false otherwise
     */
    public boolean isDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameInput));
            return usernameInput.isDisplayed() && passwordInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
