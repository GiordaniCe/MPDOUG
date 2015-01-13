/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.util;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author GiordaniAntonio
 */
public class Operator implements Serializable {

    private static final long serialVersionUID = -5211870817219518261L;

    private String name;

    private String sqlNotation;

    private String umlNotation;

    private boolean comparative;

    private boolean equality;

    private boolean logic;

    public Operator() {
    }

    public Operator(String name, String SQLNotation, String JavaNotation, boolean comparative, boolean equality, boolean logic) {
        this.name = name;
        this.sqlNotation = SQLNotation;
        this.umlNotation = JavaNotation;
        this.comparative = comparative;
        this.equality = equality;
        this.logic = logic;
    }

    /**
     * Nome
     *
     * @return
     */
    public String getStandartNotation() {
        return umlNotation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSqlNotation() {
        return sqlNotation;
    }

    public void setSqlNotation(String sqlNotation) {
        this.sqlNotation = sqlNotation;
    }

    public String getUmlNotation() {
        return umlNotation;
    }

    public void setUmlNotation(String umlNotation) {
        this.umlNotation = umlNotation;
    }

    public boolean isComparative() {
        return comparative;
    }

    public void setComparative(boolean comparative) {
        this.comparative = comparative;
    }

    public boolean isEquality() {
        return equality;
    }

    public void setEquality(boolean equality) {
        this.equality = equality;
    }

    public boolean isLogic() {
        return logic;
    }

    public void setLogic(boolean logic) {
        this.logic = logic;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.sqlNotation);
        hash = 97 * hash + Objects.hashCode(this.umlNotation);
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
        final Operator other = (Operator) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.sqlNotation, other.sqlNotation)) {
            return false;
        }
        if (!Objects.equals(this.umlNotation, other.umlNotation)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return sqlNotation;
    }

}
