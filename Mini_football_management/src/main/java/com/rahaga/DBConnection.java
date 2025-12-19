package com.rahaga;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getDBConnection() throws SQLException {
        String url = System.getenv("jdbc:postgresql://localhost:5432/mini_football_db;");
        String username = System.getenv("mini_football_db_manager;");
        String password = System.getenv("password");

        return DriverManager.getConnection(url, username, password);
    }
}
