@echo off
REM Análisis solo del backend (como antes)

echo 🔍 Analyzing Backend only...
echo.

REM Guardar directorio actual
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%.."

echo 📦 Building and testing...
cd backend
call mvn clean test jacoco:report
if errorlevel 1 (
    echo ❌ Tests failed
    cd ..
    exit /b 1
)

echo.
echo 🚀 Running SonarQube analysis...
call mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar ^
  -Dsonar.projectKey=devops-enterprise-platform-backend ^
  -Dsonar.projectName="DevOps Platform - Backend" ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502

cd ..

if errorlevel 1 (
    echo ❌ Analysis failed
    exit /b 1
)

echo.
echo ✅ Backend analysis completed!
echo 📊 View at: http://localhost:9000/dashboard?id=devops-enterprise-platform-backend
