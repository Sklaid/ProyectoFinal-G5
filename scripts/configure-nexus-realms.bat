@echo off
REM Configure Nexus Security Realms

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEXUS_PASSWORD=admin123

echo =========================================
echo Configure Nexus Security Realms
echo =========================================
echo.

echo Activating security realms...
curl -X PUT "%NEXUS_URL%/service/rest/v1/security/realms/active" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "[\"NexusAuthenticatingRealm\",\"NexusAuthorizingRealm\"]"

if %errorlevel% equ 0 (
    echo.
    echo [32m✓ Security realms configured successfully![0m
) else (
    echo.
    echo [31m✗ Failed to configure security realms[0m
)

echo.
endlocal
