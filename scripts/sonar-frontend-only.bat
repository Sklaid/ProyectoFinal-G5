@echo off
REM Análisis solo del frontend

echo 🔍 Analyzing Frontend only...
echo.

cd frontend

echo 📦 Installing dependencies...
call npm ci

echo 🧪 Running tests with coverage...
call npm run test:coverage

echo.
echo 🚀 Running SonarQube analysis...
call npx sonar-scanner ^
  -Dsonar.projectKey=devops-enterprise-platform-frontend ^
  -Dsonar.projectName="DevOps Platform - Frontend" ^
  -Dsonar.sources=src ^
  -Dsonar.tests=src ^
  -Dsonar.test.inclusions=**/*.test.ts,**/*.test.tsx ^
  -Dsonar.exclusions=**/node_modules/**,**/dist/**,**/coverage/**,**/*.config.ts ^
  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502

cd ..

if errorlevel 1 (
    echo ❌ Analysis failed
    exit /b 1
)

echo.
echo ✅ Frontend analysis completed!
echo 📊 View at: http://localhost:9000/dashboard?id=devops-enterprise-platform-frontend
