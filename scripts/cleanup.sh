#!/bin/bash

# Cleanup Development Environment Script
echo "=========================================="
echo "Cleaning Up DevOps Platform Development Environment"
echo "=========================================="
echo "WARNING: This will remove all containers, volumes, and data!"
echo ""

read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Cleanup cancelled."
    exit 0
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed."
    exit 1
fi

echo ""
echo "Stopping and removing containers..."
docker-compose -f docker-compose.dev.yml down

echo ""
echo "Removing volumes..."
docker-compose -f docker-compose.dev.yml down -v

echo ""
echo "Removing orphaned containers..."
docker-compose -f docker-compose.dev.yml down --remove-orphans

echo ""
echo "Pruning Docker system (optional)..."
read -p "Do you want to prune unused Docker resources? (yes/no): " prune

if [ "$prune" = "yes" ]; then
    docker system prune -f
    echo "Docker system pruned."
fi

echo ""
echo "=========================================="
echo "Cleanup completed successfully!"
echo "=========================================="
echo "To start fresh: ./scripts/start-dev.sh"
echo "=========================================="
