#!/bin/bash

# Nexus Repository Setup Script
# This script configures Nexus Repository Manager with required repositories

set -e

NEXUS_URL="http://localhost:8081"
NEXUS_USER="admin"
NEXUS_PASSWORD="${NEXUS_PASSWORD:-admin123}"  # Default password after initial setup

echo "========================================="
echo "Nexus Repository Setup Script"
echo "========================================="
echo ""

# Wait for Nexus to be ready
echo "Waiting for Nexus to be ready..."
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
    if curl -s -f "${NEXUS_URL}/service/rest/v1/status" > /dev/null 2>&1; then
        echo "✓ Nexus is ready!"
        break
    fi
    attempt=$((attempt + 1))
    echo "  Attempt $attempt/$max_attempts - waiting..."
    sleep 10
done

if [ $attempt -eq $max_attempts ]; then
    echo "✗ Nexus failed to start within expected time"
    exit 1
fi

echo ""
echo "========================================="
echo "Creating Maven Repositories"
echo "========================================="

# Create Maven Releases Repository
echo "Creating Maven Releases repository..."
curl -X POST "${NEXUS_URL}/service/rest/v1/repositories/maven/hosted" \
  -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "maven-releases",
    "online": true,
    "storage": {
      "blobStoreName": "default",
      "strictContentTypeValidation": true,
      "writePolicy": "ALLOW_ONCE"
    },
    "maven": {
      "versionPolicy": "RELEASE",
      "layoutPolicy": "STRICT"
    }
  }' && echo "✓ Maven Releases repository created" || echo "⚠ Maven Releases repository may already exist"

# Create Maven Snapshots Repository
echo "Creating Maven Snapshots repository..."
curl -X POST "${NEXUS_URL}/service/rest/v1/repositories/maven/hosted" \
  -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "maven-snapshots",
    "online": true,
    "storage": {
      "blobStoreName": "default",
      "strictContentTypeValidation": true,
      "writePolicy": "ALLOW"
    },
    "maven": {
      "versionPolicy": "SNAPSHOT",
      "layoutPolicy": "STRICT"
    }
  }' && echo "✓ Maven Snapshots repository created" || echo "⚠ Maven Snapshots repository may already exist"

echo ""
echo "========================================="
echo "Creating NPM Repository"
echo "========================================="

# Create NPM Hosted Repository
echo "Creating NPM hosted repository..."
curl -X POST "${NEXUS_URL}/service/rest/v1/repositories/npm/hosted" \
  -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "npm-hosted",
    "online": true,
    "storage": {
      "blobStoreName": "default",
      "strictContentTypeValidation": true,
      "writePolicy": "ALLOW"
    }
  }' && echo "✓ NPM hosted repository created" || echo "⚠ NPM hosted repository may already exist"

echo ""
echo "========================================="
echo "Creating Docker Repository"
echo "========================================="

# Create Docker Hosted Repository
echo "Creating Docker hosted repository..."
curl -X POST "${NEXUS_URL}/service/rest/v1/repositories/docker/hosted" \
  -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "docker-hosted",
    "online": true,
    "storage": {
      "blobStoreName": "default",
      "strictContentTypeValidation": true,
      "writePolicy": "ALLOW"
    },
    "docker": {
      "v1Enabled": false,
      "forceBasicAuth": true,
      "httpPort": 8082
    }
  }' && echo "✓ Docker hosted repository created" || echo "⚠ Docker hosted repository may already exist"

echo ""
echo "========================================="
echo "Verifying Repositories"
echo "========================================="

# List all repositories
echo "Listing all repositories:"
curl -s -X GET "${NEXUS_URL}/service/rest/v1/repositories" \
  -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  -H "Accept: application/json" | grep -o '"name":"[^"]*"' | cut -d'"' -f4

echo ""
echo "========================================="
echo "Nexus Setup Complete!"
echo "========================================="
echo ""
echo "Repository URLs:"
echo "  Maven Releases:  ${NEXUS_URL}/repository/maven-releases/"
echo "  Maven Snapshots: ${NEXUS_URL}/repository/maven-snapshots/"
echo "  NPM Hosted:      ${NEXUS_URL}/repository/npm-hosted/"
echo "  Docker Hosted:   ${NEXUS_URL}/repository/docker-hosted/"
echo ""
echo "Nexus Web UI:      ${NEXUS_URL}"
echo "Username:          ${NEXUS_USER}"
echo "Password:          ${NEXUS_PASSWORD}"
echo ""
