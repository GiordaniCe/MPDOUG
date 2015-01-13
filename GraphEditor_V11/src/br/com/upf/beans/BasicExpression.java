/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import br.com.upf.util.Operator;
import java.io.Serializable;
import java.util.Objects;

/**
 * Expressão Básica: Atributo, Operador, Valor;
 *
 * @author GiordaniAntonio
 */
public class BasicExpression extends ElementExpression implements Serializable{

    /**
     *
     */
    private Attribute attribute;

    /**
     *
     */
    private Operator operator;

    /**
     *
     */
    private String value;

    public BasicExpression() {
        super();
    }

    public BasicExpression(Attribute attribute, Operator operator, String value) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.attribute);
        hash = 97 * hash + Objects.hashCode(this.operator);
        hash = 97 * hash + Objects.hashCode(this.value);
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
        final BasicExpression other = (BasicExpression) obj;
        if (!Objects.equals(this.attribute, other.attribute)) {
            return false;
        }
        if (!Objects.equals(this.operator, other.operator)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return attribute + " " + operator + " '" + value + "' ";
    }

    /**
     * Retorna a representação UML da expressão (BasicExpression)
     *
     * @return
     */
    public String umlRepresetation() {
        String result = "";
        result += attribute.getName() + " " + operator.getUmlNotation() + " ";
        if (attribute.isCharacterValue()) {
            result += "'" + value + "'";
        } else {
            result += value;
        }
        return result;
    }
    
     /**
     * Retorna a representação SQL da expressão (BasicExpression)
     *
     * @return
     */
    public String sqlRepresetation() {
        String result = "";
        result += attribute.getName() + " " + operator.getSqlNotation() + " ";
        if (attribute.isCharacterValue()) {
            result += "'" + value + "'";
        } else {
            result += value;
        }
        return result;
    }

    /**
     * Retorna a representação SQL da expressão (BasicExpression). Recebe por
     * parâmetro o qualificador (new ou old).
     *
     * @param qualifying
     * @return
     */
    public String sqlRepresentation(String qualifying) {
        String result = "";
        result += qualifying + "." + attribute.getName() + " " + operator.getSqlNotation() + " ";
        if (attribute.isCharacterValue()) {
            result += "'" + value + "'";
        } else {
            result += value;
        }
        return result;
    }

}
