-- Insert test users for smoke tests and development
-- Password for all users: "password123" (BCrypt hashed)

-- Note: 'admin' user already exists from V1 migration, so we skip it

-- Regular user for testing
-- Check if user exists before inserting (works in both H2 and PostgreSQL)
INSERT INTO users (username, password, email, role, active, created_at)
SELECT 'testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'testuser@techcorp.com', 'USER', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'testuser');

-- Another test user
INSERT INTO users (username, password, email, role, active, created_at)
SELECT 'john.doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'john.doe@techcorp.com', 'USER', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'john.doe');

-- Note: Using INSERT ... SELECT ... WHERE NOT EXISTS ensures this migration is idempotent
-- and works in both H2 (tests) and PostgreSQL (production)
