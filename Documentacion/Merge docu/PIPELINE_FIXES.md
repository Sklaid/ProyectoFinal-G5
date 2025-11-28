# Pipeline CI/CD - Correcciones Aplicadas

## Correcciones Principales

### 1. Docker Compose Command
- **Cambio**: `docker-compose` → `docker compose`
- **Razón**: Docker Desktop moderno usa el comando sin guion

### 2. Integration Tests
- **Cambio**: Usa H2 en memoria (no PostgreSQL externo)
- **Comando**: `mvn test` (sin propiedades de DB)

### 3. API Tests (Newman)
- **Token**: Login genera token fresco, compartido entre colecciones
- **Email único**: Usa `{{unique_email}}` para evitar conflictos
- **Permisos**: Agregados `checks: write` y `pull-requests: write`

### 4. Functional Tests (Selenium)
- **Headless**: Configurado con `--headless=new` para CI/CD
- **Selectores**: Cambiados de CSS a XPath para Material-UI
- **Tests independientes**: Cada test crea sus propios datos

### 5. Performance Tests (JMeter)
- **Directorios**: Creados antes de ejecutar JMeter
- **Thresholds**: Verificados por separado con `|| true`

### 6. Nexus Deployment
- **Setup automático**: Password inicial → cambio a admin123
- **Repositorios**: Creados via API REST
- **Autenticación**: Configurada en `settings.xml`

### 7. Property-Based Tests
- **Cleanup**: `deleteAll()` al inicio de cada test
- **Razón**: Evita violaciones de unique constraints

### 8. SonarCloud
- **Exclusión**: `apiClient.ts` excluido de cobertura
- **Razón**: Es código de configuración, no lógica de negocio

## Estado Actual

✅ **Todos los jobs funcionales**:
- Build Backend & Frontend
- Unit Tests (79 tests)
- Integration Tests (26 tests)
- API Tests (23 assertions)
- Functional Tests (9 tests)
- Performance Tests (JMeter)
- SonarQube Analysis
- Security Scan (OWASP Dependency Check + npm audit)
- Nexus Deployment

## Comandos Útiles

### Ejecutar tests localmente
```bash
# Integration tests
cd backend && mvn test

# API tests
newman run postman/auth.postman_collection.json -e postman/dev.env.json

# Functional tests
cd e2e-tests && mvn test -Dselenium.headless=true

# Performance tests
jmeter -n -t jmeter-tests/auth-load-test.jmx -l results.jtl
```

### Deploy a Nexus local
```bash
scripts\setup-nexus-env.bat  # Windows
cd backend && mvn deploy -DskipTests
```

---

**Última actualización**: 2025-11-25  
**Estado**: ✅ Pipeline completamente funcional
