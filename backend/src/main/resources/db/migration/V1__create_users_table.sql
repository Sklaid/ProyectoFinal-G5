-- V1__create_users_table.sql
-- Create users table for authentication and authorization

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    active BOOLEAN DEFAULT true
);

-- Create unique constraint on username
ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Add check constraint for role values
ALTER TABLE users ADD CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'USER'));

-- Insert default admin user (password is 'admin123' hashed with BCrypt)
INSERT INTO users (username, password, email, role, active) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@techcorp.com', 'ADMIN', true);
