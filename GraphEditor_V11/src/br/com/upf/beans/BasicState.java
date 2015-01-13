/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author GiordaniAntonio
 */
public class BasicState implements Serializable {

    private static final long serialVersionUID = -8810086708715005267L;

    /**
     *
     */
    protected static int idStatic = 0;

    /**
     *
     */
    private int id;

    /**
     * Nome do estado
     */
    protected String name;

    /**
     *
     */
    protected boolean checkName;

    private static int generateId() {
        return idStatic++;
    }

    /**
     * Construtor Padrão
     */
    public BasicState() {
        this(null);
    }

    /**
     * Construtor
     *
     * @param nome
     */
    public BasicState(String nome) {
        this.name = nome;
        this.id = generateId();
        this.checkName = false;
    }

    protected void updateBasicState() {
        // VERIFICA O NOME
        if (name.equalsIgnoreCase("Novo Estado")) {
            checkName = false;
        } else {
            checkName = true;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateBasicState();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.id;
        hash = 13 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasicState other = (BasicState) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public boolean isCheckName() {
        return checkName;
    }

    public void setCheckName(boolean checkName) {
        this.checkName = checkName;
    }

}
