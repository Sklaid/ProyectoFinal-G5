#!/bin/bash
# Run JMeter Authentication Load Test
# This script runs the authentication load test in non-GUI mode and generates an HTML report

echo "========================================"
echo "Running Authentication Load Test"
echo "========================================"
echo

# Create directories if they don't exist
mkdir -p results
mkdir -p reports
rm -rf reports/auth-report

# Run JMeter test
echo "Running JMeter test..."
jmeter -n -t auth-load-test.jmx \
  -l results/auth-results.jtl \
  -e -o reports/auth-report

if [ $? -eq 0 ]; then
    echo
    echo "========================================"
    echo "Test completed successfully!"
    echo "========================================"
    echo
    echo "Results saved to: results/auth-results.jtl"
    echo "HTML Report: reports/auth-report/index.html"
    echo
    echo "To view the report, open: reports/auth-report/index.html"
else
    echo
    echo "========================================"
    echo "Test failed with errors!"
    echo "========================================"
    echo
    echo "Check the results file: results/auth-results.jtl"
    exit 1
fi
