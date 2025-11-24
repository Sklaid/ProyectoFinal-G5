#!/bin/bash
# Run All JMeter Performance Tests
# This script runs all performance tests sequentially

echo "========================================"
echo "Running All Performance Tests"
echo "========================================"
echo

# Create directories if they don't exist
mkdir -p results
mkdir -p reports

echo "[1/2] Running Authentication Load Test..."
echo "========================================"
rm -rf reports/auth-report
jmeter -n -t auth-load-test.jmx \
  -l results/auth-results.jtl \
  -e -o reports/auth-report

if [ $? -ne 0 ]; then
    echo "Authentication test FAILED!"
    exit 1
fi
echo "Authentication test PASSED!"
echo

echo "[2/2] Running Employee API Load Test..."
echo "========================================"
rm -rf reports/employee-report
jmeter -n -t employee-api-load-test.jmx \
  -l results/employee-results.jtl \
  -e -o reports/employee-report

if [ $? -ne 0 ]; then
    echo "Employee API test FAILED!"
    exit 1
fi
echo "Employee API test PASSED!"
echo

echo "========================================"
echo "All Tests Completed Successfully!"
echo "========================================"
echo
echo "Reports generated:"
echo "- Auth Report: reports/auth-report/index.html"
echo "- Employee Report: reports/employee-report/index.html"
echo
