# Maven Deployment to Nexus

This document explains how to configure Maven to publish artifacts to Nexus Repository Manager.

## Overview

The project is configured to publish artifacts to Nexus with the following versioning strategy:

- **SNAPSHOT versions** (e.g., `1.0.0-SNAPSHOT`): Published to `maven-snapshots` repository
  - Can be overwritten
  - Used for development builds
  - Automatically deployed from `develop` branch

- **RELEASE versions** (e.g., `1.0.0`): Published to `maven-releases` repository
  - Cannot be overwritten (immutable)
  - Used for production builds
  - Deployed from `main` branch or release tags

## Configuration

### 1. POM Configuration

The `pom.xml` already includes the distribution management configuration:

```xml
<distributionManagement>
    <repository>
        <id>nexus-releases</id>
        <name>Nexus Release Repository</name>
        <url>http://localhost:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <name>Nexus Snapshot Repository</name>
        <url>http://localhost:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

### 2. Maven Settings Configuration

#### Automated Setup (Recommended)

Run the setup script:

```bash
# Windows
.\scripts\setup-maven-nexus.bat

# Linux/Mac
./scripts/setup-maven-nexus.sh
```

#### Manual Setup

1. Copy the template to your Maven settings directory:
   ```bash
   # Windows
   copy backend\settings.xml.template %USERPROFILE%\.m2\settings.xml
   
   # Linux/Mac
   cp backend/settings.xml.template ~/.m2/settings.xml
   ```

2. Set environment variables for credentials:
   ```bash
   # Windows (PowerShell)
   $env:NEXUS_USERNAME = "admin"
   $env:NEXUS_PASSWORD = "admin123"
   
   # Windows (CMD)
   set NEXUS_USERNAME=admin
   set NEXUS_PASSWORD=admin123
   
   # Linux/Mac
   export NEXUS_USERNAME=admin
   export NEXUS_PASSWORD=admin123
   ```

### 3. Version Management

#### For Development (SNAPSHOT)

Keep the version in `pom.xml` as:
```xml
<version>1.0.0-SNAPSHOT</version>
```

#### For Release

Update the version to remove `-SNAPSHOT`:
```xml
<version>1.0.0</version>
```

Or use Maven versions plugin:
```bash
# Set release version
mvn versions:set -DnewVersion=1.0.0

# Commit the change
mvn versions:commit
```

## Deployment Commands

### Deploy to Nexus

```bash
cd backend

# Deploy with tests
mvn clean deploy

# Deploy without tests (faster)
mvn clean deploy -DskipTests

# Deploy with specific profile
mvn clean deploy -P nexus
```

### Verify Deployment

1. Check Maven output for success message:
   ```
   [INFO] Uploading to nexus-snapshots: http://localhost:8081/repository/maven-snapshots/...
   [INFO] BUILD SUCCESS
   ```

2. Verify in Nexus UI:
   - Open http://localhost:8081
   - Login with admin/admin123
   - Browse → maven-snapshots (or maven-releases)
   - Navigate to: com/techcorp/devops-platform/

3. Check artifact details:
   - Verify version number
   - Check file size
   - Review upload timestamp

## CI/CD Integration

### GitHub Actions

In the CI/CD pipeline, credentials are provided via secrets:

```yaml
- name: Deploy to Nexus
  env:
    NEXUS_USERNAME: ${{ secrets.NEXUS_USERNAME }}
    NEXUS_PASSWORD: ${{ secrets.NEXUS_PASSWORD }}
  run: |
    cd backend
    mvn deploy -DskipTests
```

### Required Secrets

Configure these secrets in GitHub repository settings:
- `NEXUS_USERNAME`: Nexus username (e.g., "admin")
- `NEXUS_PASSWORD`: Nexus password (e.g., "admin123")

## Troubleshooting

### Authentication Failed

**Error**: `401 Unauthorized`

**Solutions**:
1. Verify environment variables are set:
   ```bash
   echo $NEXUS_USERNAME
   echo $NEXUS_PASSWORD
   ```

2. Check credentials in Nexus UI (http://localhost:8081)

3. Verify server IDs match in `pom.xml` and `settings.xml`:
   - `nexus-releases`
   - `nexus-snapshots`

### Cannot Overwrite Release

**Error**: `Repository does not allow updating assets: maven-releases`

**Cause**: Trying to deploy a RELEASE version that already exists

**Solutions**:
1. Increment version number in `pom.xml`
2. Or use SNAPSHOT version for development

### Connection Refused

**Error**: `Connection refused to http://localhost:8081`

**Solutions**:
1. Verify Nexus is running:
   ```bash
   docker ps | grep nexus
   ```

2. Start Nexus if not running:
   ```bash
   docker-compose -f docker-compose.dev.yml up -d nexus
   ```

3. Wait for Nexus to fully start (check logs):
   ```bash
   docker logs devops-nexus
   ```

### Deployment Timeout

**Error**: `Read timed out`

**Solutions**:
1. Increase timeout in `settings.xml`:
   ```xml
   <server>
       <id>nexus-releases</id>
       <username>${env.NEXUS_USERNAME}</username>
       <password>${env.NEXUS_PASSWORD}</password>
       <configuration>
           <timeout>60000</timeout>
       </configuration>
   </server>
   ```

2. Check network connectivity to Nexus

3. Verify artifact size is reasonable

## Best Practices

### Version Numbering

Follow Semantic Versioning (SemVer):
- **MAJOR.MINOR.PATCH** (e.g., 1.2.3)
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

### Development Workflow

1. **Feature Development**:
   - Use SNAPSHOT version (e.g., `1.1.0-SNAPSHOT`)
   - Deploy frequently to test integration
   - Snapshots can be overwritten

2. **Release Preparation**:
   - Create release branch
   - Remove `-SNAPSHOT` from version
   - Deploy to `maven-releases`
   - Tag in Git (e.g., `v1.1.0`)

3. **Post-Release**:
   - Increment version on `develop` branch
   - Add `-SNAPSHOT` suffix
   - Continue development

### Security

1. **Never commit credentials** to version control
2. **Use environment variables** for local development
3. **Use secrets management** for CI/CD (GitHub Secrets, Vault, etc.)
4. **Rotate passwords** regularly
5. **Use different credentials** for CI/CD vs. developers

### Artifact Management

1. **Clean old snapshots** regularly to save disk space
2. **Keep all releases** for rollback capability
3. **Document breaking changes** in release notes
4. **Tag releases** in Git for traceability

## References

- [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)
- [Maven Settings Reference](https://maven.apache.org/settings.html)
- [Nexus Repository Manager](https://help.sonatype.com/repomanager3)
- [Semantic Versioning](https://semver.org/)
