-- Create database if not exists
CREATE DATABASE IF NOT EXISTS alyak CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE alyak;

-- Create user if not exists and grant privileges
CREATE USER IF NOT EXISTS 'alyak_user'@'%' IDENTIFIED BY 'alyak_password';
GRANT ALL PRIVILEGES ON alyak.* TO 'alyak_user'@'%';
FLUSH PRIVILEGES;

-- Note:
-- Do NOT insert application data here.
-- This script runs before the Spring Boot app starts, so tables do not exist yet.
-- Put seed data into src/main/resources/data.sql so it runs after JPA creates tables.