/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class Attribute implements Serializable {

    private static final long serialVersionUID = 7196784812257400239L;

    /**
     * Nome
     */
    private String name;

    /**
     * Tipo UML
     */
    private String umlType;

    /**
     * Tipo SQL
     */
    private String sqlType;

    /**
     * Monitorável
     */
    private boolean monitorable;

    /**
     * Identificador
     */
    private boolean identifier;

    /**
     * ùnico na coluna
     */
    private boolean uniqueColumn;

    /**
     * Único
     */
    private boolean uniqueTable;

    /**
     * Não Nulo
     */
    private boolean notNull;

    /**
     * Tamanho do campo
     */
    private Integer size;

    /**
     * Visibilidade
     */
    private String visibility;

    /**
     * Resumo
     */
    private String resume;

    /**
     * Caracteres representam o valor
     */
    private boolean characterValue;

    /**
     * Expressões básicas
     */
    private ArrayList<StandartExpression> expressions;

    /**
     * Construtor Padrão
     */
    public Attribute() {
        this.expressions = new ArrayList<>();
    }

    /**
     * Construtor
     *
     * @param visibility
     * @param name
     * @param type
     */
    public Attribute(String visibility, String name, String type) {
        this.visibility = visibility;
        this.name = name;
        this.umlType = type;
        this.monitorable = false;
        this.identifier = false;
        this.uniqueColumn = false;
        this.uniqueTable = false;
        this.notNull = false;

        this.sqlType = getUMLTypeForSQLType();
        this.expressions = new ArrayList<>();
    }

    private String getUMLTypeForSQLType() {
        switch (umlType) {
            case "Integer":
                return "INT";
            case "Double":
                return "DOUBLE PRECISION";
            case "Float":
                return "FLOAT";
            case "Boolean":
                return "BOOLEAN";
            case "String":
                return "VARCHAR";
            case "Char":
                return "VARCHAR";

            default:
                return "";
        }

    }

    private Integer calculteSize() {
        if (umlType.equalsIgnoreCase("String")) {
            return 50;
        } else {
            if (umlType.equalsIgnoreCase("Char")) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Calcula o resumo do atributo
     */
    public void calculateResume() {

        char visibilidade;

        switch (visibility) {
            case "public":
                visibilidade = '+';
                break;
            case "private":
                visibilidade = '-';
                break;
            case "protected":
                visibilidade = '#';
                break;
            case "package":
                visibilidade = '~';
                break;
            default:
                visibilidade = ' ';
        }

        resume = " " + visibilidade + " " + name + " : " + umlType;

    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUmlType() {
        return umlType;
    }

    public void setUmlType(String umlType) {
        this.umlType = umlType;
        this.sqlType = getUMLTypeForSQLType();
        this.size = calculteSize();
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public boolean isMonitorable() {
        return monitorable;
    }

    public void setMonitorable(boolean monitorable) {
        this.monitorable = monitorable;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isUniqueColumn() {
        return uniqueColumn;
    }

    public void setUniqueColumn(boolean uniqueColumn) {
        this.uniqueColumn = uniqueColumn;
    }

    public boolean isUniqueTable() {
        return uniqueTable;
    }

    public void setUniqueTable(boolean uniqueTable) {
        this.uniqueTable = uniqueTable;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isCharacterValue() {
        return characterValue;
    }

    public void setCharacterValue(boolean characterValue) {
        this.characterValue = characterValue;
    }

    public ArrayList<StandartExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(ArrayList<StandartExpression> expressions) {
        this.expressions = expressions;
    }

    /**
     * Adiciona Expressão
     *
     * @param expression
     * @return
     */
    public boolean addExpression(StandartExpression expression) {
        boolean result = expressions.add(expression);
        //JOptionPane.showMessageDialog(null, "Inseriu StandartExpression in Attribute?" + result);
        return result;
    }

    /**
     * Edita Expressão
     *
     * @param expression
     * @return
     */
    public boolean editExpression(StandartExpression expression) {
        int index = getIndexExpressions(expression);
        if (index >= 0) {
            expressions.remove(index);
            expressions.add(index, expression);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adiciona Expressão
     *
     * @param expression
     * @return
     */
    public boolean removeExpression(StandartExpression expression) {
        boolean result = expressions.remove(expression);
        return result;
    }

    /**
     *
     * @param expression
     * @return
     */
    private Integer getIndexExpressions(StandartExpression expression) {
        StandartExpression current;
        for (int i = 0; i < expressions.size(); i++) {
            current = expressions.get(i);
            if (current.equals(expression)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.visibility);
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.umlType);
        hash = 47 * hash + Objects.hashCode(this.resume);
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
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.visibility, other.visibility)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.umlType, other.umlType)) {
            return false;
        }
        if (!Objects.equals(this.resume, other.resume)) {
            return false;
        }
        return true;
    }

}
