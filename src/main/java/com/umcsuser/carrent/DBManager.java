package com.umcsuser.carrent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static DBManager instance;
    private final String url;

    private DBManager(String url) {
        this.url = url;
    }

    public static void init(String url) {
        if (instance == null) {
            instance = new DBManager(url);
        }
    }

    public static DBManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("dbmanager not initialized");
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
