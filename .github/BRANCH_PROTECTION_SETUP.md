# GitHub Branch Protection Rules Setup

## ⚠️ IMPORTANT: Complete this AFTER the pipeline runs successfully at least once

The branch protection rules reference specific job names from the GitHub Actions workflow. These job names only appear in GitHub's UI after the workflow has executed at least once.

## Prerequisites

1. Push the workflow file to your repository
2. Trigger the pipeline (either by pushing to `main`, `develop`, or manually)
3. Wait for the pipeline to complete successfully
4. Verify that all jobs appear in the Actions tab

## Setup Instructions

### Step 1: Navigate to Branch Protection Settings

1. Go to your GitHub repository
2. Click on **Settings** (top menu)
3. Click on **Branches** (left sidebar)
4. Click **Add branch protection rule**

### Step 2: Configure Protection for `main` Branch

1. **Branch name pattern:** `main`

2. **Protect matching branches:**
   - ✅ Enable "Require a pull request before merging"
   - Set "Required number of approvals before merging" to **1**
   
3. **Status checks:**
   - ✅ Enable "Require status checks to pass before merging"
   - ✅ Enable "Require branches to be up to date before merging"
   - Click "Add checks" and select these status checks:
     - `build-backend`
     - `build-frontend`
     - `unit-tests`
     - `sonarqube-analysis`
     - `security-scan`
     - `pipeline-summary`
   
4. **Additional settings:**
   - ✅ Enable "Do not allow bypassing the above settings"
   - ✅ Enable "Require linear history" (optional but recommended)
   - ✅ Enable "Include administrators" (optional but recommended)

5. Click **Create** to save the rule

### Step 3: Configure Protection for `develop` Branch

1. Click **Add branch protection rule** again

2. **Branch name pattern:** `develop`

3. **Protect matching branches:**
   - ✅ Enable "Require a pull request before merging"
   - Set "Required number of approvals before merging" to **1**
   
4. **Status checks:**
   - ✅ Enable "Require status checks to pass before merging"
   - ✅ Enable "Require branches to be up to date before merging"
   - Click "Add checks" and select the same status checks as for `main`:
     - `build-backend`
     - `build-frontend`
     - `unit-tests`
     - `sonarqube-analysis`
     - `security-scan`
     - `pipeline-summary`

5. Click **Create** to save the rule

### Step 4: Verify Configuration

1. Go to **Settings** → **Branches**
2. Verify both `main` and `develop` have protection rules
3. Try to push directly to `main` or `develop` - it should be blocked
4. Create a test PR to verify the status checks are required

## Job Names Reference

The following job names are defined in `.github/workflows/ci-cd-pipeline.yml`:

| Job Name | Description |
|----------|-------------|
| `build-backend` | Builds the Spring Boot backend |
| `build-frontend` | Builds the React frontend |
| `unit-tests` | Runs unit tests for both backend and frontend |
| `sonarqube-analysis` | Performs code quality analysis |
| `security-scan` | Scans for security vulnerabilities |
| `pipeline-summary` | Provides overall pipeline status |

## Troubleshooting

### Status checks don't appear in the list

**Problem:** When trying to add status checks, the job names don't appear in the dropdown.

**Solution:** 
1. Ensure the pipeline has run at least once
2. Check the Actions tab to verify the jobs completed
3. Wait a few minutes and refresh the page
4. The job names should now appear in the dropdown

### Can't push to protected branch

**Problem:** Getting "protected branch" error when trying to push.

**Solution:** This is expected behavior. You must:
1. Create a feature branch
2. Push your changes to the feature branch
3. Create a Pull Request
4. Wait for status checks to pass
5. Get approval from a reviewer
6. Merge the PR

### Emergency bypass needed

**Problem:** Need to push directly to `main` in an emergency.

**Solution:**
1. Temporarily disable branch protection (Settings → Branches → Edit rule)
2. Make your emergency fix
3. Re-enable branch protection immediately after
4. Document the bypass in your incident report

## Best Practices

1. **Never disable branch protection** except in true emergencies
2. **Always require status checks** to ensure code quality
3. **Require PR reviews** to catch issues before merge
4. **Keep branches up to date** to avoid integration issues
5. **Use linear history** to maintain clean git history

## Related Documentation

- [GitHub Branch Protection Documentation](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [GitHub Actions Status Checks](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/collaborating-on-repositories-with-code-quality-features/about-status-checks)
