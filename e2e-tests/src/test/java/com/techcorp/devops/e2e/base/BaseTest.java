package com.techcorp.devops.e2e.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base test class for all Selenium tests.
 * Handles WebDriver setup, teardown, and screenshot capture on failure.
 */
public abstract class BaseTest {
    
    protected WebDriver driver;
    protected String baseUrl;
    protected String apiUrl;
    
    @BeforeMethod
    public void setUp() {
        // Get URLs from system properties or use defaults
        baseUrl = System.getProperty("baseUrl", "http://localhost:3000");
        apiUrl = System.getProperty("apiUrl", "http://localhost:8080");
        
        // Setup WebDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        
        // Check if headless mode is requested via system property
        String headlessMode = System.getProperty("selenium.headless", "false");
        if ("true".equalsIgnoreCase(headlessMode)) {
            options.addArguments("--headless=new"); // Use new headless mode (Chrome 109+)
            System.out.println("Running in headless mode");
        }
        
        // Essential options for CI/CD environments (especially Linux)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-setuid-sandbox");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        
        // Disable unnecessary features for stability
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        System.out.println("Chrome options configured for: " + (headlessMode.equalsIgnoreCase("true") ? "CI/CD" : "Local"));
        
        // Initialize WebDriver
        driver = new ChromeDriver(options);
        
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // Maximize window
        driver.manage().window().maximize();
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        // Take screenshot on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }
        
        // Quit WebDriver
        if (driver != null) {
            driver.quit();
        }
    }
    
    /**
     * Takes a screenshot and saves it to the screenshots directory.
     * 
     * @param testName Name of the test that failed
     */
    protected void takeScreenshot(String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.png", testName, timestamp);
            
            // Take screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(screenshotsDir, fileName);
            
            // Copy screenshot to destination
            FileUtils.copyFile(screenshot, destination);
            
            System.out.println("Screenshot saved: " + destination.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Navigates to the base URL of the application.
     */
    protected void navigateToHome() {
        driver.get(baseUrl);
    }
    
    /**
     * Waits for a specified number of seconds.
     * 
     * @param seconds Number of seconds to wait
     */
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
