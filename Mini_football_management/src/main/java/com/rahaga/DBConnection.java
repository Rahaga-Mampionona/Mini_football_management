package com.rahaga;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/restaurant_db",
                    "postgres",
                    "m4mp10n0n4"
            );
        } catch (SQLException e) {
            throw new RuntimeException("Erreur connexion DB", e);
        }
        
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
