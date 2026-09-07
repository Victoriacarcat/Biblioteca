-- Create database
CREATE DATABASE IF NOT EXISTS wisebooks;
USE wisebooks;

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('EMPLOYEE'), ('CLIENT');

-- Insert default users (password: admin123, employee123, client123)
INSERT INTO users (username, password, full_name, email, active, role_id) VALUES
('admin', 'admin123', 'System Administrator', 'admin@wisebooks.com', TRUE, 1),
('employee', 'employee123', 'Library Employee', 'employee@wisebooks.com', TRUE, 2),
('client', 'client123', 'Regular Client', 'client@wisebooks.com', TRUE, 3);