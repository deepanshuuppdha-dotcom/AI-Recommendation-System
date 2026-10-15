package com.recsys.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Singleton that hands out JDBC connections using settings from db.properties. */
public final class DBConnectionManager {

    private static DBConnectionManager instance;

    private final String url;
    private final String user;
    private final String password;

    private DBConnectionManager() {
        Properties props = new Properties();
        try (InputStream in = DBConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read db.properties", e);
        }
        this.url = env("DB_URL", props.getProperty("db.url"));
        this.user = env("DB_USER", props.getProperty("db.user"));
        this.password = env("DB_PASSWORD", props.getProperty("db.password"));
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found on classpath", e);
        }
    }

    public static synchronized DBConnectionManager getInstance() {
        if (instance == null) instance = new DBConnectionManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? fallback : v;
    }
}
