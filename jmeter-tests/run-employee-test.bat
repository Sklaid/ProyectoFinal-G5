@echo off
REM Run JMeter Employee API Load Test
REM This script runs the employee API load test in non-GUI mode and generates an HTML report

echo ========================================
echo Running Employee API Load Test
echo ========================================
echo.

REM Create directories if they don't exist
if not exist "results" mkdir results
if not exist "reports" mkdir reports
if exist "reports\employee-report" rmdir /s /q "reports\employee-report"

REM Run JMeter test
echo Running JMeter test...
jmeter -n -t employee-api-load-test.jmx ^
  -l results\employee-results.jtl ^
  -e -o reports\employee-report

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Test completed successfully!
    echo ========================================
    echo.
    echo Results saved to: results\employee-results.jtl
    echo HTML Report: reports\employee-report\index.html
    echo.
    echo Opening report in browser...
    start reports\employee-report\index.html
) else (
    echo.
    echo ========================================
    echo Test failed with errors!
    echo ========================================
    echo.
    echo Check the results file: results\employee-results.jtl
)

pause
