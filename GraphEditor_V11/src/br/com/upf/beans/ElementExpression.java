/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

/**
 * Elemento do conjunto da expressão
 *
 * @author GiordaniAntonio
 */
public class ElementExpression {

    /**
     *
     */
    protected static int idStatic = 0;

    /**
     *
     */
    protected int id;

    private static int generateId() {
        return idStatic++;
    }

    public ElementExpression() {
        this.id = generateId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
