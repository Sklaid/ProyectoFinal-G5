# CI/CD Pipeline Quick Start Guide

## Prerequisites

Before running the pipeline, ensure you have:

1. ✅ GitHub repository created
2. ✅ Code pushed to `develop` or `main` branch
3. ✅ SonarQube server running (local or cloud)
4. ✅ GitHub secrets configured

## Step 1: Configure GitHub Secrets

Go to your repository → Settings → Secrets and variables → Actions

Add the following secrets:

| Secret Name | How to Get It | Example Value |
|-------------|---------------|---------------|
| `SONAR_TOKEN` | SonarQube → My Account → Security → Generate Token | `squ_abc123def456...` |
| `SONAR_HOST_URL` | Your SonarQube server URL | `http://localhost:9000` or `https://sonarcloud.io` |

### Getting SonarQube Token

**For Local SonarQube:**
1. Start SonarQube: `docker-compose -f docker-compose.dev.yml up -d sonarqube`
2. Open http://localhost:9000
3. Login (default: admin/admin)
4. Go to My Account → Security
5. Generate Token → Name: "GitHub Actions" → Generate
6. Copy the token (you won't see it again!)

**For SonarCloud:**
1. Go to https://sonarcloud.io
2. Login with GitHub
3. My Account → Security → Generate Token
4. Copy the token

## Step 2: Verify Workflow File

The workflow file should be at: `.github/workflows/ci-cd-pipeline.yml`

Check that it exists:
```bash
ls -la .github/workflows/ci-cd-pipeline.yml
```

## Step 3: Push Code to Trigger Pipeline

### Option A: Push to develop branch
```bash
git checkout develop
git add .
git commit -m "feat: trigger CI/CD pipeline"
git push origin develop
```

### Option B: Push to main branch
```bash
git checkout main
git add .
git commit -m "feat: trigger CI/CD pipeline"
git push origin main
```

### Option C: Create a Pull Request
```bash
git checkout -b feature/test-pipeline
git add .
git commit -m "feat: test CI/CD pipeline"
git push origin feature/test-pipeline
# Then create PR on GitHub
```

## Step 4: Monitor Pipeline Execution

1. Go to your GitHub repository
2. Click on **Actions** tab
3. You should see a workflow run starting
4. Click on the run to see details
5. Watch each job execute in real-time

### Expected Timeline

- ⏱️ Build Backend: 3-5 minutes
- ⏱️ Build Frontend: 2-3 minutes
- ⏱️ Unit Tests: 5-7 minutes
- ⏱️ SonarQube Analysis: 2-3 minutes
- ⏱️ Security Scan: 3-5 minutes
- ⏱️ **Total: 15-25 minutes**

## Step 5: Review Results

### ✅ If Pipeline Succeeds

1. **Check the summary:**
   - Green checkmarks for all jobs
   - Pipeline Summary shows all jobs passed

2. **Download artifacts:**
   - Scroll to bottom of workflow run
   - Download coverage reports
   - Review security scan results

3. **Check SonarQube:**
   - Open SonarQube dashboard
   - Verify quality gate passed
   - Review code coverage (should be >80%)

4. **Next steps:**
   - Configure branch protection rules (see BRANCH_PROTECTION_SETUP.md)
   - Proceed to Task 18 (Deploy and Test stages)

### ❌ If Pipeline Fails

1. **Identify the failing job:**
   - Look for red X marks
   - Click on the failed job

2. **Review the logs:**
   - Expand the failed step
   - Read error messages carefully
   - Look for stack traces

3. **Common failures and fixes:**

#### Build Failures

**Error:** "Could not resolve dependencies"
```bash
# Fix: Check internet connection and Maven Central access
# Verify pom.xml dependencies are correct
```

**Error:** "Compilation failure"
```bash
# Fix: Run locally first
mvn clean compile
# Fix any compilation errors
```

#### Test Failures

**Error:** "Tests failed"
```bash
# Fix: Run tests locally
mvn test
# Fix failing tests
```

**Error:** "Coverage below threshold"
```bash
# Fix: Add more unit tests to increase coverage
# Target: >80% coverage
```

#### SonarQube Failures

**Error:** "Quality Gate failed"
```bash
# Fix: Check SonarQube dashboard for issues
# Fix code smells, bugs, and security hotspots
# Increase test coverage
```

**Error:** "Could not connect to SonarQube"
```bash
# Fix: Verify SONAR_HOST_URL secret is correct
# Ensure SonarQube server is accessible
# Check SONAR_TOKEN is valid
```

#### Security Scan Failures

**Error:** "High/Critical vulnerabilities found"
```bash
# Fix: Update vulnerable dependencies
mvn versions:display-dependency-updates
# Update versions in pom.xml
# Or add suppression with justification
```

## Step 6: Manual Trigger (Optional)

You can also trigger the pipeline manually:

1. Go to Actions tab
2. Select "CI/CD Pipeline - DevOps Enterprise Platform"
3. Click "Run workflow"
4. Configure parameters:
   - **environment:** development (default)
   - **skip_tests:** false (default)
   - **deploy_enabled:** true (default)
5. Click "Run workflow"

### When to Use Manual Trigger

- Testing pipeline changes
- Running pipeline on specific branch
- Emergency deployments (with skip_tests=true, use with caution!)
- Debugging pipeline issues

## Troubleshooting

### Pipeline doesn't start

**Problem:** Pushed code but no workflow run appears

**Solutions:**
1. Check workflow file is in `.github/workflows/` directory
2. Verify YAML syntax is valid: https://www.yamllint.com/
3. Check branch name matches trigger configuration
4. Wait a few seconds and refresh the page

### Secrets not working

**Problem:** "Error: SONAR_TOKEN is not set"

**Solutions:**
1. Verify secret name matches exactly (case-sensitive)
2. Check secret is set at repository level, not organization
3. Re-create the secret if needed
4. Verify you have admin access to repository

### Jobs stuck in "Queued"

**Problem:** Jobs don't start, stuck in queue

**Solutions:**
1. Check GitHub Actions minutes quota
2. Verify no concurrent workflow limit reached
3. Wait for other workflows to complete
4. Consider using self-hosted runners

### Artifacts not uploading

**Problem:** "Error uploading artifact"

**Solutions:**
1. Check artifact size (max 10GB per workflow)
2. Verify path exists and contains files
3. Check retention days is valid (1-90)
4. Ensure artifact name is unique

## Next Steps

After successful pipeline execution:

1. ✅ **Configure Branch Protection** (Task 17.1.1)
   - See: `.github/BRANCH_PROTECTION_SETUP.md`
   - Protect `main` and `develop` branches
   - Require status checks to pass

2. ✅ **Review Pipeline Guide** (Optional)
   - See: `.github/CI_CD_PIPELINE_GUIDE.md`
   - Understand each job in detail
   - Learn optimization techniques

3. ✅ **Proceed to Task 18** (Next Implementation)
   - Deploy and Test stages
   - Integration tests
   - API tests with Newman
   - Functional tests with Selenium
   - Performance tests with JMeter

## Useful Commands

### Check workflow syntax locally
```bash
# Install act (GitHub Actions local runner)
# https://github.com/nektos/act

# Dry run workflow
act -n

# Run specific job
act -j build-backend
```

### View workflow runs via CLI
```bash
# Install GitHub CLI
# https://cli.github.com/

# List workflow runs
gh run list

# View specific run
gh run view <run-id>

# Watch run in real-time
gh run watch
```

### Download artifacts via CLI
```bash
# List artifacts
gh run view <run-id> --log

# Download artifact
gh run download <run-id> -n backend-coverage
```

## Support

Need help? Check these resources:

1. 📖 **Documentation:**
   - [CI/CD Pipeline Guide](.github/CI_CD_PIPELINE_GUIDE.md)
   - [Branch Protection Setup](.github/BRANCH_PROTECTION_SETUP.md)

2. 🔍 **Debugging:**
   - Check workflow logs in Actions tab
   - Review error messages carefully
   - Search GitHub Actions documentation

3. 💬 **Community:**
   - GitHub Actions Community Forum
   - Stack Overflow (tag: github-actions)
   - Team DevOps channel

## Checklist

Before considering Task 17 complete:

- [ ] Workflow file created and pushed
- [ ] GitHub secrets configured
- [ ] Pipeline triggered successfully
- [ ] All jobs passed (green checkmarks)
- [ ] Artifacts uploaded successfully
- [ ] SonarQube quality gate passed
- [ ] Security scans passed
- [ ] Coverage reports reviewed
- [ ] Branch protection rules configured (after first run)
- [ ] Documentation reviewed

---

**Status:** Task 17 - CI/CD Pipeline Part 1 (Build and Test) ✅ COMPLETE

**Next:** Task 18 - CI/CD Pipeline Part 2 (Deploy and Test)
