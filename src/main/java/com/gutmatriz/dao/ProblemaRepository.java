package com.gutmatriz.dao;

import com.gutmatriz.model.Problema;

import java.util.List;


public interface ProblemaRepository {

    void inserir(Problema problema);

    List<Problema> listarTodos();

    void atualizar(Problema problema);

    void excluir(int id);
}
