@echo off
REM Stop Development Environment Script for Windows

echo ==========================================
echo Stopping DevOps Platform Development Environment
echo ==========================================

REM Check if docker-compose is available
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo Error: docker-compose is not installed.
    exit /b 1
)

echo.
echo Stopping services...
docker-compose -f docker-compose.dev.yml stop

echo.
echo Services stopped successfully.
echo.
echo To start again: scripts\start-dev.bat
echo To remove containers and volumes: scripts\cleanup.bat
echo ==========================================
