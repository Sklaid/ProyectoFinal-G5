# Nexus Repository Manager - Guía Completa

## 🎉 Estado: Configurado y Funcionando

El Nexus Repository Manager ha sido configurado exitosamente y probado. Los artefactos Maven pueden ser desplegados y recuperados desde Nexus.

## 📋 Resumen de Configuración

### Repositorios Creados

| Repositorio | Tipo | URL | Estado |
|-------------|------|-----|--------|
| maven-snapshots | Maven Hosted | http://localhost:8081/repository/maven-snapshots/ | ✅ Probado |
| maven-releases | Maven Hosted | http://localhost:8081/repository/maven-releases/ | ✅ Listo |
| npm-hosted | NPM Hosted | http://localhost:8081/repository/npm-hosted/ | ✅ Listo |
| docker-hosted | Docker Hosted | http://localhost:8081/repository/docker-hosted/ | ✅ Listo |

### Credenciales de Acceso

- **URL**: http://localhost:8081
- **Usuario**: admin
- **Contraseña**: admin123

## 🚀 Cómo Desplegar Artefactos

### 1. Configurar Variables de Entorno

```powershell
# PowerShell
$env:NEXUS_USERNAME = "admin"
$env:NEXUS_PASSWORD = "admin123"
```

```cmd
# CMD
set NEXUS_USERNAME=admin
set NEXUS_PASSWORD=admin123
```

### 2. Ejecutar Deployment

```bash
cd backend
mvn deploy -DskipTests
```

### 3. Resultado Esperado

```
Uploading to nexus-snapshots: http://localhost:8081/repository/maven-snapshots/...
Uploaded to nexus-snapshots: .../devops-platform-1.0.0-SNAPSHOT.jar (54 MB)
[INFO] BUILD SUCCESS
```

## ✅ Deployment Exitoso

**Fecha**: 24 de Noviembre, 2025  
**Artefacto**: devops-platform-1.0.0-SNAPSHOT  
**Tamaño**: 54 MB  
**Repositorio**: maven-snapshots  
**Estado**: ✅ SUCCESS

## 📖 Documentación Disponible

### Guías en Inglés
- `README.md` - Guía completa de configuración
- `DEPLOYMENT_SUCCESS.md` - Confirmación de deployment exitoso
- `TROUBLESHOOTING.md` - Guía de resolución de problemas
- `SETUP_SUMMARY.md` - Resumen de tareas completadas

### Guías en Español
- `LEEME.md` - Este archivo

## 🔧 Scripts Disponibles

### Windows (PowerShell/CMD)

```bash
# Inicializar contraseña de admin
.\scripts\init-nexus-password.bat

# Crear todos los repositorios
.\scripts\setup-nexus.bat

# Configurar Maven settings.xml
.\scripts\setup-maven-nexus.bat

# Habilitar acceso anónimo
.\scripts\enable-nexus-anonymous.bat

# Arreglar repositorio maven-snapshots (si hay problemas)
.\scripts\fix-nexus-snapshot-repo.bat
```

### Linux/Mac

```bash
# Inicializar contraseña de admin
./scripts/init-nexus-password.sh

# Crear todos los repositorios
./scripts/setup-nexus.sh

# Configurar Maven settings.xml
./scripts/setup-maven-nexus.sh
```

## 🔍 Verificar Deployment

### Opción 1: Interfaz Web de Nexus

1. Abrir http://localhost:8081
2. Login con admin/admin123
3. Click en **Browse** (menú izquierdo)
4. Seleccionar **maven-snapshots**
5. Navegar a: com → techcorp → devops-platform → 1.0.0-SNAPSHOT
6. Deberías ver el archivo JAR y POM

### Opción 2: Línea de Comandos

```bash
# Listar artefactos
curl -u admin:admin123 "http://localhost:8081/service/rest/v1/search?repository=maven-snapshots&name=devops-platform"

# Verificar URL directa
curl -I "http://localhost:8081/repository/maven-snapshots/com/techcorp/devops-platform/1.0.0-SNAPSHOT/"
```

## 🐛 Solución de Problemas

### Error 403 Forbidden al Desplegar

**Solución**: Ejecutar el script de corrección
```bash
.\scripts\fix-nexus-snapshot-repo.bat
```

Este script recreará el repositorio maven-snapshots con la configuración correcta.

### Nexus No Está Corriendo

**Solución**: Iniciar el contenedor
```bash
docker-compose -f docker-compose.dev.yml up -d nexus
```

Esperar 1-2 minutos y verificar:
```bash
curl http://localhost:8081/service/rest/v1/status
```

### Error de Autenticación

**Solución**: Verificar credenciales
```bash
curl -u admin:admin123 http://localhost:8081/service/rest/v1/status
```

Si falla, reiniciar contraseña:
```bash
.\scripts\init-nexus-password.bat
```

## 📦 Desplegar Versiones RELEASE

Para desplegar una versión de producción (no SNAPSHOT):

1. Actualizar versión en `pom.xml`:
   ```xml
   <version>1.0.0</version>  <!-- Remover -SNAPSHOT -->
   ```

2. Desplegar:
   ```bash
   mvn deploy -DskipTests
   ```

3. El artefacto irá al repositorio maven-releases

4. Etiquetar en Git:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

## 🔄 Integración CI/CD

### Ejemplo para GitHub Actions

```yaml
- name: Deploy to Nexus
  env:
    NEXUS_USERNAME: ${{ secrets.NEXUS_USERNAME }}
    NEXUS_PASSWORD: ${{ secrets.NEXUS_PASSWORD }}
  run: |
    cd backend
    mvn deploy -DskipTests
```

### Secrets Requeridos

Configurar en GitHub → Settings → Secrets:
- `NEXUS_USERNAME`: admin
- `NEXUS_PASSWORD`: admin123

## 📊 Configuración de Repositorios

### maven-snapshots (Desarrollo)
- **Política de Versión**: SNAPSHOT
- **Política de Deployment**: Permitir redeploy
- **Política de Escritura**: ALLOW
- **Uso**: Artefactos de desarrollo que cambian frecuentemente

### maven-releases (Producción)
- **Política de Versión**: RELEASE
- **Política de Deployment**: No permitir redeploy
- **Política de Escritura**: ALLOW_ONCE
- **Uso**: Artefactos de producción inmutables

## 🎯 Próximos Pasos

1. ✅ Nexus configurado y funcionando
2. ✅ Deployment de Maven probado exitosamente
3. ⏭️ Configurar pipeline CI/CD para usar Nexus (Tareas 17-19)
4. ⏭️ Desplegar artefactos de frontend a npm-hosted (si es necesario)
5. ⏭️ Desplegar imágenes Docker a docker-hosted (si es necesario)

## 📚 Referencias

- Interfaz Web: http://localhost:8081
- Documentación Nexus: https://help.sonatype.com/repomanager3
- Maven Deploy Plugin: https://maven.apache.org/plugins/maven-deploy-plugin/

---

**Estado**: ✅ COMPLETO  
**Última Actualización**: 24 de Noviembre, 2025  
**Probado Por**: Script de deployment automatizado  
**Resultado**: ÉXITO
