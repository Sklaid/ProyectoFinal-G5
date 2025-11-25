@echo off
REM Setup Nexus Environment Variables (Windows)

echo =========================================
echo Nexus Environment Setup
echo =========================================
echo.

echo Setting environment variables for current session...
set NEXUS_USERNAME=admin
set NEXUS_PASSWORD=admin123

echo [OK] Environment variables set for current session
echo.

echo To make these permanent, run:
echo   setx NEXUS_USERNAME admin
echo   setx NEXUS_PASSWORD admin123
echo.
echo Note: setx changes take effect in NEW command prompts only
echo.

echo =========================================
echo Ready to deploy!
echo =========================================
echo.
echo To deploy:
echo   cd backend
echo   mvn deploy -DskipTests
echo.
