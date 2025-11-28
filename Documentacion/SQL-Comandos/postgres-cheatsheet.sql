-- ============================================
-- POSTGRESQL CHEATSHEET - DevOps Platform
-- ============================================
-- Comandos útiles para gestionar la base de datos
-- Ejecuta cada query individualmente según necesites

-- ============================================
-- 1. CONSULTAS DE INFORMACIÓN
-- ============================================

-- Ver todos los empleados con sus skills
SELECT 
    e.id,
    e.first_name,
    e.last_name,
    e.email,
    e.phone,
    e.gender,
    e.department,
    e.level,
    e.hire_date,
    STRING_AGG(es.skills, ', ') as skills
FROM employees e
LEFT JOIN employee_skills es ON e.id = es.employee_id
GROUP BY e.id, e.first_name, e.last_name, e.email, e.phone, e.gender, e.department, e.level, e.hire_date
ORDER BY e.id;

-- Ver solo la tabla employees (sin skills)
SELECT * FROM employees ORDER BY id;

-- Ver solo la tabla employee_skills
SELECT * FROM employee_skills ORDER BY employee_id, skills;

-- Contar empleados por departamento
SELECT department, COUNT(*) as total
FROM employees
GROUP BY department
ORDER BY total DESC;

-- Contar empleados por nivel
SELECT level, COUNT(*) as total
FROM employees
GROUP BY level
ORDER BY total DESC;

-- Ver empleados con más skills
SELECT 
    e.first_name,
    e.last_name,
    COUNT(es.skills) as skill_count,
    STRING_AGG(es.skills, ', ') as skills
FROM employees e
LEFT JOIN employee_skills es ON e.id = es.employee_id
GROUP BY e.id, e.first_name, e.last_name
ORDER BY skill_count DESC;

-- Buscar empleado por email
SELECT * FROM employees WHERE email = 'sklaid123@gmail.com';

-- Buscar empleados por skill específico
SELECT 
    e.first_name,
    e.last_name,
    e.email,
    es.skills
FROM employees e
JOIN employee_skills es ON e.id = es.employee_id
WHERE es.skills = 'JavaScript';

-- Ver todos los usuarios (tabla de autenticación)
SELECT id, username, email, role, active, created_at, last_login
FROM users
ORDER BY id;

-- ============================================
-- 2. INSERTAR DATOS DE EJEMPLO
-- ============================================

-- Insertar un empleado con skills
-- Paso 1: Insertar el empleado
INSERT INTO employees (first_name, last_name, email, phone, gender, department, level, hire_date)
VALUES ('Juan', 'Pérez', 'juan.perez@techcorp.com', '+51-999-123-456', 'MALE', 'IT', 'SENIOR', '2024-01-15')
RETURNING id;  -- Esto te muestra el ID generado

-- Paso 2: Insertar skills para ese empleado (reemplaza 1 con el ID del paso anterior)
INSERT INTO employee_skills (employee_id, skills)
VALUES 
    (1, 'Java'),
    (1, 'Spring Boot'),
    (1, 'Docker');

-- Insertar varios empleados de una vez
INSERT INTO employees (first_name, last_name, email, phone, gender, department, level, hire_date)
VALUES 
    ('María', 'García', 'maria.garcia@techcorp.com', '+51-999-222-333', 'FEMALE', 'HR', 'MID', '2024-02-01'),
    ('Carlos', 'López', 'carlos.lopez@techcorp.com', '+51-999-444-555', 'MALE', 'FINANCE', 'LEAD', '2023-12-15'),
    ('Ana', 'Martínez', 'ana.martinez@techcorp.com', '+51-999-666-777', 'FEMALE', 'IT', 'JUNIOR', '2024-03-10');

-- Insertar skills para múltiples empleados (ajusta los IDs según corresponda)
INSERT INTO employee_skills (employee_id, skills)
VALUES 
    (2, 'Communication'),
    (2, 'Recruitment'),
    (3, 'Excel'),
    (3, 'Financial Analysis'),
    (4, 'Python'),
    (4, 'React'),
    (4, 'TypeScript');

-- ============================================
-- 3. ACTUALIZAR DATOS
-- ============================================

-- Actualizar email de un empleado
UPDATE employees 
SET email = 'nuevo.email@techcorp.com', updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Actualizar departamento y nivel
UPDATE employees 
SET department = 'IT', level = 'LEAD', updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Agregar un skill a un empleado existente
INSERT INTO employee_skills (employee_id, skills)
VALUES (1, 'Kubernetes');

-- Eliminar un skill específico de un empleado
DELETE FROM employee_skills
WHERE employee_id = 1 AND skills = 'Kubernetes';

-- Reemplazar todos los skills de un empleado
-- Paso 1: Borrar skills actuales
DELETE FROM employee_skills WHERE employee_id = 1;
-- Paso 2: Insertar nuevos skills
INSERT INTO employee_skills (employee_id, skills)
VALUES 
    (1, 'AWS'),
    (1, 'Azure'),
    (1, 'GCP');

-- ============================================
-- 4. ELIMINAR DATOS
-- ============================================

-- Eliminar un empleado específico (sus skills se borran automáticamente por CASCADE)
DELETE FROM employees WHERE id = 1;

-- Eliminar empleados por departamento
DELETE FROM employees WHERE department = 'SALES';

-- Eliminar empleados contratados antes de cierta fecha
DELETE FROM employees WHERE hire_date < '2023-01-01';

-- Eliminar TODOS los empleados (¡CUIDADO!)
DELETE FROM employees;

-- Eliminar todos los skills de un empleado (sin borrar el empleado)
DELETE FROM employee_skills WHERE employee_id = 1;

-- ============================================
-- 5. GESTIÓN DE SECUENCIAS (IDs)
-- ============================================

-- Ver el estado actual de la secuencia de empleados
SELECT 
    last_value as current_value,
    is_called,
    CASE 
        WHEN is_called THEN 'Próximo ID será: ' || (last_value + 1)
        ELSE 'Próximo ID será: ' || last_value
    END as next_id
FROM employees_id_seq;

-- Ver el estado de la secuencia de usuarios
SELECT last_value, is_called FROM users_id_seq;

-- Resetear secuencia a 1 (cuando la tabla está vacía)
SELECT setval('employees_id_seq', 1, false);

-- Resetear secuencia al siguiente ID disponible (cuando hay datos)
SELECT setval('employees_id_seq', (SELECT COALESCE(MAX(id), 0) FROM employees), true);

-- ============================================
-- 6. LIMPIEZA COMPLETA Y RESET
-- ============================================

-- OPCIÓN A: Borrar todo y resetear a ID 1
DELETE FROM employees;
SELECT setval('employees_id_seq', 1, false);

-- OPCIÓN B: Borrar todo y mantener la secuencia
DELETE FROM employees;
-- (la secuencia sigue donde estaba)

-- OPCIÓN C: Borrar todo, resetear, e insertar datos de prueba
DELETE FROM employees;
SELECT setval('employees_id_seq', 1, false);

INSERT INTO employees (first_name, last_name, email, phone, gender, department, level, hire_date)
VALUES 
    ('Arturo', 'Jauregui', 'sklaid123@gmail.com', '+51-963-772-093', 'MALE', 'IT', 'LEAD', '2025-08-06'),
    ('Josesito', 'Tilfin', 'aaa@gmail.com', '+51-234-567', 'MALE', 'FINANCE', 'SENIOR', '2023-01-20');

INSERT INTO employee_skills (employee_id, skills)
VALUES 
    (1, 'TypeScript'),
    (1, 'Docker'),
    (1, 'JavaScript'),
    (1, 'Angular'),
    (2, 'JavaScript'),
    (2, 'Node.js'),
    (2, 'AWS'),
    (2, 'Python');

-- Verificar que se insertó correctamente
SELECT 
    e.id,
    e.first_name,
    e.last_name,
    e.email,
    STRING_AGG(es.skills, ', ') as skills
FROM employees e
LEFT JOIN employee_skills es ON e.id = es.employee_id
GROUP BY e.id, e.first_name, e.last_name, e.email
ORDER BY e.id;

-- ============================================
-- 7. ESTADÍSTICAS Y ANÁLISIS
-- ============================================

-- Contar total de empleados
SELECT COUNT(*) as total_employees FROM employees;

-- Contar total de skills únicos
SELECT COUNT(DISTINCT skills) as unique_skills FROM employee_skills;

-- Ver todos los skills disponibles
SELECT DISTINCT skills FROM employee_skills ORDER BY skills;

-- Empleados sin skills
SELECT e.id, e.first_name, e.last_name, e.email
FROM employees e
LEFT JOIN employee_skills es ON e.id = es.employee_id
WHERE es.skills IS NULL;

-- Skill más popular
SELECT skills, COUNT(*) as employee_count
FROM employee_skills
GROUP BY skills
ORDER BY employee_count DESC
LIMIT 5;

-- Empleados contratados por mes
SELECT 
    TO_CHAR(hire_date, 'YYYY-MM') as month,
    COUNT(*) as hires
FROM employees
GROUP BY TO_CHAR(hire_date, 'YYYY-MM')
ORDER BY month DESC;

-- ============================================
-- 8. VERIFICACIÓN DE INTEGRIDAD
-- ============================================

-- Verificar que no hay skills huérfanos (sin empleado)
SELECT es.employee_id, es.skills
FROM employee_skills es
LEFT JOIN employees e ON es.employee_id = e.id
WHERE e.id IS NULL;

-- Verificar emails duplicados
SELECT email, COUNT(*) as count
FROM employees
GROUP BY email
HAVING COUNT(*) > 1;

-- Verificar constraints
SELECT 
    conname as constraint_name,
    contype as constraint_type
FROM pg_constraint
WHERE conrelid = 'employees'::regclass;

-- ============================================
-- 9. BACKUP Y RESTORE (COMANDOS DE TERMINAL)
-- ============================================

-- Estos comandos se ejecutan en la terminal, NO en PostgreSQL

-- Hacer backup de la base de datos completa:
-- docker exec devops-postgres pg_dump -U postgres devops_platform > backup.sql

-- Hacer backup solo de la tabla employees:
-- docker exec devops-postgres pg_dump -U postgres -t employees -t employee_skills devops_platform > employees_backup.sql

-- Restaurar desde backup:
-- docker exec -i devops-postgres psql -U postgres devops_platform < backup.sql

-- ============================================
-- 10. INFORMACIÓN DEL SISTEMA
-- ============================================

-- Ver todas las tablas
SELECT tablename 
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY tablename;

-- Ver estructura de la tabla employees
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'employees'
ORDER BY ordinal_position;

-- Ver tamaño de las tablas
SELECT 
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Ver versión de PostgreSQL
SELECT version();

-- Ver historial de migraciones Flyway
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- ============================================
-- NOTAS IMPORTANTES:
-- ============================================
-- 1. Siempre haz backup antes de operaciones masivas
-- 2. Los IDs no se reutilizan por diseño (es normal tener gaps)
-- 3. CASCADE en foreign keys borra automáticamente los skills
-- 4. Las secuencias son independientes de los datos
-- 5. setval(..., false) = próximo valor será el especificado
-- 6. setval(..., true) = próximo valor será especificado + 1
-- ============================================
