# Production Deployment Guide

## Quick Reference

This guide provides a quick reference for the production deployment stages added in Part 3 of the CI/CD pipeline.

## Pipeline Stages Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    BUILD & COMPILE                               │
│  • Build Backend (Java/Maven)                                   │
│  • Build Frontend (React/TypeScript)                            │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                  TESTING & QUALITY                               │
│  • Unit Tests (JUnit, Vitest)                                   │
│  • SonarQube Analysis                                           │
│  • Security Scan (OWASP, npm audit)                             │
│  • Integration Tests                                            │
│  • API Tests (Newman/Postman)                                   │
│  • Functional Tests (Selenium)                                  │
│  • Performance Tests (JMeter)                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│              PRE-PRODUCTION DEPLOYMENT                           │
│  • Deploy to Pre-Prod Environment                               │
│  • Publish Artifacts to Nexus                                   │
│  • Tag STABLE Artifact                                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                    [main branch only]
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                  CANARY DEPLOYMENT                               │
│  • Deploy to 10% of production instances                        │
│  • Monitor for 5 minutes                                        │
│  • Validate health and metrics                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│               PRODUCTION DEPLOYMENT                              │
│  • Backup current production                                    │
│  • Deploy to production                                         │
│  • Run smoke tests                                              │
│  • Validate deployment                                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    GOLD TAGGING                                  │
│  • Tag production release                                       │
│  • Create GitHub Release                                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│            POST-DEPLOYMENT MONITORING                            │
│  • Monitor for 3 minutes                                        │
│  • Validate metrics                                             │
│  • Check resource usage                                         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   NOTIFICATIONS                                  │
│  • Send Slack notifications                                     │
│  • Report pipeline status                                       │
└─────────────────────────────────────────────────────────────────┘

                    [On Failure Only]
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                  AUTOMATIC ROLLBACK                              │
│  • Stop failed deployment                                       │
│  • Restore previous GOLD version                                │
│  • Verify rollback                                              │
│  • Notify team                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Job Details

### 1. Tag STABLE Artifact
**When:** After all pre-production tests pass  
**Branches:** `develop`, `release/*`  
**Duration:** ~30 seconds  
**Tag Format:** `v{version}-STABLE-build.{number}`

**What it does:**
- Creates a Git tag marking the artifact as stable
- Indicates all pre-production tests passed
- Ready for production deployment consideration

### 2. Canary Deployment
**When:** After STABLE tagging  
**Branches:** `main` only  
**Duration:** ~6-7 minutes (5 min monitoring + deployment)  
**Ports:** 8082 (backend), 3001 (frontend), 5433 (postgres)

**What it does:**
- Deploys to a separate canary environment
- Simulates 10% of production traffic
- Monitors health for 5 minutes
- Validates metrics and error rates
- Cleans up on failure

**Health Checks (every 30s):**
- Backend health endpoint
- Frontend availability
- Error log analysis
- Metrics validation

### 3. Production Deployment
**When:** After successful canary  
**Branches:** `main` only  
**Duration:** ~3-5 minutes  
**Ports:** 8080 (backend), 3000 (frontend), 5432 (postgres)

**What it does:**
- Creates backup of current production
- Deploys using docker-compose.prod.yml
- Validates deployment health
- Runs smoke tests
- Reports deployment status

**Smoke Tests:**
- ✅ Health endpoint responds
- ✅ Frontend loads
- ✅ API responds correctly
- ✅ Database is accessible
- ✅ Flyway migrations applied

### 4. Tag GOLD Artifact
**When:** After successful production deployment  
**Branches:** `main` only  
**Duration:** ~30 seconds  
**Tag Format:** `v{version}-GOLD-build.{number}`

**What it does:**
- Creates a Git tag marking production release
- Creates GitHub Release with details
- Documents all completed pipeline stages
- Provides deployment URLs and artifact info

### 5. Post-Deployment Monitoring
**When:** After production deployment  
**Branches:** `main` only  
**Duration:** ~3 minutes  

**What it does:**
- Monitors application for 3 minutes
- Checks health every 30 seconds
- Validates all components
- Monitors error logs
- Checks resource usage
- Generates monitoring report

**Monitored Metrics:**
- Application health status
- Component health (DB, disk, ping)
- Error and warning counts
- Container resource usage

### 6. Notifications
**When:** Always (after all jobs)  
**Branches:** All  
**Duration:** ~10 seconds  

**What it does:**
- Determines overall pipeline status
- Sends Slack notification (if configured)
- Includes build information
- Lists failed jobs (on failure)
- Provides workflow link

**Notification Types:**
- ✅ Success: Green notification with summary
- ❌ Failure: Red notification with failed jobs

### 7. Rollback on Failure
**When:** Production deployment or monitoring fails  
**Branches:** `main` only  
**Duration:** ~3-5 minutes  

**What it does:**
- Detects deployment/monitoring failure
- Finds previous GOLD tag
- Stops failed deployment
- Restores backup images
- Restores database backup
- Redeploys previous version
- Verifies rollback health
- Runs smoke tests
- Notifies team

**Rollback Steps:**
1. Stop failed containers
2. Restore Docker images from backup
3. Restore database from backup
4. Checkout previous GOLD tag
5. Redeploy previous version
6. Verify health
7. Run smoke tests
8. Send notification

## Branch-Specific Behavior

### `develop` Branch
- ✅ Build & Compile
- ✅ Testing & Quality
- ✅ Pre-Production Deployment
- ✅ Publish Artifacts (SNAPSHOT)
- ✅ Tag STABLE Artifact
- ✅ Notifications
- ❌ Canary Deployment (skipped)
- ❌ Production Deployment (skipped)
- ❌ GOLD Tagging (skipped)
- ❌ Post-Deployment Monitoring (skipped)
- ❌ Rollback (skipped)

### `release/*` Branch
- ✅ Build & Compile
- ✅ Testing & Quality
- ✅ Pre-Production Deployment
- ✅ Publish Artifacts (SNAPSHOT)
- ✅ Tag STABLE Artifact
- ✅ Notifications
- ❌ Canary Deployment (skipped)
- ❌ Production Deployment (skipped)
- ❌ GOLD Tagging (skipped)
- ❌ Post-Deployment Monitoring (skipped)
- ❌ Rollback (skipped)

### `main` Branch (Production)
- ✅ Build & Compile
- ✅ Testing & Quality
- ✅ Pre-Production Deployment
- ✅ Publish Artifacts (RELEASE)
- ✅ Tag STABLE Artifact
- ✅ **Canary Deployment**
- ✅ **Production Deployment**
- ✅ **Tag GOLD Artifact**
- ✅ **Post-Deployment Monitoring**
- ✅ Notifications
- ✅ **Rollback** (if deployment fails)

## Manual Workflow Dispatch

You can manually trigger the pipeline with custom parameters:

**Parameters:**
- `environment`: Target environment (development, preprod, production)
- `skip_tests`: Skip tests (emergency only) - default: false
- `deploy_enabled`: Enable deployment - default: true

**Usage:**
1. Go to Actions tab in GitHub
2. Select "CI/CD Pipeline - DevOps Enterprise Platform"
3. Click "Run workflow"
4. Select branch and configure parameters
5. Click "Run workflow"

## Monitoring the Pipeline

### GitHub Actions UI
1. Go to repository → Actions tab
2. Click on the running workflow
3. View job status and logs
4. Check job summaries for detailed reports

### Key Indicators
- ✅ Green checkmark: Job succeeded
- ❌ Red X: Job failed
- 🟡 Yellow circle: Job in progress
- ⚪ Gray circle: Job skipped

### Logs to Check
- **Build logs:** Compilation errors
- **Test logs:** Test failures and coverage
- **Deployment logs:** Container startup issues
- **Health check logs:** Service availability
- **Monitoring logs:** Post-deployment metrics

## Troubleshooting

### Canary Deployment Fails
**Symptoms:** Canary health checks fail  
**Check:**
- Canary container logs: `docker logs devops-backend-canary`
- Port conflicts: 8082, 3001, 5433
- Health endpoint: `curl http://localhost:8082/actuator/health`

**Fix:**
- Review error logs
- Check environment variables
- Verify database connectivity
- Ensure ports are available

### Production Deployment Fails
**Symptoms:** Production health checks fail  
**Check:**
- Production container logs: `docker logs devops-backend-prod`
- Environment configuration
- Database migrations
- Smoke test results

**Fix:**
- Review deployment logs
- Check `.env.production` file
- Verify database connectivity
- Run smoke tests manually

### Rollback Fails
**Symptoms:** Rollback job fails  
**Check:**
- Previous GOLD tag exists
- Backup images available
- Database backup exists
- Rollback logs

**Fix:**
- Verify previous GOLD tag: `git tag -l "v*-GOLD-*"`
- Check backup images: `docker images | grep backup`
- Manually restore if needed
- Contact DevOps team

### Notifications Not Sent
**Symptoms:** No Slack notifications  
**Check:**
- `SLACK_WEBHOOK_URL` secret configured
- Webhook URL is valid
- Slack channel permissions

**Fix:**
- Configure webhook in repository secrets
- Test webhook manually
- Verify Slack app permissions

## Best Practices

### Before Merging to Main
1. ✅ All tests pass on `develop`
2. ✅ Create `release/*` branch
3. ✅ Validate on release branch
4. ✅ Review STABLE tag
5. ✅ Merge to `main` via PR

### During Production Deployment
1. 👀 Monitor canary deployment
2. 👀 Watch health checks
3. 👀 Review error logs
4. 👀 Check monitoring metrics
5. 👀 Verify GOLD tag created

### After Production Deployment
1. ✅ Verify application is accessible
2. ✅ Check monitoring dashboard
3. ✅ Review GitHub Release
4. ✅ Test critical user flows
5. ✅ Monitor for 24 hours

### If Rollback Occurs
1. 🚨 Review rollback logs
2. 🚨 Verify previous version is running
3. 🚨 Check Slack notification
4. 🚨 Investigate failure cause
5. 🚨 Fix issues before next deployment

## Environment URLs

### Pre-Production
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Database: localhost:5432

### Canary
- Frontend: http://localhost:3001
- Backend: http://localhost:8082
- Database: localhost:5433

### Production
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Database: localhost:5432

## Health Check Endpoints

- Backend Health: `http://localhost:8080/actuator/health`
- Backend Info: `http://localhost:8080/actuator/info`
- Backend Metrics: `http://localhost:8080/actuator/metrics`
- Frontend: `http://localhost:3000`

## Support

For issues or questions:
1. Check GitHub Actions logs
2. Review this guide
3. Check container logs
4. Contact DevOps team
5. Create GitHub issue

## Conclusion

The production deployment pipeline provides:
- ✅ Progressive deployment (canary → production)
- ✅ Automated monitoring and validation
- ✅ Automatic rollback on failure
- ✅ Team notifications
- ✅ Comprehensive logging and reporting

Follow this guide to ensure smooth production deployments!
