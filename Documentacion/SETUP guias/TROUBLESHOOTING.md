# Troubleshooting Guide - DevOps Enterprise Platform

This guide provides solutions to common issues you may encounter while setting up and running the DevOps Enterprise Platform.

## Table of Contents

- [Docker Issues](#docker-issues)
- [Build Issues](#build-issues)
- [Test Issues](#test-issues)
- [Nexus Issues](#nexus-issues)
- [SonarQube Issues](#sonarqube-issues)
- [CI/CD Pipeline Issues](#cicd-pipeline-issues)
- [Application Issues](#application-issues)
- [Getting Help](#getting-help)

## Docker Issues

### Problem: Docker containers won't start

**Symptoms**: Containers fail to start or exit immediately

**Solutions**:

1. **Check if Docker is running**:
   ```bash
   docker ps
   ```

2. **Clean up old containers and images**:
   ```bash
   docker-compose -f docker-compose.dev.yml down -v
   docker system prune -a
   ```

3. **Restart Docker Desktop**:
   - Windows/macOS: Right-click Docker icon → Restart
   - Linux: `sudo systemctl restart docker`

4. **Check Docker logs**:
   ```bash
   docker-compose -f docker-compose.dev.yml logs
   ```

### Problem: Port already in use

**Symptoms**: Error message "port is already allocated" or "address already in use"

**Solutions**:

**Windows**:
```cmd
# Find process using port
netstat -ano | findstr :8080
# Kill process
taskkill /PID <PID> /F
```

**Linux/macOS**:
```bash
# Find process using port
lsof -i :8080
# Kill process
kill -9 <PID>
```

**Alternative**: Change port in `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Use port 8081 instead of 8080
```

### Problem: Database connection refused

**Symptoms**: Backend cannot connect to PostgreSQL

**Solutions**:

1. **Wait for PostgreSQL to be ready** (takes 30-60 seconds after starting):
   ```bash
   docker-compose -f docker-compose.dev.yml logs postgres
   # Wait for: "database system is ready to accept connections"
   ```

2. **Check if database is accessible**:
   ```bash
   docker exec -it <postgres-container-name> psql -U postgres -d devops
   ```

3. **Verify connection settings** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/devops
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

### Problem: Container keeps restarting

**Symptoms**: Container status shows "Restarting"

**Solutions**:

1. **Check container logs**:
   ```bash
   docker logs <container-name>
   ```

2. **Check resource limits**:
   - Ensure Docker has enough memory (minimum 4GB, recommended 8GB)
   - Docker Desktop → Settings → Resources → Memory

3. **Remove and recreate container**:
   ```bash
   docker-compose -f docker-compose.dev.yml down
   docker-compose -f docker-compose.dev.yml up -d
   ```

## Build Issues

### Problem: Maven build fails

**Symptoms**: `mvn clean install` or `mvn package` fails

**Solutions**:

1. **Clean and rebuild**:
   ```bash
   cd backend
   mvn clean install -U
   ```

2. **Clear Maven cache**:
   ```bash
   # Windows
   rmdir /s /q %USERPROFILE%\.m2\repository
   
   # Linux/macOS
   rm -rf ~/.m2/repository
   ```

3. **Check Java version**:
   ```bash
   java -version
   # Should show version 17 or higher
   ```

4. **Verify JAVA_HOME**:
   ```bash
   # Windows
   echo %JAVA_HOME%
   
   # Linux/macOS
   echo $JAVA_HOME
   ```

### Problem: npm install fails

**Symptoms**: `npm install` fails with errors

**Solutions**:

1. **Clear npm cache**:
   ```bash
   cd frontend
   npm cache clean --force
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **Use specific Node version**:
   ```bash
   nvm install 18
   nvm use 18
   npm install
   ```

3. **Check npm version**:
   ```bash
   npm --version
   # Should be 8.x or higher
   ```

4. **Try with legacy peer deps**:
   ```bash
   npm install --legacy-peer-deps
   ```

### Problem: Flyway migration fails

**Symptoms**: Application fails to start with Flyway errors

**Solutions**:

1. **Check database connection** in `application.properties`

2. **Reset database**:
   ```bash
   docker-compose -f docker-compose.dev.yml down -v
   docker-compose -f docker-compose.dev.yml up -d postgres
   # Wait 60 seconds
   cd backend
   mvn spring-boot:run
   ```

3. **Manually run migrations**:
   ```bash
   cd backend
   mvn flyway:migrate
   ```

4. **Check migration files** in `src/main/resources/db/migration/`:
   - Files must follow naming convention: `V{version}__{description}.sql`
   - Example: `V1__create_users_table.sql`

### Problem: TypeScript compilation errors

**Symptoms**: Frontend build fails with TypeScript errors

**Solutions**:

1. **Check TypeScript version**:
   ```bash
   cd frontend
   npx tsc --version
   ```

2. **Clean and rebuild**:
   ```bash
   cd frontend
   rm -rf node_modules dist
   npm install
   npm run build
   ```

3. **Check `tsconfig.json`** for correct configuration

## Test Issues

### Problem: Tests fail with "Connection refused"

**Symptoms**: Tests cannot connect to backend or database

**Solutions**:

1. **Ensure services are running**:
   ```bash
   docker-compose -f docker-compose.dev.yml ps
   ```

2. **Check service health**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

3. **Wait for services to be ready**:
   - Backend takes 30-60 seconds to start
   - Database takes 30-60 seconds to be ready

### Problem: Selenium tests fail

**Symptoms**: E2E tests fail with WebDriver errors

**Solutions**:

1. **Update ChromeDriver** (WebDriverManager should handle this automatically):
   ```bash
   cd e2e-tests
   mvn clean test
   ```

2. **Run in headless mode** (edit test configuration):
   ```java
   ChromeOptions options = new ChromeOptions();
   options.addArguments("--headless");
   ```

3. **Check if Chrome is installed**:
   ```bash
   # Linux
   google-chrome --version
   
   # Windows: Check in Programs and Features
   ```

4. **View screenshots** of failed tests:
   - Location: `e2e-tests/screenshots/`

### Problem: JMeter tests fail

**Symptoms**: Performance tests fail to execute

**Solutions**:

1. **Verify JMeter is in PATH**:
   ```bash
   jmeter --version
   ```

2. **Increase heap size**:
   ```bash
   # Linux/macOS
   export HEAP="-Xms1g -Xmx1g"
   
   # Windows
   set HEAP=-Xms1g -Xmx1g
   ```

3. **Check if backend is accessible**:
   ```bash
   curl http://localhost:8080/api/employees
   ```

4. **Run in non-GUI mode**:
   ```bash
   jmeter -n -t test-plan.jmx -l results.jtl
   ```

### Problem: Property-based tests fail

**Symptoms**: JUnit-Quickcheck tests fail with counterexamples

**Solutions**:

1. **Review the counterexample** in test output

2. **Check generators** for valid input generation

3. **Increase trial count** if needed:
   ```java
   @Property(trials = 200)
   ```

4. **Fix the code** if counterexample reveals a bug

## Nexus Issues

### Problem: Nexus returns 401 Unauthorized

**Symptoms**: `mvn deploy` fails with authentication error

**Solutions**:

1. **Run setup script**:
   ```bash
   # Windows
   scripts\setup-nexus-env.bat
   
   # Linux/macOS
   source scripts/setup-nexus-env.sh
   ```

2. **Verify credentials** in `~/.m2/settings.xml`:
   ```xml
   <server>
     <id>nexus-snapshots</id>
     <username>admin</username>
     <password>admin123</password>
   </server>
   ```

3. **Check Nexus is running**:
   ```bash
   curl http://localhost:8081
   ```

### Problem: Nexus not responding

**Symptoms**: Cannot access Nexus UI at http://localhost:8081

**Solutions**:

1. **Wait for Nexus to start** (takes 2-3 minutes):
   ```bash
   docker-compose -f docker-compose.dev.yml logs nexus
   # Wait for: "Started Sonatype Nexus OSS"
   ```

2. **Check container status**:
   ```bash
   docker-compose -f docker-compose.dev.yml ps nexus
   ```

3. **Restart Nexus**:
   ```bash
   docker-compose -f docker-compose.dev.yml restart nexus
   ```

### Problem: Cannot access Nexus UI

**Symptoms**: Login fails or cannot find admin password

**Solutions**:

1. **Get admin password**:
   ```bash
   docker exec -it <nexus-container-name> cat /nexus-data/admin.password
   ```

2. **Login with**:
   - Username: `admin`
   - Password: (from step 1)

3. **Change password** on first login as prompted

## SonarQube Issues

### Problem: SonarQube analysis fails

**Symptoms**: `mvn sonar:sonar` fails

**Solutions**:

1. **Check SonarQube is running**:
   ```bash
   curl http://localhost:9000/api/system/status
   ```

2. **Verify token** (if using SonarCloud):
   ```bash
   # Check SONAR_TOKEN environment variable
   echo $SONAR_TOKEN
   ```

3. **Run analysis manually**:
   ```bash
   cd backend
   mvn clean verify sonar:sonar \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.login=admin \
     -Dsonar.password=admin
   ```

4. **Check SonarQube logs**:
   ```bash
   docker-compose -f docker-compose.dev.yml logs sonarqube
   ```

### Problem: Quality gate fails

**Symptoms**: SonarQube quality gate does not pass

**Solutions**:

1. **View detailed report**:
   - Local: http://localhost:9000/dashboard?id=devops-platform
   - SonarCloud: https://sonarcloud.io/project/overview?id=Sklaid_ProyectoFinal-G5

2. **Common issues and fixes**:
   - **Coverage < 80%**: Write more unit tests
   - **Code smells**: Refactor code to fix quality issues
   - **Security hotspots**: Review and fix security vulnerabilities
   - **Duplications**: Remove duplicate code

3. **Run tests with coverage**:
   ```bash
   # Backend
   cd backend
   mvn clean test jacoco:report
   
   # Frontend
   cd frontend
   npm run test:coverage
   ```

## CI/CD Pipeline Issues

### Problem: Pipeline fails on GitHub Actions

**Symptoms**: Workflow run fails

**Solutions**:

1. **Check workflow logs**:
   - Go to Actions tab → Select failed run → View logs

2. **Run locally with act**:
   ```bash
   # Install act: https://github.com/nektos/act
   act -j build
   ```

3. **Check secrets**:
   - Verify `SONAR_TOKEN`, `NEXUS_USERNAME`, etc. are set in GitHub repository settings

4. **Check branch protection rules**:
   - Ensure required status checks are configured correctly

### Problem: Deployment fails

**Symptoms**: Deployment stage fails in pipeline

**Solutions**:

1. **Check Docker Compose logs**:
   ```bash
   docker-compose -f docker-compose.prod.yml logs
   ```

2. **Verify environment variables**:
   - Check `.env` file or GitHub secrets

3. **Manual rollback**:
   ```bash
   git checkout v1.0.0-STABLE
   docker-compose -f docker-compose.prod.yml up -d --build
   ```

4. **Check disk space**:
   ```bash
   df -h
   ```

## Application Issues

### Problem: Cannot login to application

**Symptoms**: Login fails with valid credentials

**Solutions**:

1. **Check if user exists**:
   ```bash
   docker exec -it <postgres-container> psql -U postgres -d devops
   SELECT * FROM users WHERE username = 'admin';
   ```

2. **Reset admin password**:
   - Run migration: `V3__insert_test_users.sql`
   - Or manually insert user with BCrypt hashed password

3. **Check JWT configuration**:
   - Verify `JWT_SECRET` in `application.properties`
   - Check token expiration time

4. **Check backend logs**:
   ```bash
   docker-compose -f docker-compose.dev.yml logs backend
   ```

### Problem: CORS errors in browser

**Symptoms**: Browser console shows CORS policy errors

**Solutions**:

1. **Check CORS configuration** in `SecurityConfig.java`:
   ```java
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();
       configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
       // ...
   }
   ```

2. **Check API URL** in frontend `.env.local`:
   ```env
   VITE_API_URL=http://localhost:8080/api
   ```

3. **Clear browser cache**:
   - Press `Ctrl+Shift+Delete`
   - Clear cached images and files

4. **Try different browser** or incognito mode

### Problem: 404 errors on API endpoints

**Symptoms**: API calls return 404 Not Found

**Solutions**:

1. **Verify backend is running**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Check API base path**:
   - All endpoints should start with `/api`
   - Example: `http://localhost:8080/api/employees`

3. **Check controller mappings**:
   - Review `@RequestMapping` annotations in controllers

4. **Check application logs**:
   ```bash
   docker-compose -f docker-compose.dev.yml logs backend | grep ERROR
   ```

### Problem: Frontend not loading

**Symptoms**: Blank page or loading spinner

**Solutions**:

1. **Check browser console** for errors (F12)

2. **Verify frontend is running**:
   ```bash
   curl http://localhost:3000
   ```

3. **Check if backend is accessible**:
   ```bash
   curl http://localhost:8080/api/employees
   ```

4. **Clear browser cache and reload**

5. **Check frontend logs**:
   ```bash
   docker-compose -f docker-compose.dev.yml logs frontend
   ```

## Getting Help

If you encounter issues not covered in this guide:

### 1. Check Logs

```bash
# All services
docker-compose -f docker-compose.dev.yml logs -f

# Specific service
docker-compose -f docker-compose.dev.yml logs backend
docker-compose -f docker-compose.dev.yml logs frontend
docker-compose -f docker-compose.dev.yml logs postgres
```

### 2. Check Documentation

- [Architecture Documentation](ARCHITECTURE.md)
- [Nexus Quick Start](Nexus/NEXUS_QUICK_START.md)
- [SonarQube Setup](Sonarqube/SONARQUBE_SETUP.md)
- [Branch Protection Setup](Merge%20docu/BRANCH_PROTECTION_SETUP.md)

### 3. Check GitHub Issues

Search for similar issues in the repository:
https://github.com/Sklaid/ProyectoFinal-G5/issues

### 4. Contact Team

Reach out to the development team for assistance.

## Useful Commands

### Docker Commands

```bash
# Check service status
docker-compose -f docker-compose.dev.yml ps

# Restart specific service
docker-compose -f docker-compose.dev.yml restart backend

# View resource usage
docker stats

# Remove all containers and volumes
docker-compose -f docker-compose.dev.yml down -v

# Clean up Docker system
docker system prune -a
```

### Development Commands

```bash
# Backend
cd backend
mvn clean install          # Build
mvn test                   # Run tests
mvn spring-boot:run        # Run application

# Frontend
cd frontend
npm install                # Install dependencies
npm run dev                # Run dev server
npm test                   # Run tests
npm run build              # Build for production
```

### Database Commands

```bash
# Connect to PostgreSQL
docker exec -it <postgres-container> psql -U postgres -d devops

# List tables
\dt

# Describe table
\d users

# Query data
SELECT * FROM users;

# Exit
\q
```

### Clean Start

If all else fails, clean everything and start fresh:

```bash
# Windows
scripts\cleanup.bat
scripts\start-dev.bat

# Linux/macOS
./scripts/cleanup.sh
./scripts/start-dev.sh
```

---

**Last Updated**: November 2024
