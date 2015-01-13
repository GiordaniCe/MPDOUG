/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view.dialog;

import br.com.upf.beans.Attribute;
import br.com.upf.util.RelationTypeUMLForSQL;
import br.com.upf.view.GraphEditor;
import java.util.ArrayList;

/**
 *
 * @author GiordaniAntonio
 */
public class AttributeApresentation extends javax.swing.JPanel {

    private GraphEditor graphEditor;

    private Attribute attribute;

    private ArrayList<RelationTypeUMLForSQL> relationShipTypes;

    /**
     * Creates new form AttributeApresentation
     *
     * @param editor
     * @param a
     */
    public AttributeApresentation(GraphEditor editor, Attribute a) {
        initComponents();
        this.graphEditor = editor;
        this.attribute = a;
        relationShipTypes = graphEditor.getRelationShipTypes();
        loadAttribute();
    }

    private void loadAttribute() {
        jTextFieldName.setText(attribute.getName());
        jTextFieldUMLType.setText(attribute.getUmlType());
        jCheckBoxIdentifier.setSelected(attribute.isIdentifier());
        jCheckBoxUniqueColumn.setSelected(attribute.isUniqueColumn());
        jCheckBoxNotNull.setSelected(attribute.isNotNull());
        inicializaJComboBoxSQLType();
        jComboBoxSQLType.setSelectedItem(attribute.getSqlType());
        if (attribute.getUmlType().equalsIgnoreCase("String")) {
            jTextFieldSize.setText(attribute.getSize().toString());
        } else {
            jTextFieldSize.setEnabled(false);
        }
        if (jCheckBoxIdentifier.isSelected()) {
            jCheckBoxUniqueColumn.setEnabled(false);
        }
    }

    private void inicializaJComboBoxSQLType() {
        jComboBoxSQLType.removeAllItems();
        String uml, sql;
        for (int i = 0; i < relationShipTypes.size(); i++) {
            uml = relationShipTypes.get(i).getTypeUML();
            sql = relationShipTypes.get(i).getTypeSQL();
            if (uml.equalsIgnoreCase(attribute.getUmlType())) {
                jComboBoxSQLType.addItem(sql);
            }
        }
    }

    public boolean saveAttributeEdition() {
        String msg = "";
        if (jCheckBoxIdentifier.isSelected()) {
            if (attribute.getUmlType().equalsIgnoreCase("Integer")) {
                attribute.setIdentifier(jCheckBoxIdentifier.isSelected());
                attribute.setUniqueColumn(jCheckBoxUniqueColumn.isSelected());
            } else {
                return false;
            }
        } else {
            attribute.setIdentifier(jCheckBoxIdentifier.isSelected());
            attribute.setUniqueColumn(jCheckBoxUniqueColumn.isSelected());
        }

        attribute.setNotNull(jCheckBoxNotNull.isSelected());
        attribute.setSqlType(jComboBoxSQLType.getSelectedItem().toString());
        if ((jTextFieldSize.getText().isEmpty())) {
            attribute.setSize(50);
        } else {
            try {
                if (Integer.parseInt(jTextFieldSize.getText()) >= 0) {
                    attribute.setSize(Integer.parseInt(jTextFieldSize.getText()));
                } else {
                    attribute.setSize(50);
                }
            } catch (Exception e) {
                attribute.setSize(50);
            }
        }

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox2 = new javax.swing.JCheckBox();
        jPanelWest = new javax.swing.JPanel();
        jTextFieldName = new javax.swing.JTextField();
        jPanelCenter = new javax.swing.JPanel();
        jTextFieldUMLType = new javax.swing.JTextField();
        jCheckBoxIdentifier = new javax.swing.JCheckBox();
        jCheckBoxUniqueColumn = new javax.swing.JCheckBox();
        jCheckBoxNotNull = new javax.swing.JCheckBox();
        jPanelEast = new javax.swing.JPanel();
        jComboBoxSQLType = new javax.swing.JComboBox();
        jTextFieldSize = new javax.swing.JTextField();

        jCheckBox2.setText("jCheckBox2");

        setLayout(new java.awt.BorderLayout());

        jPanelWest.setPreferredSize(new java.awt.Dimension(120, 25));
        jPanelWest.setLayout(new java.awt.GridLayout(1, 1));

        jTextFieldName.setEditable(false);
        jTextFieldName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldName.setBorder(null);
        jTextFieldName.setOpaque(false);
        jPanelWest.add(jTextFieldName);

        add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelCenter.setLayout(new java.awt.GridLayout(1, 6));

        jTextFieldUMLType.setEditable(false);
        jTextFieldUMLType.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldUMLType.setBorder(null);
        jTextFieldUMLType.setOpaque(false);
        jPanelCenter.add(jTextFieldUMLType);

        jCheckBoxIdentifier.setToolTipText("");
        jCheckBoxIdentifier.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCheckBoxIdentifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxIdentifierActionPerformed(evt);
            }
        });
        jPanelCenter.add(jCheckBoxIdentifier);

        jCheckBoxUniqueColumn.setToolTipText("");
        jCheckBoxUniqueColumn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanelCenter.add(jCheckBoxUniqueColumn);

        jCheckBoxNotNull.setToolTipText("");
        jCheckBoxNotNull.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanelCenter.add(jCheckBoxNotNull);

        add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelEast.setPreferredSize(new java.awt.Dimension(180, 25));
        jPanelEast.setLayout(new java.awt.GridLayout(1, 2));

        jComboBoxSQLType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanelEast.add(jComboBoxSQLType);

        jTextFieldSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldSize.setBorder(null);
        jTextFieldSize.setOpaque(false);
        jPanelEast.add(jTextFieldSize);

        add(jPanelEast, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxIdentifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxIdentifierActionPerformed

        if (jCheckBoxIdentifier.isSelected()) {
            jCheckBoxUniqueColumn.setSelected(jCheckBoxIdentifier.isSelected());
            jCheckBoxUniqueColumn.setEnabled(false);
        } else {
            jCheckBoxUniqueColumn.setSelected(jCheckBoxIdentifier.isSelected());
            jCheckBoxUniqueColumn.setEnabled(true);
        }
    }//GEN-LAST:event_jCheckBoxIdentifierActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBoxIdentifier;
    private javax.swing.JCheckBox jCheckBoxNotNull;
    private javax.swing.JCheckBox jCheckBoxUniqueColumn;
    private javax.swing.JComboBox jComboBoxSQLType;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldSize;
    private javax.swing.JTextField jTextFieldUMLType;
    // End of variables declaration//GEN-END:variables
}