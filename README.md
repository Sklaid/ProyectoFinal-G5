# DevOps Enterprise Platform - TechCorp Solutions

[![CI/CD Pipeline](https://github.com/Sklaid/ProyectoFinal-G5/actions/workflows/ci-cd-pipeline.yml/badge.svg)](https://github.com/Sklaid/ProyectoFinal-G5/actions/workflows/ci-cd-pipeline.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sklaid_ProyectoFinal-G5&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Sklaid_ProyectoFinal-G5)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Sklaid_ProyectoFinal-G5&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Sklaid_ProyectoFinal-G5)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)
- [CI/CD Pipeline](#cicd-pipeline)
- [Support](#support)

## Overview

This project implements a complete DevOps enterprise platform for **TechCorp Solutions**, demonstrating the transformation from traditional software development to a modern DevOps culture.

**Key Components:**
- Full-stack web application with authentication and employee management (CRUD)
- Automated CI/CD pipeline with 20+ stages
- Containerized infrastructure using Docker
- Comprehensive testing at all levels
- Code quality analysis with SonarQube
- Artifact management with Nexus Repository Manager

**Latest Release**: v1.0.0-STABLE | **Status**: Production Ready ✅

## ✨ Features

### Application Features
- 🔐 JWT-based authentication with BCrypt password hashing
- 👥 Complete CRUD operations for employee management
- 📊 Rich UI controls (radio buttons, checkboxes, comboboxes, date pickers, sortable tables)
- 🎨 Modern Material-UI components with responsive design
- ✅ Client and server-side validation

### DevOps Features
- 🚀 Automated CI/CD with GitHub Actions (20+ stages)
- 🐳 Full containerization with Docker
- 📦 Nexus Repository for artifact management
- 🔍 SonarQube code quality analysis
- 🧪 Multi-level testing (unit, integration, API, functional, performance)
- 🔒 Security scanning (OWASP, npm audit)
- 🔄 Automatic rollback on deployment failures


## 🛠️ Technology Stack

### Backend
- Java 17, Spring Boot 3.x, Spring Security
- PostgreSQL 15, Flyway migrations
- Maven, JUnit 5, Mockito, JUnit-Quickcheck

### Frontend
- React 18, TypeScript, Material-UI
- Vite, React Router, Axios
- Vitest, React Testing Library

### DevOps & Infrastructure
- Docker & Docker Compose
- GitHub Actions (CI/CD)
- SonarQube / SonarCloud
- Nexus Repository Manager
- Newman (API testing)
- Selenium WebDriver (E2E testing)
- Apache JMeter (Performance testing)

## 📋 Prerequisites

| Software | Version | Download |
|----------|---------|----------|
| Java JDK | 17+ | [Adoptium](https://adoptium.net/temurin/releases/) |
| Node.js | 18 LTS | [nodejs.org](https://nodejs.org/) |
| Docker Desktop | 20.10+ | [docker.com](https://www.docker.com/products/docker-desktop/) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Git | 2.30+ | [git-scm.com](https://git-scm.com/downloads) |

**System Requirements:**
- Windows 10/11
- RAM: 8GB minimum (16GB recommended)
- Disk Space: 20GB free
- Available Ports: 3000, 8080, 5432, 9000, 8081

## 🚀 Quick Start

### 1. Clone the Repository
```cmd
git clone https://github.com/Sklaid/ProyectoFinal-G5.git
cd ProyectoFinal-G5
```

### 2. Start Docker Desktop
Ensure Docker Desktop is running.

### 3. Start Development Environment
```cmd
scripts\start-dev.bat
```

### 4. Access the Application

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | admin / admin123 |
| Backend API | http://localhost:8080/api | - |
| SonarQube | http://localhost:9000 | admin / admin |
| Nexus | http://localhost:8081 | admin / (see console) |
| PostgreSQL | localhost:5432 | postgres / postgres |

### 5. Stop Environment
```cmd
scripts\stop-dev.bat
```


## 📚 Documentation

Comprehensive documentation is available in the `Documentacion/` directory:

### Getting Started
- **[Setup Guide](Documentacion/SETUP_GUIDE.md)** - Complete installation and setup instructions
- **[Troubleshooting Guide](Documentacion/TROUBLESHOOTING.md)** - Solutions to common issues

### Architecture & Design
- **[Architecture Documentation](Documentacion/ARCHITECTURE.md)** - System architecture and components
- **[Technical Justifications](Documentacion/TECHNICAL_JUSTIFICATIONS.md)** - Technology selection rationale

### Process & Organization
- **[Organizational Model](Documentacion/ORGANIZATIONAL_MODEL.md)** - Team structure and roles
- **[Value Stream Mapping](Documentacion/VALUE_STREAM_MAPPING.md)** - Process improvement analysis
- **[User Stories & DoD](Documentacion/USER_STORIES_AND_DOD.md)** - User stories and Definition of Done

### DevOps Maturity
- **[DSOOM Assessment](Documentacion/DSOOM/)** - DevOps maturity evaluation

### Setup Guides
- **[Nexus Quick Start](Documentacion/Nexus/NEXUS_QUICK_START.md)** - Artifact repository setup
- **[SonarQube Setup](Documentacion/Sonarqube/SONARQUBE_SETUP.md)** - Code quality analysis setup
- **[Branch Protection Setup](Documentacion/Merge%20docu/BRANCH_PROTECTION_SETUP.md)** - Git workflow configuration

### Specifications
- **[Design Document](.kiro/specs/devops-enterprise-platform/design.md)** - Detailed system design
- **[Requirements](.kiro/specs/devops-enterprise-platform/requirements.md)** - Functional requirements
- **[Tasks](.kiro/specs/devops-enterprise-platform/tasks.md)** - Implementation task list

## 📁 Project Structure

```
ProyectoFinal-G5/
├── backend/                 # Spring Boot REST API
│   ├── src/main/java/       # Java source code
│   ├── src/test/            # Test code
│   └── pom.xml              # Maven configuration
├── frontend/                # React + TypeScript
│   ├── src/                 # Frontend source code
│   └── package.json         # NPM dependencies
├── e2e-tests/               # Selenium functional tests
├── jmeter-tests/            # JMeter performance tests
├── postman/                 # Postman API tests
├── scripts/                 # Automation scripts
├── Documentacion/           # Project documentation
├── docker-compose.dev.yml   # Development environment
└── docker-compose.prod.yml  # Production environment
```


## 🔄 Development Workflow

This project follows a **Simplified Git Flow** strategy.

### Branch Strategy

| Branch | Purpose | Deployment |
|--------|---------|------------|
| `main` | Production-ready code | Production |
| `develop` | Active development | Development/Pre-prod |
| `release/*` | Release preparation | Staging |

**Important Rules:**
- ❌ NEVER commit directly to `main`
- ✅ ALL development work happens on `develop`
- ✅ Use Pull Requests to merge to `main`

### Daily Development
```cmd
git checkout develop
git pull origin develop
# ... make changes ...
git add .
git commit -m "Task X.Y: Description"
git push origin develop
```

### Creating a Release
```cmd
git checkout -b release/1.0.0 develop
# ... bump versions, final testing ...
git push origin release/1.0.0
# Create PR to main
```

### Commit Message Format
```
Task X.Y: Brief description

Optional longer description
```

**Examples:**
- `Task 1.1: Initialize backend project with Spring Boot`
- `Task 3.2: Implement JWT token provider`
- `Fix: Resolve CORS issue in API client`

## 🔄 CI/CD Pipeline

The project uses **GitHub Actions** with a comprehensive 20+ stage pipeline.

### Pipeline Stages

**All Branches (1-20):**
1-5. Build & Compile (Backend + Frontend)
6-10. Code Quality & Security Analysis
11-15. Pre-production Testing (Integration, API, E2E)
16-20. Performance Testing & Artifact Publishing

**Main Branch Only (21-27):**
21-25. Canary & Production Deployment
26-27. Monitoring & Notifications

### Triggering the Pipeline

**Automatic:**
- Push to `develop`: Runs stages 1-20
- Push to `main`: Runs all stages 1-27
- Pull Request: Runs stages 1-10

**Manual:**
```cmd
# Via GitHub UI: Actions → CI/CD Pipeline → Run workflow
```

### Viewing Results
- **GitHub Actions**: https://github.com/Sklaid/ProyectoFinal-G5/actions
- **SonarCloud**: https://sonarcloud.io/project/overview?id=Sklaid_ProyectoFinal-G5
- **Nexus**: http://localhost:8081


## 🧪 Running Tests

### Backend Tests
```cmd
cd backend
mvn test                              # Unit tests
mvn test -Dtest="*PropertiesTest"     # Property-based tests
mvn clean test jacoco:report          # With coverage
```

### Frontend Tests
```cmd
cd frontend
npm test                    # Unit tests
npm run test:coverage       # With coverage
```

### API Tests (Newman)
```cmd
npm install -g newman newman-reporter-htmlextra
newman run postman/auth.postman_collection.json -e postman/dev.env.json
postman\run-tests.bat      # Run all API tests
```

### Functional Tests (Selenium)
```cmd
cd e2e-tests
mvn test
```

### Performance Tests (JMeter)
```cmd
cd jmeter-tests
jmeter -n -t auth-load-test.jmx -l results/auth-results.jtl -e -o reports/auth-report
jmeter-tests\run-all-tests.bat    # Run all performance tests
```

### Code Quality (SonarQube)
```cmd
scripts\sonar-local-analysis.bat
```

## 🚢 Deployment

### Local Deployment
```cmd
# Development
docker-compose -f docker-compose.dev.yml up -d

# Production
docker-compose -f docker-compose.prod.yml up -d
```

### Nexus Artifact Deployment
```cmd
scripts\setup-nexus-env.bat
cd backend
mvn deploy -DskipTests
```

### CI/CD Pipeline Deployment

**Develop Branch:**
```cmd
git checkout develop
git add .
git commit -m "Task X.Y: Description"
git push origin develop
```

**Main Branch (Production):**
1. Create PR from `develop` to `main`
2. Wait for status checks
3. Review and approve
4. Merge PR → Triggers production deployment

## 📞 Support

### Documentation
- Check `Documentacion/` directory for detailed guides
- [Setup Guide](Documentacion/SETUP_GUIDE.md)
- [Troubleshooting Guide](Documentacion/TROUBLESHOOTING.md)

### Getting Help
1. Check logs: `docker-compose -f docker-compose.dev.yml logs`
2. Review documentation
3. Check GitHub Issues
4. Contact development team

### Useful Commands
```cmd
# Check service status
docker-compose -f docker-compose.dev.yml ps

# Restart service
docker-compose -f docker-compose.dev.yml restart backend

# Clean everything
scripts\cleanup.bat
scripts\start-dev.bat
```

## 👥 Team

- **Development Team**: Backend, Frontend, Full-stack developers
- **DevOps Team**: DevOps engineers, Infrastructure engineers
- **QA Team**: QA engineers, Test automation engineers
- **Management**: Product Owner, Scrum Master, Technical Lead

## 📄 License

This is an educational project developed for TechCorp Solutions as part of a DevOps transformation initiative.

---

**Built with ❤️ by TechCorp Solutions DevOps Team**

**Last Updated**: November 2024
