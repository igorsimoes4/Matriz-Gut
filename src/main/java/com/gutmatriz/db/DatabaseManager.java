package com.gutmatriz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:gut_matrix.db";
    private static Connection connection;

    private DatabaseManager() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
        return connection;
    }

    /** Cria a tabela "problemas" caso ainda nao exista. */
    public static void inicializarBanco() {
        String sql = "CREATE TABLE IF NOT EXISTS problemas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "descricao TEXT NOT NULL," +
                "gravidade INTEGER NOT NULL," +
                "urgencia INTEGER NOT NULL," +
                "tendencia INTEGER NOT NULL," +
                "data_criacao TEXT NOT NULL" +
                ")";
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela 'problemas': " + e.getMessage(), e);
        }
    }
}
