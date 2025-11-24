#!/bin/bash
# Run JMeter Employee API Load Test
# This script runs the employee API load test in non-GUI mode and generates an HTML report

echo "========================================"
echo "Running Employee API Load Test"
echo "========================================"
echo

# Create directories if they don't exist
mkdir -p results
mkdir -p reports
rm -rf reports/employee-report

# Run JMeter test
echo "Running JMeter test..."
jmeter -n -t employee-api-load-test.jmx \
  -l results/employee-results.jtl \
  -e -o reports/employee-report

if [ $? -eq 0 ]; then
    echo
    echo "========================================"
    echo "Test completed successfully!"
    echo "========================================"
    echo
    echo "Results saved to: results/employee-results.jtl"
    echo "HTML Report: reports/employee-report/index.html"
    echo
    echo "To view the report, open: reports/employee-report/index.html"
else
    echo
    echo "========================================"
    echo "Test failed with errors!"
    echo "========================================"
    echo
    echo "Check the results file: results/employee-results.jtl"
    exit 1
fi
