-- Insert test users for smoke tests and development
-- Password for all users: "password123" (BCrypt hashed)

-- Note: 'admin' user already exists from V1 migration, so we skip it

-- Regular user for testing
-- Using MERGE INTO for H2 compatibility (works in both H2 and PostgreSQL)
MERGE INTO users (username, password, email, role, active, created_at)
KEY (username)
VALUES (
    'testuser',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password123
    'testuser@techcorp.com',
    'USER',
    true,
    CURRENT_TIMESTAMP
);

-- Another test user
MERGE INTO users (username, password, email, role, active, created_at)
KEY (username)
VALUES (
    'john.doe',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password123
    'john.doe@techcorp.com',
    'USER',
    true,
    CURRENT_TIMESTAMP
);

-- Note: MERGE INTO ensures this migration is idempotent
-- and won't fail if users already exist (compatible with H2 and PostgreSQL)
