#!/bin/bash

# Setup Maven Settings for Nexus
# This script helps configure Maven to use Nexus Repository Manager

set -e

MAVEN_HOME_DIR="$HOME/.m2"
SETTINGS_FILE="$MAVEN_HOME_DIR/settings.xml"
TEMPLATE_FILE="backend/settings.xml.template"

echo "========================================="
echo "Maven Nexus Configuration Setup"
echo "========================================="
echo ""

# Check if .m2 directory exists
if [ ! -d "$MAVEN_HOME_DIR" ]; then
    echo "Creating Maven home directory: $MAVEN_HOME_DIR"
    mkdir -p "$MAVEN_HOME_DIR"
fi

# Check if settings.xml already exists
if [ -f "$SETTINGS_FILE" ]; then
    echo "⚠ Warning: settings.xml already exists at $SETTINGS_FILE"
    echo ""
    read -p "Do you want to backup the existing file? (Y/N): " BACKUP
    if [[ "$BACKUP" =~ ^[Yy]$ ]]; then
        BACKUP_FILE="${SETTINGS_FILE}.backup.$(date +%Y%m%d_%H%M%S)"
        cp "$SETTINGS_FILE" "$BACKUP_FILE"
        echo "✓ Backup created: $BACKUP_FILE"
    fi
    echo ""
    read -p "Do you want to overwrite the existing settings.xml? (Y/N): " OVERWRITE
    if [[ ! "$OVERWRITE" =~ ^[Yy]$ ]]; then
        echo "Setup cancelled. No changes made."
        exit 0
    fi
fi

# Copy template to settings.xml
echo "Copying settings template..."
cp "$TEMPLATE_FILE" "$SETTINGS_FILE"
echo "✓ Settings file created: $SETTINGS_FILE"

echo ""
echo "========================================="
echo "Configuration Complete!"
echo "========================================="
echo ""
echo "Maven settings file location: $SETTINGS_FILE"
echo ""
echo "IMPORTANT: Set environment variables for Nexus credentials:"
echo ""
echo "  Linux/Mac:"
echo "    export NEXUS_USERNAME=admin"
echo "    export NEXUS_PASSWORD=admin123"
echo ""
echo "  Add to ~/.bashrc or ~/.zshrc for permanent configuration:"
echo "    echo 'export NEXUS_USERNAME=admin' >> ~/.bashrc"
echo "    echo 'export NEXUS_PASSWORD=admin123' >> ~/.bashrc"
echo "    source ~/.bashrc"
echo ""
echo "========================================="
echo "Testing Maven Configuration"
echo "========================================="
echo ""
echo "To test the configuration, run:"
echo "  cd backend"
echo "  mvn deploy -DskipTests"
echo ""
