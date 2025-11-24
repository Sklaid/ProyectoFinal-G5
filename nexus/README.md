# Nexus Repository Manager Setup

This document describes the Nexus Repository Manager configuration for the DevOps Enterprise Platform.

## Overview

Nexus Repository Manager is used to store and manage build artifacts including:
- Maven JARs (releases and snapshots)
- NPM packages
- Docker images

## Access Information

- **URL**: http://localhost:8081
- **Username**: admin
- **Password**: admin123

## Configured Repositories

### Maven Repositories

#### maven-releases
- **Type**: Hosted
- **URL**: http://localhost:8081/repository/maven-releases/
- **Version Policy**: RELEASE
- **Write Policy**: ALLOW_ONCE (prevents overwriting releases)
- **Purpose**: Store production-ready Maven artifacts

#### maven-snapshots
- **Type**: Hosted
- **URL**: http://localhost:8081/repository/maven-snapshots/
- **Version Policy**: SNAPSHOT
- **Write Policy**: ALLOW (allows overwriting snapshots)
- **Purpose**: Store development Maven artifacts

### NPM Repository

#### npm-hosted
- **Type**: Hosted
- **URL**: http://localhost:8081/repository/npm-hosted/
- **Purpose**: Store NPM packages (if needed for frontend artifacts)

### Docker Repository

#### docker-hosted
- **Type**: Hosted
- **URL**: http://localhost:8081/repository/docker-hosted/
- **HTTP Port**: 8082
- **Purpose**: Store Docker images

## Setup Instructions

### Initial Setup

1. **Start Nexus Container**:
   ```bash
   docker-compose -f docker-compose.dev.yml up -d nexus
   ```

2. **Initialize Admin Password** (first time only):
   ```bash
   # Windows
   .\scripts\init-nexus-password.bat
   
   # Linux/Mac
   ./scripts/init-nexus-password.sh
   ```

3. **Create Repositories**:
   ```bash
   # Windows
   .\scripts\setup-nexus.bat
   
   # Linux/Mac
   ./scripts/setup-nexus.sh
   ```

### Verify Setup

Access the Nexus web UI at http://localhost:8081 and verify:
- Login works with admin/admin123
- All repositories are visible in the repository list
- Repository health checks pass

## Maven Configuration

To publish artifacts to Nexus, Maven needs to be configured with:

1. **Distribution Management** in `pom.xml`
2. **Server Credentials** in `~/.m2/settings.xml`

See Task 16.2 for detailed configuration steps.

## Troubleshooting

### Nexus Won't Start

Check container logs:
```bash
docker logs devops-nexus
```

Common issues:
- Port 8081 already in use
- Insufficient disk space
- Insufficient memory (Nexus requires ~2GB RAM)

### Cannot Access Nexus UI

1. Verify container is running:
   ```bash
   docker ps | grep nexus
   ```

2. Check health status:
   ```bash
   curl http://localhost:8081/service/rest/v1/status
   ```

3. Wait for startup (can take 1-2 minutes)

### Repository Creation Failed

- Verify admin credentials are correct
- Check if repository already exists
- Review Nexus logs for errors

## Security Considerations

### Production Deployment

For production environments:

1. **Change Default Password**: Use a strong, unique password
2. **Enable HTTPS**: Configure SSL/TLS certificates
3. **Configure Authentication**: Integrate with LDAP/Active Directory
4. **Set Up Roles**: Create role-based access control
5. **Enable Audit Logging**: Track all repository access
6. **Regular Backups**: Backup `/nexus-data` volume

### Credentials Management

- Store Nexus credentials in environment variables or secrets manager
- Never commit credentials to version control
- Use different credentials for CI/CD pipelines
- Rotate credentials regularly

## Maintenance

### Backup

Backup the Nexus data volume:
```bash
docker run --rm -v devops-platform_nexus_data:/data -v $(pwd)/backup:/backup alpine tar czf /backup/nexus-backup-$(date +%Y%m%d).tar.gz /data
```

### Restore

Restore from backup:
```bash
docker run --rm -v devops-platform_nexus_data:/data -v $(pwd)/backup:/backup alpine tar xzf /backup/nexus-backup-YYYYMMDD.tar.gz -C /
```

### Cleanup

Remove old snapshots and unused artifacts:
1. Access Nexus UI
2. Go to Administration → Tasks
3. Create "Cleanup" tasks for each repository
4. Schedule regular execution

## References

- [Nexus Repository Manager Documentation](https://help.sonatype.com/repomanager3)
- [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)
- [Docker Registry API](https://docs.docker.com/registry/spec/api/)
