@echo off
REM Initialize Nexus Admin Password
REM This script changes the initial admin password to a known password

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEW_PASSWORD=admin123

echo =========================================
echo Nexus Password Initialization
echo =========================================
echo.

REM Get the initial password from the container
echo Getting initial admin password from container...
for /f "delims=" %%i in ('docker exec devops-nexus cat /nexus-data/admin.password 2^>nul') do set INITIAL_PASSWORD=%%i

if "%INITIAL_PASSWORD%"=="" (
    echo [33m⚠ Could not retrieve initial password. It may have already been changed.[0m
    echo [33m  Trying with default password: admin123[0m
    set INITIAL_PASSWORD=admin123
)

echo Initial password retrieved: %INITIAL_PASSWORD%
echo.

echo Waiting for Nexus to be ready...
set max_attempts=30
set attempt=0

:wait_loop
curl -s -f "%NEXUS_URL%/service/rest/v1/status" >nul 2>&1
if %errorlevel% equ 0 (
    echo [32m✓ Nexus is ready![0m
    goto nexus_ready
)
set /a attempt+=1
if %attempt% geq %max_attempts% (
    echo [31m✗ Nexus failed to start within expected time[0m
    exit /b 1
)
echo   Attempt %attempt%/%max_attempts% - waiting...
timeout /t 10 /nobreak >nul
goto wait_loop

:nexus_ready
echo.
echo Changing admin password...

REM Change the password using the script API
curl -X PUT "%NEXUS_URL%/service/rest/v1/security/users/admin/change-password" ^
  -u "%NEXUS_USER%:%INITIAL_PASSWORD%" ^
  -H "Content-Type: text/plain" ^
  -d "%NEW_PASSWORD%" >nul 2>&1

if %errorlevel% equ 0 (
    echo [32m✓ Password changed successfully![0m
    echo.
    echo New credentials:
    echo   Username: %NEXUS_USER%
    echo   Password: %NEW_PASSWORD%
) else (
    echo [33m⚠ Password may have already been changed or an error occurred[0m
    echo [33m  Try logging in with: admin / admin123[0m
)

echo.
echo =========================================
echo Next Steps:
echo =========================================
echo 1. Access Nexus at: %NEXUS_URL%
echo 2. Login with: admin / admin123
echo 3. Run setup-nexus.bat to create repositories
echo.

endlocal
