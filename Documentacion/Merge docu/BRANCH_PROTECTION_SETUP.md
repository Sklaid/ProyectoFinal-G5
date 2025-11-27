# Branch Protection Rules Setup

## Objetivo

Configurar reglas de protección para la rama `main` que fuercen el uso de Pull Requests y validaciones antes de hacer merge.

## Prerequisitos

⚠️ **Importante**: Antes de configurar Branch Protection Rules, asegúrate de que:
- ✅ El pipeline de GitHub Actions se haya ejecutado **al menos una vez** en `develop`
- ✅ Todos los jobs hayan reportado su estado a GitHub

**¿Por qué?** GitHub solo muestra los status checks que ya han corrido al menos una vez en el repositorio.

**Estado actual**: Si ya ejecutaste el pipeline en `develop`, puedes configurar las reglas ahora mismo.

## Configuración en GitHub

### 1. Acceder a Branch Protection

1. Ve a tu repositorio en GitHub
2. Click en **Settings** → **Branches**
3. Click en **Add rule**

### 2. Configurar la Regla para `main`

**Branch name pattern**: `main`

**Configuraciones recomendadas**:

#### ✅ Require a pull request before merging
- **Required approvals**: 1
- ✅ Dismiss stale PR approvals when new commits are pushed

#### ✅ Require status checks to pass before merging
- ✅ Require branches to be up to date before merging
- Click en **"Search for status checks"** para ver los checks disponibles

**Status checks requeridos** (selecciona los que aparezcan):
- `build-backend`
- `build-frontend`
- `unit-tests`
- `sonarqube-analysis`
- `integration-tests`
- `api-tests`
- `functional-tests`
- `performance-tests`

**Nota**: Solo aparecerán los checks que ya hayan corrido al menos una vez. Si no ves alguno, verifica que el job correspondiente esté habilitado en el workflow.

#### ✅ Require conversation resolution before merging

#### ❌ Include administrators
- Desmarcar para que los admins también sigan las reglas

### 3. Guardar

Click en **Create** para aplicar las reglas.

## Resultado

### ❌ No Permitido:
- Push directo a `main`
- Merge sin aprobación
- Merge con tests fallando

### ✅ Permitido Solo:
- Merge via Pull Request
- Con al menos 1 aprobación
- Con todos los status checks pasando

## Flujo de Trabajo

```
1. Desarrollar en develop
   ↓
2. Push a develop → Pipeline ejecuta hasta Tag STABLE
   ↓
3. Crear PR: develop → main
   ↓
4. Revisar y aprobar PR
   ↓
5. Merge PR → Pipeline ejecuta Canary → Production → Tag GOLD
   ↓
6. Código en producción ✅
```

---

**Configurado**: Pendiente  
**Responsable**: Administrador del repositorio  
**Prioridad**: Alta (requerido para Task 20.2)
