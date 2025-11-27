# Quick Start Guide - JMeter Performance Tests

## 🚀 Get Started in 3 Steps

### Step 1: Install JMeter

**Don't have JMeter?** Download it here: https://jmeter.apache.org/download_jmeter.cgi

**Quick Install:**
- **Windows**: Download ZIP, extract to `C:\jmeter`, add `C:\jmeter\bin` to PATH
- **Mac**: `brew install jmeter`
- **Linux**: Download and extract to `/opt/jmeter`

**Verify Installation:**
```bash
jmeter --version
```

Need help? See [SETUP_GUIDE.md](SETUP_GUIDE.md) for detailed instructions.

### Step 2: Start the Application

Make sure your backend is running on http://localhost:8080

```bash
# Option A: Using Docker Compose (from project root)
docker-compose -f docker-compose.dev.yml up -d

# Option B: Run backend directly
cd backend
mvn spring-boot:run

# Verify it's running
curl http://localhost:8080/actuator/health
```

### Step 3: Run the Tests

**Windows:**
```cmd
cd jmeter-tests
run-all-tests.bat
```

**Linux/Mac:**
```bash
cd jmeter-tests
chmod +x *.sh
./run-all-tests.sh
```

That's it! 🎉 Reports will open automatically (Windows) or be available in `reports/` directory.

---

## 📊 What Gets Tested?

### Authentication Test
- **50 concurrent users** logging in
- **500 total requests** (10 loops per user)
- **Target**: < 500ms response time, < 1% error rate

### Employee API Test
- **100 concurrent users** performing CRUD operations
- **2,500 total requests** across 5 endpoints
- **Target**: < 500ms response time, < 1% error rate

---

## 📈 Viewing Results

After tests complete, open the HTML reports:

```
jmeter-tests/reports/auth-report/index.html
jmeter-tests/reports/employee-report/index.html
```

Or check thresholds programmatically:
```bash
python check-performance-thresholds.py results/employee-results.jtl
```

---

## 🔧 Troubleshooting

**"jmeter: command not found"**
- Install JMeter (see Step 1)
- Add JMeter bin directory to your PATH
- Restart your terminal

**"Connection refused" errors**
- Make sure the application is running (see Step 2)
- Verify: `curl http://localhost:8080/actuator/health`

**"Authentication failed" errors**
- Check if default user exists (username: admin, password: admin123)
- Review application logs

**High error rates**
- Application might be under too much load
- Check database is running
- Review application logs for errors

---

## 📚 More Information

- **Full Documentation**: [README.md](README.md)
- **Installation Help**: [SETUP_GUIDE.md](SETUP_GUIDE.md)
- **Test Details**: [TEST_SUMMARY.md](TEST_SUMMARY.md)

---

## ✅ Checklist

Before running tests, ensure:
- [ ] JMeter is installed and in PATH
- [ ] Java 8+ is installed
- [ ] Backend application is running on port 8080
- [ ] Database is accessible
- [ ] Test user exists (admin/admin123)

---

## 🎯 Expected Results

If everything is working correctly:
- ✅ All tests should pass
- ✅ Error rate should be 0% or very low (< 1%)
- ✅ Average response time should be < 300ms
- ✅ 95th percentile should be < 500ms
- ✅ HTML reports generated successfully

---

## 🚨 Need Help?

1. Check [SETUP_GUIDE.md](SETUP_GUIDE.md) for installation issues
2. Check [README.md](README.md) for detailed troubleshooting
3. Review application logs for errors
4. Verify all prerequisites are met

---

**Ready to test?** Run `./run-all-tests.sh` (Linux/Mac) or `run-all-tests.bat` (Windows) and watch your application perform! 🚀
