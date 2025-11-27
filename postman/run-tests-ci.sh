#!/bin/bash
# CI/CD Script to run Postman collections with Newman
# Generates JUnit XML reports for pipeline integration
# Exits with error code if any tests fail

set -e  # Exit on error

echo "========================================"
echo "Running API Tests for CI/CD Pipeline"
echo "========================================"
echo ""

# Create reports directory
mkdir -p reports

# Run authentication tests
echo "[1/2] Running Authentication Tests..."
newman run postman/auth.postman_collection.json \
    -e postman/dev.env.json \
    --reporters cli,junit \
    --reporter-junit-export reports/auth-junit.xml \
    --bail || AUTH_FAILED=1

echo ""

# Run employee tests
echo "[2/2] Running Employee CRUD Tests..."
newman run postman/employees.postman_collection.json \
    -e postman/dev.env.json \
    --reporters cli,junit \
    --reporter-junit-export reports/employees-junit.xml \
    --bail || EMP_FAILED=1

echo ""
echo "========================================"
echo "Test Results"
echo "========================================"

if [ -n "$AUTH_FAILED" ]; then
    echo "❌ Authentication tests FAILED"
fi

if [ -n "$EMP_FAILED" ]; then
    echo "❌ Employee tests FAILED"
fi

if [ -z "$AUTH_FAILED" ] && [ -z "$EMP_FAILED" ]; then
    echo "✅ All tests PASSED"
    exit 0
else
    echo ""
    echo "See JUnit reports in reports/ directory for details"
    exit 1
fi
