@echo off
REM Fix maven-snapshots repository configuration

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEXUS_PASSWORD=admin123

echo =========================================
echo Fix Maven Snapshots Repository
echo =========================================
echo.

echo Deleting existing maven-snapshots repository...
curl -X DELETE "%NEXUS_URL%/service/rest/v1/repositories/maven-snapshots" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json"

echo.
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo.
echo Recreating maven-snapshots with correct configuration...
curl -X POST "%NEXUS_URL%/service/rest/v1/repositories/maven/hosted" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"maven-snapshots\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW\"},\"cleanup\":null,\"maven\":{\"versionPolicy\":\"SNAPSHOT\",\"layoutPolicy\":\"STRICT\",\"contentDisposition\":\"INLINE\"}}"

if %errorlevel% equ 0 (
    echo.
    echo [32m✓ Repository recreated successfully![0m
) else (
    echo.
    echo [31m✗ Failed to recreate repository[0m
)

echo.
echo Verifying repository configuration...
curl -s "%NEXUS_URL%/service/rest/v1/repositories/maven/hosted/maven-snapshots" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" | findstr /i "writePolicy"

echo.
endlocal
