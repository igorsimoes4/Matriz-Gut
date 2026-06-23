package com.gutmatriz.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Problema implements Avaliavel {

    private final IntegerProperty id;
    private final StringProperty descricao;
    private final IntegerProperty gravidade;
    private final IntegerProperty urgencia;
    private final IntegerProperty tendencia;
    private final StringProperty dataCriacao;

    public Problema(int id, String descricao, int gravidade, int urgencia, int tendencia, String dataCriacao) {
        this.id = new SimpleIntegerProperty(id);
        this.descricao = new SimpleStringProperty(descricao);
        this.gravidade = new SimpleIntegerProperty(gravidade);
        this.urgencia = new SimpleIntegerProperty(urgencia);
        this.tendencia = new SimpleIntegerProperty(tendencia);
        this.dataCriacao = new SimpleStringProperty(dataCriacao);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getDescricao() {
        return descricao.get();
    }

    public void setDescricao(String value) {
        descricao.set(value);
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    @Override
    public int getGravidade() {
        return gravidade.get();
    }

    public void setGravidade(int value) {
        gravidade.set(value);
    }

    public IntegerProperty gravidadeProperty() {
        return gravidade;
    }

    @Override
    public int getUrgencia() {
        return urgencia.get();
    }

    public void setUrgencia(int value) {
        urgencia.set(value);
    }

    public IntegerProperty urgenciaProperty() {
        return urgencia;
    }

    @Override
    public int getTendencia() {
        return tendencia.get();
    }

    public void setTendencia(int value) {
        tendencia.set(value);
    }

    public IntegerProperty tendenciaProperty() {
        return tendencia;
    }

    public String getDataCriacao() {
        return dataCriacao.get();
    }

    public void setDataCriacao(String value) {
        dataCriacao.set(value);
    }

    public StringProperty dataCriacaoProperty() {
        return dataCriacao;
    }
}
