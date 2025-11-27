# CI/CD Pipeline Quick Reference

## Pipeline Jobs Overview

| # | Job Name | Purpose | Runs On | Duration |
|---|----------|---------|---------|----------|
| 1 | build-backend | Compile backend JAR | All branches | ~2-3 min |
| 2 | build-frontend | Build frontend dist | All branches | ~2-3 min |
| 3 | unit-tests | Run unit tests | All branches | ~3-5 min |
| 4 | sonarqube-analysis | Code quality analysis | All branches | ~2-3 min |
| 5 | security-scan | OWASP & npm audit | All branches | ~3-5 min |
| 6 | deploy-preprod | Deploy to pre-prod | develop/main/release/* | ~3-5 min |
| 7 | integration-tests | Integration tests | develop/main/release/* | ~2-3 min |
| 8 | api-tests | Newman API tests | develop/main/release/* | ~2-3 min |
| 9 | publish-artifacts | Publish to Nexus | develop/main/release/* | ~1-2 min |
| 10 | functional-tests | Selenium E2E tests | develop/main/release/* | ~5-10 min |
| 11 | performance-tests | JMeter load tests | develop/main/release/* | ~5-10 min |
| 12 | pipeline-summary | Summary & status | All branches | ~30 sec |

**Total Pipeline Duration:** ~30-45 minutes (full run with all tests)

## Quick Commands

### Trigger Pipeline Manually
```bash
# Via GitHub UI: Actions → CI/CD Pipeline → Run workflow
# Select branch, environment, and options
```

### View Pipeline Status
```bash
# Via GitHub UI: Actions tab
# Or via CLI:
gh run list --workflow=ci-cd-pipeline.yml
```

### Download Artifacts
```bash
# Via GitHub UI: Actions → Select run → Artifacts section
# Or via CLI:
gh run download <run-id>
```

## Common Scenarios

### Scenario 1: Quick Feature Branch Test
**Branch:** `feature/my-feature`
**Runs:** Jobs 1-5 only (build, test, analyze)
**Duration:** ~15 minutes
**Use Case:** Validate code changes before PR

### Scenario 2: Develop Branch Integration
**Branch:** `develop`
**Runs:** All jobs 1-12
**Duration:** ~30-45 minutes
**Use Case:** Full validation before release

### Scenario 3: Release Preparation
**Branch:** `release/1.0.0`
**Runs:** All jobs 1-12
**Duration:** ~30-45 minutes
**Use Case:** Final validation before production

### Scenario 4: Production Deployment
**Branch:** `main`
**Runs:** All jobs 1-12 + production stages (Task 19)
**Duration:** ~45-60 minutes
**Use Case:** Deploy to production

### Scenario 5: Emergency Hotfix
**Manual Trigger:** `skip_tests=true`
**Runs:** Jobs 1-2, 6, 9 only
**Duration:** ~10 minutes
**Use Case:** Emergency deployment (NOT RECOMMENDED)

## Troubleshooting

### Pipeline Fails at Build Stage
**Symptoms:** Jobs 1 or 2 fail
**Common Causes:**
- Compilation errors in code
- Missing dependencies
- Syntax errors

**Actions:**
1. Check build logs in GitHub Actions
2. Run `mvn clean package` locally (backend)
3. Run `npm run build` locally (frontend)
4. Fix errors and push again

### Pipeline Fails at Test Stage
**Symptoms:** Job 3 fails
**Common Causes:**
- Test failures
- Low code coverage (<80%)
- Test configuration issues

**Actions:**
1. Check test logs in GitHub Actions
2. Run `mvn test` locally (backend)
3. Run `npm test` locally (frontend)
4. Fix failing tests and push again

### Pipeline Fails at SonarQube Stage
**Symptoms:** Job 4 fails
**Common Causes:**
- Quality gate failure
- Critical security issues
- High code complexity

**Actions:**
1. Check SonarQube dashboard
2. Review code smells and bugs
3. Fix issues and push again

### Pipeline Fails at Security Scan
**Symptoms:** Job 5 fails
**Common Causes:**
- High/critical vulnerabilities in dependencies
- Outdated packages

**Actions:**
1. Check OWASP report artifact
2. Check npm audit report artifact
3. Update vulnerable dependencies
4. Add suppressions if false positives

### Pipeline Fails at Deployment
**Symptoms:** Job 6 fails
**Common Causes:**
- Docker build failures
- Container startup issues
- Health check timeouts

**Actions:**
1. Check Docker logs in GitHub Actions
2. Test `docker-compose up` locally
3. Verify Dockerfiles are correct
4. Check health check endpoints

### Pipeline Fails at API Tests
**Symptoms:** Job 8 fails
**Common Causes:**
- API endpoint changes
- Authentication issues
- Test data problems

**Actions:**
1. Download Newman reports artifact
2. Review failed assertions
3. Test Postman collections locally
4. Update collections or fix API

### Pipeline Fails at Functional Tests
**Symptoms:** Job 10 fails
**Common Causes:**
- UI changes breaking selectors
- Timing issues
- Browser compatibility

**Actions:**
1. Download Selenium screenshots artifact
2. Review test reports
3. Run Selenium tests locally
4. Update page objects or fix UI

### Pipeline Fails at Performance Tests
**Symptoms:** Job 11 fails
**Common Causes:**
- Performance thresholds exceeded
- API response time too slow
- High error rate

**Actions:**
1. Download JMeter reports artifact
2. Review performance metrics
3. Optimize slow endpoints
4. Adjust thresholds if needed

## Artifact Locations

All artifacts are available in GitHub Actions for 7 days:

```
Actions → Select Run → Artifacts (bottom of page)
```

### Key Artifacts
- **newman-reports** - API test results (HTML)
- **selenium-screenshots** - Failed test screenshots
- **jmeter-reports** - Performance test results (HTML)
- **backend-coverage** - Code coverage reports
- **owasp-report** - Security scan results

## Environment Variables

### Required Secrets
Configure in: Settings → Secrets and variables → Actions

```
SONAR_TOKEN=<your-sonarqube-token>
SONAR_HOST_URL=<your-sonarqube-url>
NEXUS_USERNAME=<your-nexus-username>
NEXUS_PASSWORD=<your-nexus-password>
NEXUS_URL=<your-nexus-url>
```

### Pipeline Variables
Set in workflow file:

```yaml
JAVA_VERSION: '17'
NODE_VERSION: '18'
MAVEN_OPTS: '-Xmx1024m'
SONAR_PROJECT_KEY: 'devops-enterprise-platform'
```

## Best Practices

### ✅ DO
- Run full pipeline on `develop` before creating release branch
- Review all test reports before merging to `main`
- Keep dependencies up to date
- Monitor pipeline duration and optimize slow jobs
- Use manual triggers for testing pipeline changes

### ❌ DON'T
- Skip tests in production deployments
- Ignore quality gate failures
- Deploy with known security vulnerabilities
- Commit directly to `main` (use PRs)
- Ignore performance test failures

## Pipeline Metrics

Track these metrics to measure DevOps maturity:

- **Deployment Frequency:** How often code is deployed
- **Lead Time:** Time from commit to production
- **Change Failure Rate:** % of deployments causing failures
- **MTTR:** Mean time to recovery from failures

**Target Metrics (DORA High Performers):**
- Deployment Frequency: Multiple per day
- Lead Time: < 1 hour
- Change Failure Rate: < 5%
- MTTR: < 1 hour

## Support

For pipeline issues:
1. Check this guide first
2. Review GitHub Actions logs
3. Download and review artifacts
4. Contact DevOps team if needed

## Related Documentation

- [Pipeline Part 2 Summary](./PIPELINE_PART2_SUMMARY.md)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Newman Documentation](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/)
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [JMeter Documentation](https://jmeter.apache.org/usermanual/index.html)
