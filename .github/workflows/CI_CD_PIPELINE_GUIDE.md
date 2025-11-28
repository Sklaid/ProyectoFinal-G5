# CI/CD Pipeline Implementation Guide

## Overview

This document describes the GitHub Actions CI/CD pipeline implemented for the DevOps Enterprise Platform. The pipeline automates the build, test, and quality assurance processes for both backend and frontend components.

## Pipeline Architecture

The pipeline is implemented in `.github/workflows/ci-cd-pipeline.yml` and consists of the following jobs:

### Job Flow

```
┌─────────────────┐     ┌─────────────────┐
│  Build Backend  │     │ Build Frontend  │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───────────┬───────────┘
                     │
              ┌──────▼──────┐
              │ Unit Tests  │
              └──────┬──────┘
                     │
         ┌───────────┴───────────┐
         │                       │
  ┌──────▼──────┐        ┌──────▼──────┐
  │  SonarQube  │        │  Security   │
  │  Analysis   │        │    Scan     │
  └──────┬──────┘        └──────┬──────┘
         │                       │
         └───────────┬───────────┘
                     │
              ┌──────▼──────┐
              │  Pipeline   │
              │   Summary   │
              └─────────────┘
```

## Jobs Description

### 1. Build Backend
- **Purpose:** Compile the Spring Boot backend application
- **Steps:**
  - Checkout code with full history
  - Set up JDK 17 (Temurin distribution)
  - Build with Maven (skip tests for speed)
  - Upload JAR artifact for later stages
- **Artifacts:** `backend-jar` (JAR file)
- **Cache:** Maven dependencies

### 2. Build Frontend
- **Purpose:** Build the React frontend application
- **Steps:**
  - Checkout code
  - Set up Node.js 18
  - Install dependencies with `npm ci`
  - Build production bundle
  - Upload dist folder
- **Artifacts:** `frontend-dist` (static files)
- **Cache:** npm dependencies

### 3. Unit Tests
- **Purpose:** Run unit tests for both backend and frontend
- **Dependencies:** Requires build-backend and build-frontend to complete
- **Steps:**
  - Backend: Run `mvn test` with JaCoCo coverage
  - Frontend: Run `npm run test:coverage`
  - Upload coverage reports
- **Artifacts:** 
  - `backend-coverage` (JaCoCo HTML report)
  - `frontend-coverage` (Vitest coverage report)
- **Skip Condition:** Can be skipped with `skip_tests` input parameter

### 4. SonarQube Analysis
- **Purpose:** Perform static code analysis and quality gate check
- **Dependencies:** Requires unit-tests to complete
- **Steps:**
  - Download coverage reports from previous job
  - Run SonarQube Maven plugin
  - Check quality gate status via API
  - Fail pipeline if quality gate fails
- **Required Secrets:**
  - `SONAR_TOKEN`: Authentication token for SonarQube
  - `SONAR_HOST_URL`: URL of SonarQube server
- **Skip Condition:** Skipped if `skip_tests` is true

### 5. Security Scan
- **Purpose:** Scan for security vulnerabilities in dependencies
- **Dependencies:** Requires build jobs to complete
- **Steps:**
  - Backend: Run OWASP Dependency Check
  - Frontend: Run `npm audit`
  - Fail on high or critical vulnerabilities
  - Upload security reports
- **Artifacts:**
  - `owasp-report` (HTML report)
  - `npm-audit-report` (JSON report)
- **Thresholds:**
  - OWASP: Fail on CVSS score ≥ 7
  - npm: Fail on high or critical vulnerabilities

### 6. Pipeline Summary
- **Purpose:** Aggregate results and provide summary
- **Dependencies:** Runs after all other jobs (always)
- **Steps:**
  - Generate markdown summary
  - Display status of all jobs
  - Fail if any critical job failed
- **Condition:** Always runs, even if previous jobs fail

## Triggers

### Automatic Triggers

1. **Push to branches:**
   - `main` - Production branch
   - `develop` - Development branch
   - `release/**` - Release branches

2. **Pull Requests:**
   - To `main` branch
   - To `develop` branch

### Manual Trigger (workflow_dispatch)

The pipeline can be triggered manually with the following parameters:

| Parameter | Type | Options | Default | Description |
|-----------|------|---------|---------|-------------|
| `environment` | choice | development, preprod, production | development | Target environment |
| `skip_tests` | boolean | true/false | false | Skip tests (emergency only) |
| `deploy_enabled` | boolean | true/false | true | Enable deployment stages |

**To trigger manually:**
1. Go to Actions tab in GitHub
2. Select "CI/CD Pipeline - DevOps Enterprise Platform"
3. Click "Run workflow"
4. Select branch and configure parameters
5. Click "Run workflow"

## Environment Variables

### Global Variables

```yaml
JAVA_VERSION: '17'           # Java version for backend
NODE_VERSION: '18'           # Node.js version for frontend
MAVEN_OPTS: '-Xmx1024m'      # Maven memory settings
SONAR_PROJECT_KEY: 'devops-enterprise-platform'  # SonarQube project key
```

### Required Secrets

Configure these secrets in GitHub repository settings (Settings → Secrets and variables → Actions):

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `SONAR_TOKEN` | SonarQube authentication token | `squ_abc123...` |
| `SONAR_HOST_URL` | SonarQube server URL | `http://localhost:9000` |

**To create secrets:**
1. Go to repository Settings
2. Click "Secrets and variables" → "Actions"
3. Click "New repository secret"
4. Enter name and value
5. Click "Add secret"

## Artifacts

All artifacts are retained for 7 days and can be downloaded from the Actions run page.

| Artifact Name | Contents | Size (approx) |
|---------------|----------|---------------|
| `backend-jar` | Compiled Spring Boot JAR | 50-80 MB |
| `frontend-dist` | Production build of React app | 2-5 MB |
| `backend-coverage` | JaCoCo HTML coverage report | 1-2 MB |
| `frontend-coverage` | Vitest coverage report | 500 KB |
| `owasp-report` | OWASP dependency check HTML | 500 KB |
| `npm-audit-report` | npm audit JSON report | 50 KB |

## Quality Gates

### SonarQube Quality Gate

The pipeline enforces the following quality standards:

- **Coverage:** > 80%
- **Duplications:** < 3%
- **Maintainability Rating:** A
- **Reliability Rating:** A
- **Security Rating:** A
- **Security Hotspots:** 0 high/critical
- **Bugs:** 0 high/critical
- **Code Smells:** < 10 high

If any condition fails, the pipeline will fail at the SonarQube Analysis stage.

### Security Thresholds

- **OWASP:** Fails on vulnerabilities with CVSS score ≥ 7
- **npm audit:** Fails on any high or critical vulnerabilities

## Caching Strategy

The pipeline uses GitHub Actions caching to speed up builds:

1. **Maven Dependencies:**
   - Cache key: Hash of `pom.xml`
   - Location: `~/.m2/repository`
   - Benefit: Reduces build time by 2-3 minutes

2. **npm Dependencies:**
   - Cache key: Hash of `package-lock.json`
   - Location: `~/.npm`
   - Benefit: Reduces build time by 1-2 minutes

## Troubleshooting

### Build Failures

**Problem:** Backend build fails with "Could not resolve dependencies"

**Solution:**
1. Check if Maven Central is accessible
2. Verify `pom.xml` has correct dependency versions
3. Clear Maven cache and retry

**Problem:** Frontend build fails with "Module not found"

**Solution:**
1. Verify `package-lock.json` is committed
2. Check Node.js version compatibility
3. Run `npm ci` locally to reproduce

### Test Failures

**Problem:** Unit tests fail in CI but pass locally

**Solution:**
1. Check for environment-specific configurations
2. Verify test database is properly configured
3. Look for timezone or locale differences
4. Check for race conditions in async tests

### SonarQube Failures

**Problem:** "Quality Gate failed" error

**Solution:**
1. Check SonarQube dashboard for specific issues
2. Review coverage reports
3. Fix code smells and bugs
4. Increase test coverage if below 80%

**Problem:** "Could not connect to SonarQube"

**Solution:**
1. Verify `SONAR_HOST_URL` secret is correct
2. Check if SonarQube server is running
3. Verify `SONAR_TOKEN` is valid and not expired
4. Check network connectivity

### Security Scan Failures

**Problem:** OWASP check fails with high CVSS vulnerabilities

**Solution:**
1. Review the OWASP report artifact
2. Update vulnerable dependencies to patched versions
3. If no patch available, add suppression with justification
4. Create security ticket to track remediation

**Problem:** npm audit fails with critical vulnerabilities

**Solution:**
1. Run `npm audit fix` locally
2. If auto-fix doesn't work, manually update packages
3. Check for breaking changes in updated packages
4. Test thoroughly after updates

## Performance Optimization

### Current Performance

- **Build Backend:** ~3-5 minutes
- **Build Frontend:** ~2-3 minutes
- **Unit Tests:** ~5-7 minutes
- **SonarQube Analysis:** ~2-3 minutes
- **Security Scan:** ~3-5 minutes
- **Total Pipeline:** ~15-25 minutes

### Optimization Tips

1. **Use caching effectively:**
   - Maven and npm caches are already configured
   - Consider caching Docker layers for future stages

2. **Parallelize jobs:**
   - Build jobs run in parallel
   - Consider splitting test suites

3. **Skip unnecessary steps:**
   - Use `skip_tests` parameter for hotfixes (with caution)
   - Skip SonarQube for feature branches (optional)

4. **Use self-hosted runners:**
   - Faster than GitHub-hosted runners
   - Better for large projects
   - Requires infrastructure setup

## Next Steps

This pipeline implements Part 1 (Build and Test) of the complete CI/CD pipeline. The following stages will be added in subsequent tasks:

### Part 2 (Deploy and Test) - Task 18
- Pre-production deployment
- Integration tests
- API tests with Newman
- Artifact publishing to Nexus
- Functional tests with Selenium
- Performance tests with JMeter

### Part 3 (Production Deploy) - Task 19
- STABLE artifact tagging
- Canary deployment
- Production deployment
- GOLD artifact tagging
- Post-deployment monitoring
- Notifications
- Rollback on failure

## Best Practices

1. **Always run the pipeline before merging:**
   - Never merge PRs with failing checks
   - Review all warnings and errors

2. **Monitor pipeline performance:**
   - Track build times
   - Optimize slow stages
   - Use caching effectively

3. **Keep secrets secure:**
   - Never commit secrets to code
   - Rotate tokens regularly
   - Use least privilege principle

4. **Review artifacts:**
   - Download and review coverage reports
   - Check security scan results
   - Verify build artifacts

5. **Document failures:**
   - Create issues for recurring failures
   - Document workarounds
   - Share knowledge with team

## Related Documentation

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Documentation](https://maven.apache.org/guides/)
- [SonarQube Documentation](https://docs.sonarqube.org/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [Branch Protection Setup](.github/BRANCH_PROTECTION_SETUP.md)

## Support

For issues or questions about the pipeline:
1. Check this documentation first
2. Review the workflow file: `.github/workflows/ci-cd-pipeline.yml`
3. Check GitHub Actions logs for detailed error messages
4. Consult the team's DevOps channel
