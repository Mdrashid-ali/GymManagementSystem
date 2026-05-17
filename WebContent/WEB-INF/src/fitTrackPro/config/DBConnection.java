package com.fitTrackPro.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBConnection {
    private static final String DATABASE_NAME = "fittrackpro";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/fittrackpro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static volatile boolean initialized;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        ensureInitialized();
        return DriverManager.getConnection(getUrl(), getUsername(), getPassword());
    }

    public static String getUrl() { return getConfig("FITTRACK_DB_URL", "fittrack.db.url", DEFAULT_URL); }
    public static String getUsername() { return getConfig("FITTRACK_DB_USER", "fittrack.db.user", DEFAULT_USERNAME); }
    public static String getPassword() { return getConfig("FITTRACK_DB_PASSWORD", "fittrack.db.password", DEFAULT_PASSWORD); }

    private static String getConfig(String envName, String propertyName, String defaultValue) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) value = System.getProperty(propertyName);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static void ensureInitialized() throws SQLException {
        if (initialized) return;
        synchronized (DBConnection.class) {
            if (initialized) return;
            try {
                Class.forName(DRIVER_CLASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC driver not found in WEB-INF/lib.", e);
            }
            initializeDatabase();
            initialized = true;
        }
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(getServerUrl(), getUsername(), getPassword());
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            statement.executeUpdate("USE " + DATABASE_NAME);
            createTables(statement);
            migrateTables(connection);
            seedData(statement);
        }
    }

    private static void createTables(Statement s) throws SQLException {
        s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NULL,
                    role VARCHAR(20) NULL DEFAULT 'MEMBER',
                    status VARCHAR(20) NULL DEFAULT 'ACTIVE',
                    failed_login_attempts INT NOT NULL DEFAULT 0,
                    locked_until TIMESTAMP NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS password_reset_tokens (
                    token_id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    token VARCHAR(128) NOT NULL UNIQUE,
                    expires_at TIMESTAMP NOT NULL,
                    used_at TIMESTAMP NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        s.executeUpdate("""
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
                    membership_type VARCHAR(30) NOT NULL DEFAULT 'BASIC',
                    join_date DATE NOT NULL,
                    membership_expiry_date DATE NOT NULL,
                    height_cm DECIMAL(5,2) NULL,
                    weight_kg DECIMAL(5,2) NULL,
                    fitness_goal VARCHAR(500) NULL,
                    medical_notes VARCHAR(1000) NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """);
        s.executeUpdate("""
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
                )
                """);
        s.executeUpdate("""
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
                )
                """);
        s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS attendance (
                    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
                    member_id INT NOT NULL,
                    check_in_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    check_out_time TIMESTAMP NULL,
                    check_in_method VARCHAR(30) NOT NULL DEFAULT 'WEB',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
    }

    private static void migrateTables(Connection c) throws SQLException {
        addColumnIfMissing(c, "users", "username", "VARCHAR(50) NULL");
        addColumnIfMissing(c, "users", "password", "VARCHAR(255) NULL");
        addColumnIfMissing(c, "users", "name", "VARCHAR(100) NULL");
        addColumnIfMissing(c, "users", "email", "VARCHAR(100) NULL");
        addColumnIfMissing(c, "users", "role", "VARCHAR(20) NULL DEFAULT 'MEMBER'");
        addColumnIfMissing(c, "users", "status", "VARCHAR(20) NULL DEFAULT 'ACTIVE'");
        addColumnIfMissing(c, "users", "failed_login_attempts", "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(c, "users", "locked_until", "TIMESTAMP NULL");
        addColumnIfMissing(c, "users", "created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        try (Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS password_reset_tokens (token_id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, token VARCHAR(128) NOT NULL UNIQUE, expires_at TIMESTAMP NOT NULL, used_at TIMESTAMP NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        }
        if (columnExists(c, "users", "password_hash")) {
            try (Statement s = c.createStatement()) { s.executeUpdate("UPDATE users SET password = password_hash WHERE (password IS NULL OR password = '') AND password_hash IS NOT NULL"); }
        }
        try (Statement s = c.createStatement()) {
            s.executeUpdate("UPDATE users SET username = COALESCE(NULLIF(username, ''), email, CONCAT('user', id)) WHERE username IS NULL OR username = ''");
            s.executeUpdate("UPDATE users SET name = COALESCE(NULLIF(name, ''), username, email, CONCAT('User ', id)) WHERE name IS NULL OR name = ''");
            s.executeUpdate("UPDATE users SET role = 'MEMBER' WHERE role IS NULL OR role = ''");
            s.executeUpdate("UPDATE users SET status = 'ACTIVE' WHERE status IS NULL OR status = ''");
            s.executeUpdate("UPDATE users SET failed_login_attempts = 0 WHERE failed_login_attempts IS NULL");
        }

        addColumnIfMissing(c, "members", "user_id", "INT NULL");
        addColumnIfMissing(c, "members", "first_name", "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(c, "members", "last_name", "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(c, "members", "phone", "VARCHAR(30) NOT NULL DEFAULT ''");
        addColumnIfMissing(c, "members", "date_of_birth", "DATE NULL");
        addColumnIfMissing(c, "members", "gender", "VARCHAR(20) NULL");
        addColumnIfMissing(c, "members", "address", "VARCHAR(500) NULL");
        addColumnIfMissing(c, "members", "emergency_contact_name", "VARCHAR(150) NULL");
        addColumnIfMissing(c, "members", "emergency_contact_phone", "VARCHAR(30) NULL");
        addColumnIfMissing(c, "members", "membership_type", "VARCHAR(30) NOT NULL DEFAULT 'BASIC'");
        addColumnIfMissing(c, "members", "join_date", "DATE NULL");
        addColumnIfMissing(c, "members", "membership_expiry_date", "DATE NULL");
        addColumnIfMissing(c, "members", "height_cm", "DECIMAL(5,2) NULL");
        addColumnIfMissing(c, "members", "weight_kg", "DECIMAL(5,2) NULL");
        addColumnIfMissing(c, "members", "fitness_goal", "VARCHAR(500) NULL");
        addColumnIfMissing(c, "members", "medical_notes", "VARCHAR(1000) NULL");
        addColumnIfMissing(c, "members", "created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        addColumnIfMissing(c, "members", "updated_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
        try (Statement s = c.createStatement()) {
            s.executeUpdate("UPDATE members SET join_date = CURRENT_DATE WHERE join_date IS NULL");
            s.executeUpdate("UPDATE members SET membership_expiry_date = DATE_ADD(join_date, INTERVAL 1 MONTH) WHERE membership_expiry_date IS NULL");
        }
    }

    private static void seedData(Statement s) throws SQLException {
        s.executeUpdate("""
                INSERT INTO users (username, password, name, email, role, status)
                VALUES
                    ('admin@fittrackpro.com', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'Admin', 'admin@fittrackpro.com', 'ADMIN', 'ACTIVE'),
                    ('john.trainer@fittrackpro.com', '02496313d77d42f04054b6809bfba704fd653f9bd05ac396d3cfdee93051c378', 'John Trainer', 'john.trainer@fittrackpro.com', 'TRAINER', 'ACTIVE'),
                    ('jane.member@email.com', 'abe2d3ed5419e1a2293c034a6b375a622ff5a60e5ac30f29c461220898ffdd97', 'Jane Member', 'jane.member@email.com', 'MEMBER', 'ACTIVE')
                ON DUPLICATE KEY UPDATE username = VALUES(username)
                """);
        s.executeUpdate("""
                INSERT INTO trainers (user_id, first_name, last_name, phone, specialization, experience_years, certification, hire_date)
                SELECT id, 'John', 'Trainer', '9800000001', 'Strength Training', 5, 'Certified Personal Trainer', CURRENT_DATE
                FROM users WHERE username = 'john.trainer@fittrackpro.com'
                ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name)
                """);
        s.executeUpdate("""
                INSERT INTO members (user_id, first_name, last_name, phone, membership_type, join_date, membership_expiry_date)
                SELECT id, 'Jane', 'Member', '9800000002', 'PREMIUM', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 3 MONTH)
                FROM users WHERE username = 'jane.member@email.com'
                ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name)
                """);
    }

    private static void addColumnIfMissing(Connection c, String table, String column, String definition) throws SQLException {
        if (!columnExists(c, table, column)) {
            try (Statement s = c.createStatement()) { s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition); }
        }
    }

    private static boolean columnExists(Connection c, String table, String column) throws SQLException {
        String sql = "SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static String getServerUrl() {
        String url = getUrl();
        int schemeEnd = url.indexOf("://");
        int slashAfterHost = url.indexOf('/', schemeEnd + 3);
        if (slashAfterHost < 0) return url;
        int queryStart = url.indexOf('?', slashAfterHost);
        String hostPart = url.substring(0, slashAfterHost + 1);
        return queryStart < 0 ? hostPart : hostPart + url.substring(queryStart);
    }
}