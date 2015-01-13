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
public class Method implements Serializable {

    private static final long serialVersionUID = 4552768896636712276L;

    /**
     *
     */
    private String modifier;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String returnType;

    /**
     * Construtor Padrão
     */
    public Method() {
    }

    /**
     * Construtor
     *
     * @param modifier
     * @param description
     * @param returnType
     */
    public Method(String modifier, String description, String returnType) {
        this.modifier = modifier;
        this.description = description;
        this.returnType = returnType;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

}
