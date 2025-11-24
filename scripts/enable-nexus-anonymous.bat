@echo off
REM Enable Anonymous Access in Nexus
REM This allows reading from repositories without authentication

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEXUS_PASSWORD=admin123

echo =========================================
echo Enable Nexus Anonymous Access
echo =========================================
echo.

echo Enabling anonymous access...
curl -X PUT "%NEXUS_URL%/service/rest/v1/security/anonymous" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "{\"enabled\":true,\"userId\":\"anonymous\",\"realmName\":\"NexusAuthorizingRealm\"}"

if %errorlevel% equ 0 (
    echo.
    echo [32m✓ Anonymous access enabled successfully![0m
) else (
    echo.
    echo [31m✗ Failed to enable anonymous access[0m
)

echo.
endlocal
