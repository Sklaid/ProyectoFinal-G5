-- V2__create_employees_table.sql
-- Create employees table for CRUD operations

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    gender VARCHAR(10) NOT NULL,
    department VARCHAR(20) NOT NULL,
    level VARCHAR(20) NOT NULL,
    hire_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create employee_skills junction table for many-to-many relationship
CREATE TABLE employee_skills (
    employee_id BIGINT NOT NULL,
    skills VARCHAR(50) NOT NULL,
    PRIMARY KEY (employee_id, skills),
    CONSTRAINT fk_employee_skills_employee FOREIGN KEY (employee_id) 
        REFERENCES employees(id) ON DELETE CASCADE
);

-- Create indexes on email and department columns for faster lookups
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_department ON employees(department);

-- Add check constraints for enum-like values
ALTER TABLE employees ADD CONSTRAINT chk_employees_gender 
    CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'));

ALTER TABLE employees ADD CONSTRAINT chk_employees_department 
    CHECK (department IN ('IT', 'HR', 'FINANCE', 'SALES'));

ALTER TABLE employees ADD CONSTRAINT chk_employees_level 
    CHECK (level IN ('JUNIOR', 'MID', 'SENIOR', 'LEAD'));

-- Create index on employee_skills for faster skill lookups
CREATE INDEX idx_employee_skills_skills ON employee_skills(skills);

-- Insert sample employees for testing
INSERT INTO employees (first_name, last_name, email, phone, gender, department, level, hire_date) 
VALUES 
    ('John', 'Doe', 'john.doe@techcorp.com', '+51-999-111-222', 'MALE', 'IT', 'SENIOR', '2022-01-15'),
    ('Jane', 'Smith', 'jane.smith@techcorp.com', '+51-999-333-444', 'FEMALE', 'HR', 'MID', '2023-03-20'),
    ('Carlos', 'Rodriguez', 'carlos.rodriguez@techcorp.com', '+51-999-555-666', 'MALE', 'FINANCE', 'LEAD', '2021-06-10');

-- Insert sample skills for employees
INSERT INTO employee_skills (employee_id, skills) 
VALUES 
    (1, 'Java'),
    (1, 'Spring Boot'),
    (1, 'PostgreSQL'),
    (2, 'Communication'),
    (2, 'Recruitment'),
    (3, 'Excel'),
    (3, 'Financial Analysis');
