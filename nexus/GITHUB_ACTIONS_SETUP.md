# Configuración de Nexus para GitHub Actions

## Problema

El job "Publish Artifacts to Nexus" en GitHub Actions falla porque Nexus está configurado para `localhost:8081`, que no es accesible desde los runners de GitHub Actions.

## Soluciones

### Opción 1: Nexus Público (Recomendado para Producción)

Si tienes un servidor Nexus accesible públicamente:

#### 1. Configurar GitHub Secrets

Ve a tu repositorio en GitHub → Settings → Secrets and variables → Actions → New repository secret

Agrega los siguientes secrets:

- **NEXUS_URL**: `https://tu-nexus-server.com` (URL pública de tu Nexus)
- **NEXUS_USERNAME**: `admin` (o tu usuario de Nexus)
- **NEXUS_PASSWORD**: `tu-password-seguro`

#### 2. Habilitar el Job

Agrega una variable de repositorio:

- Ve a Settings → Secrets and variables → Actions → Variables tab
- Crea una nueva variable:
  - **Name**: `NEXUS_ENABLED`
  - **Value**: `true`

#### 3. Verificar

El job ahora se ejecutará automáticamente cuando:
- Los tests de integración pasen
- Estés en las ramas: `develop`, `main`, o `release/*`
- La variable `NEXUS_ENABLED` esté en `true`

### Opción 2: Nexus en Docker (Para Testing)

Si quieres probar con Nexus en Docker dentro de GitHub Actions:

#### Modificar el Workflow

Agregar un servicio de Nexus al job:

```yaml
publish-artifacts:
  name: Publish Artifacts to Nexus
  runs-on: ubuntu-latest
  needs: [integration-tests, api-tests, functional-tests, performance-tests]
  
  services:
    nexus:
      image: sonatype/nexus3:latest
      ports:
        - 8081:8081
      options: >-
        --health-cmd "curl -f http://localhost:8081/service/rest/v1/status || exit 1"
        --health-interval 30s
        --health-timeout 10s
        --health-retries 10
  
  steps:
    - name: Wait for Nexus
      run: |
        echo "Waiting for Nexus to be ready..."
        for i in {1..60}; do
          if curl -f http://localhost:8081/service/rest/v1/status; then
            echo "Nexus is ready!"
            break
          fi
          echo "Attempt $i/60: Nexus not ready yet..."
          sleep 10
        done
    
    - name: Setup Nexus repositories
      run: |
        # Script para crear repositorios via API
        # Ver scripts/setup-nexus.sh para referencia
```

**Nota**: Esta opción es más lenta (Nexus tarda ~2-3 minutos en iniciar) y consume más recursos.

### Opción 3: Deshabilitar Temporalmente (Actual)

El job actualmente está configurado para solo ejecutarse si `NEXUS_ENABLED == 'true'`.

Si no configuras esta variable, el job se skipea automáticamente sin causar errores.

## Configuración Local

Para desarrollo local, Nexus funciona normalmente:

```bash
# Iniciar Nexus
docker-compose -f docker-compose.dev.yml up -d nexus

# Esperar a que inicie (1-2 minutos)
curl http://localhost:8081/service/rest/v1/status

# Publicar artifacts localmente
cd backend
mvn deploy -DskipTests
```

## Credenciales por Defecto

- **URL**: http://localhost:8081
- **Username**: admin
- **Password**: admin123

**⚠️ IMPORTANTE**: Cambiar estas credenciales en producción.

## Verificación

Para verificar que Nexus está funcionando:

1. Acceder a http://localhost:8081 (local) o tu URL pública
2. Login con admin/admin123
3. Verificar que existen los repositorios:
   - maven-releases
   - maven-snapshots
4. Intentar publicar un artifact manualmente

## Troubleshooting

### Error: "Cannot access /repository/maven-snapshots"

**Causa**: Nexus no está accesible o las credenciales son incorrectas.

**Solución**:
- Verificar que NEXUS_URL es accesible públicamente
- Verificar que las credenciales en GitHub Secrets son correctas
- Verificar que los repositorios existen en Nexus

### Job se Skipea

**Causa**: Variable `NEXUS_ENABLED` no está configurada.

**Solución**:
- Agregar la variable en GitHub Settings → Variables
- O modificar la condición `if` del job para remover esta verificación

### Nexus Tarda Mucho en Iniciar

**Causa**: Nexus requiere ~2GB RAM y puede tardar 2-3 minutos en iniciar.

**Solución**:
- Aumentar el timeout del health check
- Usar un Nexus pre-configurado
- Considerar usar GitHub Packages como alternativa

## Alternativa: GitHub Packages

Si no quieres mantener un servidor Nexus, puedes usar GitHub Packages:

1. Modificar `pom.xml` para usar GitHub Packages
2. Usar `GITHUB_TOKEN` (ya disponible en Actions)
3. No requiere infraestructura adicional

Ver documentación: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry

## Referencias

- [Nexus Repository Manager](https://help.sonatype.com/repomanager3)
- [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)
- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
