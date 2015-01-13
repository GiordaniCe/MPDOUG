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
 * Utilizada no construtor de expressões Standart
 *
 * @author GiordaniAntonio
 */
public class StandartExpression implements Serializable, Cloneable {

    private static final long serialVersionUID = 8071051024307333633L;

    /**
     *
     */
    private static Integer idStatic = 0;

    public static Integer getIdStatic() {
        return idStatic;
    }

    public static void setIdStatic(Integer aIdStatic) {
        idStatic = aIdStatic;
    }

    /**
     *
     */
    private Integer id;

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

    /**
     *
     */
    private State state;

    /**
     *
     * @return
     */
    private static Integer generateId() {
        return idStatic++;
    }

    /**
     * Construtor Padrão
     */
    public StandartExpression() {
    }

    public StandartExpression(Integer id, Attribute attribute, Operator operator, String value, State state) {
        this.id = id;
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
        this.state = state;
    }

    public void setId() {
        this.id = generateId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.attribute);
        hash = 83 * hash + Objects.hashCode(this.operator);
        hash = 83 * hash + Objects.hashCode(this.value);
        hash = 83 * hash + Objects.hashCode(this.state);
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
        final StandartExpression other = (StandartExpression) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.attribute, other.attribute)) {
            return false;
        }
        if (!Objects.equals(this.operator, other.operator)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.state, other.state)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return attribute.getName() + " " + operator + " " + value + " -> " + state;
    }

    @Override
    public StandartExpression clone() throws CloneNotSupportedException {
        StandartExpression expressionCloned = new StandartExpression(this.id, this.attribute, this.operator, this.value, this.state);
        return expressionCloned;
    }

}
