-- Insert test users for smoke tests and development
-- Password for all users: "password123" (BCrypt hashed)

-- Admin user
INSERT INTO users (username, password, email, role, active, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password123
    'admin@techcorp.com',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Regular user for testing
INSERT INTO users (username, password, email, role, active, created_at, updated_at)
VALUES (
    'testuser',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password123
    'testuser@techcorp.com',
    'USER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Another test user
INSERT INTO users (username, password, email, role, active, created_at, updated_at)
VALUES (
    'john.doe',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password123
    'john.doe@techcorp.com',
    'USER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Note: ON CONFLICT DO NOTHING ensures this migration is idempotent
-- and won't fail if users already exist
