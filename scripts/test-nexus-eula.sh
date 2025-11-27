#!/bin/bash

# Test Nexus EULA Acceptance
# Este script prueba la aceptación del EULA de Nexus

NEXUS_URL="${NEXUS_URL:-http://localhost:8081}"
NEXUS_USER="${NEXUS_USER:-admin}"
NEXUS_PASSWORD="${NEXUS_PASSWORD:-admin123}"

echo "========================================="
echo "Nexus EULA Test"
echo "========================================="
echo "URL: $NEXUS_URL"
echo ""

# 1. Check EULA status
echo "1. Checking EULA status..."
curl -s -u "$NEXUS_USER:$NEXUS_PASSWORD" "$NEXUS_URL/service/rest/v1/system/eula" | jq '.'
echo ""

# 2. Accept EULA
echo "2. Accepting EULA..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" \
  -X POST "$NEXUS_URL/service/rest/v1/system/eula/accept" \
  -u "$NEXUS_USER:$NEXUS_PASSWORD" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | grep -v "HTTP_CODE:")

echo "HTTP Code: $HTTP_CODE"
echo "Response: $BODY"
echo ""

if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "204" ]; then
  echo "✅ EULA accepted successfully"
else
  echo "❌ EULA acceptance failed (HTTP $HTTP_CODE)"
fi

# 3. Verify EULA status after acceptance
echo ""
echo "3. Verifying EULA status after acceptance..."
curl -s -u "$NEXUS_USER:$NEXUS_PASSWORD" "$NEXUS_URL/service/rest/v1/system/eula" | jq '.'
echo ""

# 4. Test write access
echo "4. Testing write access after EULA acceptance..."
TEST_PATH="com/test/eula-test/1.0-SNAPSHOT/eula-test-1.0-$(date +%Y%m%d.%H%M%S)-1.txt"
HTTP_CODE=$(curl -s -u "$NEXUS_USER:$NEXUS_PASSWORD" \
  -X PUT \
  "$NEXUS_URL/repository/maven-snapshots/$TEST_PATH" \
  -H "Content-Type: text/plain" \
  -d "EULA test - $(date)" \
  -w "%{http_code}" \
  -o /dev/null)

echo "Write test HTTP Code: $HTTP_CODE"

if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
  echo "✅ Write access confirmed!"
else
  echo "❌ Write access failed (HTTP $HTTP_CODE)"
  echo ""
  echo "Detailed error:"
  curl -v -u "$NEXUS_USER:$NEXUS_PASSWORD" \
    -X PUT \
    "$NEXUS_URL/repository/maven-snapshots/$TEST_PATH" \
    -H "Content-Type: text/plain" \
    -d "EULA test - $(date)" 2>&1 | tail -20
fi

echo ""
echo "========================================="
echo "Test completed"
echo "========================================="
