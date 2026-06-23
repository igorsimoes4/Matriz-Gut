package com.gutmatriz.dao;

import com.gutmatriz.db.DatabaseManager;
import com.gutmatriz.model.Problema;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProblemaRepositorySQLite implements ProblemaRepository {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void inserir(Problema p) {
        String sql = "INSERT INTO problemas (descricao, gravidade, urgencia, tendencia, data_criacao) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseManager.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getDescricao());
            stmt.setInt(2, p.getGravidade());
            stmt.setInt(3, p.getUrgencia());
            stmt.setInt(4, p.getTendencia());
            String data = LocalDateTime.now().format(FORMATTER);
            stmt.setString(5, data);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                    p.setDataCriacao(data);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir problema: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Problema> listarTodos() {
        List<Problema> lista = new ArrayList<>();
        String sql = "SELECT * FROM problemas";
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Problema(
                        rs.getInt("id"),
                        rs.getString("descricao"),
                        rs.getInt("gravidade"),
                        rs.getInt("urgencia"),
                        rs.getInt("tendencia"),
                        rs.getString("data_criacao")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar problemas: " + e.getMessage(), e);
        }
        // Ordena pela prioridade GUT, da mais critica para a menos critica
        lista.sort((a, b) -> Integer.compare(b.calcularPrioridade(), a.calcularPrioridade()));
        return lista;
    }

    @Override
    public void atualizar(Problema p) {
        String sql = "UPDATE problemas SET descricao = ?, gravidade = ?, urgencia = ?, tendencia = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getDescricao());
            stmt.setInt(2, p.getGravidade());
            stmt.setInt(3, p.getUrgencia());
            stmt.setInt(4, p.getTendencia());
            stmt.setInt(5, p.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar problema: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM problemas WHERE id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir problema: " + e.getMessage(), e);
        }
    }
}
