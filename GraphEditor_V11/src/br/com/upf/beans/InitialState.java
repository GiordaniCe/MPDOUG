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
public class InitialState extends BasicState implements Serializable{

    private static final long serialVersionUID = -835188605929446715L;

    /**
     * Construtor Padrão
     */
    public InitialState() {
        super();
    }

    /**
     * Construtor
     *
     * @param nome
     */
    public InitialState(String nome) {
        super(nome);
    }

    @Override
    public String toString() {
        return "";
    }

}
