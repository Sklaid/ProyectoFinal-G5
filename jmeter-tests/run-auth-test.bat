@echo off
REM Run JMeter Authentication Load Test
REM This script runs the authentication load test in non-GUI mode and generates an HTML report

echo ========================================
echo Running Authentication Load Test
echo ========================================
echo.

REM Create directories if they don't exist
if not exist "results" mkdir results
if not exist "reports" mkdir reports
if exist "reports\auth-report" rmdir /s /q "reports\auth-report"

REM Run JMeter test
echo Running JMeter test...
jmeter -n -t auth-load-test.jmx ^
  -l results\auth-results.jtl ^
  -e -o reports\auth-report

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Test completed successfully!
    echo ========================================
    echo.
    echo Results saved to: results\auth-results.jtl
    echo HTML Report: reports\auth-report\index.html
    echo.
    echo Opening report in browser...
    start reports\auth-report\index.html
) else (
    echo.
    echo ========================================
    echo Test failed with errors!
    echo ========================================
    echo.
    echo Check the results file: results\auth-results.jtl
)

pause
