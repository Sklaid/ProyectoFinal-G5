# SonarQube Setup and Analysis Guide

## Prerequisites

1. Docker and Docker Compose installed
2. SonarQube container running (via docker-compose.dev.yml)
3. Maven installed for backend analysis
4. Node.js and npm installed for frontend analysis

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

### Backend Analysis

```bash
# Navigate to backend directory
cd backend

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
