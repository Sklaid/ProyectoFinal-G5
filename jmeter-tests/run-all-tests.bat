@echo off
REM Run All JMeter Performance Tests
REM This script runs all performance tests sequentially

echo ========================================
echo Running All Performance Tests
echo ========================================
echo.

REM Create directories if they don't exist
if not exist "results" mkdir results
if not exist "reports" mkdir reports

echo [1/2] Running Authentication Load Test...
echo ========================================
if exist "reports\auth-report" rmdir /s /q "reports\auth-report"
jmeter -n -t auth-load-test.jmx ^
  -l results\auth-results.jtl ^
  -e -o reports\auth-report

if %ERRORLEVEL% NEQ 0 (
    echo Authentication test FAILED!
    pause
    exit /b 1
)
echo Authentication test PASSED!
echo.

echo [2/2] Running Employee API Load Test...
echo ========================================
if exist "reports\employee-report" rmdir /s /q "reports\employee-report"
jmeter -n -t employee-api-load-test.jmx ^
  -l results\employee-results.jtl ^
  -e -o reports\employee-report

if %ERRORLEVEL% NEQ 0 (
    echo Employee API test FAILED!
    pause
    exit /b 1
)
echo Employee API test PASSED!
echo.

echo ========================================
echo All Tests Completed Successfully!
echo ========================================
echo.
echo Reports generated:
echo - Auth Report: reports\auth-report\index.html
echo - Employee Report: reports\employee-report\index.html
echo.
echo Opening reports in browser...
start reports\auth-report\index.html
start reports\employee-report\index.html

pause
