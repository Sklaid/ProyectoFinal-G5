# Nexus EULA Fix - Error 403

## Problema
Nexus 3.86+ requiere aceptar EULA antes de permitir escritura en repositorios.

**Error**: `HTTP 403 Forbidden - You must accept the End User License Agreement (EULA)`

## Solución

**Endpoint**: `POST /service/rest/v1/system/eula`

**Body**: Debe incluir disclaimer completo del GET:

```bash
# 1. Obtener EULA
EULA_DATA=$(curl -s -u "admin:admin123" http://localhost:8081/service/rest/v1/system/eula)
DISCLAIMER=$(echo "$EULA_DATA" | jq -r '.disclaimer')

# 2. Aceptar EULA
curl -X POST "http://localhost:8081/service/rest/v1/system/eula" \
  -u "admin:admin123" \
  -H "Content-Type: application/json" \
  -d "{\"accepted\": true, \"disclaimer\": $(echo \"$DISCLAIMER\" | jq -R .)}"
```

## Estado
✅ **Implementado en workflow** - El pipeline acepta automáticamente el EULA

---
**Fecha**: 2025-11-24 | **Estado**: ✅ RESUELTO
