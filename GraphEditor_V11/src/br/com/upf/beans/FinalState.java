/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;

/**
 *
 * @author GiordaniAntonio
 */
public class FinalState extends BasicState implements Serializable {

    private static final long serialVersionUID = 5990596340630694199L;

    /**
     * Construtor Padrão
     */
    public FinalState() {
        super();
    }

    /**
     * Construtor
     *
     * @param nome
     */
    public FinalState(String nome) {
        super(nome);
    }

    @Override
    public String toString() {
        return "";
    }

}
