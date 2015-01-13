/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view.dialog;

import br.com.upf.beans.Attribute;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.view.GraphEditor;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class ParametersTable extends javax.swing.JDialog {

    private static final long serialVersionUID = -6852207441561951795L;

    private GraphEditor graphEditor;

    private ClassElement classElement;

    private ClassModel classModel;

    private ArrayList<Attribute> attributes;

    private ArrayList<AttributeApresentation> attributeApresentations;

    /**
     * Creates new form UMLForSQLConversion
     *
     * @param editor
     * @param element
     */
    public ParametersTable(GraphEditor editor, ClassElement element) {
        super(editor, true);
        this.graphEditor = editor;
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.attributes = classModel.getAttributes();
        this.attributeApresentations = new ArrayList<>();
        initComponents();
        loadAttributesVisualization();
        updateTitle();
    }

    private void loadAttributesVisualization() {
        for (int i = 0; i < attributes.size(); i++) {
            AttributeApresentation aa = new AttributeApresentation(graphEditor, attributes.get(i));
            attributeApresentations.add(aa);
            jPanelApresentation.add(aa);
        }
    }

    private void updateTitle() {
        this.setTitle("Parâmetros da Tabela");
    }

    /**
     * Verifica se existe ao menos um atributo identificador
     *
     * @return
     */
    private boolean checkContainsIdentifier() {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isIdentifier()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica as informações e as salva
     *
     * @return
     */
    private boolean checkAndSaveEdition() {
        boolean validation = true;
        for (int i = 0; i < attributeApresentations.size(); i++) {
            AttributeApresentation current = attributeApresentations.get(i);
            if (!current.saveAttributeEdition()) {
                validation = false;
            }
        }

        return validation;
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
        jButtonGenerateTable = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jPanelCenterTitles = new javax.swing.JPanel();
        jPanelTitlesWest = new javax.swing.JPanel();
        jLabelUMLAttribute = new javax.swing.JLabel();
        jPanelTitlesCenter = new javax.swing.JPanel();
        jLabelUMLType = new javax.swing.JLabel();
        jLabelSQLIdentifier = new javax.swing.JLabel();
        jLabelSQLUniqueColumn = new javax.swing.JLabel();
        jLabelSQLNotNull = new javax.swing.JLabel();
        jPanelTitlesEast = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanelSpace = new javax.swing.JPanel();
        jPanelCenterApresentation = new javax.swing.JPanel();
        jPanelApresentationAttributes = new javax.swing.JPanel();
        jScrollPaneApresentationAttributes = new javax.swing.JScrollPane();
        jPanelApresentation = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(600, 45));

        jButtonGenerateTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonGenerateTable.setText("Gerar Tabela");
        jButtonGenerateTable.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonGenerateTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateTableActionPerformed(evt);
            }
        });

        jButtonExit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonExit.setText("Sair");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSouthLayout = new javax.swing.GroupLayout(jPanelSouth);
        jPanelSouth.setLayout(jPanelSouthLayout);
        jPanelSouthLayout.setHorizontalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSouthLayout.createSequentialGroup()
                .addContainerGap(481, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonGenerateTable, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelSouthLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonExit, jButtonGenerateTable});

        jPanelSouthLayout.setVerticalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSouthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGenerateTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanelCenterTitles.setPreferredSize(new java.awt.Dimension(600, 40));
        jPanelCenterTitles.setLayout(new java.awt.BorderLayout());

        jPanelTitlesWest.setPreferredSize(new java.awt.Dimension(120, 40));
        jPanelTitlesWest.setLayout(new java.awt.BorderLayout());

        jLabelUMLAttribute.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelUMLAttribute.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelUMLAttribute.setText("Atributo");
        jPanelTitlesWest.add(jLabelUMLAttribute, java.awt.BorderLayout.CENTER);

        jPanelCenterTitles.add(jPanelTitlesWest, java.awt.BorderLayout.WEST);

        jPanelTitlesCenter.setPreferredSize(new java.awt.Dimension(500, 40));
        jPanelTitlesCenter.setLayout(new java.awt.GridLayout(1, 6));

        jLabelUMLType.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelUMLType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelUMLType.setText("Tipo");
        jPanelTitlesCenter.add(jLabelUMLType);

        jLabelSQLIdentifier.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelSQLIdentifier.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSQLIdentifier.setText("Identificador");
        jPanelTitlesCenter.add(jLabelSQLIdentifier);

        jLabelSQLUniqueColumn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelSQLUniqueColumn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSQLUniqueColumn.setText("Único");
        jPanelTitlesCenter.add(jLabelSQLUniqueColumn);

        jLabelSQLNotNull.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelSQLNotNull.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSQLNotNull.setText("Não Nulo");
        jPanelTitlesCenter.add(jLabelSQLNotNull);

        jPanelCenterTitles.add(jPanelTitlesCenter, java.awt.BorderLayout.CENTER);

        jPanelTitlesEast.setPreferredSize(new java.awt.Dimension(203, 40));
        jPanelTitlesEast.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tipo");
        jPanel1.add(jLabel2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Tamanho");
        jLabel1.setToolTipText("");
        jPanel1.add(jLabel1);

        jPanelTitlesEast.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanelSpace.setPreferredSize(new java.awt.Dimension(23, 40));

        javax.swing.GroupLayout jPanelSpaceLayout = new javax.swing.GroupLayout(jPanelSpace);
        jPanelSpace.setLayout(jPanelSpaceLayout);
        jPanelSpaceLayout.setHorizontalGroup(
            jPanelSpaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );
        jPanelSpaceLayout.setVerticalGroup(
            jPanelSpaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanelTitlesEast.add(jPanelSpace, java.awt.BorderLayout.EAST);

        jPanelCenterTitles.add(jPanelTitlesEast, java.awt.BorderLayout.EAST);

        jPanelCenter.add(jPanelCenterTitles, java.awt.BorderLayout.PAGE_START);

        jPanelCenterApresentation.setLayout(new java.awt.BorderLayout());

        jPanelApresentationAttributes.setPreferredSize(new java.awt.Dimension(640, 25));
        jPanelApresentationAttributes.setLayout(new java.awt.BorderLayout());

        jScrollPaneApresentationAttributes.setToolTipText("");

        jPanelApresentation.setLayout(new java.awt.GridLayout(50, 1));
        jScrollPaneApresentationAttributes.setViewportView(jPanelApresentation);

        jPanelApresentationAttributes.add(jScrollPaneApresentationAttributes, java.awt.BorderLayout.CENTER);

        jPanelCenterApresentation.add(jPanelApresentationAttributes, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanelCenterApresentation, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 818, 492);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonGenerateTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateTableActionPerformed
        if (checkAndSaveEdition()) {
            if (checkContainsIdentifier()) {
                this.setVisible(false);
                classModel.generateTableCode();
                graphEditor.getClassEditor().refreshClassSelected();
                graphEditor.tableCodeView(classElement);
            } else {
                JOptionPane.showMessageDialog(graphEditor, "Informe o's' Identificador'es'!", "Validação", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(graphEditor, "Identicador'es' deve'm' ser de tipo Inteiro!", "Validação", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonGenerateTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonGenerateTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelSQLIdentifier;
    private javax.swing.JLabel jLabelSQLNotNull;
    private javax.swing.JLabel jLabelSQLUniqueColumn;
    private javax.swing.JLabel jLabelUMLAttribute;
    private javax.swing.JLabel jLabelUMLType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelApresentation;
    private javax.swing.JPanel jPanelApresentationAttributes;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCenterApresentation;
    private javax.swing.JPanel jPanelCenterTitles;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelTitlesCenter;
    private javax.swing.JPanel jPanelTitlesEast;
    private javax.swing.JPanel jPanelTitlesWest;
    private javax.swing.JScrollPane jScrollPaneApresentationAttributes;
    // End of variables declaration//GEN-END:variables
}
