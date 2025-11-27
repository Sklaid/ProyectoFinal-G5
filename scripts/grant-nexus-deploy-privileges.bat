@echo off
REM Grant deployment privileges to admin user in Nexus

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEXUS_PASSWORD=admin123

echo =========================================
echo Grant Nexus Deployment Privileges
echo =========================================
echo.

echo Checking current privileges for admin user...
curl -s -X GET "%NEXUS_URL%/service/rest/v1/security/users/admin" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Accept: application/json"

echo.
echo.
echo Checking repository write policy...
curl -s -X GET "%NEXUS_URL%/service/rest/v1/repositories/maven-snapshots" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Accept: application/json" | findstr /i "writePolicy"

echo.
echo.
echo Checking if nx-admin role is assigned...
curl -s -X GET "%NEXUS_URL%/service/rest/v1/security/users/admin" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Accept: application/json" | findstr /i "roles"

echo.
echo.
echo [33mNote: The admin user should have nx-admin role by default.[0m
echo [33mIf deployment still fails, check Nexus UI for security realms configuration.[0m
echo.

endlocal
