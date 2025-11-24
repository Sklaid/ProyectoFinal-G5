@echo off
REM Script to run Postman collections with Newman and generate HTML + JUnit reports
REM This script runs authentication tests first, then employee tests

echo ========================================
echo Running DevOps Platform API Tests
echo With HTML and JUnit Reports
echo ========================================
echo.

REM Create reports directory if it doesn't exist
if not exist "reports" mkdir reports

echo [1/2] Running Authentication Tests...
echo ----------------------------------------
newman run postman/auth.postman_collection.json ^
    -e postman/dev.env.json ^
    --reporters cli,htmlextra ^
    --reporter-htmlextra-export reports/auth-report.html ^
    --reporter-htmlextra-title "Authentication API Tests" ^
    --reporter-htmlextra-darkTheme

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo WARNING: Some authentication tests failed!
    echo See reports/auth-report.html for details
    echo.
)

echo.
echo [2/2] Running Employee CRUD Tests...
echo ----------------------------------------
newman run postman/employees.postman_collection.json ^
    -e postman/dev.env.json ^
    --reporters cli,htmlextra ^
    --reporter-htmlextra-export reports/employees-report.html ^
    --reporter-htmlextra-title "Employee CRUD API Tests" ^
    --reporter-htmlextra-darkTheme

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo WARNING: Some employee tests failed!
    echo See reports/employees-report.html for details
    echo.
)

echo.
echo ========================================
echo Test Execution Complete
echo ========================================
echo.
echo HTML Reports generated:
echo   - reports/auth-report.html
echo   - reports/employees-report.html
echo.
echo Open these files in your browser to view detailed test results
echo.

pause
