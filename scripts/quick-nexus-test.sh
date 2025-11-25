#!/bin/bash

# Quick Nexus Test Script
# Prueba rápida de conectividad y permisos de Nexus

set -e

NEXUS_URL="${NEXUS_URL:-http://localhost:8081}"
NEXUS_USER="${NEXUS_USER:-admin}"
NEXUS_PASSWORD="${NEXUS_PASSWORD:-admin123}"

echo "🔍 Quick Nexus Test"
echo "==================="
echo ""

# Test 1: Connectivity
echo "1. Testing connectivity..."
if curl -s -f "$NEXUS_URL/service/rest/v1/status" > /dev/null 2>&1; then
    echo "✅ Nexus is reachable"
else
    echo "❌ Cannot reach Nexus at $NEXUS_URL"
    exit 1
fi

# Test 2: Authentication
echo ""
echo "2. Testing authentication..."
if curl -s -f -u "$NEXUS_USER:$NEXUS_PASSWORD" "$NEXUS_URL/service/rest/v1/status" > /dev/null 2>&1; then
    echo "✅ Authentication successful"
else
    echo "❌ Authentication failed"
    exit 1
fi

# Test 3: Repository exists
echo ""
echo "3. Checking maven-snapshots repository..."
if curl -s -f -u "$NEXUS_USER:$NEXUS_PASSWORD" \
    "$NEXUS_URL/service/rest/v1/repositories/maven-snapshots" > /dev/null 2>&1; then
    echo "✅ Repository exists"
    
    # Check writePolicy
    WRITE_POLICY=$(curl -s -u "$NEXUS_USER:$NEXUS_PASSWORD" \
        "$NEXUS_URL/service/rest/v1/repositories/maven-snapshots" \
        | jq -r '.storage.writePolicy')
    echo "   WritePolicy: $WRITE_POLICY"
    
    if [ "$WRITE_POLICY" = "ALLOW" ] || [ "$WRITE_POLICY" = "ALLOW_ONCE" ]; then
        echo "✅ WritePolicy allows writes"
    else
        echo "❌ WritePolicy does not allow writes: $WRITE_POLICY"
        exit 1
    fi
else
    echo "❌ Repository not found or not accessible"
    exit 1
fi

# Test 4: Write access
echo ""
echo "4. Testing write access..."
# Usar path válido de Maven: groupId/artifactId/version/artifactId-version-timestamp-buildNumber.extension
TEST_FILE="com/test/quick-test/1.0-SNAPSHOT/quick-test-1.0-$(date +%Y%m%d.%H%M%S)-1.txt"
HTTP_CODE=$(curl -s -u "$NEXUS_USER:$NEXUS_PASSWORD" \
    -X PUT \
    "$NEXUS_URL/repository/maven-snapshots/$TEST_FILE" \
    -H "Content-Type: text/plain" \
    -d "Quick test - $(date)" \
    -w "%{http_code}" \
    -o /dev/null)

if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Write access confirmed (HTTP $HTTP_CODE)"
else
    echo "❌ Write access failed (HTTP $HTTP_CODE)"
    exit 1
fi

# Test 5: User permissions
echo ""
echo "5. Checking user permissions..."
USER_ROLES=$(curl -s -u "$NEXUS_USER:$NEXUS_PASSWORD" \
    "$NEXUS_URL/service/rest/v1/security/users/$NEXUS_USER" \
    | jq -r '.roles[]' 2>/dev/null || echo "")

if echo "$USER_ROLES" | grep -q "nx-admin"; then
    echo "✅ User has nx-admin role"
else
    echo "⚠️  User does not have nx-admin role"
    echo "   Roles: $USER_ROLES"
fi

echo ""
echo "==================="
echo "✅ All tests passed!"
echo "==================="
echo ""
echo "Nexus is ready for Maven deploy"
echo ""
echo "To deploy:"
echo "  cd backend"
echo "  mvn deploy -DskipTests"
echo ""
