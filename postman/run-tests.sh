#!/bin/bash
# Script to run Postman collections with Newman
# This script runs authentication tests first, then employee tests

echo "========================================"
echo "Running DevOps Platform API Tests"
echo "========================================"
echo ""

# Create reports directory if it doesn't exist
mkdir -p reports

echo "[1/2] Running Authentication Tests..."
echo "----------------------------------------"
newman run postman/auth.postman_collection.json -e postman/dev.env.json
AUTH_EXIT_CODE=$?
if [ $AUTH_EXIT_CODE -ne 0 ]; then
    echo ""
    echo "WARNING: Some authentication tests failed!"
    echo ""
fi

echo ""
echo "[2/2] Running Employee CRUD Tests..."
echo "----------------------------------------"
newman run postman/employees.postman_collection.json -e postman/dev.env.json
EMP_EXIT_CODE=$?
if [ $EMP_EXIT_CODE -ne 0 ]; then
    echo ""
    echo "WARNING: Some employee tests failed!"
    echo ""
fi

echo ""
echo "========================================"
echo "Test Execution Complete"
echo "========================================"
echo ""
echo "Reports have been generated in the reports/ directory"
echo ""

# Exit with error if any tests failed
if [ $AUTH_EXIT_CODE -ne 0 ] || [ $EMP_EXIT_CODE -ne 0 ]; then
    exit 1
fi

exit 0
