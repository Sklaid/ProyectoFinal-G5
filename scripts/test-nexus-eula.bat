@echo off
REM Test Nexus EULA Acceptance (Windows)

set NEXUS_URL=http://localhost:8081
set NEXUS_USER=admin
set NEXUS_PASSWORD=admin123

echo =========================================
echo Nexus EULA Test
echo =========================================
echo URL: %NEXUS_URL%
echo.

REM 1. Check EULA status
echo 1. Checking EULA status...
curl -s -u "%NEXUS_USER%:%NEXUS_PASSWORD%" "%NEXUS_URL%/service/rest/v1/system/eula"
echo.

REM 2. Accept EULA
echo 2. Accepting EULA...
curl -s -w "%%{http_code}" -X POST "%NEXUS_URL%/service/rest/v1/system/eula/accept" ^
  -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -H "Content-Type: application/json" > temp_eula_response.txt

set /p HTTP_CODE=<temp_eula_response.txt
echo HTTP Code: %HTTP_CODE%

if "%HTTP_CODE%"=="200" (
    echo [OK] EULA accepted successfully
) else if "%HTTP_CODE%"=="204" (
    echo [OK] EULA accepted successfully
) else (
    echo [ERROR] EULA acceptance failed ^(HTTP %HTTP_CODE%^)
)

del temp_eula_response.txt
echo.

REM 3. Verify EULA status after acceptance
echo 3. Verifying EULA status after acceptance...
curl -s -u "%NEXUS_USER%:%NEXUS_PASSWORD%" "%NEXUS_URL%/service/rest/v1/system/eula"
echo.

REM 4. Test write access
echo 4. Testing write access after EULA acceptance...
set TEST_PATH=com/test/eula-test/1.0-SNAPSHOT/eula-test-1.0-%random%.txt
echo Test content > temp_test.txt
curl -s -u "%NEXUS_USER%:%NEXUS_PASSWORD%" ^
  -X PUT "%NEXUS_URL%/repository/maven-snapshots/%TEST_PATH%" ^
  -H "Content-Type: text/plain" ^
  --data-binary @temp_test.txt ^
  -w "%%{http_code}" -o nul > temp_http_code.txt

set /p HTTP_CODE=<temp_http_code.txt
echo Write test HTTP Code: %HTTP_CODE%

if "%HTTP_CODE%"=="201" (
    echo [OK] Write access confirmed!
) else if "%HTTP_CODE%"=="200" (
    echo [OK] Write access confirmed!
) else (
    echo [ERROR] Write access failed ^(HTTP %HTTP_CODE%^)
)

del temp_test.txt temp_http_code.txt
echo.

echo =========================================
echo Test completed
echo =========================================
