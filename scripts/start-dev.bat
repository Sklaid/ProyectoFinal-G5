@echo off
REM Start Development Environment Script for Windows

echo ==========================================
echo Starting DevOps Platform Development Environment
echo ==========================================

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running. Please start Docker Desktop.
    exit /b 1
)

REM Check if docker-compose is available
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo Error: docker-compose is not installed.
    exit /b 1
)

echo.
echo Starting services with docker-compose...
docker-compose -f docker-compose.dev.yml up -d

echo.
echo Waiting for services to be healthy...
timeout /t 10 /nobreak >nul

echo.
echo Checking service health...

REM Check PostgreSQL
docker exec devops-postgres pg_isready -U postgres >nul 2>&1
if errorlevel 1 (
    echo X PostgreSQL is not healthy
) else (
    echo √ PostgreSQL is healthy
)

REM Wait for other services
echo Waiting for Backend to start...
timeout /t 30 /nobreak >nul

echo Waiting for Frontend to start...
timeout /t 10 /nobreak >nul

echo.
echo ==========================================
echo Development Environment Status
echo ==========================================
echo Frontend:   http://localhost:3000
echo Backend:    http://localhost:8080
echo SonarQube:  http://localhost:9000 (admin/admin)
echo Nexus:      http://localhost:8081 (admin/admin123)
echo PostgreSQL: localhost:5432 (postgres/postgres)
echo.
echo To view logs: docker-compose -f docker-compose.dev.yml logs -f [service]
echo To stop: scripts\stop-dev.bat
echo ==========================================
