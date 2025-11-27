# Pull Request Workflow - Develop to Main

## Objetivo

Documentar el flujo completo para llevar código desde `develop` hasta producción usando Pull Requests.

## Flujo Completo

### Fase 1: Desarrollo en Develop ✅ (Task 20.1)

```bash
# 1. Trabajar en develop
git checkout develop
git pull origin develop

# 2. Hacer cambios y commit
git add .
git commit -m "feat: nueva funcionalidad"

# 3. Push a develop
git push origin develop
```

**Resultado**: Pipeline ejecuta hasta **Tag STABLE** ✅

### Fase 2: Pull Request a Main 🚀 (Task 20.2)

#### Paso 1: Crear Pull Request

**Via GitHub Web UI**:
1. Ve a tu repositorio en GitHub
2. Click en **Pull requests** → **New pull request**
3. Selecciona:
   - **Base**: `main` ← **Compare**: `develop`
4. Agrega título y descripción
5. Click en **Create pull request**

**Via GitHub CLI** (opcional):
```bash
gh pr create --base main --head develop --title "Release: Deploy to Production" --body "Deploy stable code to production"
```

#### Paso 2: GitHub Actions se Ejecuta Automáticamente

**Trigger**: `pull_request` hacia `main`

**Jobs que se ejecutan**:
```
✅ Build Backend
✅ Build Frontend
✅ Unit Tests
✅ SonarQube Analysis
✅ Integration Tests
✅ API Tests
✅ Functional Tests
✅ Performance Tests
⏭️ Skip: Canary/Production (no es push a main aún)
```

#### Paso 3: Revisión del Pull Request

**Verificaciones automáticas**:
- ✅ Todos los status checks deben pasar
- ✅ Branch debe estar actualizada con `main`
- ✅ No debe haber conflictos

**Revisión manual**:
1. **Files changed**: Revisar todos los cambios
2. **Commits**: Verificar que los commits son apropiados
3. **Checks**: Confirmar que todos los tests pasaron

**Aprobar el PR**:
- Click en **Review changes** → **Approve** → **Submit review**

#### Paso 4: Merge del Pull Request

**Opciones de merge**:

1. **Create a merge commit** (Recomendado):
   - Mantiene historial completo
   - Fácil de revertir

2. **Squash and merge**:
   - Combina todos los commits en uno
   - Historial más limpio

**Ejecutar merge**:
- Click en **Merge pull request** → **Confirm merge**

#### Paso 5: Pipeline de Producción se Ejecuta Automáticamente

**Trigger**: `push` a `main` (resultado del merge)

**Jobs que se ejecutan**:
```
✅ Build Backend
✅ Build Frontend
✅ Unit Tests
✅ SonarQube Analysis
✅ Integration Tests
✅ API Tests
✅ Functional Tests
✅ Performance Tests
✅ Publish Artifacts to Nexus
✅ Tag STABLE Artifact
🚀 Canary Deployment        ← NUEVO (solo en main)
🚀 Production Deployment     ← NUEVO (solo en main)
🚀 Tag GOLD Artifact         ← NUEVO (solo en main)
✅ Post-Deployment Monitoring
✅ Send Notifications
```

#### Paso 6: Verificación Post-Deploy

**Verificaciones automáticas**:
- ✅ Canary deployment exitoso
- ✅ Production deployment exitoso
- ✅ Tag GOLD creado
- ✅ Monitoring activo

**Verificaciones manuales**:
- 🌐 Aplicación accesible en producción
- 📊 Métricas de performance normales
- 🔍 Logs sin errores críticos

## Comparación de Flujos

| Aspecto | Push Directo | Pull Request |
|---------|--------------|--------------|
| **Control** | ❌ Bajo | ✅ Alto |
| **Revisión** | ❌ No | ✅ Obligatoria |
| **Validación** | ⚠️ Solo local | ✅ CI/CD completo |
| **Trazabilidad** | ❌ Limitada | ✅ Completa |
| **Rollback** | ❌ Difícil | ✅ Fácil |

## Flujo de Rollback

Si algo sale mal en producción:

### Opción 1: Revert del Merge Commit
```bash
# 1. Identificar el merge commit
git log --oneline main

# 2. Revert el merge
git revert -m 1 <merge-commit-hash>

# 3. Push del revert
git push origin main
```
→ Esto ejecutará el pipeline nuevamente con el código anterior

### Opción 2: Hotfix via PR
```bash
# 1. Crear branch de hotfix desde main
git checkout -b hotfix/rollback-feature main

# 2. Hacer cambios de corrección
# ... editar archivos ...

# 3. Commit y push
git commit -m "hotfix: corregir problema en producción"
git push origin hotfix/rollback-feature

# 4. Crear PR urgente
gh pr create --base main --head hotfix/rollback-feature
```

## Checklist de Validación

### Pre-PR:
- [ ] Código funciona localmente
- [ ] Tests pasan localmente
- [ ] Pipeline en `develop` exitoso
- [ ] Tag STABLE creado

### Durante PR:
- [ ] Todos los status checks pasan
- [ ] Código revisado por al menos 1 persona
- [ ] Conflictos resueltos
- [ ] Descripción clara del PR

### Post-Merge:
- [ ] Pipeline en `main` exitoso
- [ ] Canary deployment exitoso
- [ ] Production deployment exitoso
- [ ] Tag GOLD creado
- [ ] Aplicación funciona en producción
- [ ] Métricas normales

---

**Implementado**: ✅ Workflow configurado  
**Pendiente**: Branch Protection Rules  
**Próximo paso**: Ejecutar Task 20.2
