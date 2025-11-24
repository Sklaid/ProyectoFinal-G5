# JMeter Setup and Installation Guide

This guide will help you install Apache JMeter and run the performance tests.

## Prerequisites

- Java JDK 8 or higher (JDK 17 recommended)
- Running backend application (on http://localhost:8080)

## Installing Apache JMeter

### Option 1: Download Binary (Recommended)

1. **Download JMeter**
   - Visit: https://jmeter.apache.org/download_jmeter.cgi
   - Download the latest binary (e.g., `apache-jmeter-5.6.3.zip` or `.tgz`)
   - Recommended version: 5.6.3 or later

2. **Extract the Archive**
   
   **Windows:**
   ```cmd
   # Extract to C:\jmeter (or any location)
   # Right-click the ZIP file > Extract All
   ```
   
   **Linux/Mac:**
   ```bash
   tar -xzf apache-jmeter-5.6.3.tgz
   sudo mv apache-jmeter-5.6.3 /opt/jmeter
   ```

3. **Add JMeter to PATH**
   
   **Windows:**
   ```cmd
   # Add to System Environment Variables:
   # Variable: JMETER_HOME
   # Value: C:\jmeter\apache-jmeter-5.6.3
   
   # Add to PATH:
   # %JMETER_HOME%\bin
   ```
   
   **Linux/Mac:**
   ```bash
   # Add to ~/.bashrc or ~/.zshrc
   export JMETER_HOME=/opt/jmeter
   export PATH=$PATH:$JMETER_HOME/bin
   
   # Reload shell configuration
   source ~/.bashrc
   ```

4. **Verify Installation**
   ```bash
   jmeter --version
   ```
   
   Expected output:
   ```
   Apache JMeter 5.6.3
   Copyright (c) 1999-2024 The Apache Software Foundation
   ```

### Option 2: Using Package Managers

**Windows (Chocolatey):**
```cmd
choco install jmeter
```

**Mac (Homebrew):**
```bash
brew install jmeter
```

**Linux (Ubuntu/Debian):**
```bash
# Note: Repository version may be outdated
sudo apt-get update
sudo apt-get install jmeter

# Or download latest binary (recommended)
```

## Verifying Java Installation

JMeter requires Java. Verify you have Java installed:

```bash
java -version
```

Expected output (example):
```
openjdk version "17.0.9" 2023-10-17
OpenJDK Runtime Environment Temurin-17.0.9+9 (build 17.0.9+9)
OpenJDK 64-Bit Server VM Temurin-17.0.9+9 (build 17.0.9+9, mixed mode, sharing)
```

If Java is not installed:
- **Windows**: Download from https://adoptium.net/
- **Mac**: `brew install openjdk@17`
- **Linux**: `sudo apt-get install openjdk-17-jdk`

## Starting the Application

Before running performance tests, ensure the backend application is running:

### Option 1: Using Docker Compose (Recommended)
```bash
# From project root
docker-compose -f docker-compose.dev.yml up -d

# Verify application is running
curl http://localhost:8080/actuator/health
```

### Option 2: Running Backend Directly
```bash
# From backend directory
cd backend
mvn spring-boot:run

# Or if already built
java -jar target/devops-platform-*.jar
```

### Verify Application Health
```bash
# Should return: {"status":"UP"}
curl http://localhost:8080/actuator/health
```

## Running Your First Test

Once JMeter is installed and the application is running:

### GUI Mode (for exploration)
```bash
cd jmeter-tests
jmeter
```

Then:
1. File > Open > Select `auth-load-test.jmx`
2. Click the green "Start" button (▶)
3. View results in "View Results Tree" or "Summary Report"

### Non-GUI Mode (for actual testing)
```bash
cd jmeter-tests

# Windows
run-auth-test.bat

# Linux/Mac
./run-auth-test.sh
```

## Troubleshooting

### "jmeter: command not found"
- Verify JMeter is in your PATH
- Try using full path: `/opt/jmeter/bin/jmeter` or `C:\jmeter\bin\jmeter.bat`
- Restart your terminal after adding to PATH

### "Connection refused" errors
- Verify application is running: `curl http://localhost:8080/actuator/health`
- Check if port 8080 is in use: `netstat -an | grep 8080`
- Verify database is running (if using Docker Compose)

### "Out of Memory" errors
- Increase JMeter heap size:
  ```bash
  # Linux/Mac
  export JVM_ARGS="-Xms512m -Xmx2048m"
  
  # Windows (edit jmeter.bat)
  set HEAP=-Xms512m -Xmx2048m
  ```

### High error rates in tests
- Check application logs for errors
- Verify test credentials (default: admin/admin123)
- Ensure database has test user created
- Check if application is under too much load

### CSV file not found
- Ensure you're running tests from `jmeter-tests/` directory
- Verify `employee-test-data.csv` exists
- Check file path in test plan matches your directory structure

## Next Steps

1. ✅ Install JMeter
2. ✅ Start the application
3. ✅ Run authentication test: `./run-auth-test.sh`
4. ✅ Run employee API test: `./run-employee-test.sh`
5. ✅ Review HTML reports in `reports/` directory
6. ✅ Analyze performance metrics
7. ✅ Adjust test parameters as needed

## Additional Resources

- [JMeter Getting Started](https://jmeter.apache.org/usermanual/get-started.html)
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)
- [Performance Testing Tutorial](https://jmeter.apache.org/usermanual/build-web-test-plan.html)

## Support

For issues or questions:
1. Check the main README.md in `jmeter-tests/`
2. Review JMeter documentation
3. Check application logs
4. Verify all prerequisites are met
