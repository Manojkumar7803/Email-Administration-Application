CREATE DATABASE IF NOT EXISTS email_admin;
USE email_admin;

CREATE TABLE IF NOT EXISTS employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_name VARCHAR(100) NOT NULL,
    department VARCHAR(60) NOT NULL,
    email_address VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    alternate_email VARCHAR(180),
    mailbox_capacity_mb INT NOT NULL DEFAULT 1024,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
