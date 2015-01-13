/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view.dialog.validation;

import br.com.upf.beans.Attribute;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.beans.StandartExpression;
import br.com.upf.beans.State;
import br.com.upf.view.GraphEditor;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class DictionaryOfStatesValidation extends javax.swing.JDialog {
    
    private static final long serialVersionUID = -585371567424043369L;

    /**
     *
     */
    private GraphEditor graphEditor;

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
    private DiagramOfStateModel diagramOfStateModel;

    /**
     *
     */
    private ArrayList<State> states;

    /**
     *
     */
    private ArrayList<Attribute> attributes;

    /**
     *
     */
    private String yesUrlIcon = "/br/com/upf/images/yes.png";

    /**
     *
     */
    private String noUrlIcon = "/br/com/upf/images/no.png";

    /**
     *
     */
    private Icon yesIcon = new ImageIcon(DiagramOfStatesValidation.class.getResource(yesUrlIcon));

    /**
     *
     */
    private Icon noIcon = new ImageIcon(DiagramOfStatesValidation.class.getResource(noUrlIcon));
    
    private JLabel jLabelOrder;
    
    private JLabel jLabelState;
    
    private JLabel jLabelSyntax;
    
    private JLabel jLabelExpression;

    /**
     * Creates new form DictionaryOfStatesValidation
     *
     * @param editor
     * @param element
     */
    public DictionaryOfStatesValidation(GraphEditor editor, ClassElement element) {
        super(editor, true);
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.diagramOfStateModel = classModel.getDiagramOfStateModel();
        this.states = diagramOfStateModel.getStates();
        this.attributes = classModel.getAttributes();
        initComponents();
        updateTitle();
        submitValidation();
    }
    
    private void updateTitle() {
        this.setTitle("Validação - Dicionário de Estados");
    }
    
    private Dimension calculateDimension() {
        if (diagramOfStateModel.getStates().size() > 8) {
            return new Dimension(600, diagramOfStateModel.getStates().size() * 40);
        } else {
            return new Dimension(600, 320);
        }
    }
    
    private int calculateNumberOfLines() {
        if (diagramOfStateModel.getStates().size() > 8) {
            return diagramOfStateModel.getStates().size();
        } else {
            return 8;
        }
    }
    
    private void submitValidation() {
        
        if (classModel.isMethodElaborationCustom()) {
            validationMethodCustom();
        } else {
            jLabelTitleSyntax.setVisible(false);
            validationMethodStandart();
        }
    }
    
    private void validationMethodCustom() {
        State currentState;
        for (int i = 0; i < states.size(); i++) {
            currentState = states.get(i);
            
            jLabelOrder = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
            jLabelState = new JLabel(currentState.getName());
            
            if (currentState.getCustomExpression().validateSyntax()) {
                jLabelSyntax = new JLabel(yesIcon, JLabel.CENTER);
            } else {
                jLabelSyntax = new JLabel(noIcon, JLabel.CENTER);
            }
            
            if (currentState.getCustomExpression().validateExpression(classModel.getMonitorableAttributes())) {
                jLabelExpression = new JLabel(yesIcon, JLabel.CENTER);
            } else {
                jLabelExpression = new JLabel(noIcon, JLabel.CENTER);
            }
            
            jPanelOrder.add(jLabelOrder);
            jPanelState.add(jLabelState);
            jPanelSyntax.add(jLabelSyntax);
            jPanelExpression.add(jLabelExpression);
        }
    }
    
    private void validationMethodStandart() {
        State currentState;
        for (int i = 0; i < states.size(); i++) {
            currentState = states.get(i);
            
            jLabelOrder = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
            jLabelState = new JLabel(currentState.getName());
            
            if (validateExpressionsState(currentState)) {
                jLabelExpression = new JLabel(yesIcon, JLabel.CENTER);
            } else {
                jLabelExpression = new JLabel(noIcon, JLabel.CENTER);
            }
            
            jPanelOrder.add(jLabelOrder);
            jPanelState.add(jLabelState);
            jPanelExpression.add(jLabelExpression);
        }
    }
    
    private boolean validateExpressionsState(State state) {
        
        ArrayList<StandartExpression> currentExpressions = state.getStandartExpressions();
        // PERCORRE TODOS OS ATRIBUTOS
        for (int i = 0; i < attributes.size(); i++) {
            Attribute currentAttribute = attributes.get(i);
            boolean validState = true;
            
            if (currentAttribute.isMonitorable()) {
                ArrayList<StandartExpression> expressionsContainsAttribute = new ArrayList<>();
                // PERCORRE TODAS AS EXPRESSÕES DO ESTADO CORRENTE
                for (int k = 0; k < currentExpressions.size(); k++) {
                    StandartExpression currentExpression = currentExpressions.get(k);
                    if (currentExpression.getAttribute().equals(currentAttribute)) {
                        expressionsContainsAttribute.add(currentExpression);
                    }
                }

                // VERIFICA QUANTAS EXPRESSÕES RELACIONADAS AO ATRIBUTO EXISTEM NO ESTADO
                if (expressionsContainsAttribute.size() > 0) {
                    // estado correto
                } else {
                    validState = false;
                }
            }
            if (!validState) {
                return false;
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

        jPanelCenter = new javax.swing.JPanel();
        jPanelValidationTitles = new javax.swing.JPanel();
        jPanelTitleOrder = new javax.swing.JPanel();
        jLabelTitleOrder = new javax.swing.JLabel();
        jPanelTitleState = new javax.swing.JPanel();
        jLabelTitleState = new javax.swing.JLabel();
        jPanelTitleStatus = new javax.swing.JPanel();
        jPanelTitleSyntax = new javax.swing.JPanel();
        jLabelTitleSyntax = new javax.swing.JLabel();
        jPanelTitleExpression = new javax.swing.JPanel();
        jLabelTitleExpression = new javax.swing.JLabel();
        jScrollPaneApresentation = new javax.swing.JScrollPane();
        jPanelValidationApresentation = new javax.swing.JPanel();
        jPanelOrder = new javax.swing.JPanel();
        jPanelState = new javax.swing.JPanel();
        jPanelStatus = new javax.swing.JPanel();
        jPanelSyntax = new javax.swing.JPanel();
        jPanelExpression = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanelValidationRules = new javax.swing.JPanel();
        jScrollPaneValidationRules = new javax.swing.JScrollPane();
        jTextAreaValidationRules = new javax.swing.JTextArea();
        jPanelValidationControl = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelCenter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Validação", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 13), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanelValidationTitles.setPreferredSize(new java.awt.Dimension(600, 45));
        jPanelValidationTitles.setLayout(new java.awt.BorderLayout());

        jPanelTitleOrder.setPreferredSize(new java.awt.Dimension(75, 40));
        jPanelTitleOrder.setRequestFocusEnabled(false);
        jPanelTitleOrder.setLayout(new java.awt.BorderLayout());

        jLabelTitleOrder.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelTitleOrder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleOrder.setText("Ordem");
        jPanelTitleOrder.add(jLabelTitleOrder, java.awt.BorderLayout.CENTER);

        jPanelValidationTitles.add(jPanelTitleOrder, java.awt.BorderLayout.LINE_START);

        jPanelTitleState.setPreferredSize(new java.awt.Dimension(600, 750));
        jPanelTitleState.setLayout(new java.awt.BorderLayout());

        jLabelTitleState.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelTitleState.setText("  Estado");
        jPanelTitleState.add(jLabelTitleState, java.awt.BorderLayout.CENTER);

        jPanelValidationTitles.add(jPanelTitleState, java.awt.BorderLayout.CENTER);

        jPanelTitleStatus.setPreferredSize(new java.awt.Dimension(250, 40));
        jPanelTitleStatus.setLayout(new java.awt.BorderLayout());

        jPanelTitleSyntax.setPreferredSize(new java.awt.Dimension(125, 100));
        jPanelTitleSyntax.setLayout(new java.awt.BorderLayout());

        jLabelTitleSyntax.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelTitleSyntax.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleSyntax.setText("Sintaxe");
        jPanelTitleSyntax.add(jLabelTitleSyntax, java.awt.BorderLayout.CENTER);

        jPanelTitleStatus.add(jPanelTitleSyntax, java.awt.BorderLayout.CENTER);

        jPanelTitleExpression.setPreferredSize(new java.awt.Dimension(125, 100));
        jPanelTitleExpression.setLayout(new java.awt.BorderLayout());

        jLabelTitleExpression.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelTitleExpression.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleExpression.setText("Expressão");
        jPanelTitleExpression.add(jLabelTitleExpression, java.awt.BorderLayout.CENTER);

        jPanelTitleStatus.add(jPanelTitleExpression, java.awt.BorderLayout.EAST);

        jPanelValidationTitles.add(jPanelTitleStatus, java.awt.BorderLayout.LINE_END);

        jPanelCenter.add(jPanelValidationTitles, java.awt.BorderLayout.NORTH);

        jScrollPaneApresentation.setBorder(null);
        jScrollPaneApresentation.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneApresentation.setPreferredSize(new java.awt.Dimension(600, 200));

        jPanelValidationApresentation.setPreferredSize(calculateDimension());
        jPanelValidationApresentation.setLayout(new java.awt.BorderLayout());

        jPanelOrder.setPreferredSize(new java.awt.Dimension(75, 213));
        jPanelOrder.setLayout(new java.awt.GridLayout(calculateNumberOfLines(), 1));
        jPanelValidationApresentation.add(jPanelOrder, java.awt.BorderLayout.LINE_START);

        jPanelState.setLayout(new java.awt.GridLayout(calculateNumberOfLines(), 1));
        jPanelValidationApresentation.add(jPanelState, java.awt.BorderLayout.CENTER);

        jPanelStatus.setPreferredSize(new java.awt.Dimension(250, 213));
        jPanelStatus.setLayout(new java.awt.BorderLayout());

        jPanelSyntax.setLayout(new java.awt.GridLayout(calculateNumberOfLines(), 1));
        jPanelStatus.add(jPanelSyntax, java.awt.BorderLayout.CENTER);

        jPanelExpression.setPreferredSize(new java.awt.Dimension(125, 320));
        jPanelExpression.setLayout(new java.awt.GridLayout(calculateNumberOfLines(), 1));
        jPanelStatus.add(jPanelExpression, java.awt.BorderLayout.EAST);

        jPanelValidationApresentation.add(jPanelStatus, java.awt.BorderLayout.LINE_END);

        jScrollPaneApresentation.setViewportView(jPanelValidationApresentation);

        jPanelCenter.add(jScrollPaneApresentation, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(600, 155));
        jPanelSouth.setLayout(new java.awt.BorderLayout());

        jPanelValidationRules.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Regras de Validação", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 13), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanelValidationRules.setPreferredSize(new java.awt.Dimension(800, 120));
        jPanelValidationRules.setLayout(new java.awt.BorderLayout());

        jScrollPaneValidationRules.setBorder(null);
        jScrollPaneValidationRules.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneValidationRules.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextAreaValidationRules.setEditable(false);
        jTextAreaValidationRules.setColumns(20);
        jTextAreaValidationRules.setFont(new java.awt.Font("Monospaced", 2, 13)); // NOI18N
        jTextAreaValidationRules.setForeground(new java.awt.Color(102, 102, 102));
        jTextAreaValidationRules.setLineWrap(true);
        jTextAreaValidationRules.setRows(5);
        jTextAreaValidationRules.setText("Deve Haver uma expressão lógica para cada estado do objeto. A expressão deve descrever quais \nvalores poderão ser assumidos, pelos atributos monitorados, durante o ciclo de vida do objeto.");
        jTextAreaValidationRules.setToolTipText("");
        jTextAreaValidationRules.setBorder(null);
        jTextAreaValidationRules.setOpaque(false);
        jScrollPaneValidationRules.setViewportView(jTextAreaValidationRules);

        jPanelValidationRules.add(jScrollPaneValidationRules, java.awt.BorderLayout.CENTER);

        jPanelSouth.add(jPanelValidationRules, java.awt.BorderLayout.CENTER);

        jPanelValidationControl.setPreferredSize(new java.awt.Dimension(800, 45));

        jButtonExit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonExit.setText("Sair");
        jButtonExit.setPreferredSize(new java.awt.Dimension(150, 25));
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelValidationControlLayout = new javax.swing.GroupLayout(jPanelValidationControl);
        jPanelValidationControl.setLayout(jPanelValidationControlLayout);
        jPanelValidationControlLayout.setHorizontalGroup(
            jPanelValidationControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelValidationControlLayout.createSequentialGroup()
                .addContainerGap(638, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelValidationControlLayout.setVerticalGroup(
            jPanelValidationControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelValidationControlLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelSouth.add(jPanelValidationControl, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        setBounds(0, 0, 818, 592);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JLabel jLabelTitleExpression;
    private javax.swing.JLabel jLabelTitleOrder;
    private javax.swing.JLabel jLabelTitleState;
    private javax.swing.JLabel jLabelTitleSyntax;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelExpression;
    private javax.swing.JPanel jPanelOrder;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelState;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelSyntax;
    private javax.swing.JPanel jPanelTitleExpression;
    private javax.swing.JPanel jPanelTitleOrder;
    private javax.swing.JPanel jPanelTitleState;
    private javax.swing.JPanel jPanelTitleStatus;
    private javax.swing.JPanel jPanelTitleSyntax;
    private javax.swing.JPanel jPanelValidationApresentation;
    private javax.swing.JPanel jPanelValidationControl;
    private javax.swing.JPanel jPanelValidationRules;
    private javax.swing.JPanel jPanelValidationTitles;
    private javax.swing.JScrollPane jScrollPaneApresentation;
    private javax.swing.JScrollPane jScrollPaneValidationRules;
    private javax.swing.JTextArea jTextAreaValidationRules;
    // End of variables declaration//GEN-END:variables
}
