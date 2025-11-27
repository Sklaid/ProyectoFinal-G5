# JMeter Performance Tests

This directory contains JMeter performance test plans for the DevOps Enterprise Platform.

## Test Plans

### 1. Authentication Load Test (`auth-load-test.jmx`)
- **Purpose**: Load test for authentication endpoint
- **Concurrent Users**: 50
- **Ramp-up Period**: 30 seconds
- **Loops**: 10 per user
- **Total Requests**: 500 (50 users × 10 loops)
- **Assertions**:
  - Response code: 200
  - Response contains JWT token
  - Response time < 500ms
  - Error rate < 1%

### 2. Employee API Load Test (`employee-api-load-test.jmx`)
- **Purpose**: Load test for Employee CRUD operations
- **Concurrent Users**: 100
- **Ramp-up Period**: 30 seconds
- **Loops**: 5 per user
- **Total Requests**: 2,500 (100 users × 5 loops × 5 endpoints)
- **Endpoints Tested**:
  - GET /api/employees (List all)
  - POST /api/employees (Create)
  - GET /api/employees/{id} (Get by ID)
  - PUT /api/employees/{id} (Update)
  - DELETE /api/employees/{id} (Delete)
- **Assertions**:
  - Response codes: 200, 201, 204
  - Response time < 500ms
  - Error rate < 1%

## Prerequisites

1. **Apache JMeter**: Download from https://jmeter.apache.org/download_jmeter.cgi
   - Minimum version: 5.5
   - Recommended: 5.6.3 or later

2. **Running Application**: Ensure the backend application is running
   ```bash
   # Start the application using Docker Compose
   docker-compose -f docker-compose.dev.yml up -d
   
   # Or run backend directly
   cd backend
   mvn spring-boot:run
   ```

3. **Test Data**: The employee test uses CSV data from `employee-test-data.csv`

## Running Tests

### Option 1: GUI Mode (for development and debugging)

```bash
# Run JMeter GUI
jmeter

# Then open the test plan file:
# File > Open > Select auth-load-test.jmx or employee-api-load-test.jmx
# Click the green "Start" button to run
```

### Option 2: Non-GUI Mode (recommended for actual load testing)

#### Authentication Test
```bash
jmeter -n -t jmeter-tests/auth-load-test.jmx \
  -l jmeter-tests/results/auth-results.jtl \
  -e -o jmeter-tests/reports/auth-report
```

#### Employee API Test
```bash
jmeter -n -t jmeter-tests/employee-api-load-test.jmx \
  -l jmeter-tests/results/employee-results.jtl \
  -e -o jmeter-tests/reports/employee-report
```

### Option 3: Using Helper Scripts

#### Windows
```cmd
cd jmeter-tests
run-auth-test.bat
run-employee-test.bat
run-all-tests.bat
```

#### Linux/Mac
```bash
cd jmeter-tests
./run-auth-test.sh
./run-employee-test.sh
./run-all-tests.sh
```

## Command Line Options Explained

- `-n`: Non-GUI mode
- `-t`: Test plan file
- `-l`: Log file (JTL format with results)
- `-e`: Generate HTML report after test
- `-o`: Output folder for HTML report
- `-J`: Set JMeter property (e.g., `-JHOST=localhost -JPORT=8080`)

## Customizing Tests

### Change Target Server
```bash
jmeter -n -t jmeter-tests/auth-load-test.jmx \
  -JHOST=production.example.com \
  -JPORT=443 \
  -JPROTOCOL=https \
  -l results.jtl
```

### Change Number of Users
Edit the test plan XML or use JMeter GUI:
- Open test plan
- Select "Thread Group"
- Modify "Number of Threads (users)"
- Modify "Ramp-up Period (seconds)"

## Analyzing Results

### HTML Reports
After running tests in non-GUI mode with `-e -o` options, open:
```
jmeter-tests/reports/auth-report/index.html
jmeter-tests/reports/employee-report/index.html
```

### Key Metrics to Review
1. **Response Time**:
   - Average: Should be < 200ms
   - 90th Percentile: Should be < 500ms
   - 95th Percentile: Should be < 500ms

2. **Throughput**:
   - Target: > 100 requests/second

3. **Error Rate**:
   - Target: < 1%
   - Acceptable: < 5%

4. **Latency**:
   - Should be consistent across test duration
   - Watch for degradation over time

### Performance Thresholds

The tests include assertions for:
- **Response Time**: < 500ms (fails if exceeded)
- **Error Rate**: < 1% (calculated post-test)

If tests fail these thresholds, investigate:
1. Database query performance
2. Connection pool settings
3. JVM heap size
4. Network latency
5. Concurrent request handling

## Troubleshooting

### Test Fails Immediately
- Verify application is running: `curl http://localhost:8080/actuator/health`
- Check credentials in test plan (default: admin/admin123)
- Verify database is accessible

### High Error Rate
- Check application logs for errors
- Verify database connection pool size
- Check for resource exhaustion (CPU, memory, connections)

### Slow Response Times
- Monitor application metrics during test
- Check database query performance
- Review connection pool configuration
- Consider increasing JVM heap: `export JAVA_OPTS="-Xmx2g"`

### CSV Data Not Found
- Ensure `employee-test-data.csv` is in `jmeter-tests/` directory
- Check file path in test plan matches your directory structure

## CI/CD Integration

### GitHub Actions Example
```yaml
- name: Run JMeter Performance Tests
  run: |
    jmeter -n -t jmeter-tests/employee-api-load-test.jmx \
      -l results.jtl \
      -e -o jmeter-report/
    
- name: Check Performance Thresholds
  run: |
    python scripts/check-jmeter-results.py results.jtl
```

### Jenkins Example
```groovy
stage('Performance Tests') {
    steps {
        sh 'jmeter -n -t jmeter-tests/employee-api-load-test.jmx -l results.jtl'
        perfReport sourceDataFiles: 'results.jtl'
    }
}
```

## Best Practices

1. **Warm-up**: Run a small test first to warm up the application
2. **Realistic Load**: Model tests after actual user behavior
3. **Think Time**: Include delays between requests (already configured)
4. **Monitor Resources**: Watch CPU, memory, and database during tests
5. **Baseline**: Establish baseline metrics before making changes
6. **Incremental Load**: Start with fewer users and increase gradually

## Additional Resources

- [JMeter User Manual](https://jmeter.apache.org/usermanual/index.html)
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)
- [Performance Testing Guide](https://jmeter.apache.org/usermanual/component_reference.html)
