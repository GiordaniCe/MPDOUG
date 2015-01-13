/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.upf.util;

/**
 *
 * @author GiordaniAntonio
 */
public class RelationTypeUMLForSQL {
    
    private String typeUML;
    
    private String typeSQL;

    public RelationTypeUMLForSQL() {
    }

    public RelationTypeUMLForSQL(String typeUML, String typeSQL) {
        this.typeUML = typeUML;
        this.typeSQL = typeSQL;
    }

    public String getTypeUML() {
        return typeUML;
    }

    public void setTypeUML(String typeUML) {
        this.typeUML = typeUML;
    }

    public String getTypeSQL() {
        return typeSQL;
    }

    public void setTypeSQL(String typeSQL) {
        this.typeSQL = typeSQL;
    }
   
}
