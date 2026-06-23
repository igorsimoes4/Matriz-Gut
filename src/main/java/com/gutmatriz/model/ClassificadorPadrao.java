package com.gutmatriz.model;

/** Implementação padrão de {@link ClassificadorPrioridade}, com faixas fixas. */
public class ClassificadorPadrao implements ClassificadorPrioridade {

    @Override
    public String classificar(int prioridade) {
        if (prioridade >= 81) return "Crítica";
        if (prioridade >= 41) return "Alta";
        if (prioridade >= 15) return "Média";
        return "Baixa";
    }
}
