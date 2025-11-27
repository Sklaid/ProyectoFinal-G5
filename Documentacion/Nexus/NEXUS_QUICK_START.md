# Nexus Deploy - Quick Start

## Deploy a Nexus

```bash
# 1. Setup (solo primera vez)
scripts\setup-nexus-env.bat  # Windows
source scripts/setup-nexus-env.sh  # Linux/Mac

# 2. Deploy
cd backend
mvn deploy -DskipTests
```

## Verificar
http://localhost:8081/#browse/browse:maven-snapshots

## Troubleshooting

**Error 401**: Ejecuta el script de setup
**Nexus no responde**: `docker-compose -f docker-compose.dev.yml up -d nexus` (espera 2-3 min)
