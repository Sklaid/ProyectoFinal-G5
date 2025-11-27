#!/bin/bash

# Stop Development Environment Script
echo "=========================================="
echo "Stopping DevOps Platform Development Environment"
echo "=========================================="

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed."
    exit 1
fi

echo ""
echo "Stopping services..."
docker-compose -f docker-compose.dev.yml stop

echo ""
echo "Services stopped successfully."
echo ""
echo "To start again: ./scripts/start-dev.sh"
echo "To remove containers and volumes: ./scripts/cleanup.sh"
echo "=========================================="
