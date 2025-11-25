#!/bin/bash
# Setup Nexus Environment Variables (Linux/Mac)

echo "========================================="
echo "Nexus Environment Setup"
echo "========================================="
echo ""

echo "Setting environment variables for current session..."
export NEXUS_USERNAME=admin
export NEXUS_PASSWORD=admin123

echo "✅ Environment variables set for current session"
echo ""

echo "To make these permanent, add to your ~/.bashrc or ~/.zshrc:"
echo "  export NEXUS_USERNAME=admin"
echo "  export NEXUS_PASSWORD=admin123"
echo ""
echo "Then run: source ~/.bashrc"
echo ""

echo "========================================="
echo "Ready to deploy!"
echo "========================================="
echo ""
echo "To deploy:"
echo "  cd backend"
echo "  mvn deploy -DskipTests"
echo ""
