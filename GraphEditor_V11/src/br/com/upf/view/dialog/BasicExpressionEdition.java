/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view.dialog;

import br.com.upf.beans.Attribute;
import br.com.upf.beans.BasicExpression;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.util.Operator;
import br.com.upf.view.GraphEditor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class BasicExpressionEdition extends javax.swing.JDialog {

    private static final long serialVersionUID = -1297061756984582051L;

    /**
     *
     */
    private GraphEditor graphEditor;

    /**
     *
     */
    private BasicExpression editingBasicExpression;

    /**
     *
     */
    private ClassElement classElement;

    /**
     *
     */
    private ClassModel classModel;

    /**
     *
     */
    private ArrayList<Attribute> attributes;

    /**
     *
     */
    private ArrayList<Operator> operators;

    /**
     *
     */
    private Attribute selectedAttribute;

    /**
     *
     */
    private Operator selectedOperator;

    /**
     * Creates new form BasicExpressionEdition
     *
     * @param editor
     * @param expression
     * @param edition
     * @param element
     */
    public BasicExpressionEdition(GraphEditor editor, BasicExpression expression, boolean edition, ClassElement element) {
        super(editor, true);
        this.graphEditor = editor;
        this.editingBasicExpression = expression;
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.attributes = classModel.getAttributes();
        this.operators = graphEditor.getOperators();
        initComponents();

        updateTitle();
        // Edição Expressão
        if (edition) {
            attributesLoad();
            operatorsLoad();
            loadBasicExpression();
            viewFields(true);
            loadBasicExpression();
        } else {
            // Nova Expressão
            attributesLoad();
            viewFields(false);
            viewValidationFields(false);
        }
    }

    /**
     * Atualiza o titulo
     */
    private void updateTitle() {
        this.setTitle("Edição - Expressão Básica");
    }

    /**
     * Carrega a expressão para edição
     */
    private void loadBasicExpression() {
        operatorsLoad();
        viewFields(true);
        jComboBoxAttributes.setSelectedItem(editingBasicExpression.getAttribute().getName());
        jComboBoxOperators.setSelectedItem(editingBasicExpression.getOperator().getSqlNotation());
        jTextFieldValue.setText(editingBasicExpression.getValue());
    }

    /**
     * Carrega os atributos
     */
    private void attributesLoad() {
        jComboBoxAttributes.removeAllItems();
        jComboBoxAttributes.addItem("Selecione");
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isMonitorable()) {
                jComboBoxAttributes.addItem(attributes.get(i).getName());
            }
        }
    }

    /**
     * Carrega os operadores Adequados ao tipo do atributo selectedIndex
     */
    private void operatorsLoad() {
        if (selectedAttribute != null) {
            if (selectedAttribute.isCharacterValue()) {
                characterOperatorsLoad();
            } else {
                numberOperatorsLoad();
            }
        }
    }

    /**
     * Carrega OPeradores de Números
     */
    private void numberOperatorsLoad() {
        int count = 1;
        Operator operator;
        jComboBoxOperators.removeAllItems();
        jComboBoxOperators.addItem("Selecione");
        for (int index = 0; index < operators.size(); index++) {
            operator = operators.get(index);
            if (operator.isEquality() || operator.isComparative()) {
                jComboBoxOperators.addItem(operator.getSqlNotation());
                count++;
            }
        }
        jComboBoxOperators.setMaximumRowCount(count);
    }

    /**
     * Carrega Operadores para Caracteres
     */
    private void characterOperatorsLoad() {
        int count = 1;
        Operator operator;
        jComboBoxOperators.removeAllItems();
        jComboBoxOperators.addItem("Selecione");
        for (int index = 0; index < operators.size(); index++) {
            operator = operators.get(index);
            if (operator.isEquality()) {
                jComboBoxOperators.addItem(operator.getSqlNotation());
                count++;
            }
        }
        jComboBoxOperators.setMaximumRowCount(count);
    }

    /**
     * Controla a visualização dos campos complementares ao atributo
     *
     * @param value
     */
    private void viewFields(boolean value) {
        jLabelAttributeType.setEnabled(value);
        jLabelOPerators.setEnabled(value);
        jLabelValues.setEnabled(value);
        jComboBoxOperators.setEnabled(value);
        jTextFieldValue.setEnabled(value);
    }

    /**
     * Controla a visualização dos campos de válidação
     *
     * @param value
     */
    private void viewValidationFields(boolean value) {
        jLabelOperatorsInformation.setVisible(value);
        jLabelValueInformation.setVisible(value);
    }

    /**
     * Limpa os campos em edição
     */
    private void clearFiels() {
        jLabelAttributeType.setText("");
        jComboBoxOperators.removeAllItems();
        jTextFieldValue.setText("");
    }

    /**
     * Valida os campos editaveis da expressão
     *
     * @return
     */
    private boolean validateBasicExpressionEditing() {
        boolean validOperator, validValue;

        viewValidationFields(false);

        // Validação - Operador
        if (jComboBoxOperators.getSelectedIndex() > 0) {
            validOperator = true;
        } else {
            jLabelOperatorsInformation.setVisible(true);
            validOperator = false;
        }

        // Validação - Valor
        if (!jTextFieldValue.getText().isEmpty()) {
            validValue = true;
        } else {
            jLabelValueInformation.setVisible(true);
            validValue = false;
        }

        return validOperator && validValue;
    }

    /**
     * Getter - Retorna um Objeto Atributo com o name recebido por parâmetro
     *
     * @param name
     * @return
     */
    private Attribute getAttribute(String name) {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getName().equalsIgnoreCase(name)) {
                return attributes.get(i);
            }
        }
        return null;
    }

    /**
     * Getter - Retorna um Objeto Operador com o nome recebido por parâmetro
     *
     * @param name
     * @return
     */
    private Operator getOperator(String name) {
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).getSqlNotation().equalsIgnoreCase(name)) {
                return operators.get(i);
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelSouth = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jLabelAttributes = new javax.swing.JLabel();
        jLabelOPerators = new javax.swing.JLabel();
        jLabelValues = new javax.swing.JLabel();
        jComboBoxAttributes = new javax.swing.JComboBox();
        jComboBoxOperators = new javax.swing.JComboBox();
        jTextFieldValue = new javax.swing.JTextField();
        jLabelAttributeType = new javax.swing.JLabel();
        jLabelOperatorsInformation = new javax.swing.JLabel();
        jLabelValueInformation = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(400, 45));

        jButtonSave.setText("Salvar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSouthLayout = new javax.swing.GroupLayout(jPanelSouth);
        jPanelSouth.setLayout(jPanelSouthLayout);
        jPanelSouthLayout.setHorizontalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSouthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSouthLayout.setVerticalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSouthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        jLabelAttributes.setText("Atributo:");

        jLabelOPerators.setText("Operador:");

        jLabelValues.setText("Valor:");

        jComboBoxAttributes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxAttributesActionPerformed(evt);
            }
        });

        jComboBoxOperators.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxOperators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOperatorsActionPerformed(evt);
            }
        });

        jTextFieldValue.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldValueKeyTyped(evt);
            }
        });

        jLabelOperatorsInformation.setForeground(new java.awt.Color(255, 102, 0));
        jLabelOperatorsInformation.setText("Campo Obrigatório");

        jLabelValueInformation.setForeground(new java.awt.Color(255, 102, 0));
        jLabelValueInformation.setText("Campo Obrigatório");

        javax.swing.GroupLayout jPanelCenterLayout = new javax.swing.GroupLayout(jPanelCenter);
        jPanelCenter.setLayout(jPanelCenterLayout);
        jPanelCenterLayout.setHorizontalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCenterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabelAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabelOPerators, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxOperators, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabelValues, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelAttributeType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabelOperatorsInformation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                        .addComponent(jLabelValueInformation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelCenterLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBoxAttributes, jComboBoxOperators, jTextFieldValue});

        jPanelCenterLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelAttributes, jLabelOPerators, jLabelValues});

        jPanelCenterLayout.setVerticalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCenterLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAttributeType, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelOPerators, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxOperators, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOperatorsInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelValues, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelValueInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelCenterLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelAttributeType, jLabelOperatorsInformation, jLabelValueInformation});

        jPanelCenterLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxAttributes, jComboBoxOperators, jTextFieldValue});

        jPanelCenterLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelAttributes, jLabelOPerators, jLabelValues});

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 359, 212);
    }// </editor-fold>//GEN-END:initComponents

    /**
     *
     * @param evt
     */
    private void jComboBoxAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxAttributesActionPerformed
        if (jComboBoxAttributes.getSelectedItem() != null) {
            if (jComboBoxAttributes.getSelectedIndex() != 0) {
                selectedAttribute = getAttribute(jComboBoxAttributes.getSelectedItem().toString());
                if (selectedAttribute != null) {
                    jLabelAttributeType.setText(selectedAttribute.getUmlType());
                    operatorsLoad();
                    viewFields(true);
                }
            } else {
                clearFiels();
                viewFields(true);
                viewValidationFields(false);
            }
        }
    }//GEN-LAST:event_jComboBoxAttributesActionPerformed

    /**
     *
     * @param evt
     */
    private void jComboBoxOperatorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxOperatorsActionPerformed
        if (jComboBoxOperators.getSelectedItem() != null) {
            if (jComboBoxOperators.getSelectedIndex() > 0) {
                selectedOperator = getOperator(jComboBoxOperators.getSelectedItem().toString());
            } else {
                selectedOperator = null;
            }
        } else {
            selectedOperator = null;
        }
    }//GEN-LAST:event_jComboBoxOperatorsActionPerformed

    /**
     *
     * @param evt
     */
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        if (validateBasicExpressionEditing()) {
            editingBasicExpression.setAttribute(selectedAttribute);
            editingBasicExpression.setOperator(selectedOperator);
            editingBasicExpression.setValue(jTextFieldValue.getText());
            this.setVisible(false);
        } else {
            System.out.println("Validação false");
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    /**
     *
     * @param evt
     */
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     *
     * @param evt
     */
    private void jTextFieldValueKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldValueKeyTyped
        char c = evt.getKeyChar();

        if (selectedAttribute.getUmlType().equalsIgnoreCase("Integer")) {
            if (!Character.isDigit(c) && KeyEvent.VK_BACK_SPACE != c) {
                evt.consume();
            }
        }
        if (selectedAttribute.getUmlType().equalsIgnoreCase("Float")
                || selectedAttribute.getUmlType().equalsIgnoreCase("Double")) {
            if (!Character.isDigit(c) && KeyEvent.VK_BACK_SPACE != c) {
                if (c != 46) {
                    evt.consume();
                }
            }
        }
    }//GEN-LAST:event_jTextFieldValueKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox jComboBoxAttributes;
    private javax.swing.JComboBox jComboBoxOperators;
    private javax.swing.JLabel jLabelAttributeType;
    private javax.swing.JLabel jLabelAttributes;
    private javax.swing.JLabel jLabelOPerators;
    private javax.swing.JLabel jLabelOperatorsInformation;
    private javax.swing.JLabel jLabelValueInformation;
    private javax.swing.JLabel jLabelValues;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JTextField jTextFieldValue;
    // End of variables declaration//GEN-END:variables
}
