# SonarQube Setup and Analysis Guide

## Prerequisites

1. Docker and Docker Compose installed
2. SonarQube container running (via docker-compose.dev.yml)
3. Maven installed for backend analysis
4. Node.js and npm installed for frontend analysis
5. SonarScanner CLI (for multi-module analysis)

## Initial SonarQube Setup

### 1. Access SonarQube

Open your browser and navigate to: http://localhost:9000

**Default credentials:**
- Username: `admin`
- Password: `admin` | Devops_Grupo5

You will be prompted to change the password on first login.

### 2. Authentication Token

Your SonarQube token (non-expiring):
```
squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502
```

This token is already configured and ready to use in the commands below.

### 3. Create Quality Gate

SonarQube comes with a default "Sonar way" quality gate. For this project, we want to ensure:

1. Go to Quality Gates
2. Create new gate or modify "Sonar way":
   - Security Hotspots Reviewed = 100%
   - Coverage on New Code > 80%
   - Duplicated Lines on New Code < 3%
   - Reliability Rating on New Code = A
   - Security Rating on New Code = A
   - Maintainability Rating on New Code = A

## Running SonarQube Analysis

### Option 1: Multi-Module Analysis (Recommended) 🎯

Analyze backend + frontend together (same as SonarCloud):

```bash
# From project root
scripts\sonar-local-analysis.bat
```

This will:
- ✅ Build backend
- ✅ Run tests and generate coverage
- ✅ Analyze both backend and frontend
- ✅ Create a single project with 2 modules
- ✅ View at: http://localhost:9000/dashboard?id=devops-enterprise-platform

**Advantages:**
- Same structure as SonarCloud
- Single project view
- Combined metrics
- Easier to manage

### Option 2: Separate Analysis (Legacy) 🔧

Analyze backend and frontend separately:

#### Backend Only

```bash
# From project root
scripts\sonar-backend-only.bat

# Run Maven with SonarQube analysis (Windows PowerShell)
mvn sonar:sonar "-Dsonar.projectKey=devops-enterprise-platform" "-Dsonar.host.url=http://localhost:9000" "-Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502"

# Note: Use quotes around each -D parameter in PowerShell to avoid parsing issues
```

**✅ Status**: Backend analysis completed successfully - Quality Gate PASSED  
**View Results**: http://localhost:9000/dashboard?id=devops-enterprise-platform

### Frontend Analysis

For frontend analysis, you need to install SonarScanner:

#### Install SonarScanner

**Option 1: Using npm (Recommended)**
```bash
npm install -g sonarqube-scanner
```

#### Run Frontend Analysis

```bash
# Navigate to frontend directory
cd frontend

# First, generate coverage report
npm run test:coverage

# Run SonarScanner (Windows PowerShell)
# The sonar-project.properties file is already configured with all settings
sonar-scanner "-Dsonar.host.url=http://localhost:9000" "-Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502"
```

# 3. Check results in SonarQube dashboard
# http://localhost:9000/dashboard?id=devops-enterprise-platform-frontend
```

## Viewing Results

1. Go to http://localhost:9000
2. Click on "Projects"
3. You should see:
   - `devops-enterprise-platform-backend` (Backend)
   - `devops-enterprise-platform-frontend` (Frontend)
4. Click on each project to view:
   - Code coverage
   - Bugs
   - Vulnerabilities
   - Code smells
   - Security hotspots
   - Duplications

## Quality Gate Status

The analysis will show if the quality gate passed or failed. If it fails:

1. Review the issues in SonarQube dashboard
2. Fix critical issues first (bugs, vulnerabilities)
3. Address code smells and improve coverage
4. Re-run the analysis

## Troubleshooting

### SonarQube not accessible
```bash
# Check if container is running
docker ps | findstr sonarqube

# Check logs
docker logs devops-sonarqube

# Restart if needed
docker-compose -f docker-compose.dev.yml restart sonarqube
```

### Analysis fails with authentication error
- Verify your token is correct
- Check that SonarQube is accessible at http://localhost:9000
- Ensure you're using the correct project key

### Coverage not showing
- Ensure tests ran successfully before analysis
- Check that jacoco.xml (backend) or lcov.info (frontend) exists
- Verify coverage report paths in sonar-project.properties

### Quality Gate fails
- Review specific metrics that failed
- Check SonarQube dashboard for details
- Fix issues and re-run analysis

## CI/CD Integration

For GitHub Actions pipeline, you'll need to:

1. Add `SONAR_TOKEN` as a GitHub secret
2. Add `SONAR_HOST_URL` as a GitHub secret (or use http://localhost:9000 for self-hosted runner)
3. The pipeline will automatically run SonarQube analysis on each build

## Notes

- First analysis may take longer as SonarQube indexes the code
- Subsequent analyses are incremental and faster
- Quality gate checks only "New Code" by default
- You can configure quality gates to check "Overall Code" if needed


# Or manually:
cd backend
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=devops-enterprise-platform-backend \
  -Dsonar.projectName="DevOps Platform - Backend" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502
```

View at: http://localhost:9000/dashboard?id=devops-enterprise-platform-backend

#### Frontend Only

```bash
# From project root
scripts\sonar-frontend-only.bat

# Or manually:
cd frontend
npm ci
npm run test:coverage
npx sonar-scanner \
  -Dsonar.projectKey=devops-enterprise-platform-frontend \
  -Dsonar.projectName="DevOps Platform - Frontend" \
  -Dsonar.sources=src \
  -Dsonar.tests=src \
  -Dsonar.test.inclusions=**/*.test.ts,**/*.test.tsx \
  -Dsonar.exclusions=**/node_modules/**,**/dist/**,**/coverage/** \
  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=squ_9ee713d1bb15f9eba7c740a2b665d8b8db590502
```

View at: http://localhost:9000/dashboard?id=devops-enterprise-platform-frontend

**Disadvantages:**
- Two separate projects
- Harder to see combined metrics
- Different from SonarCloud structure

## Comparison: Local vs Cloud

| Aspect | SonarQube Local | SonarCloud |
|--------|----------------|------------|
| **Location** | http://localhost:9000 | https://sonarcloud.io |
| **Trigger** | Manual (run script) | Automatic (on push) |
| **Speed** | ⚡ Fast (30 sec) | 🐢 Slower (5-10 min) |
| **Purpose** | Development/Testing | CI/CD Validation |
| **Configuration** | Same as cloud | Same as local |
| **When to use** | Before commit | After push |

## Recommended Workflow

```bash
# 1. During development
# Write code → Run local analysis
scripts\sonar-local-analysis.bat

# 2. Review results
# Open http://localhost:9000
# Fix any code smells or issues

# 3. When ready
git add .
git commit -m "Feature: Added new functionality"
git push

# 4. Automatic CI/CD
# GitHub Actions → SonarCloud analysis
# Quality Gate check
```

## Troubleshooting

### SonarQube not running

```bash
# Start development environment
scripts\start-dev.bat

# Wait for SonarQube to be healthy
# Check at: http://localhost:9000
```

### SonarScanner not found

For multi-module analysis, you need SonarScanner CLI:

**Windows:**
1. Download from: https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/
2. Extract to `C:\sonar-scanner`
3. Add to PATH: `C:\sonar-scanner\bin`

**Or use separate analysis** (Option 2) which doesn't require SonarScanner CLI.

### Coverage not showing

Make sure to run tests before analysis:

```bash
# Backend
cd backend
mvn test jacoco:report

# Frontend
cd frontend
npm run test:coverage
```

## Additional Resources

- SonarQube Documentation: https://docs.sonarqube.org/
- SonarCloud Documentation: https://docs.sonarcloud.io/
- Quality Gates: https://docs.sonarqube.org/latest/user-guide/quality-gates/
