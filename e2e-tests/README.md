# E2E Tests - DevOps Enterprise Platform

This directory contains Selenium WebDriver functional tests for the DevOps Enterprise Platform.

## Overview

The E2E tests validate the complete user flows of the application, including:
- Login flow (successful and failed attempts)
- Employee CRUD operations (Create, Read, Update, Delete)
- UI interactions with various controls (forms, tables, buttons)

## Technology Stack

- **Selenium WebDriver 4.15.0**: Browser automation
- **TestNG 7.8.0**: Test framework
- **WebDriverManager 5.6.2**: Automatic browser driver management
- **Maven**: Build and dependency management

## Project Structure

```
e2e-tests/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/techcorp/devops/e2e/
│       │       ├── base/
│       │       │   └── BaseTest.java          # Base test class with setup/teardown
│       │       ├── config/
│       │       │   └── TestConfig.java        # Test configuration and constants
│       │       ├── pages/
│       │       │   ├── LoginPage.java         # Login page object
│       │       │   ├── EmployeeListPage.java  # Employee list page object
│       │       │   └── EmployeeFormPage.java  # Employee form page object
│       │       └── tests/
│       │           ├── LoginFlowTest.java     # Login flow tests
│       │           └── EmployeeCRUDTest.java  # Employee CRUD tests
│       └── resources/
│           └── testng.xml                     # TestNG suite configuration
├── screenshots/                               # Screenshots on test failure
├── pom.xml                                    # Maven configuration
└── README.md                                  # This file
```

## Prerequisites

1. **Java 17** or higher installed
2. **Maven 3.8+** installed
3. **Chrome browser** installed (tests run in headless mode)
4. **Application running** at:
   - Frontend: http://localhost:3000
   - Backend: http://localhost:8080

## Running the Tests

### 1. Start the Application

Before running tests, ensure the application is running:

```bash
# From project root
docker-compose -f docker-compose.dev.yml up -d

# Or use the start script
./scripts/start-dev.sh  # Linux/Mac
scripts\start-dev.bat   # Windows
```

Wait for all services to be healthy (check with `docker ps`).

### 2. Run All Tests

```bash
# From e2e-tests directory
mvn clean test

# Or from project root
mvn clean test -f e2e-tests/pom.xml
```

### 3. Run with Selenium Profile

```bash
mvn test -P selenium-tests
```

### 4. Run Specific Test Class

```bash
# Run only login tests
mvn test -Dtest=LoginFlowTest

# Run only CRUD tests
mvn test -Dtest=EmployeeCRUDTest
```

### 5. Run with Custom URLs

```bash
mvn test -DbaseUrl=http://localhost:3000 -DapiUrl=http://localhost:8080
```

## Test Configuration

### System Properties

- `baseUrl`: Frontend URL (default: http://localhost:3000)
- `apiUrl`: Backend URL (default: http://localhost:8080)

### Test Data

Test credentials and data are defined in `TestConfig.java`:
- Username: `admin`
- Password: `admin123`

### Browser Configuration

Tests run in **headless Chrome** by default for CI/CD compatibility. To run with visible browser, modify `BaseTest.java`:

```java
// Comment out this line in BaseTest.setUp()
// options.addArguments("--headless");
```

## Test Reports

### TestNG Reports

After running tests, TestNG generates HTML reports:
- Location: `target/surefire-reports/index.html`
- Open in browser to view detailed results

### Screenshots on Failure

When a test fails, a screenshot is automatically captured:
- Location: `screenshots/`
- Filename format: `{testName}_{timestamp}.png`

### Console Output

Tests print progress messages to console:
```
✓ Successful login test passed
✓ Create employee test passed - Created employee: test.1234567890@techcorp.com
✓ Update employee test passed - Updated employee: test.1234567890@techcorp.com
✓ Delete employee test passed - Deleted employee: test.1234567890@techcorp.com
```

## Page Object Model

The tests use the Page Object Model (POM) pattern for maintainability:

### LoginPage
- Methods: `login()`, `enterUsername()`, `enterPassword()`, `clickLoginButton()`
- Locators: Username input, password input, submit button, error message

### EmployeeListPage
- Methods: `clickAddEmployee()`, `clickEditEmployee()`, `clickDeleteEmployee()`, `isEmployeeDisplayed()`
- Locators: Add button, employee table, edit/delete buttons

### EmployeeFormPage
- Methods: `fillEmployeeForm()`, `enterFirstName()`, `selectGender()`, `selectDepartment()`
- Locators: Form inputs (text, radio, checkbox, select), submit button

## Test Cases

### LoginFlowTest
1. **testSuccessfulLogin**: Validates successful login with valid credentials
2. **testFailedLoginWithInvalidCredentials**: Validates error handling for invalid credentials
3. **testLoginWithEmptyCredentials**: Validates form validation for empty fields
4. **testCompleteLoginFlow**: Validates end-to-end login flow with redirects

### EmployeeCRUDTest
1. **testViewEmployeeList**: Validates employee list page display
2. **testCreateEmployee**: Validates employee creation flow
3. **testUpdateEmployee**: Validates employee update flow
4. **testDeleteEmployee**: Validates employee deletion flow
5. **testCompleteCRUDFlow**: Validates complete CRUD cycle

## CI/CD Integration

### GitHub Actions

The tests are integrated into the CI/CD pipeline:

```yaml
- name: Run Selenium tests
  run: mvn test -P selenium-tests
  working-directory: ./e2e-tests

- name: Upload screenshots on failure
  if: failure()
  uses: actions/upload-artifact@v4
  with:
    name: selenium-screenshots
    path: e2e-tests/screenshots/
```

### Requirements Validation

These tests validate the following requirements:
- **Requirement 10.1**: Selenium WebDriver and TestNG setup
- **Requirement 10.2**: Page Object Model implementation
- **Requirement 10.3**: Test cases for login and CRUD flows
- **Requirement 10.4**: Screenshot capture on failure
- **Requirement 10.5**: Test report generation

## Troubleshooting

### Tests Fail with "Connection Refused"
- Ensure the application is running at the correct URLs
- Check `docker ps` to verify all containers are healthy
- Wait 30-60 seconds after starting containers for services to initialize

### ChromeDriver Issues
- WebDriverManager automatically downloads the correct ChromeDriver version
- If issues persist, manually specify ChromeDriver version in `pom.xml`

### Element Not Found Errors
- Increase implicit wait time in `BaseTest.setUp()`
- Check if UI element IDs/selectors have changed
- Review screenshots in `screenshots/` directory

### Tests Pass Locally but Fail in CI
- Ensure headless mode is enabled in `BaseTest.java`
- Check CI environment has sufficient resources
- Increase wait times for slower CI environments

## Best Practices

1. **Always run tests against a clean database** or use unique test data
2. **Review screenshots** when tests fail to understand the issue
3. **Keep page objects updated** when UI changes
4. **Use explicit waits** for dynamic content
5. **Run tests locally** before pushing to CI/CD

## Maintenance

When the UI changes:
1. Update the corresponding Page Object class
2. Update element locators (@FindBy annotations)
3. Run tests locally to verify
4. Update test data in TestConfig if needed

## Contact

For questions or issues with E2E tests, contact the QA team or refer to the main project documentation.
