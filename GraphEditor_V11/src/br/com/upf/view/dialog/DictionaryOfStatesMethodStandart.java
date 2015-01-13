/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view.dialog;

import br.com.upf.beans.Attribute;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.CustomExpression;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.beans.ElementExpression;
import br.com.upf.beans.StandartExpression;
import br.com.upf.beans.State;
import br.com.upf.util.Operator;
import br.com.upf.view.GraphEditor;
import com.mxgraph.util.mxResources;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author GiordaniAntonio
 */
public class DictionaryOfStatesMethodStandart extends javax.swing.JDialog {

    private static final long serialVersionUID = 2923949263882749441L;

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
    private Icon yesIcon = new ImageIcon(DictionaryOfStatesMethodStandart.class.getResource(yesUrlIcon));

    /**
     *
     */
    private Icon noIcon = new ImageIcon(DictionaryOfStatesMethodStandart.class.getResource(noUrlIcon));

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
    private ArrayList<Operator> operators;

    /**
     *
     */
    private ArrayList<StandartExpression> expressionsAttributes;

    /**
     * Atributo - expressão em construção
     */
    private Attribute selectedAttribute;

    /**
     *
     */
    private Operator selectedOperator;

    /**
     *
     */
    private State selectedState;

    /**
     *
     */
    private StandartExpression selectedStandartExpression;

    /**
     * Expressão em construção;
     */
    private StandartExpression editingStandartExpression;

    /**
     *
     */
    private StandartExpression previousStandartExpression;

    /**
     *
     */
    private StandartExpression newStandartExpression;

    /**
     *
     */
    private boolean editing;

    /**
     *
     */
    private Integer rowClickStandartExpressions;

    /**
     *
     */
    private MethodElaborationDictionary methodElaborationDictionary;

    /**
     * Construtor
     *
     * @param editor
     * @param element
     */
    public DictionaryOfStatesMethodStandart(GraphEditor editor, ClassElement element) {
        super(editor, true);
        this.graphEditor = editor;
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.attributes = classModel.getAttributes();
        this.diagramOfStateModel = classElement.getStateEditor().getDiagramOfStateModel();
        this.states = diagramOfStateModel.getStates();
        this.newStandartExpression = new StandartExpression();
        this.operators = graphEditor.getOperators();
        initComponents();

        if (!classModel.isInitializedDictionaryOfStates()) {
            classModel.setInitializedDictionaryOfStates(true);
            graphEditor.getMenuBarCustom().updateContextStateEditor(element);
        }

        selectedAttribute = null;
        selectedOperator = null;
        selectedState = null;

        contextualizeBuilder();
    }

    private void updateTitle(String title) {
        this.setTitle(title);
    }

    /**
     * Contextualiza o Construtor Simples
     */
    private void contextualizeBuilder() {
        updateTitle("Dicionário de Estados - Método Básico");
        setAlignColumnJTable();
        attributesLoad();
        enableFields(false);
        visibleValidationFields(false);
        editing = false;
    }

    /**
     * Configuração - Define o alinhamento do cabeçalho e conteudo das células
     * da tabela
     */
    private void setAlignColumnJTable() {
        TableColumn column;
        TableCellRenderer tcr = new AlinharCentro();
        for (int i = 0; i < jTableExpressions.getColumnCount(); i++) {
            column = jTableExpressions.getColumnModel().getColumn(i);
            column.setCellRenderer(tcr);
            column.setHeaderRenderer(tcr);
        }
    }

    /**
     * Limpa os campos de edição
     */
    private void clearFiels() {
        jLabelAttributeType.setText("");
        jComboBoxOperators.removeAllItems();
        jTextFieldValue.setText("");
        jComboBoxStates.removeAllItems();
    }

    /**
     * Preenche os campos com valores padrão
     */
    private void standartDataFields() {
        jLabelAttributeType.setText("");
        jComboBoxOperators.setSelectedIndex(0);
        jTextFieldValue.setText("");
        jComboBoxStates.setSelectedIndex(0);
        jButtonAdd.setText("Adicionar");
    }

    /**
     * Ativa ou desativa os campos de edição
     *
     * @param value
     */
    private void enableFields(boolean value) {
        jLabelOperator.setEnabled(value);
        jComboBoxOperators.setEnabled(value);
        jLabelValue.setEnabled(value);
        jTextFieldValue.setEnabled(value);
        jLabelState.setEnabled(value);
        jComboBoxStates.setEnabled(value);
        jButtonAdd.setEnabled(value);
    }

    /**
     * Ativa ou desativa os campos de validação
     *
     * @param value
     */
    private void visibleValidationFields(boolean value) {
        jLabelOperatorInformation.setVisible(value);
        jLabelValueInformation.setVisible(value);
        jLabelStateInformation.setVisible(value);
    }

    /**
     * Carrega as expressões do atributo selecionado
     *
     * @param attribute
     * @return
     */
    private boolean expressionsLoad(Attribute attribute) {
        listStandartExpressions.clear();
        if (attribute != null) {
            return listStandartExpressions.addAll(attribute.getExpressions());
        }
        return false;
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
                jComboBoxOperators.addItem(operator.getStandartNotation());
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
                jComboBoxOperators.addItem(operator.getStandartNotation());
                count++;
            }
        }
        jComboBoxOperators.setMaximumRowCount(count);
    }

    /**
     * Carrega os estados
     */
    private void statesLoad() {
        jComboBoxStates.removeAllItems();
        jComboBoxStates.addItem("Selecione");
        for (int i = 0; i < states.size(); i++) {
            jComboBoxStates.addItem(states.get(i).getName());
        }
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
     * Getter - Retorna um Objeto Estado com o name recebido por parâmetro
     *
     * @param name
     * @return
     */
    private State getState(String name) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).getName().equalsIgnoreCase(name)) {
                return states.get(i);
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
            if (operators.get(i).getStandartNotation().equalsIgnoreCase(name)) {
                return operators.get(i);
            }
        }
        JOptionPane.showMessageDialog(rootPane, "Não encontrou o operator");
        return null;
    }

    /**
     * Valida os campos editaveis da expressão
     *
     * @return
     */
    private boolean validateExpressionEditing() {
        boolean validOperator, validValue, validState;

        visibleValidationFields(false);

        // Validação - Operador
        if (jComboBoxOperators.getSelectedIndex() > 0) {
            validOperator = true;
        } else {
            jLabelOperatorInformation.setVisible(true);
            validOperator = false;
        }

        // Validação - Valor
        if (!jTextFieldValue.getText().isEmpty()) {
            validValue = true;
        } else {
            jLabelValueInformation.setVisible(true);
            validValue = false;
        }

        // Validação - Estado
        if (jComboBoxStates.getSelectedIndex() > 0) {
            validState = true;
        } else {
            jLabelStateInformation.setVisible(true);
            validState = false;
        }

        return validOperator && validValue && validState;
    }

    /**
     * Classe Interna - Utilizada no alinhamento do buildPanel das colunas da
     * Tabela
     */
    class AlinharCentro extends DefaultTableCellRenderer {

        public AlinharCentro() {
            setHorizontalAlignment(CENTER); // ou LEFT, RIGHT, etc
        }
    }

    /**
     * Exibe Dialogo para Alteração do Método de Elaboração do Dicionário de
     * estados
     *
     */
    public void enableMethodElaboration() {
        methodElaborationDictionary = new MethodElaborationDictionary(graphEditor, classElement);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - methodElaborationDictionary.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - methodElaborationDictionary.getHeight()) / 2;

        methodElaborationDictionary.setLocation(x, y);
        methodElaborationDictionary.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        listStandartExpressions = new LinkedList<br.com.upf.beans.StandartExpression>();
        listElements = new LinkedList<ElementExpression>();
        jPanelCenter = new javax.swing.JPanel();
        jPanelView = new javax.swing.JPanel();
        jScrollPaneExpressions = new javax.swing.JScrollPane();
        jTableExpressions = new javax.swing.JTable();
        jPanelBuilder = new javax.swing.JPanel();
        jComboBoxAttributes = new javax.swing.JComboBox();
        jComboBoxOperators = new javax.swing.JComboBox();
        jTextFieldValue = new javax.swing.JTextField();
        jComboBoxStates = new javax.swing.JComboBox();
        jButtonAdd = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jLabelAttribute = new javax.swing.JLabel();
        jLabelOperator = new javax.swing.JLabel();
        jLabelValue = new javax.swing.JLabel();
        jLabelState = new javax.swing.JLabel();
        jLabelAttributeType = new javax.swing.JLabel();
        jLabelOperatorInformation = new javax.swing.JLabel();
        jLabelValueInformation = new javax.swing.JLabel();
        jLabelStateInformation = new javax.swing.JLabel();
        jButtonClean = new javax.swing.JButton();
        jPanelSouth = new javax.swing.JPanel();
        jButtonGenerateTrigger = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jButtonParameters = new javax.swing.JButton();
        jButtonMethodElaboration = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        listStandartExpressions = org.jdesktop.observablecollections.ObservableCollections.observableList(listStandartExpressions);

        listElements = org.jdesktop.observablecollections.ObservableCollections.observableList(listElements);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanelView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelView.setPreferredSize(new java.awt.Dimension(788, 150));
        jPanelView.setLayout(new java.awt.BorderLayout());

        jTableExpressions.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStandartExpressions, jTableExpressions);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${attribute}"));
        columnBinding.setColumnName("Attribute");
        columnBinding.setColumnClass(br.com.upf.beans.Attribute.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${operator}"));
        columnBinding.setColumnName("Operator");
        columnBinding.setColumnClass(br.com.upf.util.Operator.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${value}"));
        columnBinding.setColumnName("Value");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${state}"));
        columnBinding.setColumnName("State");
        columnBinding.setColumnClass(br.com.upf.beans.State.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jTableExpressions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableExpressionsMouseClicked(evt);
            }
        });
        jScrollPaneExpressions.setViewportView(jTableExpressions);
        if (jTableExpressions.getColumnModel().getColumnCount() > 0) {
            jTableExpressions.getColumnModel().getColumn(0).setResizable(false);
            jTableExpressions.getColumnModel().getColumn(1).setResizable(false);
            jTableExpressions.getColumnModel().getColumn(2).setResizable(false);
            jTableExpressions.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanelView.add(jScrollPaneExpressions, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanelView, java.awt.BorderLayout.CENTER);

        jPanelBuilder.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelBuilder.setName(""); // NOI18N
        jPanelBuilder.setPreferredSize(new java.awt.Dimension(788, 160));

        jComboBoxAttributes.setToolTipText("Atributo");
        jComboBoxAttributes.setNextFocusableComponent(jComboBoxOperators);
        jComboBoxAttributes.setPreferredSize(new java.awt.Dimension(150, 25));
        jComboBoxAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxAttributesActionPerformed(evt);
            }
        });

        jComboBoxOperators.setToolTipText("Operador");
        jComboBoxOperators.setNextFocusableComponent(jTextFieldValue);
        jComboBoxOperators.setPreferredSize(new java.awt.Dimension(150, 25));
        jComboBoxOperators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOperatorsActionPerformed(evt);
            }
        });

        jTextFieldValue.setToolTipText("Valor");
        jTextFieldValue.setNextFocusableComponent(jComboBoxStates);
        jTextFieldValue.setPreferredSize(new java.awt.Dimension(150, 25));
        jTextFieldValue.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldValueKeyTyped(evt);
            }
        });

        jComboBoxStates.setToolTipText("Estado");
        jComboBoxStates.setNextFocusableComponent(jButtonAdd);
        jComboBoxStates.setPreferredSize(new java.awt.Dimension(150, 25));
        jComboBoxStates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStatesActionPerformed(evt);
            }
        });

        jButtonAdd.setText("Adicionar");
        jButtonAdd.setNextFocusableComponent(jComboBoxOperators);
        jButtonAdd.setPreferredSize(new java.awt.Dimension(100, 25));
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonEdit.setText("Editar");
        jButtonEdit.setNextFocusableComponent(jButtonDelete);
        jButtonEdit.setPreferredSize(new java.awt.Dimension(100, 25));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTableExpressions, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonEdit, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Excluir");
        jButtonDelete.setNextFocusableComponent(jButtonAdd);
        jButtonDelete.setPreferredSize(new java.awt.Dimension(100, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTableExpressions, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonDelete, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTableExpressions, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonDelete, org.jdesktop.beansbinding.BeanProperty.create("name"));
        bindingGroup.addBinding(binding);

        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jLabelAttribute.setText("Atributo:");
        jLabelAttribute.setPreferredSize(new java.awt.Dimension(50, 25));

        jLabelOperator.setText("Operador:");
        jLabelOperator.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabelValue.setText("Valor:");
        jLabelValue.setPreferredSize(new java.awt.Dimension(35, 25));

        jLabelState.setText("Estado:");
        jLabelState.setPreferredSize(new java.awt.Dimension(43, 25));

        jLabelAttributeType.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabelOperatorInformation.setForeground(new java.awt.Color(255, 102, 0));
        jLabelOperatorInformation.setText("Selecione o Operador!");
        jLabelOperatorInformation.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabelValueInformation.setForeground(new java.awt.Color(255, 102, 0));
        jLabelValueInformation.setText("Informe o valor!");
        jLabelValueInformation.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabelStateInformation.setForeground(new java.awt.Color(255, 102, 0));
        jLabelStateInformation.setText("Selecione o Estado!");
        jLabelStateInformation.setPreferredSize(new java.awt.Dimension(150, 25));

        jButtonClean.setText("Limpar");
        jButtonClean.setPreferredSize(new java.awt.Dimension(100, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jComboBoxOperators, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jButtonClean, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCleanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBuilderLayout = new javax.swing.GroupLayout(jPanelBuilder);
        jPanelBuilder.setLayout(jPanelBuilderLayout);
        jPanelBuilderLayout.setHorizontalGroup(
            jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBuilderLayout.createSequentialGroup()
                        .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxStates, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelAttributeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelValueInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelStateInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                        .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonClean, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonAdd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelBuilderLayout.createSequentialGroup()
                        .addComponent(jLabelOperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxOperators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelOperatorInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelBuilderLayout.setVerticalGroup(
            jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAttributeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelOperatorInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxOperators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelValueInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBuilderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxStates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStateInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonClean, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelCenter.add(jPanelBuilder, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(523, 45));

        jButtonGenerateTrigger.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonGenerateTrigger.setText("Criar Gatilhos");
        jButtonGenerateTrigger.setToolTipText("Criar Gatilhos");
        jButtonGenerateTrigger.setNextFocusableComponent(jButtonParameters);
        jButtonGenerateTrigger.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonGenerateTrigger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateTriggerActionPerformed(evt);
            }
        });

        jButtonExit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonExit.setText("Sair");
        jButtonExit.setToolTipText("Sair");
        jButtonExit.setNextFocusableComponent(jButtonGenerateTrigger);
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jButtonParameters.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonParameters.setText("Parâmetros");
        jButtonParameters.setToolTipText("Parâmetros do Dicionário de estados");
        jButtonParameters.setNextFocusableComponent(jButtonExit);
        jButtonParameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParametersActionPerformed(evt);
            }
        });

        jButtonMethodElaboration.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonMethodElaboration.setText("Método");
        jButtonMethodElaboration.setToolTipText("Método de elaboração do Dicionário de estados");
        jButtonMethodElaboration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMethodElaborationActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanelSouthLayout = new javax.swing.GroupLayout(jPanelSouth);
        jPanelSouth.setLayout(jPanelSouthLayout);
        jPanelSouthLayout.setHorizontalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSouthLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonMethodElaboration, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonParameters, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonGenerateTrigger, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelSouthLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonExit, jButtonGenerateTrigger});

        jPanelSouthLayout.setVerticalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSouthLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonGenerateTrigger, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonParameters, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonMethodElaboration, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelSouthLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonMethodElaboration, jButtonParameters});

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        setBounds(0, 0, 718, 447);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * BOTÃO - Exibir diálogo de Parametros
     *
     * @param evt
     */
    private void jButtonParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParametersActionPerformed
        graphEditor.parametersTriggersView(classElement);
        attributesLoad();
    }//GEN-LAST:event_jButtonParametersActionPerformed

    /**
     * BOTÃO - Sair
     *
     * @param evt
     */
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonExitActionPerformed

    /**
     * BOTÃO - Gerar gatilhos
     *
     * @param evt
     */
    private void jButtonGenerateTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateTriggerActionPerformed
        if (classModel.validateDictionaryOfStates()) {
            this.setVisible(false);
            classModel.generateTriggerCode();
            graphEditor.triggersCodeView(classElement);
        } else {
            JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDictionary"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
            graphEditor.dictionaryOfStatesValidationView(classElement);
        }
    }//GEN-LAST:event_jButtonGenerateTriggerActionPerformed

    /**
     * JCOMBOBOX - Seleção de Atributo Standart
     *
     * @param evt
     */
    private void jComboBoxAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxAttributesActionPerformed
        if (jComboBoxAttributes.getSelectedItem() != null) {
            // Atributo Selecionado
            if (jComboBoxAttributes.getSelectedIndex() != 0) {
                selectedAttribute = getAttribute(jComboBoxAttributes.getSelectedItem().toString());
                if (selectedAttribute != null) {
                    clearFiels();
                    enableFields(true);
                    operatorsLoad();
                    statesLoad();
                    expressionsLoad(selectedAttribute);
                    jLabelAttributeType.setText(selectedAttribute.getUmlType());
                }
            } else {
                // Atributo Não Selecionado
                clearFiels();
                enableFields(false);
                expressionsLoad(null);
            }
        }
    }//GEN-LAST:event_jComboBoxAttributesActionPerformed

    /**
     * JCOMBOBOX - Seleção de Estado - Standart
     *
     * @param evt
     */
    private void jComboBoxStatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStatesActionPerformed
        if (jComboBoxStates.getSelectedItem() != null) {
            if (jComboBoxStates.getSelectedIndex() > 0) {
                selectedState = getState(jComboBoxStates.getSelectedItem().toString());
            } else {
                selectedState = null;
            }
        } else {
            selectedState = null;
        }
    }//GEN-LAST:event_jComboBoxStatesActionPerformed

    /**
     * JCOMBOBOX - Seleção de Operador - Standart
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
     * BOTÃO - Adicionar standart expressão - Standart
     *
     * @param evt
     */
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed

        if (editing) {
            // EXPRESSÃO EM EDIÇÃO
            newStandartExpression = new StandartExpression();
            if (validateExpressionEditing()) {
                newStandartExpression.setId(previousStandartExpression.getId());
                newStandartExpression.setAttribute(selectedAttribute);
                newStandartExpression.setOperator(selectedOperator);
                newStandartExpression.setValue(jTextFieldValue.getText());

                if (previousStandartExpression.getState().equals(newStandartExpression.getState())) {
                    // PERMANECE O ESTADO
                    // VERIFICA SE A EDIÇÃO DA EXPRESSÃO É PERMITIDA
                    if (newStandartExpression.getState().validateEditionExpression(graphEditor, previousStandartExpression, newStandartExpression)) {
                        editingStandartExpression.setAttribute(newStandartExpression.getAttribute());
                        editingStandartExpression.setOperator(newStandartExpression.getOperator());
                        editingStandartExpression.setValue(newStandartExpression.getValue());
                        editingStandartExpression.setState(newStandartExpression.getState());
                        expressionsLoad(newStandartExpression.getAttribute());
                        standartDataFields();
                        editing = false;
                    } else {
//                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidEdition"),
//                                mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // ALTEROU O ESTADO
                    // VERIFICA SE A EDIÇÃO DA EXPRESSÃO É PERMITIDA NO NOVO ESTADO
                    if (newStandartExpression.getState().validateNewExpression(graphEditor, newStandartExpression)) {
                        // REMOVE A EXPRESSÃO NO ESTADO E ATRIBUTO
                        if (previousStandartExpression.getState().removeExpression(previousStandartExpression)
                                && previousStandartExpression.getAttribute().removeExpression(previousStandartExpression)) {
                            // ADICIONA A EXPRESSÃO NO ESTADO E ATRIBUTO
                            if (newStandartExpression.getState().addExpression(newStandartExpression)
                                    && newStandartExpression.getAttribute().addExpression(newStandartExpression)) {
                                standartDataFields();
                                expressionsLoad(newStandartExpression.getAttribute());
                                editing = false;
                            } else {
                                JOptionPane.showMessageDialog(graphEditor, mxResources.get("errorToAddExpression"),
                                        mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("errorRemovingExpression"),
                                    mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
//                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("errorToAddExpression"),
//                                mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } else {
            // EXPRESSÃO NOVA
            newStandartExpression = new StandartExpression();
            if (validateExpressionEditing()) {
                // CARREGA DADOS NA EXPRESSÃO
                newStandartExpression.setId();
                newStandartExpression.setAttribute(selectedAttribute);
                newStandartExpression.setOperator(selectedOperator);
                newStandartExpression.setValue(jTextFieldValue.getText());
                newStandartExpression.setState(selectedState);

                // VALIDA A EXPRESSÃO NO ESTADO
                if (newStandartExpression.getState().validateNewExpression(graphEditor, newStandartExpression)) {
                    boolean stateInsert, attributeInsert;
                    //editingExpression.setId();

                    // ADICIONA A EXPRESSÃO AO ESTADO
                    stateInsert = newStandartExpression.getState().addExpression(newStandartExpression);

                    // ADICIONA A EXPRESSÃO AO ATRIBUTO
                    attributeInsert = newStandartExpression.getAttribute().addExpression(newStandartExpression);

                    // VERIFICA SE A EXPRESSÃO FOI INSERIDA NO ESTADO E ATRIBUTO
                    if (stateInsert && attributeInsert) {
                        standartDataFields();
                        expressionsLoad(newStandartExpression.getAttribute());
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("errorToAddExpression"),
                                mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                } else {
//                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("errorToAddExpression"),
//                            mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    /**
     * VALIDAÇÃO - Valida entrada do campo valor - Standart
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

    /**
     * BOTÃO - Editar standart expressão - Standart
     *
     * @param evt
     */
    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed

        if (selectedStandartExpression != null) {
            jComboBoxAttributes.setSelectedItem(selectedStandartExpression.getAttribute().getName());
            jComboBoxOperators.setSelectedItem(selectedStandartExpression.getOperator().getStandartNotation());
            jTextFieldValue.setText(selectedStandartExpression.getValue());
            jComboBoxStates.setSelectedItem(selectedStandartExpression.getState().getName());
            jButtonAdd.setText("Salvar");
            enableFields(true);
            editing = true;
            editingStandartExpression = selectedStandartExpression;
            previousStandartExpression = new StandartExpression(selectedStandartExpression.getId(), selectedStandartExpression.getAttribute(), selectedStandartExpression.getOperator(), selectedStandartExpression.getValue(), selectedStandartExpression.getState());
        } else {
            JOptionPane.showMessageDialog(graphEditor, mxResources.get("expressionNull"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonEditActionPerformed

    /**
     * BOTÃO - Deletar standart expressão - Standart
     *
     * @param evt
     */
    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed

        if (selectedStandartExpression != null) {
            int option = JOptionPane.showConfirmDialog(graphEditor, "Excluir o Elemento?", "Exclusão",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (option == 0) {
                boolean resultState, resultAtributo;

                // REMOVE A EXPRESSÃO NO ESTADO
                resultState = selectedStandartExpression.getState().removeExpression(selectedStandartExpression);

                // REMOVE A EXPRESSÃO NO ATRIBUTO
                resultAtributo = selectedStandartExpression.getAttribute().removeExpression(selectedStandartExpression);

                if (resultState && resultAtributo) {
                    standartDataFields();
                    expressionsLoad(selectedStandartExpression.getAttribute());
                    selectedStandartExpression = null;
                } else {
                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("errorRemovingExpression"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(graphEditor, mxResources.get("expressionNull"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    /**
     * BOTÃO - Limpar os campos de edição de expressão - Standart
     *
     * @param evt
     */
    private void jButtonCleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCleanActionPerformed
        if (selectedAttribute != null) {
            newStandartExpression = new StandartExpression();
            jComboBoxOperators.setSelectedIndex(0);
            jTextFieldValue.setText("");
            jComboBoxStates.setSelectedIndex(0);
            jButtonAdd.setText("Adicionar");
            editing = false;
        }
    }//GEN-LAST:event_jButtonCleanActionPerformed

    /**
     * CLIQUE DO MOUSE - Standart espressões
     *
     * @param evt
     */
    private void jTableExpressionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableExpressionsMouseClicked

        if (rowClickStandartExpressions == null) {
            // PRIMEIRO CLIQUE
            rowClickStandartExpressions = jTableExpressions.getSelectedRow();
            selectedStandartExpression = listStandartExpressions.get(rowClickStandartExpressions);
        } else {
            // SEGUNDO CLIQUE
            if (rowClickStandartExpressions == jTableExpressions.getSelectedRow()) {
                // CLIQUE IGUAL
                rowClickStandartExpressions = null;
                jTableExpressions.clearSelection();
                selectedStandartExpression = null;
            } else {
                //CLIQUE DIFERENTE
                rowClickStandartExpressions = jTableExpressions.getSelectedRow();
                selectedStandartExpression = listStandartExpressions.get(rowClickStandartExpressions);
            }
        }
    }//GEN-LAST:event_jTableExpressionsMouseClicked

    /**
     * BOTÃO - Permite alterar método de elaboração do dicionário
     *
     * @param evt
     */
    private void jButtonMethodElaborationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMethodElaborationActionPerformed
        this.enableMethodElaboration();
        if (classModel.isMethodElaborationCustom()) {
            this.setVisible(false);
            graphEditor.dictionaryOfStatesMethodCustomView(classElement);
        } else {
            // Permanece no Método atual
        }
    }//GEN-LAST:event_jButtonMethodElaborationActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonClean;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonGenerateTrigger;
    private javax.swing.JButton jButtonMethodElaboration;
    private javax.swing.JButton jButtonParameters;
    private javax.swing.JComboBox jComboBoxAttributes;
    private javax.swing.JComboBox jComboBoxOperators;
    private javax.swing.JComboBox jComboBoxStates;
    private javax.swing.JLabel jLabelAttribute;
    private javax.swing.JLabel jLabelAttributeType;
    private javax.swing.JLabel jLabelOperator;
    private javax.swing.JLabel jLabelOperatorInformation;
    private javax.swing.JLabel jLabelState;
    private javax.swing.JLabel jLabelStateInformation;
    private javax.swing.JLabel jLabelValue;
    private javax.swing.JLabel jLabelValueInformation;
    private javax.swing.JPanel jPanelBuilder;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelView;
    private javax.swing.JScrollPane jScrollPaneExpressions;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableExpressions;
    private javax.swing.JTextField jTextFieldValue;
    private java.util.List listElements;
    private java.util.List<br.com.upf.beans.StandartExpression> listStandartExpressions;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
