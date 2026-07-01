package com.gutmatriz.db;

import java.sql.SQLException;
import java.sql.Statement;

public class CriadorTabelaProblema {
    private CriadorTabelaProblema() {}

    public static void criar() {
        criarTabelaProblemas();
    }

    private static void criarTabelaProblemas() {
        String sql = "CREATE TABLE IF NOT EXISTS problemas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "descricao TEXT NOT NULL," +
                "gravidade INTEGER NOT NULL," +
                "urgencia INTEGER NOT NULL," +
                "tendencia INTEGER NOT NULL," +
                "data_criacao TEXT NOT NULL" +
                ")";
        try (Statement stmt = DatabaseManager.getInstance().getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela 'problemas': " + e.getMessage(), e);
        }
    }
}
