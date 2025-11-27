@echo off
REM Script para análisis local multi-módulo en SonarQube
REM Similar a como funciona en SonarCloud

echo 🔍 Starting local SonarQube multi-module analysis...
echo.

REM Verificar que SonarQube esté corriendo
curl -s http://localhost:9000 >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: SonarQube is not running on http://localhost:9000
    echo Please start it with: scripts\start-dev.bat
    exit /b 1
)

REM Compilar backend
echo 📦 Building backend...
cd backend
call mvn clean compile -DskipTests
if errorlevel 1 (
    echo ❌ Backend build failed
    cd ..
    exit /b 1
)
cd ..

REM Ejecutar tests y generar cobertura
echo 🧪 Running tests and generating coverage...
cd backend
call mvn test jacoco:report
cd ..

REM Crear configuración multi-módulo temporal
echo 📝 Creating multi-module configuration...
(
echo # SonarQube Local Multi-Module Configuration
echo sonar.projectKey=devops-enterprise-platform
echo sonar.projectName=DevOps Enterprise Platform
echo sonar.projectVersion=1.0.0
echo.
echo # Source encoding
echo sonar.sourceEncoding=UTF-8
echo.
echo # Define modules
echo sonar.modules=backend,frontend
echo.
echo # ============================================
echo # Backend Module ^(Java/Spring Boot^)
echo # ============================================
echo backend.sonar.projectName=Backend ^(Java^)
echo backend.sonar.projectBaseDir=backend
echo backend.sonar.sources=src/main,pom.xml
echo backend.sonar.tests=src/test
echo backend.sonar.java.binaries=target/classes
echo backend.sonar.java.test.binaries=target/test-classes
echo backend.sonar.java.source=17
echo backend.sonar.java.target=17
echo backend.sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
echo backend.sonar.exclusions=**/dto/**,**/entity/**,**/config/**,**/DevOpsPlatformApplication.java,**/target/**
echo backend.sonar.coverage.exclusions=**/dto/**,**/entity/**,**/config/**,**/DevOpsPlatformApplication.java
echo.
echo # ============================================
echo # Frontend Module ^(TypeScript/React^)
echo # ============================================
echo frontend.sonar.projectName=Frontend ^(TypeScript/React^)
echo frontend.sonar.projectBaseDir=frontend
echo frontend.sonar.sources=src
echo frontend.sonar.tests=src
echo frontend.sonar.test.inclusions=**/*.test.ts,**/*.test.tsx,**/*.spec.ts,**/*.spec.tsx
echo frontend.sonar.exclusions=**/node_modules/**,**/dist/**,**/build/**,**/coverage/**,**/*.config.ts,**/*.config.js,**/vite-env.d.ts,**/*.test.ts,**/*.test.tsx
echo frontend.sonar.javascript.lcov.reportPaths=coverage/lcov.info
echo frontend.sonar.typescript.lcov.reportPaths=coverage/lcov.info
echo frontend.sonar.typescript.tsconfigPath=tsconfig.json
echo.
echo # Quality Gate
echo sonar.qualitygate.wait=false
) > sonar-project.properties

echo ✅ Configuration created
echo.

REM Ejecutar análisis con sonar-scanner
echo 🚀 Running SonarQube analysis...
echo.

REM Verificar si sonar-scanner está instalado
where sonar-scanner >nul 2>&1
if errorlevel 1 (
    echo ⚠️  sonar-scanner not found in PATH
    echo Please install it or use Option 2 ^(separate analysis^)
    del sonar-project.properties
    exit /b 1
)

REM Ejecutar análisis
sonar-scanner -Dsonar.host.url=http://localhost:9000 -Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502

if errorlevel 1 (
    echo.
    echo ❌ Analysis failed
    del sonar-project.properties
    exit /b 1
)

echo.
echo ✅ Analysis completed successfully!
echo 📊 View results at: http://localhost:9000/dashboard?id=devops-enterprise-platform

REM Limpiar archivo temporal
del sonar-project.properties

echo.
echo 🎉 Done!
