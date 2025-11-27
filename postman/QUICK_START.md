# 🚀 Guía Rápida - Tests de API

## ⚡ Inicio Rápido (5 minutos)

### 1️⃣ Verificar que el Backend Esté Corriendo
```bash
curl http://localhost:8080/actuator/health
```
✅ Debe devolver: `{"status":"UP"}`

---

### 2️⃣ Limpiar Datos de Prueba (Opcional)
```sql
-- Ejecutar en PostgreSQL
DELETE FROM employee_skills;
DELETE FROM employees WHERE email LIKE '%@techcorp.com';
```

---

### 3️⃣ Ejecutar los Tests

#### Windows:
```bash
cd postman
run-tests-with-reports.bat
```

#### Linux/Mac:
```bash
cd postman
chmod +x run-tests.sh
./run-tests.sh
```

---

### 4️⃣ Ver los Resultados

Abre en tu navegador:
- `reports/auth-report-final.html` - Tests de autenticación
- `reports/employees-report-final.html` - Tests de empleados

---

## 📊 Resultados Esperados

### ✅ Lo que DEBE pasar:
- **20 de 23** assertions en autenticación ✅
- **39 de 41** assertions en empleados ✅
- **92% de éxito total** ✅

### ⚠️ Fallos esperados (bugs del backend):
- 3 fallos en autenticación (devuelve 500 en lugar de 401)
- 2 fallos en empleados (devuelve 500 en lugar de 400)

**Estos fallos son NORMALES** - son bugs del backend que los tests detectaron correctamente.

---

## 🔧 Solución de Problemas

### ❌ Error: "ECONNREFUSED"
**Problema:** El backend no está corriendo.

**Solución:**
```bash
# Verificar que el backend esté corriendo
curl http://localhost:8080/actuator/health
```

---

### ❌ Error: "401 Unauthorized" en todos los tests
**Problema:** El token JWT expiró.

**Solución:**
1. Genera un nuevo token desde Postman (POST /api/auth/login)
2. Actualiza el token en `postman/dev.env.json`:
```json
{
    "key": "auth_token",
    "value": "TU_NUEVO_TOKEN_AQUI"
}
```

---

### ❌ Error: "400 Bad Request" al crear empleado
**Problema:** El email ya existe en la base de datos.

**Solución:** Ejecuta el SQL de limpieza (paso 2 arriba)

---

## 📁 Archivos Importantes

| Archivo | Descripción |
|---------|-------------|
| `auth.postman_collection.json` | Tests de autenticación |
| `employees.postman_collection.json` | Tests de CRUD |
| `dev.env.json` | Ambiente con token JWT |
| `reports/*.html` | Reportes visuales |

---

## 🎯 Comandos Útiles

### Ejecutar solo autenticación:
```bash
newman run postman/auth.postman_collection.json -e postman/dev.env.json
```

### Ejecutar solo empleados:
```bash
newman run postman/employees.postman_collection.json -e postman/dev.env.json
```

### Generar reporte HTML:
```bash
newman run postman/employees.postman_collection.json \
    -e postman/dev.env.json \
    -r htmlextra \
    --reporter-htmlextra-export reports/mi-reporte.html
```

---

## 📚 Documentación Completa

Para más detalles, consulta:
- `README.md` - Guía completa de uso
- `RESUMEN_ESPAÑOL.md` - Resumen en español
- `FINAL_RESULTS.md` - Resultados detallados
- `TROUBLESHOOTING.md` - Solución de problemas

---

## ✅ Checklist de Validación

Antes de ejecutar los tests, verifica:

- [ ] Backend corriendo en http://localhost:8080
- [ ] PostgreSQL corriendo
- [ ] Base de datos limpia (sin datos de prueba anteriores)
- [ ] Token JWT válido en `dev.env.json`
- [ ] Newman instalado (`newman --version`)

---

## 🎉 ¡Listo!

Ahora puedes ejecutar los tests y ver los resultados en los reportes HTML.

**¿Preguntas?** Consulta `TROUBLESHOOTING.md` o `README.md`

---

**Última actualización:** 23 de Noviembre, 2024
