# Nexus Troubleshooting Guide

## Common Issues and Solutions

### Issue: 403 Forbidden When Deploying Artifacts

**Symptoms:**
```
[ERROR] Failed to execute goal maven-deploy-plugin:deploy: 
status code: 403, reason phrase: Forbidden (403)
```

**Possible Causes:**

1. **Insufficient Permissions**
   - The user account doesn't have deployment privileges
   - Repository write policy is too restrictive

2. **Security Realm Configuration**
   - Required security realms are not active
   - Authentication realm is not properly configured

3. **Repository Configuration**
   - Repository is in read-only mode
   - Deployment policy prevents writes

**Solutions:**

#### Solution 1: Verify User Permissions via Nexus UI

1. Access Nexus UI at http://localhost:8081
2. Login with admin/admin123
3. Go to **Administration** (gear icon) → **Security** → **Users**
4. Select the **admin** user
5. Verify the user has the **nx-admin** role assigned
6. If not, click **Edit** and add the **nx-admin** role

#### Solution 2: Check Repository Configuration

1. In Nexus UI, go to **Administration** → **Repository** → **Repositories**
2. Select **maven-snapshots**
3. Verify settings:
   - **Online**: Should be checked
   - **Deployment Policy**: Should be "Allow redeploy"
   - **Storage** → **Write Policy**: Should be "Allow write"
4. Click **Save** if any changes were made

#### Solution 3: Verify Security Realms

1. In Nexus UI, go to **Administration** → **Security** → **Realms**
2. Ensure **Local Authenticating Realm** is in the **Active** list
3. If not, select it from **Available** and move it to **Active**
4. Click **Save**

#### Solution 4: Enable Anonymous Access for Reading

If you want to allow anonymous reading (but not writing):

1. In Nexus UI, go to **Administration** → **Security** → **Anonymous Access**
2. Check **Allow anonymous users to access the server**
3. Click **Save**

Or use the script:
```bash
.\scripts\enable-nexus-anonymous.bat
```

#### Solution 5: Create a Deployment User

Instead of using the admin account, create a dedicated deployment user:

1. In Nexus UI, go to **Administration** → **Security** → **Users**
2. Click **Create local user**
3. Fill in details:
   - **ID**: deployer
   - **Password**: (choose a strong password)
   - **Status**: Active
   - **Roles**: nx-deploy (or create a custom role with deployment privileges)
4. Click **Create**

5. Update Maven settings.xml:
   ```xml
   <server>
       <id>nexus-snapshots</id>
       <username>deployer</username>
       <password>your-password</password>
   </server>
   ```

#### Solution 6: Use Nexus API Token (Recommended for CI/CD)

1. In Nexus UI, click your username → **User Token**
2. Click **Access user token**
3. Copy the generated token
4. In Maven settings.xml, use the token:
   ```xml
   <server>
       <id>nexus-snapshots</id>
       <username>admin</username>
       <password>your-token-here</password>
   </server>
   ```

### Issue: Cannot Connect to Nexus

**Symptoms:**
```
Connection refused: http://localhost:8081
```

**Solutions:**

1. **Verify Nexus is Running**:
   ```bash
   docker ps | grep nexus
   ```

2. **Start Nexus if Stopped**:
   ```bash
   docker-compose -f docker-compose.dev.yml up -d nexus
   ```

3. **Check Nexus Logs**:
   ```bash
   docker logs devops-nexus
   ```
   Look for "Started Sonatype Nexus" message

4. **Wait for Startup**:
   Nexus can take 1-2 minutes to fully start. Check status:
   ```bash
   curl http://localhost:8081/service/rest/v1/status
   ```

### Issue: Maven Cannot Download Dependencies from Nexus

**Symptoms:**
```
Could not transfer artifact from/to nexus-maven-central: 
status code: 403, reason phrase: Forbidden (403)
```

**Solution:**

This happens when Maven is configured to use Nexus as a mirror for Maven Central, but anonymous access is not enabled.

**Option 1**: Enable anonymous access (see Solution 4 above)

**Option 2**: Remove the mirror configuration from `~/.m2/settings.xml`:
```xml
<!-- Comment out or remove this section -->
<!--
<mirrors>
    <mirror>
        <id>nexus-maven-central</id>
        <name>Nexus Maven Central Mirror</name>
        <url>http://localhost:8081/repository/maven-public/</url>
        <mirrorOf>central</mirrorOf>
    </mirror>
</mirrors>
-->
```

**Option 3**: Use the simplified settings file:
```bash
# Windows
copy backend\settings-deploy-only.xml %USERPROFILE%\.m2\settings.xml

# Linux/Mac
cp backend/settings-deploy-only.xml ~/.m2/settings.xml
```

### Issue: Repository Already Exists

**Symptoms:**
When running setup-nexus script:
```
Repository with name 'maven-releases' already exists
```

**Solution:**
This is not an error - the repository already exists. You can:
1. Continue using the existing repository
2. Or delete it via Nexus UI and recreate:
   - Go to **Administration** → **Repository** → **Repositories**
   - Select the repository
   - Click **Delete**
   - Run the setup script again

### Issue: Deployment Succeeds but Artifact Not Visible

**Symptoms:**
Maven reports successful deployment, but artifact doesn't appear in Nexus UI.

**Solutions:**

1. **Refresh the Browser**: Sometimes the UI cache needs refreshing

2. **Check the Correct Repository**:
   - SNAPSHOT versions go to `maven-snapshots`
   - RELEASE versions go to `maven-releases`

3. **Browse Directly**:
   Navigate to: http://localhost:8081/repository/maven-snapshots/com/techcorp/devops-platform/

4. **Check Nexus Logs**:
   ```bash
   docker logs devops-nexus | grep -i error
   ```

### Issue: Out of Disk Space

**Symptoms:**
```
No space left on device
```

**Solutions:**

1. **Check Docker Volume Size**:
   ```bash
   docker system df -v
   ```

2. **Clean Up Old Snapshots**:
   - In Nexus UI, go to **Administration** → **Tasks**
   - Create a **Cleanup** task for maven-snapshots
   - Configure to remove snapshots older than X days

3. **Increase Docker Disk Space**:
   - Docker Desktop → Settings → Resources → Disk image size

## Manual Deployment Workaround

If automated deployment continues to fail, you can manually upload artifacts:

### Using Nexus UI

1. Access Nexus UI at http://localhost:8081
2. Login with admin/admin123
3. Click **Upload** (upload icon in left sidebar)
4. Select **maven-snapshots** repository
5. Upload the JAR file from `backend/target/devops-platform-1.0.0-SNAPSHOT.jar`
6. Fill in Maven coordinates:
   - **Group ID**: com.techcorp
   - **Artifact ID**: devops-platform
   - **Version**: 1.0.0-SNAPSHOT
   - **Packaging**: jar
7. Click **Upload**

### Using curl

```bash
# Set variables
NEXUS_URL="http://localhost:8081"
NEXUS_USER="admin"
NEXUS_PASSWORD="admin123"
GROUP_ID="com.techcorp"
ARTIFACT_ID="devops-platform"
VERSION="1.0.0-SNAPSHOT"
FILE="backend/target/devops-platform-1.0.0-SNAPSHOT.jar"

# Upload
curl -v -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  --upload-file "${FILE}" \
  "${NEXUS_URL}/repository/maven-snapshots/${GROUP_ID//.//}/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.jar"
```

## Getting Help

If issues persist:

1. **Check Nexus Logs**:
   ```bash
   docker logs devops-nexus --tail 100
   ```

2. **Enable Maven Debug Logging**:
   ```bash
   mvn deploy -X -DskipTests
   ```

3. **Verify Network Connectivity**:
   ```bash
   curl -v http://localhost:8081/service/rest/v1/status
   ```

4. **Check Nexus Documentation**:
   - [Nexus Repository Manager 3 Documentation](https://help.sonatype.com/repomanager3)
   - [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)

5. **Reset Nexus** (last resort):
   ```bash
   docker-compose -f docker-compose.dev.yml down
   docker volume rm devops-platform_nexus_data
   docker-compose -f docker-compose.dev.yml up -d nexus
   # Wait for startup, then run setup scripts again
   ```
