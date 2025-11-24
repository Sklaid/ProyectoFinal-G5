@echo off
REM Nexus Repository Setup Script for Windows
REM This script configures Nexus Repository Manager with required repositories

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
if "%NEXUS_PASSWORD%"=="" set NEXUS_PASSWORD=admin123

echo =========================================
echo Nexus Repository Setup Script
echo =========================================
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
echo =========================================
echo Creating Maven Repositories
echo =========================================

echo Creating Maven Releases repository...
curl -X POST "%NEXUS_URL%/service/rest/v1/repositories/maven/hosted" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"maven-releases\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW_ONCE\"},\"maven\":{\"versionPolicy\":\"RELEASE\",\"layoutPolicy\":\"STRICT\"}}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [32m✓ Maven Releases repository created[0m
) else (
    echo [33m⚠ Maven Releases repository may already exist[0m
)

echo Creating Maven Snapshots repository...
curl -X POST "%NEXUS_URL%/service/rest/v1/repositories/maven/hosted" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"maven-snapshots\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW\"},\"maven\":{\"versionPolicy\":\"SNAPSHOT\",\"layoutPolicy\":\"STRICT\"}}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [32m✓ Maven Snapshots repository created[0m
) else (
    echo [33m⚠ Maven Snapshots repository may already exist[0m
)

echo.
echo =========================================
echo Creating NPM Repository
echo =========================================

echo Creating NPM hosted repository...
curl -X POST "%NEXUS_URL%/service/rest/v1/repositories/npm/hosted" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"npm-hosted\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW\"}}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [32m✓ NPM hosted repository created[0m
) else (
    echo [33m⚠ NPM hosted repository may already exist[0m
)

echo.
echo =========================================
echo Creating Docker Repository
echo =========================================

echo Creating Docker hosted repository...
curl -X POST "%NEXUS_URL%/service/rest/v1/repositories/docker/hosted" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"docker-hosted\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW\"},\"docker\":{\"v1Enabled\":false,\"forceBasicAuth\":true,\"httpPort\":8082}}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [32m✓ Docker hosted repository created[0m
) else (
    echo [33m⚠ Docker hosted repository may already exist[0m
)

echo.
echo =========================================
echo Verifying Repositories
echo =========================================

echo Listing all repositories:
curl -s -X GET "%NEXUS_URL%/service/rest/v1/repositories" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Accept: application/json"

echo.
echo =========================================
echo Nexus Setup Complete!
echo =========================================
echo.
echo Repository URLs:
echo   Maven Releases:  %NEXUS_URL%/repository/maven-releases/
echo   Maven Snapshots: %NEXUS_URL%/repository/maven-snapshots/
echo   NPM Hosted:      %NEXUS_URL%/repository/npm-hosted/
echo   Docker Hosted:   %NEXUS_URL%/repository/docker-hosted/
echo.
echo Nexus Web UI:      %NEXUS_URL%
echo Username:          %NEXUS_USER%
echo Password:          %NEXUS_PASSWORD%
echo.

endlocal
