#!/bin/bash

# Script para análisis local multi-módulo en SonarQube
# Similar a como funciona en SonarCloud

echo "🔍 Starting local SonarQube multi-module analysis..."
echo ""

# Verificar que SonarQube esté corriendo
if ! curl -s http://localhost:9000 > /dev/null; then
    echo "❌ Error: SonarQube is not running on http://localhost:9000"
    echo "Please start it with: ./scripts/start-dev.bat"
    exit 1
fi

# Compilar backend
echo "📦 Building backend..."
cd backend
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Backend build failed"
    exit 1
fi
cd ..

# Ejecutar tests y generar cobertura
echo "🧪 Running tests and generating coverage..."
cd backend
mvn test jacoco:report
cd ..

# Crear configuración multi-módulo temporal
echo "📝 Creating multi-module configuration..."
cat > sonar-project.properties << 'EOF'
# SonarQube Local Multi-Module Configuration
sonar.projectKey=devops-enterprise-platform
sonar.projectName=DevOps Enterprise Platform
sonar.projectVersion=1.0.0

# Source encoding
sonar.sourceEncoding=UTF-8

# Define modules
sonar.modules=backend,frontend

# ============================================
# Backend Module (Java/Spring Boot)
# ============================================
backend.sonar.projectName=Backend (Java)
backend.sonar.projectBaseDir=backend
backend.sonar.sources=src/main,pom.xml
backend.sonar.tests=src/test
backend.sonar.java.binaries=target/classes
backend.sonar.java.test.binaries=target/test-classes
backend.sonar.java.source=17
backend.sonar.java.target=17
backend.sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
backend.sonar.exclusions=**/dto/**,**/entity/**,**/config/**,**/DevOpsPlatformApplication.java,**/target/**
backend.sonar.coverage.exclusions=**/dto/**,**/entity/**,**/config/**,**/DevOpsPlatformApplication.java

# ============================================
# Frontend Module (TypeScript/React)
# ============================================
frontend.sonar.projectName=Frontend (TypeScript/React)
frontend.sonar.projectBaseDir=frontend
frontend.sonar.sources=src
frontend.sonar.tests=src
frontend.sonar.test.inclusions=**/*.test.ts,**/*.test.tsx,**/*.spec.ts,**/*.spec.tsx
frontend.sonar.exclusions=**/node_modules/**,**/dist/**,**/build/**,**/coverage/**,**/*.config.ts,**/*.config.js,**/vite-env.d.ts,**/*.test.ts,**/*.test.tsx
frontend.sonar.javascript.lcov.reportPaths=coverage/lcov.info
frontend.sonar.typescript.lcov.reportPaths=coverage/lcov.info
frontend.sonar.typescript.tsconfigPath=tsconfig.json

# Quality Gate
sonar.qualitygate.wait=false
EOF

echo "✅ Configuration created"
echo ""

# Ejecutar análisis con sonar-scanner
echo "🚀 Running SonarQube analysis..."
echo ""

# Verificar si sonar-scanner está instalado
if ! command -v sonar-scanner &> /dev/null; then
    echo "⚠️  sonar-scanner not found. Installing..."
    
    # Descargar sonar-scanner
    wget -q https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-5.0.1.3006-windows.zip
    unzip -q sonar-scanner-cli-5.0.1.3006-windows.zip
    export PATH="$PATH:$(pwd)/sonar-scanner-5.0.1.3006-windows/bin"
fi

# Ejecutar análisis
sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Analysis completed successfully!"
    echo "📊 View results at: http://localhost:9000/dashboard?id=devops-enterprise-platform"
else
    echo ""
    echo "❌ Analysis failed"
    exit 1
fi

# Limpiar archivo temporal
rm sonar-project.properties

echo ""
echo "🎉 Done!"
