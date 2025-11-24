@echo off
REM Script to run JMeter tests with full path
REM Update JMETER_HOME to your JMeter installation directory

REM ========================================
REM CONFIGURE THIS PATH TO YOUR JMETER INSTALLATION
REM ========================================
SET JMETER_HOME=C:\Users\sklai\OneDrive\Documentos\UNI\2025-2\DEvops\apache-jmeter-5.6.3
REM Alternative: Use environment variable if already set
REM SET JMETER_HOME=%JMETER_HOME%

REM ========================================
REM DO NOT MODIFY BELOW THIS LINE
REM ========================================

echo ========================================
echo JMeter Performance Tests Runner
echo ========================================
echo.
echo JMeter Home: %JMETER_HOME%
echo.

REM Check if JMeter exists
if not exist "%JMETER_HOME%\bin\jmeter.bat" (
    echo ERROR: JMeter not found at %JMETER_HOME%
    echo.
    echo Please update JMETER_HOME in this script to point to your JMeter installation.
    echo Example: SET JMETER_HOME=C:\apache-jmeter-5.6.3
    echo.
    pause
    exit /b 1
)

REM Create directories
if not exist "results" mkdir results
if not exist "reports" mkdir reports

echo [1/2] Running Authentication Load Test...
echo ========================================
if exist "reports\auth-report" rmdir /s /q "reports\auth-report"
"%JMETER_HOME%\bin\jmeter.bat" -n -t auth-load-test.jmx ^
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
"%JMETER_HOME%\bin\jmeter.bat" -n -t employee-api-load-test.jmx ^
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
