-- Initialization script for SysML v2 PostgreSQL Database
-- This file is executed when the PostgreSQL container starts

-- Create the database if it doesn't exist (handled by POSTGRES_DB environment variable)
-- Additional setup can be added here if needed

-- Set timezone
SET timezone = 'UTC';

-- Create any additional users or permissions here if needed
-- For now, using the default postgres user as configured in docker-compose.yml

-- Log that initialization is complete
SELECT 'SysML v2 Database initialized successfully' AS message;