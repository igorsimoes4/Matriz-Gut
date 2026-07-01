package com.gutmatriz.db;

public class InicializadorBanco {

    private InicializadorBanco() {}

    public static void inicializar() {
        // 1. Garante que a conexão (Singleton) está aberta
        DatabaseManager.getInstance().getConnection();

        // 2. Delega a criação das tabelas para quem é responsável por isso
        CriadorTabelaProblema.criar();
    }
}
