package com.gutmatriz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection con;

    private DatabaseManager() {
        try {
            this.con = DriverManager.getConnection("jdbc:sqlite:gut_matrix.db");
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão do banco!", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return con;
    }
}

