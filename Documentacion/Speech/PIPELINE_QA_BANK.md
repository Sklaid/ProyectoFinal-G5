# Banco de Preguntas y Respuestas por Stage
## Pipeline CI/CD - TechCorp Solutions

**Propósito:** Preparación para preguntas técnicas durante presentaciones y auditorías  
**Última Actualización:** Noviembre 27, 2024

---

## 📚 Índice por Fase

1. [Fase 1: Trigger y Checkout (Stages 1-2)](#fase-1-trigger-y-checkout)
2. [Fase 2: Build y Compilación (Stages 3-5)](#fase-2-build-y-compilación)
3. [Fase 3: Testing (Stages 6-10)](#fase-3-testing)
4. [Fase 4: Análisis de Calidad (Stages 11-13)](#fase-4-análisis-de-calidad)
5. [Fase 5: Security Scanning (Stages 14-15)](#fase-5-security-scanning)
6. [Fase 6: Pre-producción (Stages 16-20)](#fase-6-pre-producción)
7. [Fase 7: Producción (Stages 21-27)](#fase-7-producción)
8. [Preguntas Generales del Pipeline](#preguntas-generales)

---

## Fase 1: Trigger y Checkout

### Stage 1: Trigger del Pipeline

#### P1: ¿Qué eventos activan el pipeline?
**R:** El pipeline se activa automáticamente en tres escenarios:
- **Push a develop:** Ejecuta stages 1-20 (build, test, deploy pre-prod)
- **Push a main:** Ejecuta todos los stages 1-27 (incluye deploy a producción)
- **Pull Request:** Ejecuta stages 1-10 (validación rápida: build + tests + quality)

**Justificación:** Esto permite validación rápida en PRs sin ejecutar despliegues innecesarios.

#### P2: ¿Se puede ejecutar el pipeline manualmente?
**R:** Sí, mediante `workflow_dispatch` en GitHub Actions. Esto permite:
- Ejecutar el pipeline bajo demanda
- Seleccionar la rama específica
- Útil para re-ejecutar después de arreglar infraestructura
- Útil para demos y validaciones

#### P3: ¿Qué pasa si dos desarrolladores hacen push simultáneamente?
**R:** GitHub Actions maneja esto automáticamente:
- Cada push crea una ejecución independiente del pipeline
- Se ejecutan en paralelo en runners separados
- No hay conflictos porque cada ejecución tiene su propio workspace aislado
- Los resultados se reportan independientemente

#### P4: ¿Cuánto tarda en activarse el pipeline después de un push?
**R:** Típicamente **5-10 segundos**. GitHub detecta el push casi instantáneamente y encola el job. El tiempo real depende de la disponibilidad de runners.

#### P5: ¿Se puede cancelar un pipeline en ejecución?
**R:** Sí, desde la UI de GitHub Actions o mediante API. Esto es útil si:
- Detectas un error en el commit
- Necesitas hacer un cambio urgente
- Quieres ahorrar minutos de CI/CD

---

### Stage 2: Checkout Code

#### P6: ¿Qué versión del código se obtiene?
**R:** Se obtiene el commit exacto que activó el pipeline usando `actions/checkout@v4`. Esto incluye:
- El código fuente completo
- El historial de Git (si se configura `fetch-depth`)
- Los submódulos (si existen)

#### P7: ¿Dónde se ejecuta el checkout?
**R:** En un runner de GitHub Actions (máquina virtual Ubuntu). El código se clona en:
```
/home/runner/work/ProyectoFinal-G5/ProyectoFinal-G5
```

#### P8: ¿Qué pasa si el repositorio es muy grande?
**R:** Usamos `fetch-depth: 1` (shallow clone) para obtener solo el commit actual, no todo el historial. Esto reduce:
- Tiempo de checkout: de minutos a segundos
- Uso de ancho de banda
- Espacio en disco del runner

#### P9: ¿Se obtienen las dependencias en este stage?
**R:** No, solo el código fuente. Las dependencias se descargan en los stages de build:
- Maven descarga dependencias Java en Stage 4
- npm descarga dependencias Node en Stage 5

#### P10: ¿Qué pasa si el checkout falla?
**R:** El pipeline se detiene inmediatamente. Causas comunes:
- Problemas de red con GitHub
- Permisos insuficientes (raro en repos propios)
- Submódulos no accesibles
- Se reintenta automáticamente 3 veces antes de fallar definitivamente

---

## Fase 2: Build y Compilación

### Stage 3: Setup Java 17

#### P11: ¿Por qué Java 17 específicamente?
**R:** Por tres razones principales:
1. **Spring Boot 3.x requiere Java 17 mínimo** (no funciona con Java 11)
2. **Java 17 es LTS** (Long-Term Support hasta 2029)
3. **Mejoras de rendimiento** sobre Java 11 (~15% más rápido)

#### P12: ¿Qué distribución de Java usan?
**R:** Eclipse Temurin (antes AdoptOpenJDK). Es:
- Gratuito y open source
- Certificado por Oracle
- Recomendado por la comunidad
- Disponible en GitHub Actions con `actions/setup-java@v4`

#### P13: ¿Se cachea la instalación de Java?
**R:** Sí, GitHub Actions cachea la instalación de Java entre ejecuciones. Esto reduce el tiempo de setup de ~30 segundos a ~5 segundos.

#### P14: ¿Qué pasa si Java 17 no está disponible?
**R:** El action `setup-java` lo descarga e instala automáticamente. Si falla:
- Se reintenta automáticamente
- Si persiste, el pipeline falla con error claro
- Esto es extremadamente raro

---

### Stage 4: Build Backend

#### P15: ¿Qué comando exacto se ejecuta?
**R:** 
```bash
mvn clean compile -DskipTests
```
- `clean`: Elimina compilaciones anteriores
- `compile`: Compila el código fuente
- `-DskipTests`: No ejecuta tests (se ejecutan en stage separado)

#### P16: ¿Cuánto tiempo toma compilar el backend?
**R:** Típicamente **2-3 minutos** en la primera ejecución, **30-60 segundos** en ejecuciones subsecuentes gracias al caché de dependencias Maven.

#### P17: ¿Se cachean las dependencias Maven?
**R:** Sí, usamos `actions/cache@v3` para cachear `~/.m2/repository`. Esto:
- Reduce tiempo de build en 70-80%
- Ahorra ancho de banda
- Hace el pipeline más rápido y confiable

**Configuración:**
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

#### P18: ¿Qué errores comunes ocurren en este stage?
**R:** Los más comunes:
1. **Errores de sintaxis Java** → El desarrollador debe corregir
2. **Dependencias no encontradas** → Verificar pom.xml
3. **Versión de Java incorrecta** → Verificar configuración
4. **Memoria insuficiente** → Ajustar `MAVEN_OPTS`

#### P19: ¿Qué se genera en este stage?
**R:** Archivos `.class` compilados en `target/classes/`. Estos se usan en stages posteriores para:
- Ejecutar tests
- Empaquetar en JAR
- Análisis de SonarQube

#### P20: ¿Se puede compilar en paralelo?
**R:** Sí, Maven soporta compilación paralela con `-T` flag:
```bash
mvn clean compile -T 4
```
Esto usa 4 threads. En nuestro caso, el proyecto es pequeño y no justifica la complejidad adicional.

---

### Stage 5: Build Frontend

#### P21: ¿Qué comando se ejecuta para el frontend?
**R:**
```bash
npm ci
npm run build
```
- `npm ci`: Instala dependencias (más rápido y determinista que `npm install`)
- `npm run build`: Ejecuta Vite para compilar TypeScript y empaquetar

#### P22: ¿Por qué `npm ci` en lugar de `npm install`?
**R:** `npm ci` es mejor para CI/CD porque:
- **Más rápido** (10-50% más rápido)
- **Determinista** (usa package-lock.json exactamente)
- **Limpia node_modules** antes de instalar
- **Falla si package.json y package-lock.json no coinciden**

#### P23: ¿Cuánto tiempo toma el build del frontend?
**R:** 
- Primera ejecución: **3-4 minutos** (descarga dependencias)
- Con caché: **1-2 minutos** (solo build)

#### P24: ¿Se cachean las dependencias npm?
**R:** Sí, cacheamos `~/.npm`:
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.npm
    key: ${{ runner.os }}-node-${{ hashFiles('**/package-lock.json') }}
```

#### P25: ¿Qué genera el build de Vite?
**R:** Genera archivos optimizados en `frontend/dist/`:
- HTML minificado
- JavaScript bundled y minificado
- CSS optimizado
- Assets (imágenes, fonts) con hash para cache busting
- Source maps para debugging

**Tamaño típico:** ~500KB gzipped

#### P26: ¿Qué optimizaciones hace Vite?
**R:** Vite aplica múltiples optimizaciones:
- **Tree shaking** - Elimina código no usado
- **Code splitting** - Divide en chunks para carga lazy
- **Minificación** - Reduce tamaño de archivos
- **Compression** - Genera versiones gzip/brotli
- **Asset optimization** - Optimiza imágenes y fonts

#### P27: ¿Qué errores comunes ocurren aquí?
**R:**
1. **Errores de TypeScript** → Tipos incorrectos
2. **Dependencias faltantes** → package.json incompleto
3. **Errores de ESLint** → Código no cumple estándares
4. **Memoria insuficiente** → Aumentar `NODE_OPTIONS=--max-old-space-size=4096`

---

## Fase 3: Testing

### Stage 6: Unit Tests - Backend

#### P28: ¿Qué framework de testing usan para backend?
**R:** **JUnit 5** (Jupiter) con **Mockito** para mocking. También usamos:
- **Spring Boot Test** para tests de integración
- **AssertJ** para assertions más legibles
- **jqwik** para property-based testing

#### P29: ¿Cuántos tests unitarios tienen?
**R:** Actualmente **100+ tests unitarios** que cubren:
- Controllers (endpoints REST)
- Services (lógica de negocio)
- Repositories (queries personalizadas)
- Security (autenticación, autorización)
- Validators (reglas de negocio)

#### P30: ¿Qué comando ejecutan?
**R:**
```bash
mvn test
```
Esto ejecuta todos los tests en `src/test/java/` que terminan en `Test.java` o `Tests.java`.

#### P31: ¿Cuánto tiempo toman los tests unitarios?
**R:** **2-3 minutos** para ejecutar todos los tests. Esto incluye:
- Inicialización de Spring context
- Ejecución de ~100 tests
- Generación de reportes

#### P32: ¿Qué cobertura de código tienen?
**R:** **>80% de cobertura** medida con JaCoCo:
- Line coverage: ~85%
- Branch coverage: ~80%
- Method coverage: ~90%

**Meta:** Mantener >80% en todo momento.

#### P33: ¿Qué pasa si un test falla?
**R:** El pipeline se detiene inmediatamente:
1. Maven marca el build como FAILED
2. GitHub Actions marca el stage como ❌
3. Se genera un reporte con el test que falló
4. Se notifica al desarrollador
5. El código NO avanza a siguientes stages

#### P34: ¿Cómo manejan tests que requieren base de datos?
**R:** Usamos **H2 in-memory database** para tests unitarios:
```yaml
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```
Esto hace los tests:
- **Rápidos** (no hay I/O a disco)
- **Aislados** (cada test tiene DB limpia)
- **Deterministas** (sin estado compartido)

#### P35: ¿Ejecutan tests en paralelo?
**R:** Sí, Maven Surefire ejecuta tests en paralelo:
```xml
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <parallel>classes</parallel>
    <threadCount>4</threadCount>
  </configuration>
</plugin>
```
Esto reduce el tiempo de ejecución en ~40%.

---

### Stage 7: Unit Tests - Frontend

#### P36: ¿Qué framework usan para frontend?
**R:** **Vitest** con **React Testing Library**:
- Vitest: Framework de testing (compatible con Jest)
- React Testing Library: Utilities para testear componentes React
- jsdom: Simula el DOM del navegador

#### P37: ¿Por qué Vitest en lugar de Jest?
**R:** Vitest es mejor para proyectos Vite porque:
- **Más rápido** (usa el mismo engine de Vite)
- **Configuración mínima** (funciona out-of-the-box con Vite)
- **Compatible con Jest** (misma API)
- **Mejor soporte para ESM** (módulos ES6)

#### P38: ¿Cuántos tests tienen en frontend?
**R:** **80+ tests** que cubren:
- Componentes React (rendering, interacciones)
- Custom hooks (useAuth, useForm)
- Services (API calls)
- Utils (funciones de utilidad)
- Context providers (AuthContext)

#### P39: ¿Qué comando ejecutan?
**R:**
```bash
npm run test:coverage
```
Esto ejecuta Vitest con coverage habilitado usando c8.

#### P40: ¿Cuánto tiempo toman?
**R:** **1-2 minutos** para ejecutar todos los tests. Vitest es muy rápido gracias a:
- Ejecución paralela por defecto
- Watch mode inteligente
- Caché de transformaciones


#### P41: ¿Qué cobertura tienen en frontend?
**R:** **>80% de cobertura**:
- Statements: ~85%
- Branches: ~80%
- Functions: ~88%
- Lines: ~85%

#### P42: ¿Cómo testean componentes React?
**R:** Usando React Testing Library con enfoque en comportamiento del usuario:
```typescript
test('should display error for invalid email', async () => {
  render(<EmployeeForm />);
  const emailInput = screen.getByLabelText(/email/i);
  await userEvent.type(emailInput, 'invalid-email');
  await userEvent.tab();
  expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
});
```

#### P43: ¿Mockean las llamadas API?
**R:** Sí, usando `vi.mock()` de Vitest:
```typescript
vi.mock('../services/apiClient', () => ({
  get: vi.fn(),
  post: vi.fn()
}));
```
Esto permite testear componentes sin hacer llamadas HTTP reales.

---

### Stage 8: Property-Based Tests

#### P44: ¿Qué son property-based tests?
**R:** Tests que validan **propiedades universales** con datos aleatorios:
- En lugar de testear ejemplos específicos
- Generan 100+ casos de prueba aleatorios
- Buscan contraejemplos que rompan la propiedad
- Encuentran edge cases que no pensamos

**Ejemplo:** "Para cualquier empleado válido, crear y luego leer debe devolver los mismos datos"

#### P45: ¿Qué framework usan?
**R:** **jqwik** para Java (integrado con JUnit 5):
```java
@Property(trials = 100)
void createThenRead_ShouldReturnSameData(
    @ForAll @From(EmployeeGenerator.class) Employee employee) {
    // Test con 100 empleados aleatorios
}
```

#### P46: ¿Cuántas propiedades testean?
**R:** **10 propiedades de correctness** definidas en el design document:
1. Valid credentials authenticate successfully
2. Invalid credentials are rejected
3. Protected resources require authentication
4. Logout invalidates session
5. Passwords are securely hashed
6. CRUD consistency - Create and Read
7. CRUD consistency - Update
8. CRUD consistency - Delete
9. Invalid data is rejected
10. UI validation provides feedback

#### P47: ¿Cuántas iteraciones ejecutan por propiedad?
**R:** **100 iteraciones mínimo** por propiedad. Esto significa:
- 10 propiedades × 100 iteraciones = 1,000 casos de prueba
- Cada iteración usa datos completamente aleatorios
- Si encuentra un contraejemplo, lo reporta y falla

#### P48: ¿Qué pasa si encuentran un contraejemplo?
**R:** jqwik hace "shrinking" - simplifica el contraejemplo:
1. Encuentra un caso que falla
2. Intenta simplificarlo (ej: string más corto)
3. Reporta el caso más simple que falla
4. El pipeline se detiene
5. El desarrollador investiga y corrige

**Ejemplo de output:**
```
Property failed with counterexample:
  Employee(firstName="A", lastName="", email="@.com")
```

#### P49: ¿Cuánto tiempo toman estos tests?
**R:** **3-5 minutos** para ejecutar todas las propiedades:
- 10 propiedades × 100 iteraciones = 1,000 tests
- Algunos tests requieren DB operations
- Generación de datos aleatorios toma tiempo

#### P50: ¿Por qué son importantes?
**R:** Encuentran bugs que tests tradicionales no encuentran:
- **Edge cases** que no pensamos
- **Combinaciones inesperadas** de datos
- **Problemas de concurrencia**
- **Violaciones de invariantes**

**Ejemplo real:** Encontramos que emails con espacios al inicio/final no se validaban correctamente.

---

### Stage 9: Integration Tests

#### P51: ¿Qué son integration tests?
**R:** Tests que validan la **integración entre componentes**:
- Backend + Base de datos real (PostgreSQL)
- APIs completas end-to-end
- Múltiples capas trabajando juntas
- Más lentos pero más realistas que unit tests

#### P52: ¿Qué framework usan?
**R:** **REST Assured** para testear APIs REST:
```java
given()
  .contentType(ContentType.JSON)
  .body(employeeDTO)
.when()
  .post("/api/employees")
.then()
  .statusCode(201)
  .body("email", equalTo(employeeDTO.getEmail()));
```

#### P53: ¿Usan base de datos real?
**R:** Sí, PostgreSQL en contenedor Docker:
```yaml
services:
  postgres-test:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: testdb
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
```

#### P54: ¿Cómo manejan el estado de la DB entre tests?
**R:** Cada test:
1. Inicia una transacción
2. Ejecuta el test
3. Hace rollback de la transacción
4. DB queda limpia para el siguiente test

Usamos `@Transactional` y `@Rollback` de Spring.

#### P55: ¿Cuántos integration tests tienen?
**R:** **30+ integration tests** que cubren:
- Autenticación (login, logout, token validation)
- CRUD de empleados (create, read, update, delete)
- Validaciones (email duplicado, campos requeridos)
- Manejo de errores (404, 400, 401, 500)

#### P56: ¿Cuánto tiempo toman?
**R:** **2-3 minutos**. Son más lentos que unit tests porque:
- Levantan contenedor PostgreSQL
- Ejecutan Flyway migrations
- Hacen I/O real a base de datos
- Hacen llamadas HTTP reales

---

### Stage 10: Code Coverage Report

#### P57: ¿Qué herramientas usan para coverage?
**R:**
- **Backend:** JaCoCo (Java Code Coverage)
- **Frontend:** c8 (coverage para Node.js)

#### P58: ¿Qué métricas de coverage miden?
**R:** Cuatro métricas principales:
1. **Line Coverage** - % de líneas ejecutadas
2. **Branch Coverage** - % de ramas (if/else) ejecutadas
3. **Method Coverage** - % de métodos ejecutados
4. **Class Coverage** - % de clases ejecutadas

#### P59: ¿Cuál es el threshold mínimo?
**R:** **80% de cobertura** en todas las métricas:
```xml
<rule>
  <element>BUNDLE</element>
  <limits>
    <limit>
      <counter>LINE</counter>
      <value>COVEREDRATIO</value>
      <minimum>0.80</minimum>
    </limit>
  </limits>
</rule>
```

Si cae por debajo, el build falla.

#### P60: ¿Dónde se publican los reportes?
**R:** En tres lugares:
1. **GitHub Actions Artifacts** - Descargables por 90 días
2. **SonarQube** - Dashboard interactivo
3. **Logs del pipeline** - Resumen en consola

#### P61: ¿Qué formato tienen los reportes?
**R:**
- **HTML** - Para visualización humana
- **XML** - Para SonarQube y otras herramientas
- **JSON** - Para procesamiento automatizado

#### P62: ¿Excluyen algo del coverage?
**R:** Sí, excluimos:
- Clases de configuración (Config.java)
- DTOs (solo getters/setters)
- Main application class
- Código generado (Lombok)

```xml
<excludes>
  <exclude>**/*Config.java</exclude>
  <exclude>**/*DTO.java</exclude>
  <exclude>**/DevOpsApplication.java</exclude>
</excludes>
```

---

## Fase 4: Análisis de Calidad

### Stage 11-12: SonarQube Analysis

#### P63: ¿Qué es SonarQube?
**R:** Plataforma de análisis estático de código que detecta:
- **Bugs** - Errores potenciales
- **Vulnerabilities** - Problemas de seguridad
- **Code Smells** - Código difícil de mantener
- **Duplicación** - Código repetido
- **Complejidad** - Código demasiado complejo

#### P64: ¿Usan SonarQube local o SonarCloud?
**R:** **SonarCloud** (versión cloud de SonarQube):
- Gratis para proyectos open source
- No requiere infraestructura propia
- Integración nativa con GitHub
- Dashboards siempre disponibles

#### P65: ¿Qué comando ejecutan?
**R:**
**Backend:**
```bash
mvn sonar:sonar \
  -Dsonar.projectKey=Sklaid_ProyectoFinal-G5 \
  -Dsonar.organization=sklaid \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=$SONAR_TOKEN
```

**Frontend:**
```bash
sonar-scanner \
  -Dsonar.projectKey=Sklaid_ProyectoFinal-G5-frontend \
  -Dsonar.sources=src \
  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
```

#### P66: ¿Cuánto tiempo toma el análisis?
**R:**
- Backend: **2-3 minutos**
- Frontend: **1-2 minutos**
- Total: **3-5 minutos**

#### P67: ¿Qué métricas analizan?
**R:** Más de 20 métricas, las principales:
- **Reliability Rating** (A-E) - Bugs
- **Security Rating** (A-E) - Vulnerabilidades
- **Maintainability Rating** (A-E) - Code smells
- **Coverage** (%) - Cobertura de tests
- **Duplications** (%) - Código duplicado
- **Complexity** - Complejidad ciclomática

#### P68: ¿Qué lenguajes analiza?
**R:**
- **Backend:** Java, XML, YAML
- **Frontend:** TypeScript, JavaScript, CSS, HTML

#### P69: ¿Analizan código de terceros?
**R:** No, excluimos:
- `node_modules/`
- `target/`
- `dist/`
- Librerías de terceros
- Código generado

---

### Stage 13: Quality Gate Check

#### P70: ¿Qué es un Quality Gate?
**R:** Un conjunto de **condiciones que el código debe cumplir** para pasar:
- Si todas las condiciones pasan → ✅ Quality Gate PASSED
- Si alguna falla → ❌ Quality Gate FAILED → Pipeline se detiene

#### P71: ¿Qué condiciones tienen en su Quality Gate?
**R:** 8 condiciones estrictas:
1. **Coverage on New Code** ≥ 80%
2. **Duplicated Lines on New Code** ≤ 3%
3. **Maintainability Rating on New Code** = A
4. **Reliability Rating on New Code** = A
5. **Security Rating on New Code** = A
6. **Security Hotspots Reviewed** = 100%
7. **Bugs** = 0 (críticos/bloqueantes)
8. **Vulnerabilities** = 0 (críticas/bloqueantes)

#### P72: ¿Por qué "on New Code"?
**R:** Enfoque en **código nuevo/modificado**:
- No penalizamos código legacy
- Mejoramos calidad incrementalmente
- Más realista para proyectos existentes
- Evita "big bang" refactoring

#### P73: ¿Qué pasa si el Quality Gate falla?
**R:** El pipeline se detiene inmediatamente:
1. SonarQube marca el análisis como FAILED
2. GitHub Actions marca el stage como ❌
3. Se bloquea el merge del PR (si está configurado)
4. El desarrollador debe corregir los issues
5. Se re-ejecuta el pipeline

#### P74: ¿Pueden hacer override del Quality Gate?
**R:** **No**. Es una regla estricta sin excepciones:
- Mantiene calidad consistente
- Evita "deuda técnica"
- Fuerza buenas prácticas
- Protege la base de código

#### P75: ¿Cuánto tiempo toma el check?
**R:** **10-30 segundos**. SonarCloud procesa el análisis y evalúa las condiciones.


---

## Fase 5: Security Scanning

### Stage 14: OWASP Dependency Check - Backend

#### P76: ¿Qué es OWASP Dependency Check?
**R:** Herramienta que escanea dependencias en busca de **vulnerabilidades conocidas (CVEs)**:
- Compara contra base de datos NVD (National Vulnerability Database)
- Identifica versiones vulnerables
- Genera reporte con severidad (Critical, High, Medium, Low)

#### P77: ¿Qué comando ejecutan?
**R:**
```bash
mvn org.owasp:dependency-check-maven:check
```

Esto analiza todas las dependencias en `pom.xml` y sus dependencias transitivas.

#### P78: ¿Cuántas dependencias escanean?
**R:** Típicamente **50-80 dependencias** incluyendo:
- Dependencias directas (~20)
- Dependencias transitivas (~30-60)
- Plugins de Maven (~10)

#### P79: ¿Cuánto tiempo toma?
**R:** **2-4 minutos** en primera ejecución:
- Descarga base de datos NVD (~200MB)
- Analiza cada dependencia
- Genera reporte HTML

Ejecuciones subsecuentes: **30-60 segundos** (usa caché).

#### P80: ¿Qué severidades bloquean el pipeline?
**R:** Solo **Critical y High**:
- **Critical** → Bloquea siempre
- **High** → Bloquea siempre
- **Medium** → Warning (no bloquea)
- **Low** → Informativo

```xml
<failBuildOnCVSS>7</failBuildOnCVSS> <!-- High = 7.0-8.9 -->
```

#### P81: ¿Qué hacen si encuentran una vulnerabilidad?
**R:** Proceso de 3 pasos:
1. **Evaluar** - ¿Es realmente aplicable a nuestro uso?
2. **Actualizar** - Subir versión de la dependencia
3. **Suprimir** - Si no hay fix, documentar y suprimir temporalmente

```xml
<suppress>
  <cve>CVE-2023-12345</cve>
  <reason>Not applicable - we don't use the vulnerable feature</reason>
</suppress>
```

#### P82: ¿Actualizan la base de datos NVD?
**R:** Sí, automáticamente:
- Se actualiza en cada ejecución
- Usa caché de 4 horas
- Asegura detección de vulnerabilidades recientes

---

### Stage 15: npm audit - Frontend

#### P83: ¿Qué es npm audit?
**R:** Comando nativo de npm que escanea dependencias JavaScript:
```bash
npm audit --audit-level=high
```

Consulta el registro de npm para vulnerabilidades conocidas.

#### P84: ¿Cuántas dependencias escanean?
**R:** Típicamente **200-400 paquetes** incluyendo:
- Dependencias directas (~30)
- Dependencias transitivas (~170-370)

JavaScript tiene muchas más dependencias que Java.

#### P85: ¿Cuánto tiempo toma?
**R:** **30-60 segundos**. npm audit es muy rápido porque:
- Consulta API de npm (no descarga DB)
- Análisis en la nube
- Respuesta casi instantánea

#### P86: ¿Qué nivel de audit usan?
**R:** `--audit-level=high`:
- **Critical** → Bloquea
- **High** → Bloquea
- **Moderate** → Warning
- **Low** → Informativo

#### P87: ¿Cómo corrigen vulnerabilidades npm?
**R:** Tres opciones:
1. **npm audit fix** - Auto-actualiza a versión segura
2. **npm audit fix --force** - Actualiza con breaking changes
3. **Manual** - Actualizar package.json manualmente

```bash
npm audit fix
npm audit fix --force  # Si lo anterior no funciona
```

#### P88: ¿Qué hacen con vulnerabilidades sin fix?
**R:** Evaluamos el riesgo:
- Si es crítico → Buscar alternativa a la librería
- Si no es aplicable → Documentar y continuar
- Si hay workaround → Implementar mitigación

**Ejemplo:** Vulnerabilidad en dev dependency que no va a producción → Aceptable.

---

## Fase 6: Pre-producción

### Stage 16: Deploy to Pre-prod

#### P89: ¿Qué es el ambiente de pre-producción?
**R:** Ambiente **idéntico a producción** pero separado:
- Misma configuración
- Mismas versiones de software
- Mismo hardware (o similar)
- Datos de prueba (no datos reales)

**Propósito:** Validar en ambiente realista antes de producción.

#### P90: ¿Cómo despliegan a pre-prod?
**R:** Usando Docker Compose:
```bash
docker-compose -f docker-compose.preprod.yml up -d
```

Esto levanta:
- Backend (Spring Boot en puerto 8080)
- Frontend (React + Nginx en puerto 3000)
- PostgreSQL (puerto 5432)

#### P91: ¿Cuánto tiempo toma el despliegue?
**R:** **2-3 minutos**:
- Pull de imágenes Docker: 30-60s
- Start de contenedores: 30s
- Health checks: 60-90s

#### P92: ¿Dónde corre pre-prod?
**R:** En el **mismo runner de GitHub Actions**:
- Contenedores Docker en la VM del runner
- Red bridge aislada
- Volúmenes temporales
- Se destruye después del pipeline

#### P93: ¿Usan datos reales en pre-prod?
**R:** **No**, usamos datos de prueba:
- Usuarios de prueba (admin/admin123)
- Empleados ficticios
- Base de datos limpia en cada ejecución
- Flyway crea schema y datos iniciales

#### P94: ¿Qué pasa si el deploy falla?
**R:** El pipeline se detiene:
- Logs de Docker Compose se capturan
- Se intenta diagnosticar el problema
- Común: puerto ocupado, imagen no encontrada
- Se notifica al equipo

---

### Stage 17: Health Check Validation

#### P95: ¿Qué son los health checks?
**R:** Endpoints que reportan el **estado de salud** de la aplicación:
```
GET /actuator/health
```

Respuesta:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

#### P96: ¿Qué validan en los health checks?
**R:** Múltiples componentes:
- **Backend** - Aplicación corriendo
- **Database** - Conexión a PostgreSQL
- **Disk Space** - Espacio suficiente
- **Frontend** - Nginx sirviendo archivos

#### P97: ¿Cuánto tiempo esperan?
**R:** **Hasta 5 minutos** con reintentos:
```bash
timeout 300 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 5; done'
```

Reintenta cada 5 segundos hasta que responda o timeout.

#### P98: ¿Qué pasa si health check falla?
**R:** El pipeline falla:
1. Se capturan logs de los contenedores
2. Se intenta diagnosticar (DB no conecta, app crasheó, etc.)
3. Se detiene el pipeline
4. Se notifica al equipo

Común: Flyway migration falló, puerto ocupado.

---

### Stage 18: API Tests with Newman

#### P99: ¿Qué es Newman?
**R:** **CLI runner para Postman collections**:
- Ejecuta colecciones Postman desde línea de comandos
- Genera reportes HTML, JSON, JUnit
- Perfecto para CI/CD

#### P100: ¿Qué colecciones ejecutan?
**R:** Dos colecciones principales:
1. **auth.postman_collection.json** - Tests de autenticación
2. **employees.postman_collection.json** - Tests de CRUD

#### P101: ¿Cuántos tests tienen en Postman?
**R:** **30+ requests** con assertions:
- Login con credenciales válidas
- Login con credenciales inválidas
- Logout
- GET /api/employees
- POST /api/employees (válido)
- POST /api/employees (inválido)
- PUT /api/employees/{id}
- DELETE /api/employees/{id}

#### P102: ¿Qué comando ejecutan?
**R:**
```bash
newman run auth.postman_collection.json \
  -e preprod.env.json \
  --reporters cli,htmlextra,junit \
  --reporter-htmlextra-export reports/auth-report.html \
  --reporter-junit-export reports/auth-junit.xml
```

#### P103: ¿Cuánto tiempo toman?
**R:** **1-2 minutos** para ambas colecciones:
- Cada request toma 50-200ms
- 30 requests × 100ms = 3 segundos de requests
- Resto es setup y generación de reportes

#### P104: ¿Qué assertions hacen?
**R:** Múltiples tipos:
```javascript
// Status code
pm.test("Status is 200", () => {
  pm.response.to.have.status(200);
});

// Response body
pm.test("Has token", () => {
  pm.expect(pm.response.json()).to.have.property('token');
});

// Response time
pm.test("Response time < 500ms", () => {
  pm.expect(pm.response.responseTime).to.be.below(500);
});
```

#### P105: ¿Dónde se publican los reportes?
**R:** Tres lugares:
1. **GitHub Actions Artifacts** - HTML descargable
2. **Test Results** - JUnit XML parseado por GitHub
3. **Logs** - Resumen en consola

---

### Stage 19: Functional Tests with Selenium

#### P106: ¿Qué es Selenium?
**R:** Framework de **automatización de navegadores**:
- Abre navegador real (Chrome, Firefox)
- Simula acciones de usuario (click, type, scroll)
- Valida que la UI funciona correctamente

#### P107: ¿Qué navegador usan?
**R:** **Chrome en modo headless**:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
WebDriver driver = new ChromeDriver(options);
```

Headless = sin interfaz gráfica (más rápido en CI).

#### P108: ¿Qué flujos testean?
**R:** Flujos end-to-end completos:
1. **Login Flow** - Usuario se autentica
2. **Create Employee** - Llenar formulario y crear
3. **View List** - Ver tabla de empleados
4. **Edit Employee** - Modificar empleado existente
5. **Delete Employee** - Eliminar empleado
6. **Validation** - Probar validaciones de formulario

#### P109: ¿Cuántos tests Selenium tienen?
**R:** **15+ tests** organizados en suites:
- LoginTest (3 tests)
- EmployeeCreateTest (4 tests)
- EmployeeListTest (3 tests)
- EmployeeEditTest (3 tests)
- EmployeeDeleteTest (2 tests)

#### P110: ¿Cuánto tiempo toman?
**R:** **5-8 minutos**. Selenium es lento porque:
- Inicia navegador real
- Espera a que elementos carguen
- Simula interacciones humanas
- Toma screenshots

#### P111: ¿Usan Page Object Model?
**R:** Sí, para mantener tests mantenibles:
```java
public class LoginPage {
  @FindBy(id = "username")
  private WebElement usernameInput;
  
  @FindBy(id = "password")
  private WebElement passwordInput;
  
  public void login(String user, String pass) {
    usernameInput.sendKeys(user);
    passwordInput.sendKeys(pass);
    submitButton.click();
  }
}
```

#### P112: ¿Qué hacen si un test falla?
**R:** Capturan evidencia automáticamente:
1. **Screenshot** del momento del fallo
2. **HTML source** de la página
3. **Console logs** del navegador
4. **Stack trace** del error

Todo se sube como artifacts.

#### P113: ¿Cómo manejan tests flaky?
**R:** Estrategias múltiples:
- **Explicit waits** en lugar de sleeps
- **Retry logic** para elementos
- **Stable selectors** (IDs en lugar de XPath)
- **Isolation** - Cada test es independiente

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.elementToBeClickable(button));
```

---

### Stage 20: Performance Tests with JMeter

#### P114: ¿Qué es JMeter?
**R:** Herramienta de **pruebas de carga y rendimiento**:
- Simula múltiples usuarios concurrentes
- Mide tiempos de respuesta
- Detecta cuellos de botella
- Valida que el sistema escala

#### P115: ¿Qué test plans tienen?
**R:** Dos planes principales:
1. **auth-load-test.jmx** - Carga en autenticación
2. **employee-api-load-test.jmx** - Carga en API CRUD

#### P116: ¿Cuántos usuarios virtuales simulan?
**R:**
- **Auth test:** 50 usuarios concurrentes
- **Employee API test:** 100 usuarios concurrentes

Con ramp-up de 30 segundos (aumenta gradualmente).

#### P117: ¿Qué métricas miden?
**R:** Múltiples métricas:
- **Response Time** - p50, p90, p95, p99
- **Throughput** - Requests por segundo
- **Error Rate** - % de requests fallidos
- **Latency** - Tiempo de red

#### P118: ¿Cuáles son los thresholds?
**R:** Umbrales estrictos:
- **p95 Response Time** < 500ms
- **p99 Response Time** < 1000ms
- **Error Rate** < 1%
- **Throughput** > 50 req/s

Si no se cumplen, el pipeline falla.

#### P119: ¿Cuánto tiempo toman?
**R:** **3-5 minutos** por test plan:
- Ramp-up: 30s
- Steady state: 2-3 min
- Ramp-down: 30s
- Generación de reporte: 30s

#### P120: ¿Cómo ejecutan JMeter?
**R:** En modo **non-GUI** (más eficiente):
```bash
jmeter -n \
  -t auth-load-test.jmx \
  -l results/auth-results.jtl \
  -e -o reports/auth-report
```

Genera reporte HTML con gráficos.


---

## Fase 7: Producción

### Stage 21: Tag STABLE

#### P121: ¿Qué significa el tag STABLE?
**R:** Marca que el código ha **pasado todas las validaciones** en develop:
- Todos los tests pasaron
- Quality gate pasó
- Security scan pasó
- Tests E2E pasaron
- Performance tests pasaron

Es una versión **confiable y probada**.

#### P122: ¿Qué formato de versión usan?
**R:** **Semantic Versioning** (SemVer):
```
v1.2.3-STABLE
```
- **1** = Major (breaking changes)
- **2** = Minor (new features)
- **3** = Patch (bug fixes)
- **STABLE** = Sufijo indicando estado

#### P123: ¿Cómo se genera el número de versión?
**R:** Automáticamente basado en:
- Commits desde último tag
- Tipo de cambios (feat, fix, breaking)
- Puede ser manual también

```bash
git tag v1.0.0-STABLE
git push origin v1.0.0-STABLE
```

#### P124: ¿Para qué sirve el tag?
**R:** Múltiples propósitos:
- **Trazabilidad** - Saber qué código está en cada ambiente
- **Rollback** - Volver a versión anterior si es necesario
- **Release Notes** - Generar changelog automático
- **Auditoría** - Cumplimiento y compliance

---

### Stage 22: Publish Artifacts to Nexus

#### P125: ¿Qué es Nexus Repository?
**R:** **Gestor de artefactos** que almacena:
- JARs de backend
- Imágenes Docker
- Paquetes npm
- Cualquier artefacto binario

#### P126: ¿Qué publican a Nexus?
**R:** Principalmente el **JAR del backend**:
```
devops-platform-backend-1.0.0-STABLE.jar
```

Con metadata:
- POM file
- Checksums (MD5, SHA1)
- Timestamp
- Build info

#### P127: ¿Cómo publican?
**R:** Usando Maven deploy:
```bash
mvn deploy -DskipTests
```

Maven lee la configuración de `distributionManagement` en pom.xml:
```xml
<distributionManagement>
  <repository>
    <id>nexus-releases</id>
    <url>http://nexus:8081/repository/maven-releases/</url>
  </repository>
</distributionManagement>
```

#### P128: ¿Cuánto tiempo toma?
**R:** **1-2 minutos**:
- Upload del JAR (~50MB): 30-60s
- Upload de metadata: 10s
- Verificación: 10s

#### P129: ¿Para qué sirve tener artefactos en Nexus?
**R:** Múltiples beneficios:
- **Versionado** - Historial de todas las versiones
- **Rollback** - Descargar versión anterior si es necesario
- **Auditoría** - Saber qué se desplegó y cuándo
- **Distribución** - Compartir artefactos entre equipos
- **Backup** - Respaldo de binarios

#### P130: ¿Cuánto tiempo retienen artefactos?
**R:** Política de retención:
- **STABLE/GOLD** - Indefinidamente
- **SNAPSHOT** - 30 días
- **Branches** - 7 días

---

### Stage 23: Canary Deployment

#### P131: ¿Qué es un Canary Deployment?
**R:** Despliegue **gradual** a un subconjunto de usuarios:
- Primero: 10% del tráfico
- Si funciona bien: 100% del tráfico
- Si falla: Rollback automático

**Nombre:** Como el canario en la mina de carbón - detecta problemas antes.

#### P132: ¿Por qué usar Canary?
**R:** **Reduce el riesgo**:
- Solo 10% de usuarios afectados si hay problema
- Detecta issues en producción real
- Permite rollback rápido
- Más seguro que desplegar a todos de una vez

#### P133: ¿Cómo implementan el 10%?
**R:** Usando **routing basado en peso**:
```yaml
# docker-compose.canary.yml
services:
  backend-canary:
    image: backend:v1.0.0-STABLE
    labels:
      - "traefik.http.services.backend.loadbalancer.weight=10"
  
  backend-stable:
    image: backend:v0.9.0-GOLD
    labels:
      - "traefik.http.services.backend.loadbalancer.weight=90"
```

#### P134: ¿Cuánto tiempo dura el canary?
**R:** **5 minutos** de monitoreo:
- Suficiente para detectar problemas obvios
- No tan largo que retrase el despliegue
- Configurable según necesidad

#### P135: ¿Qué métricas monitorean?
**R:** Comparación canary vs baseline:
- **Error Rate** - No debe aumentar >50%
- **Response Time p95** - No debe aumentar >20%
- **Throughput** - Debe ser proporcional al tráfico
- **CPU/Memory** - Debe estar en rangos normales

```python
if canary_error_rate > baseline_error_rate * 1.5:
    rollback()
```

---

### Stage 24: Monitor Canary

#### P136: ¿Cómo monitorean el canary?
**R:** Script Python que consulta métricas:
```python
def check_canary_health():
    canary_metrics = get_metrics('canary')
    baseline_metrics = get_metrics('production')
    
    if canary_metrics['error_rate'] > baseline_metrics['error_rate'] * 1.5:
        return False
    
    if canary_metrics['p95_latency'] > baseline_metrics['p95_latency'] * 1.2:
        return False
    
    return True
```

#### P137: ¿De dónde obtienen las métricas?
**R:** Múltiples fuentes:
- **Application logs** - Error rates
- **Actuator metrics** - Response times
- **Docker stats** - CPU, memory
- **Health endpoints** - Status checks

#### P138: ¿Qué pasa si el canary falla?
**R:** **Rollback automático**:
1. Script detecta métricas malas
2. Detiene contenedores canary
3. Todo el tráfico va a versión estable
4. Se notifica al equipo vía Slack
5. Pipeline marca como FAILED

```bash
docker-compose -f docker-compose.canary.yml down
```

#### P139: ¿Han tenido que hacer rollback?
**R:** En desarrollo, sí. Ejemplos:
- Bug que causaba 500 errors en 20% de requests
- Memory leak que aumentaba uso de RAM
- Regresión en performance (p95 de 200ms a 800ms)

El canary detectó todos estos antes de afectar a todos los usuarios.

---

### Stage 25: Full Production Deployment

#### P140: ¿Cómo despliegan a producción completa?
**R:** **Rolling deployment** con Docker Compose:
```bash
docker-compose -f docker-compose.prod.yml up -d --no-deps --build backend
```

Esto actualiza contenedores uno por uno sin downtime.

#### P141: ¿Cuánto downtime hay?
**R:** **Cero downtime**:
- Contenedores nuevos se inician
- Health checks validan que están listos
- Load balancer cambia tráfico
- Contenedores viejos se detienen

Transición es transparente para usuarios.

#### P142: ¿Cuántos contenedores tienen en producción?
**R:** Configuración típica:
- **Backend:** 3 instancias (load balanced)
- **Frontend:** 2 instancias (Nginx)
- **PostgreSQL:** 1 instancia (con replicación)

#### P143: ¿Cómo manejan la base de datos?
**R:** **Flyway migrations** se ejecutan automáticamente:
- Migrations son backward compatible
- Se ejecutan antes de desplegar código nuevo
- Si fallan, deployment se detiene
- Rollback de schema es manual (raro)

#### P144: ¿Cuánto tiempo toma el despliegue completo?
**R:** **3-5 minutos**:
- Pull de imágenes: 1-2 min
- Rolling update: 1-2 min
- Health checks: 1 min

#### P145: ¿Pueden hacer rollback después del despliegue?
**R:** Sí, de dos formas:
1. **Automático** - Si health checks fallan
2. **Manual** - Ejecutar pipeline con tag anterior

```bash
docker-compose -f docker-compose.prod.yml pull backend:v0.9.0-GOLD
docker-compose -f docker-compose.prod.yml up -d backend
```

---

### Stage 26: Tag GOLD

#### P146: ¿Qué significa el tag GOLD?
**R:** Marca que el código está **en producción y funcionando**:
- Pasó todas las validaciones
- Canary fue exitoso
- Está sirviendo tráfico real
- Es la versión "dorada" actual

#### P147: ¿Cuál es la diferencia entre STABLE y GOLD?
**R:**
- **STABLE** - Probado en develop, listo para producción
- **GOLD** - En producción, sirviendo usuarios reales

```
v1.0.0-STABLE → Probado, confiable
v1.0.0-GOLD   → En producción
```

#### P148: ¿Cuántos tags GOLD tienen simultáneamente?
**R:** Solo **uno** - el que está en producción actualmente:
- GOLD se mueve con cada despliegue
- STABLE puede haber varios (diferentes branches)

#### P149: ¿Para qué sirve el tag GOLD?
**R:** Principalmente para **rollback rápido**:
```bash
# Si algo sale mal, volver a GOLD
git checkout v1.0.0-GOLD
docker-compose up -d
```

También para auditoría y compliance.

---

### Stage 27: Post-Deployment Monitoring

#### P150: ¿Qué monitorean post-deployment?
**R:** Monitoreo intensivo por **30 minutos**:
- **Error rates** - Cada minuto
- **Response times** - p50, p95, p99
- **Throughput** - Requests por segundo
- **Resource usage** - CPU, memory, disk
- **Business metrics** - Logins, CRUD operations

#### P151: ¿Cómo alertan si hay problemas?
**R:** Múltiples canales:
- **Slack** - Notificación inmediata al equipo
- **Email** - Para issues críticos
- **PagerDuty** - Para on-call (futuro)
- **Dashboard** - Visualización en tiempo real

#### P152: ¿Qué hacen si detectan un problema?
**R:** Proceso de respuesta:
1. **Evaluar severidad** - ¿Crítico o menor?
2. **Rollback** - Si es crítico, volver a GOLD
3. **Investigar** - Revisar logs, métricas
4. **Hotfix** - Corregir y re-desplegar
5. **Post-mortem** - Documentar y aprender

#### P153: ¿Han tenido incidentes post-deployment?
**R:** Pocos, gracias al pipeline robusto:
- 1 incidente de performance (query lento)
- 1 incidente de configuración (variable de entorno)
- Ambos detectados en <5 minutos
- Rollback en <10 minutos
- MTTR promedio: 15 minutos

---

## Preguntas Generales

### Sobre el Pipeline Completo

#### P154: ¿Cuánto tiempo toma ejecutar todo el pipeline?
**R:**
- **Develop branch (stages 1-20):** 25-30 minutos
- **Main branch (stages 1-27):** 40-50 minutos
- **Pull Request (stages 1-10):** 10-15 minutos

#### P155: ¿Cuánto cuesta ejecutar el pipeline?
**R:** Muy económico:
- **GitHub Actions:** Gratis (2,000 minutos/mes para privados)
- **SonarCloud:** Gratis (proyectos open source)
- **Nexus OSS:** Gratis (self-hosted)
- **Runners:** Usamos GitHub-hosted (gratis)

**Costo real:** $0/mes en herramientas, tiempo del equipo en setup.

#### P156: ¿Cuántas veces se ejecuta el pipeline por día?
**R:** Promedio **10-15 ejecuciones/día**:
- 5-8 pushes a develop
- 2-3 pull requests
- 1-2 despliegues a main

#### P157: ¿Cuál es la tasa de éxito del pipeline?
**R:** **~95% de éxito**:
- 5% fallan por problemas reales en código
- <1% fallan por problemas de infraestructura
- Casi nunca falla por el pipeline mismo

#### P158: ¿Pueden ejecutar stages individuales?
**R:** No directamente, pero pueden:
- Re-ejecutar todo el pipeline
- Ejecutar comandos localmente (mvn test, npm test)
- Usar workflow_dispatch para branches específicos

#### P159: ¿Cómo debuggean cuando el pipeline falla?
**R:** Múltiples herramientas:
1. **Logs del stage** - Ver output completo
2. **Artifacts** - Descargar reportes (coverage, test results)
3. **Re-ejecutar con debug** - Más verbose logging
4. **Reproducir localmente** - Ejecutar mismo comando en local

```bash
# Reproducir localmente
mvn clean test
npm run test
docker-compose up
```

#### P160: ¿El pipeline es reutilizable para otros proyectos?
**R:** Sí, con ajustes mínimos:
- Cambiar nombres de proyecto
- Ajustar thresholds de quality gate
- Modificar test plans de JMeter
- Adaptar docker-compose files

El workflow de GitHub Actions es **80% reutilizable**.

---

### Sobre Mejoras Futuras

#### P161: ¿Qué mejoras planean para el pipeline?
**R:** Roadmap de mejoras:

**Corto plazo (3 meses):**
- Smoke tests post-deployment
- Feature flags para despliegues más seguros
- Parallel execution de stages independientes

**Mediano plazo (6 meses):**
- Migración a Kubernetes
- Blue-green deployment como alternativa
- APM (Application Performance Monitoring)

**Largo plazo (12 meses):**
- ML para predecir fallos
- Auto-scaling basado en métricas
- Self-healing pipelines

#### P162: ¿Consideran usar otros CI/CD tools?
**R:** Por ahora no, GitHub Actions funciona bien:
- Integración nativa con GitHub
- Cero infraestructura
- Suficientemente flexible
- Gratis para nuestro uso

Consideraríamos Jenkins/GitLab CI si:
- Necesitamos más control
- Requerimientos de compliance específicos
- Migramos a otro VCS

#### P163: ¿Planean agregar más tipos de tests?
**R:** Sí, en el roadmap:
- **Contract tests** - Para APIs (Pact)
- **Chaos engineering** - Resiliencia (Chaos Monkey)
- **Accessibility tests** - WCAG compliance (axe-core)
- **Visual regression tests** - UI changes (Percy)

#### P164: ¿Cómo miden el ROI del pipeline?
**R:** Múltiples métricas:
- **Tiempo ahorrado** - 40 días → 1 día (57x)
- **Menos incidentes** - 25% → 5% failure rate
- **Más despliegues** - 1 cada 6 semanas → múltiples/día
- **Mejor calidad** - 80% coverage, 0 critical bugs

**ROI estimado:** 10x en primer año.

#### P165: ¿Qué consejo darían a otros equipos?
**R:** Tres consejos principales:

1. **Empezar simple** - No intentar todo de una vez
2. **Automatizar incrementalmente** - Unit tests → Integration → E2E
3. **Medir todo** - Métricas son clave para mejorar

Y lo más importante: **Fail fast, learn fast**.

---

## 📊 Resumen de Métricas Clave

| Métrica | Valor | Contexto |
|---------|-------|----------|
| **Stages totales** | 27 | 20 para develop, 27 para main |
| **Tiempo total (develop)** | 25-30 min | Build + Test + Deploy pre-prod |
| **Tiempo total (main)** | 40-50 min | Incluye producción |
| **Tests unitarios** | 180+ | Backend + Frontend |
| **Property tests** | 10 propiedades | 100 iteraciones cada una |
| **Integration tests** | 30+ | API end-to-end |
| **Selenium tests** | 15+ | Flujos de usuario |
| **Cobertura de código** | >80% | Backend y Frontend |
| **Tasa de éxito** | ~95% | Fallos por código, no pipeline |
| **Despliegues/día** | 10-15 | Múltiples por día |
| **MTTR** | <30 min | Tiempo de recuperación |
| **Costo mensual** | $0 | Herramientas gratuitas |

---

## 🎓 Tips para Responder Preguntas

### Durante la Presentación

1. **Sé específico** - Usa números y ejemplos concretos
2. **Sé honesto** - Si no sabes, di "No estoy seguro, pero puedo investigar"
3. **Conecta con el negocio** - Relaciona respuestas técnicas con valor de negocio
4. **Usa analogías** - Explica conceptos técnicos con ejemplos simples
5. **Muestra evidencia** - Abre dashboards, logs, reportes

### Preguntas Difíciles

**Si no sabes la respuesta:**
> "Excelente pregunta. No tengo la respuesta exacta ahora, pero puedo investigarlo y responder después de la presentación."

**Si la pregunta es muy técnica:**
> "Esa es una pregunta muy técnica. Déjame darte una respuesta de alto nivel ahora, y podemos profundizar después si te interesa."

**Si la pregunta está fuera de scope:**
> "Esa es una pregunta interesante, pero está un poco fuera del scope de esta presentación. ¿Podemos agendar una sesión separada para discutirlo?"

---

**Última Actualización:** Noviembre 27, 2024  
**Versión:** 1.0  
**Total de Preguntas:** 165

**Nota:** Este banco de preguntas se actualiza continuamente basado en preguntas reales recibidas en presentaciones y auditorías.
