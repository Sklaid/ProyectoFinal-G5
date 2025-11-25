# DevOps Enterprise Platform - TechCorp Solutions

## Overview

This project implements a complete DevOps enterprise platform for TechCorp Solutions, demonstrating the transformation from traditional software development to a modern DevOps culture. The platform includes a full-stack web application with automated CI/CD pipeline, containerized infrastructure, and comprehensive testing at all levels.

## Project Structure

```
.
├── backend/                 # Spring Boot REST API
│   ├── src/main/java/       # Java source code
│   ├── src/main/resources/  # Configuration and migrations
│   ├── src/test/            # Test code
│   ├── Dockerfile           # Backend container image
│   └── pom.xml              # Maven configuration
├── frontend/                # React + TypeScript application
│   ├── src/                 # Frontend source code
│   ├── Dockerfile           # Frontend container image
│   ├── nginx.conf           # Nginx configuration
│   ├── package.json         # NPM dependencies
│   └── vite.config.ts       # Vite configuration
├── scripts/                 # Development automation scripts
│   ├── start-dev.sh/.bat    # Start development environment
│   ├── stop-dev.sh/.bat     # Stop services
│   └── cleanup.sh/.bat      # Clean up containers and volumes
├── docker-compose.dev.yml   # Development environment setup
├── docker-compose.prod.yml  # Production environment setup
└── .kiro/specs/             # Project specifications and documentation
```

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.x
- PostgreSQL 15
- Flyway (Database migrations)
- Maven

### Frontend
- React 18
- TypeScript
- Material-UI
- Vite

### DevOps Tools
- Docker & Docker Compose
- GitHub Actions (CI/CD)
- SonarQube (Code quality)
- Nexus Repository Manager
- Newman (API testing)
- Selenium (Functional testing)
- JMeter (Performance testing)

## Prerequisites

- Java 17 or higher
- Node.js 18+ LTS
- Docker Desktop (Windows/macOS) or Docker Engine (Linux)
- Maven 3.8+
- Git 2.30+

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd ProyectoFinal-G5
```

### 2. Start Docker Desktop

Ensure Docker Desktop is running before proceeding.

### 3. Run the development environment

**Windows (CMD/PowerShell):**
```cmd
scripts\start-dev.bat
scripts\stop-dev.bat
```

**Linux/Mac/Git Bash:**
```bash
./scripts/start-dev.sh
./scripts/stop-dev.sh
```

### 4. Access the application

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- SonarQube: http://localhost:9000
- Nexus: http://localhost:8081

## Development Workflow

This project follows a simplified Git Flow strategy:

### Branch Strategy

- `main` - Production-ready code (NEVER commit directly here)
- `develop` - Active development branch (ALL development work happens here)
- `release/*` - Release preparation branches (created only at the end)

### Workflow

1. All development work is done on the `develop` branch
2. Make frequent commits with descriptive messages: `git commit -m "Task X.Y: Brief description"`
3. After completing all tasks, create a release branch: `git checkout -b release/1.0.0 develop`
4. Perform final testing and validation on the release branch
5. Merge to `main` and tag: `git checkout main && git merge release/1.0.0 && git tag v1.0.0`
6. Merge back to `develop`: `git checkout develop && git merge release/1.0.0`

### Commit Message Format

Use the format: `Task X.Y: Brief description`

Examples:
- `Task 1.1: Initialize backend project with Spring Boot`
- `Task 3.1: Create User entity and repository`
- `Task 7.3: Implement Login page with validation`

## Testing

```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test

# API tests with Newman
newman run postman/auth.postman_collection.json

# Functional tests with Selenium
cd e2e-tests
mvn test

# Performance tests with JMeter
jmeter -n -t jmeter/employee-api-load-test.jmx -l results.jtl
```

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment. The pipeline includes:

1. Build and compile
2. Unit tests
3. SonarQube analysis
4. Security scanning
5. Pre-production deployment
6. Integration tests
7. API tests (Newman)
8. Functional tests (Selenium)
9. Performance tests (JMeter)
10. Production deployment

## Documentation

Detailed documentation can be found in the `Documentacion/` directory:

- Architecture documentation
- API documentation
- Deployment guides
- Value Stream Mapping
- DevOps maturity assessment (DSOOM)

### 🔧 Nexus Deploy

**Setup local**:
```bash
scripts\setup-nexus-env.bat  # Windows
source scripts/setup-nexus-env.sh  # Linux/Mac
cd backend && mvn deploy -DskipTests
```

**Documentación**: Ver `Documentacion/Nexus/NEXUS_QUICK_START.md`

### 🚀 Production Deployment

**Flujo con Pull Request**:
1. Desarrollar en `develop` → Pipeline ejecuta hasta Tag STABLE
2. Crear PR de `develop` → `main`
3. Merge PR → Pipeline ejecuta Canary → Production → Tag GOLD

**Documentación**:
- [Branch Protection Setup](Documentacion/BRANCH_PROTECTION_SETUP.md)
- [Pull Request Workflow](Documentacion/PULL_REQUEST_WORKFLOW.md)

## License

This is an educational project for TechCorp Solutions.

## Contributors

- Development Team
- DevOps Team
- QA Team
