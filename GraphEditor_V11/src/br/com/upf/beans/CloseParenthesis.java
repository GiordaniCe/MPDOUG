/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;

/**
 * Fechar parênteses
 *
 * @author GiordaniAntonio
 */
public class CloseParenthesis extends ElementExpression implements Serializable {

    private static final long serialVersionUID = 2592716951231802552L;

    private String defaultValue = ")";

    public CloseParenthesis() {
        super();
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return defaultValue + " ";
    }

}
