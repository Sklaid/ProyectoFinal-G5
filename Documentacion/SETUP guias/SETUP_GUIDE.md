# Setup Guide - DevOps Enterprise Platform

This comprehensive guide will walk you through setting up the DevOps Enterprise Platform from scratch.

## Table of Contents

- [Prerequisites Installation](#prerequisites-installation)
- [Project Setup](#project-setup)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Docker Environment Setup](#docker-environment-setup)
- [Testing Setup](#testing-setup)
- [DevOps Tools Setup](#devops-tools-setup)
- [IDE Configuration](#ide-configuration)
- [Verification](#verification)

## Prerequisites Installation

### 1. Install Java Development Kit (JDK)

**Recommended**: Eclipse Temurin JDK 21 (or OpenJDK 17 minimum)

**Windows**:
1. Download from: https://adoptium.net/temurin/releases/
2. Select: Version 21 (LTS), Windows, x64, JDK
3. Run installer and follow prompts
4. Set JAVA_HOME environment variable:
   ```cmd
   setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21.0.x-hotspot"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```

**Linux (Ubuntu/Debian)**:
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**macOS**:
```bash
brew install openjdk@17
```

**Verify Installation**:
```bash
java -version
javac -version
echo $JAVA_HOME  # Linux/macOS
echo %JAVA_HOME%  # Windows
```

### 2. Install Apache Maven

**Windows**:
1. Download from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   ```cmd
   setx PATH "%PATH%;C:\Program Files\Apache\maven\bin"
   ```

**Linux (Ubuntu/Debian)**:
```bash
sudo apt install maven
```

**macOS**:
```bash
brew install maven
```

**Verify Installation**:
```bash
mvn -version
```

### 3. Install Node.js and npm

**Recommended**: Node.js 18 LTS

**Windows/macOS**:
1. Download from: https://nodejs.org/
2. Run installer and follow prompts

**Linux (Ubuntu/Debian)**:
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

**Verify Installation**:
```bash
node --version  # Should show v18.x.x
npm --version   # Should show 8.x.x or higher
```

**Update npm** (optional but recommended):
```bash
npm install -g npm@latest
```

### 4. Install Docker Desktop

**Windows**:
1. Download from: https://www.docker.com/products/docker-desktop/
2. Run installer
3. Restart computer
4. Start Docker Desktop
5. Verify in system tray

**macOS**:
1. Download from: https://www.docker.com/products/docker-desktop/
2. Drag Docker.app to Applications
3. Launch Docker Desktop
4. Grant permissions when prompted

**Linux (Ubuntu)**:
```bash
# Install Docker Engine
sudo apt-get update
sudo apt-get install ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker
```

**Verify Installation**:
```bash
docker --version
docker-compose --version
docker run hello-world
```

**Configure Docker Resources** (Windows/macOS):
- Open Docker Desktop → Settings → Resources
- Set Memory: 8GB (minimum 4GB)
- Set CPUs: 4 (minimum 2)
- Set Disk: 50GB

### 5. Install Git

**Windows**:
1. Download from: https://git-scm.com/download/win
2. Run installer
3. Use recommended settings

**Linux (Ubuntu/Debian)**:
```bash
sudo apt install git
```

**macOS**:
```bash
brew install git
```

**Configure Git**:
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

**Verify Installation**:
```bash
git --version
```

### 6. Install Optional Tools

#### Newman (for API testing)
```bash
npm install -g newman newman-reporter-htmlextra
newman --version
```

#### Apache JMeter (for performance testing)
1. Download from: https://jmeter.apache.org/download_jmeter.cgi
2. Extract to desired location (e.g., `C:\jmeter` or `/opt/jmeter`)
3. Add to PATH:
   ```bash
   # Windows
   setx PATH "%PATH%;C:\jmeter\bin"
   
   # Linux/macOS
   export PATH=$PATH:/opt/jmeter/bin
   ```
4. Verify: `jmeter --version`

## Project Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Sklaid/ProyectoFinal-G5.git
cd ProyectoFinal-G5
```

### 2. Verify Project Structure

```bash
# Windows
dir

# Linux/macOS
ls -la
```

You should see:
- `backend/` - Spring Boot application
- `frontend/` - React application
- `docker-compose.dev.yml` - Development environment
- `docker-compose.prod.yml` - Production environment
- `scripts/` - Automation scripts

### 3. Check Available Ports

Ensure these ports are available:
- 3000 (Frontend)
- 8080 (Backend)
- 5432 (PostgreSQL)
- 9000 (SonarQube)
- 8081 (Nexus)

**Check ports** (Windows):
```cmd
netstat -ano | findstr :3000
netstat -ano | findstr :8080
netstat -ano | findstr :5432
```

**Check ports** (Linux/macOS):
```bash
lsof -i :3000
lsof -i :8080
lsof -i :5432
```

## Backend Setup

### 1. Navigate to Backend Directory

```bash
cd backend
```

### 2. Install Dependencies

```bash
mvn clean install
```

This will:
- Download all Maven dependencies
- Compile the code
- Run tests
- Package the application

**Expected output**: `BUILD SUCCESS`

### 3. Configure Application Properties

The default configuration in `src/main/resources/application.properties` should work with Docker:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/devops
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# JWT Configuration
jwt.secret=your-secret-key-change-in-production
jwt.expiration=86400000
```

### 4. Verify Database Migrations

Check migration files in `src/main/resources/db/migration/`:
- `V1__create_users_table.sql`
- `V2__create_employees_table.sql`
- `V3__insert_test_users.sql`

### 5. Run Backend (Optional - for testing without Docker)

```bash
mvn spring-boot:run
```

Backend will start on: http://localhost:8080

**Test health endpoint**:
```bash
curl http://localhost:8080/actuator/health
```

Press `Ctrl+C` to stop.

## Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd frontend
```

### 2. Install Dependencies

```bash
npm install
```

This will download all npm packages defined in `package.json`.

**Expected output**: No errors, packages installed successfully

### 3. Configure Environment Variables

Create `.env.local` file (optional):

```env
VITE_API_URL=http://localhost:8080/api
```

### 4. Verify Configuration Files

Check these files exist:
- `vite.config.ts` - Vite configuration
- `tsconfig.json` - TypeScript configuration
- `package.json` - Dependencies and scripts

### 5. Run Frontend (Optional - for testing without Docker)

```bash
npm run dev
```

Frontend will start on: http://localhost:3000

Press `Ctrl+C` to stop.

## Docker Environment Setup

### 1. Review Docker Compose Files

**Development** (`docker-compose.dev.yml`):
- PostgreSQL database
- Backend (Spring Boot)
- Frontend (React + Nginx)
- SonarQube
- Nexus Repository Manager

**Production** (`docker-compose.prod.yml`):
- Optimized for production
- No development tools

### 2. Start Development Environment

**Windows**:
```cmd
scripts\start-dev.bat
```

**Linux/macOS**:
```bash
chmod +x scripts/start-dev.sh
./scripts/start-dev.sh
```

The script will:
1. Start all Docker containers
2. Wait for services to be healthy
3. Run database migrations
4. Display access URLs

**Expected output**:
```
✅ All services are healthy!
🌐 Frontend: http://localhost:3000
🔧 Backend API: http://localhost:8080
📊 SonarQube: http://localhost:9000
📦 Nexus: http://localhost:8081
🗄️  PostgreSQL: localhost:5432
```

### 3. Verify Services

**Check container status**:
```bash
docker-compose -f docker-compose.dev.yml ps
```

All services should show status: `Up` or `Up (healthy)`

**Check logs**:
```bash
# All services
docker-compose -f docker-compose.dev.yml logs

# Specific service
docker-compose -f docker-compose.dev.yml logs backend
docker-compose -f docker-compose.dev.yml logs frontend
```

### 4. Access Services

| Service | URL | Default Credentials |
|---------|-----|---------------------|
| Frontend | http://localhost:3000 | - |
| Backend API | http://localhost:8080/api | - |
| SonarQube | http://localhost:9000 | admin / admin |
| Nexus | http://localhost:8081 | admin / (see below) |
| PostgreSQL | localhost:5432 | postgres / postgres |

**Get Nexus admin password**:
```bash
docker exec -it <nexus-container-name> cat /nexus-data/admin.password
```

### 5. Test the Application

1. Open browser: http://localhost:3000
2. Login with default credentials:
   - Username: `admin`
   - Password: `admin123`
3. Navigate to Employees page
4. Try creating, editing, and deleting employees

### 6. Stop Environment

**Windows**:
```cmd
scripts\stop-dev.bat
```

**Linux/macOS**:
```bash
./scripts/stop-dev.sh
```

## Testing Setup

### 1. Backend Tests

**Run all tests**:
```bash
cd backend
mvn test
```

**Run specific test**:
```bash
mvn test -Dtest=AuthServiceTest
```

**Run with coverage**:
```bash
mvn clean test jacoco:report
```

**View coverage report**:
- Open: `backend/target/site/jacoco/index.html`

### 2. Frontend Tests

**Run all tests**:
```bash
cd frontend
npm test
```

**Run with coverage**:
```bash
npm run test:coverage
```

**View coverage report**:
- Open: `frontend/coverage/index.html`

### 3. API Tests (Newman)

**Install Newman** (if not already installed):
```bash
npm install -g newman newman-reporter-htmlextra
```

**Run tests**:
```bash
# Authentication tests
newman run postman/auth.postman_collection.json -e postman/dev.env.json

# Employee API tests
newman run postman/employees.postman_collection.json -e postman/dev.env.json
```

**Generate HTML report**:
```bash
newman run postman/auth.postman_collection.json \
  -e postman/dev.env.json \
  -r htmlextra \
  --reporter-htmlextra-export reports/auth-report.html
```

### 4. Functional Tests (Selenium)

**Prerequisites**:
- Chrome browser installed
- ChromeDriver (managed automatically)

**Run tests**:
```bash
cd e2e-tests
mvn test
```

**View screenshots** (on failure):
- Location: `e2e-tests/screenshots/`

### 5. Performance Tests (JMeter)

**Prerequisites**:
- JMeter installed and in PATH

**Run tests**:
```bash
cd jmeter-tests
jmeter -n -t auth-load-test.jmx -l results/auth-results.jtl -e -o reports/auth-report
```

**View report**:
- Open: `jmeter-tests/reports/auth-report/index.html`

## DevOps Tools Setup

### 1. SonarQube Setup

**Access SonarQube**:
- URL: http://localhost:9000
- Default credentials: admin / admin
- Change password on first login

**Run analysis**:

**Backend**:
```bash
cd backend
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=<your-new-password>
```

**Frontend**:
```bash
cd frontend
npm run test:coverage
npx sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=<your-new-password>
```

**Or use script**:
```bash
# Windows
scripts\sonar-local-analysis.bat

# Linux/macOS
./scripts/sonar-local-analysis.sh
```

### 2. Nexus Repository Setup

**Access Nexus**:
- URL: http://localhost:8081
- Get admin password:
  ```bash
  docker exec -it <nexus-container> cat /nexus-data/admin.password
  ```
- Login and change password

**Configure Maven**:

**Windows**:
```cmd
scripts\setup-nexus-env.bat
```

**Linux/macOS**:
```bash
source scripts/setup-nexus-env.sh
```

This creates `~/.m2/settings.xml` with Nexus configuration.

**Deploy artifact**:
```bash
cd backend
mvn deploy -DskipTests
```

**Verify**:
- Open: http://localhost:8081/#browse/browse:maven-snapshots

For detailed setup, see: [Nexus Quick Start](Nexus/NEXUS_QUICK_START.md)

### 3. GitHub Actions Setup

**Prerequisites**:
- GitHub repository
- GitHub account with admin access

**Configure secrets**:
1. Go to repository → Settings → Secrets and variables → Actions
2. Add secrets:
   - `SONAR_TOKEN` - SonarCloud token
   - `NEXUS_USERNAME` - Nexus username
   - `NEXUS_PASSWORD` - Nexus password

**Trigger pipeline**:
```bash
git checkout develop
git add .
git commit -m "Task X.Y: Description"
git push origin develop
```

**View pipeline**:
- Go to repository → Actions tab

For detailed setup, see: [Branch Protection Setup](Merge%20docu/BRANCH_PROTECTION_SETUP.md)

## IDE Configuration

### IntelliJ IDEA

1. **Open Project**:
   - File → Open → Select project root directory

2. **Import as Maven Project**:
   - IntelliJ should auto-detect `pom.xml`
   - If not: Right-click `pom.xml` → Add as Maven Project

3. **Enable Annotation Processing**:
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Install Plugins**:
   - Lombok
   - Docker
   - Database Navigator

5. **Configure JDK**:
   - File → Project Structure → Project
   - Set SDK to Java 17 or 21

### Visual Studio Code

1. **Open Project**:
   - File → Open Folder → Select project root

2. **Install Extensions**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - ESLint
   - Prettier - Code formatter
   - Docker
   - PostgreSQL

3. **Configure Java**:
   - Press `Ctrl+Shift+P`
   - Type "Java: Configure Java Runtime"
   - Set Java 17 or 21

4. **Configure Settings** (`.vscode/settings.json`):
   ```json
   {
     "java.configuration.updateBuildConfiguration": "automatic",
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode"
   }
   ```

## Verification

### 1. Verify Prerequisites

Run this verification script:

```bash
# Java
java -version
javac -version
echo $JAVA_HOME

# Maven
mvn -version

# Node.js and npm
node --version
npm --version

# Docker
docker --version
docker-compose --version
docker ps

# Git
git --version
```

All commands should execute without errors.

### 2. Verify Project Build

```bash
# Backend
cd backend
mvn clean install
# Should show: BUILD SUCCESS

# Frontend
cd frontend
npm install
npm run build
# Should complete without errors
```

### 3. Verify Docker Environment

```bash
# Start environment
scripts\start-dev.bat  # Windows
./scripts/start-dev.sh  # Linux/macOS

# Check services
docker-compose -f docker-compose.dev.yml ps
# All services should be "Up" or "Up (healthy)"

# Test endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:3000
```

### 4. Verify Tests

```bash
# Backend tests
cd backend
mvn test
# Should show: Tests run: X, Failures: 0, Errors: 0

# Frontend tests
cd frontend
npm test
# Should show: Tests passed
```

### 5. Verify Application

1. Open browser: http://localhost:3000
2. Login with: admin / admin123
3. Navigate to Employees page
4. Create a new employee
5. Edit the employee
6. Delete the employee

All operations should work without errors.

## Next Steps

After completing the setup:

1. **Read Documentation**:
   - [Architecture Documentation](ARCHITECTURE.md)
   - [Development Workflow](../README.md#development-workflow)
   - [Testing Strategy](../README.md#running-tests)

2. **Explore the Code**:
   - Backend: `backend/src/main/java/com/techcorp/devops/`
   - Frontend: `frontend/src/`

3. **Run Tests**:
   - Unit tests
   - Integration tests
   - API tests
   - Functional tests
   - Performance tests

4. **Start Development**:
   - Follow Git Flow workflow
   - Create feature branches
   - Write tests
   - Submit pull requests

## Troubleshooting

If you encounter any issues during setup, refer to:
- [Troubleshooting Guide](TROUBLESHOOTING.md)

Common issues:
- Port conflicts
- Docker not starting
- Build failures
- Test failures
- Connection refused errors

## Support

For additional help:
- Check documentation in `Documentacion/` directory
- Review GitHub Issues
- Contact development team

---

**Last Updated**: November 2024
