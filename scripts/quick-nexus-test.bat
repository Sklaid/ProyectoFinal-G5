@echo off
REM Quick Nexus Test Script (Windows)
REM Prueba rapida de conectividad y permisos de Nexus

setlocal enabledelayedexpansion

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEXUS_PASSWORD=admin123

echo ==================
echo Quick Nexus Test
echo ==================
echo.

REM Test 1: Connectivity
echo 1. Testing connectivity...
curl -s -f "%NEXUS_URL%/service/rest/v1/status" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Nexus is reachable
) else (
    echo [ERROR] Cannot reach Nexus at %NEXUS_URL%
    exit /b 1
)

REM Test 2: Authentication
echo.
echo 2. Testing authentication...
curl -s -f -u "%NEXUS_USER%:%NEXUS_PASSWORD%" "%NEXUS_URL%/service/rest/v1/status" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Authentication successful
) else (
    echo [ERROR] Authentication failed
    exit /b 1
)

REM Test 3: Repository exists
echo.
echo 3. Checking maven-snapshots repository...
curl -s -f -u "%NEXUS_USER%:%NEXUS_PASSWORD%" "%NEXUS_URL%/service/rest/v1/repositories/maven-snapshots" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Repository exists
    echo    Checking writePolicy...
    REM Note: En Windows es mas complejo parsear JSON sin herramientas adicionales
    echo    ^(Verificacion de writePolicy requiere jq o PowerShell^)
) else (
    echo [ERROR] Repository not found or not accessible
    exit /b 1
)

REM Test 4: Write access
echo.
echo 4. Testing write access...
set TEST_FILE=test/quick-test/1.0-SNAPSHOT/test-%random%.txt
echo Quick test > temp_test.txt
curl -s -u "%NEXUS_USER%:%NEXUS_PASSWORD%" -X PUT "%NEXUS_URL%/repository/maven-snapshots/%TEST_FILE%" -H "Content-Type: text/plain" --data-binary @temp_test.txt -w "%%{http_code}" -o nul > temp_http_code.txt
set /p HTTP_CODE=<temp_http_code.txt

if "%HTTP_CODE%"=="201" (
    echo [OK] Write access confirmed ^(HTTP %HTTP_CODE%^)
) else if "%HTTP_CODE%"=="200" (
    echo [OK] Write access confirmed ^(HTTP %HTTP_CODE%^)
) else (
    echo [ERROR] Write access failed ^(HTTP %HTTP_CODE%^)
    del temp_test.txt temp_http_code.txt
    exit /b 1
)

del temp_test.txt temp_http_code.txt

REM Test 5: User permissions
echo.
echo 5. Checking user permissions...
echo    ^(Verificacion completa requiere jq o PowerShell^)
curl -s -u "%NEXUS_USER%:%NEXUS_PASSWORD%" "%NEXUS_URL%/service/rest/v1/security/users/%NEXUS_USER%" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] User information accessible
) else (
    echo [WARN] Could not retrieve user information
)

echo.
echo ==================
echo [OK] All tests passed!
echo ==================
echo.
echo Nexus is ready for Maven deploy
echo.
echo To deploy:
echo   cd backend
echo   mvn deploy -DskipTests
echo.

endlocal
