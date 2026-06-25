package com.gutmatriz.model;


public interface Avaliavel {

    int getGravidade();
    int getUrgencia();
    int getTendencia();

    /** Prioridade GUT = Gravidade x Urgência x Tendência (varia de 1 a 125). */
    default int calcularPrioridade() {
        return getGravidade() * getUrgencia() * getTendencia();
    }
}
