package com.gutmatriz.model;

/**
 * Contrato para qualquer item que possa ser avaliado pela Matriz GUT
 * (Gravidade x Urgência x Tendência).
 *
 * Qualquer classe que implemente esta interface ganha automaticamente
 * o cálculo de prioridade através do método default abaixo — não precisa
 * reescrever a fórmula em cada classe que quiser ser "avaliável".
 */
public interface Avaliavel {

    int getGravidade();
    int getUrgencia();
    int getTendencia();

    /** Prioridade GUT = Gravidade x Urgência x Tendência (varia de 1 a 125). */
    default int calcularPrioridade() {
        return getGravidade() * getUrgencia() * getTendencia();
    }
}
