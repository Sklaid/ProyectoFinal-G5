#!/bin/bash

# Start Development Environment Script
echo "=========================================="
echo "Starting DevOps Platform Development Environment"
echo "=========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker Desktop."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed."
    exit 1
fi

echo ""
echo "Starting services with docker-compose..."
docker-compose -f ../docker-compose.dev.yml up -d

echo ""
echo "Waiting for services to be healthy..."
sleep 10

# Function to check service health
check_health() {
    local service=$1
    local url=$2
    local max_attempts=30
    local attempt=1

    echo "Checking $service health..."
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$url" > /dev/null 2>&1; then
            echo "✓ $service is healthy"
            return 0
        fi
        echo "  Attempt $attempt/$max_attempts - waiting for $service..."
        sleep 5
        attempt=$((attempt + 1))
    done
    
    echo "✗ $service failed to become healthy"
    return 1
}

# Check PostgreSQL
echo ""
if docker exec devops-postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "✓ PostgreSQL is healthy"
else
    echo "✗ PostgreSQL is not healthy"
fi

# Check Backend
check_health "Backend" "http://localhost:8080/actuator/health"

# Check Frontend
check_health "Frontend" "http://localhost:3000"

# Check SonarQube
check_health "SonarQube" "http://localhost:9000/api/system/status"

# Check Nexus
check_health "Nexus" "http://localhost:8081/service/rest/v1/status"

echo ""
echo "=========================================="
echo "Development Environment Status"
echo "=========================================="
echo "Frontend:   http://localhost:3000 (admin/admin123)"
echo "Backend:    http://localhost:8080 (API - use Postman)"
echo "SonarQube:  http://localhost:9000 (admin/admin)"
echo "Nexus:      http://localhost:8081 (admin/see admin.password)"
echo "PostgreSQL: localhost:5432 (postgres/postgres)"
echo ""
echo "Login Credentials:"
echo "  Frontend/Backend: admin / admin123"
echo "  SonarQube: admin / admin (change on first login)"
echo "  Nexus: Get password with: docker exec devops-nexus cat /nexus-data/admin.password"
echo ""
echo "To view logs: docker-compose -f docker-compose.dev.yml logs -f [service]"
echo "To stop: ./scripts/stop-dev.sh"
echo "=========================================="
