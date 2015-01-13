/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;

/**
 * Operador Lógico NOT
 *
 * @author GiordaniAntonio
 */
public class OperatorNot extends LogicalOperator implements Serializable {

    private static final long serialVersionUID = -6946715033233534611L;

    private String defaultValue = "Não";

    private String umlValue = "!";

    private String sqlValue = "NOT";

    public OperatorNot() {
        super();
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getUmlValue() {
        return umlValue;
    }

    public void setUmlValue(String umlValue) {
        this.umlValue = umlValue;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public void setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    @Override
    public String toString() {
        return sqlValue + " ";
    }

}
