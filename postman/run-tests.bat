@echo off
REM Script to run Postman collections with Newman
REM This script runs authentication tests first, then employee tests

echo ========================================
echo Running DevOps Platform API Tests
echo ========================================
echo.

REM Create reports directory if it doesn't exist
if not exist "reports" mkdir reports

echo [1/2] Running Authentication Tests...
echo ----------------------------------------
newman run postman/auth.postman_collection.json -e postman/dev.env.json
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo WARNING: Some authentication tests failed!
    echo.
)

echo.
echo [2/2] Running Employee CRUD Tests...
echo ----------------------------------------
newman run postman/employees.postman_collection.json -e postman/dev.env.json
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo WARNING: Some employee tests failed!
    echo.
)

echo.
echo ========================================
echo Test Execution Complete
echo ========================================
echo.
echo Reports have been generated in the reports/ directory
echo.

pause
