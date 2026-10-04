package com.helpdesk.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Hands out JDBC connections. Settings come from environment variables
 * (DB_URL, DB_USER, DB_PASSWORD) or from a db.properties file in the working directory.
 */
public final class Database {
    private static Properties props;

    private Database() { }

    public static Connection getConnection() throws SQLException {
        Properties p = load();
        return DriverManager.getConnection(
                p.getProperty("db.url"),
                p.getProperty("db.user"),
                p.getProperty("db.password"));
    }

    private static synchronized Properties load() {
        if (props != null) {
            return props;
        }
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (IOException ignored) {
            // fall through to environment variables
        }
        overrideFromEnv(p, "db.url", "DB_URL");
        overrideFromEnv(p, "db.user", "DB_USER");
        overrideFromEnv(p, "db.password", "DB_PASSWORD");

        if (p.getProperty("db.url") == null) {
            throw new IllegalStateException(
                    "No database settings found. Copy db.properties.example to db.properties "
                            + "or set DB_URL, DB_USER and DB_PASSWORD.");
        }
        props = p;
        return props;
    }

    private static void overrideFromEnv(Properties p, String key, String envName) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            p.setProperty(key, value);
        }
    }
}
