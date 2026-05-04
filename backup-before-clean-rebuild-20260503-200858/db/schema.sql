CREATE DATABASE IF NOT EXISTS fittrackpro;
USE fittrackpro;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NULL,
    role VARCHAR(20) NULL DEFAULT 'MEMBER',
    status VARCHAR(20) NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    date_of_birth DATE NULL,
    gender VARCHAR(20) NULL,
    address VARCHAR(500) NULL,
    emergency_contact_name VARCHAR(150) NULL,
    emergency_contact_phone VARCHAR(30) NULL,
    membership_type VARCHAR(30) NOT NULL,
    join_date DATE NOT NULL,
    membership_expiry_date DATE NOT NULL,
    height_cm DECIMAL(5,2) NULL,
    weight_kg DECIMAL(5,2) NULL,
    fitness_goal VARCHAR(500) NULL,
    medical_notes VARCHAR(1000) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS trainers (
    trainer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NULL,
    specialization VARCHAR(150) NULL,
    experience_years INT NOT NULL DEFAULT 0,
    certification VARCHAR(255) NULL,
    bio VARCHAR(1000) NULL,
    availability_schedule VARCHAR(1000) NULL,
    hire_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workout_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    trainer_id INT NOT NULL,
    member_id INT NOT NULL,
    plan_name VARCHAR(150) NOT NULL,
    description VARCHAR(1000) NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    exercises TEXT NULL,
    notes VARCHAR(1000) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    check_in_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    check_out_time TIMESTAMP NULL,
    check_in_method VARCHAR(30) NOT NULL DEFAULT 'WEB',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, name, email, role, status)
VALUES
    ('admin@fittrackpro.com', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'Admin', 'admin@fittrackpro.com', 'ADMIN', 'ACTIVE'),
    ('john.trainer@fittrackpro.com', '02496313d77d42f04054b6809bfba704fd653f9bd05ac396d3cfdee93051c378', 'John Trainer', 'john.trainer@fittrackpro.com', 'TRAINER', 'ACTIVE'),
    ('jane.member@email.com', 'abe2d3ed5419e1a2293c034a6b375a622ff5a60e5ac30f29c461220898ffdd97', 'Jane Member', 'jane.member@email.com', 'MEMBER', 'ACTIVE')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO trainers (user_id, first_name, last_name, phone, specialization, experience_years, certification, hire_date)
SELECT id, 'John', 'Trainer', '9800000001', 'Strength Training', 5, 'Certified Personal Trainer', CURRENT_DATE
FROM users WHERE username = 'john.trainer@fittrackpro.com'
ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name);

INSERT INTO members (user_id, first_name, last_name, phone, membership_type, join_date, membership_expiry_date)
SELECT id, 'Jane', 'Member', '9800000002', 'PREMIUM', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 3 MONTH)
FROM users WHERE username = 'jane.member@email.com'
ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name);