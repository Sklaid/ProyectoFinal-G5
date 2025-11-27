@echo off
REM Cleanup Development Environment Script for Windows

echo ==========================================
echo Cleaning Up DevOps Platform Development Environment
echo ==========================================
echo WARNING: This will remove all containers, volumes, and data!
echo.

set /p confirm="Are you sure you want to continue? (yes/no): "

if not "%confirm%"=="yes" (
    echo Cleanup cancelled.
    exit /b 0
)

REM Check if docker-compose is available
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo Error: docker-compose is not installed.
    exit /b 1
)

echo.
echo Stopping and removing containers...
docker-compose -f docker-compose.dev.yml down

echo.
echo Removing volumes...
docker-compose -f docker-compose.dev.yml down -v

echo.
echo Removing orphaned containers...
docker-compose -f docker-compose.dev.yml down --remove-orphans

echo.
set /p prune="Do you want to prune unused Docker resources? (yes/no): "

if "%prune%"=="yes" (
    docker system prune -f
    echo Docker system pruned.
)

echo.
echo ==========================================
echo Cleanup completed successfully!
echo ==========================================
echo To start fresh: scripts\start-dev.bat
echo ==========================================
