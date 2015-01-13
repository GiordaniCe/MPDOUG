/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view.dialog;

import br.com.upf.beans.BasicExpression;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.CloseParenthesis;
import br.com.upf.beans.CustomExpression;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.beans.ElementExpression;
import br.com.upf.beans.OpenParenthesis;
import br.com.upf.beans.OperatorAnd;
import br.com.upf.beans.OperatorNot;
import br.com.upf.beans.OperatorOr;
import br.com.upf.beans.State;
import br.com.upf.view.GraphEditor;
import br.com.upf.view.dialog.validation.DictionaryOfStatesValidation;
import com.mxgraph.util.mxResources;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author GiordaniAntonio
 */
public class DictionaryOfStatesMethodCustom extends javax.swing.JDialog {

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
    private Icon yesIcon = new ImageIcon(DictionaryOfStatesMethodCustom.class.getResource(yesUrlIcon));

    /**
     *
     */
    private Icon noIcon = new ImageIcon(DictionaryOfStatesMethodCustom.class.getResource(noUrlIcon));

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
    private boolean editing;

    /**
     *
     */
    private Integer editingIndex;

    /**
     *
     */
    private ElementExpression previousEditingElementExpression;

    /**
     * Elemento em edição
     */
    private ElementExpression editingElementExpression;

    /**
     * Expressão Selecionada na Lista
     */
    private ElementExpression selectedElementExpression;

    /**
     * Expressão (CustomExpression) em edição
     */
    private CustomExpression editingCustomExpression;

    /**
     * Estado selecionado
     */
    private State selectedState;

    /**
     *
     */
    private Integer rowClickElements = -1;

    /**
     *
     */
    private Integer rowClickCustomExpressions = -1;

    /**
     * Expressão (CustomExpression) selecionada na tabela
     */
    private CustomExpression selectedCustomExpression;

    /**
     * BasicExpression em edição
     */
    private BasicExpression editingBasicExpression;

    /**
     *
     */
    private BasicExpressionEdition basicExpressionEdition;

    /**
     *
     */
    private MethodElaborationDictionary methodElaborationDictionary;

    /**
     *
     */
    private DictionaryOfStatesValidation dictionaryOfStatesValidation;

    /**
     * Construtor
     *
     * @param editor
     * @param element
     */
    public DictionaryOfStatesMethodCustom(GraphEditor editor, ClassElement element) {
        super(editor, true);
        this.graphEditor = editor;
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.diagramOfStateModel = classElement.getStateEditor().getDiagramOfStateModel();
        this.states = diagramOfStateModel.getStates();
        initComponents();
        setDimensionColumnJTable();
        setAlignColumnJTable();

        if (!classModel.isInitializedDictionaryOfStates()) {
            classModel.setInitializedDictionaryOfStates(true);
            graphEditor.getMenuBarCustom().updateContextStateEditor(element);
        }
        contextualizeBuilder();
        updateTitle();
    }

    /**
     * Atualiza O titulo do Dialogo
     *
     * @param title
     */
    private void updateTitle() {
        this.setTitle("Dicionário de Estados - Método Avançado");
    }

    /**
     * Atualiza validação de sintase da Expressão selecionada
     */
    private void updateValidationSyntaxCustomExpressionSelected() {
        if (selectedState.getCustomExpression().validateSyntax()) {
            jButtonValidationSyntax.setIcon(yesIcon);
            jButtonValidationSyntax.setText("Sintaxe Válida!");
            jButtonValidationSyntax.setVisible(true);
        } else {
            jButtonValidationSyntax.setIcon(noIcon);
            jButtonValidationSyntax.setText("Sintaxe Inválida!");
            jButtonValidationSyntax.setVisible(true);
        }
    }

    /**
     * Atualiza validação Expressão selecionada
     */
    private void updateValidationCustomExpressionSelected() {
        if (selectedState.getCustomExpression().validateExpression(classModel.getMonitorableAttributes())) {
            jButtonValidationExpression.setIcon(yesIcon);
            jButtonValidationExpression.setText("Expressão Válida!");
            jButtonValidationExpression.setVisible(true);
        } else {
            jButtonValidationExpression.setIcon(noIcon);
            jButtonValidationExpression.setText("Expressão Inválida!");
            jButtonValidationExpression.setVisible(true);
        }
    }

    /**
     * Contextualiza o Construtor Personalizado
     */
    private void contextualizeBuilder() {
        statesLoad();
        customExpressionsLoad();
        editing = false;
        editingIndex = -1;
    }

    /**
     * Configuração - Define o alinhamento do cabeçalho e conteudo das células
     * da tabela
     */
    private void setAlignColumnJTable() {
        DefaultTableCellRenderer rendererCenter = new DefaultTableCellRenderer();
        rendererCenter.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumn columnState, columnExpressions;

        if (jTableCustomExpressions.getColumnCount() == 2) {
            columnState = jTableCustomExpressions.getColumnModel().getColumn(0);
            columnState.setCellRenderer(rendererCenter);
            columnState.setHeaderRenderer(rendererCenter);
            columnExpressions = jTableCustomExpressions.getColumnModel().getColumn(1);
            columnExpressions.setHeaderRenderer(rendererCenter);
        }
    }

    /**
     * Configuração - Define a dimensão das colunas da tabela da tabela
     */
    private void setDimensionColumnJTable() {
        DefaultTableCellRenderer rendererCenter = new DefaultTableCellRenderer();
        rendererCenter.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumn columnState, columnExpression;

        columnState = jTableCustomExpressions.getColumnModel().getColumn(0);
        columnState.setMaxWidth(250);
        columnState.setMinWidth(150);
        columnExpression = jTableCustomExpressions.getColumnModel().getColumn(1);

    }

    /**
     * Controle de exibição do construtor de Expressões Customizadas
     *
     * @param value
     */
    private void builderCustomExpressionView(boolean value) {
        CardLayout buildPanel = (CardLayout) jPanelBuilderExpressions.getLayout();

        if (value) {
            buildPanel.show(jPanelBuilderExpressions, "card1");
        } else {
            buildPanel.show(jPanelBuilderExpressions, "card0");
        }
    }

    /**
     * Carrega os estados
     */
    private void statesLoad() {
        jComboBoxStates.removeAllItems();
        jComboBoxStates.addItem("Selecione o estado");
        for (int i = 0; i < states.size(); i++) {
            jComboBoxStates.addItem(states.get(i).getName());
        }
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
     * Adiciona o elemento
     *
     * @return
     */
    private boolean addElementExpression() {
        boolean result;
        // Edição Elemento
        if (editing) {
            result = editingCustomExpression.addElement(editingElementExpression, editingIndex);
            if (result) {
                updateView();
                jComboBoxElements.setSelectedIndex(0);
                jComboBoxElements.requestFocus();
                jButtonAddElement.setText("Adicionar");
                editing = false;
                editingIndex = -1;
                previousEditingElementExpression = null;
                return true;
            } else {
                return false;
            }
        } else {
            // Novo Elemento
            result = editingCustomExpression.addElement(editingElementExpression);
            if (result) {
                updateView();
                jComboBoxElements.setSelectedIndex(0);
                jComboBoxElements.requestFocus();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Atualiza a lista de elementos, a visualização do expressão em edição e a
     * lista de customExpressions e a validação da expressão
     */
    private void updateView() {
        elementsLoad();
        updateCustomExpressionView();
        customExpressionsLoad();
        updateValidationSyntaxCustomExpressionSelected();
        updateValidationCustomExpressionSelected();
    }

    /**
     * Carrega os elementos
     *
     * @param attribute
     * @return
     */
    private boolean elementsLoad() {
        listElements.clear();
        return listElements.addAll(editingCustomExpression.getElements());
    }

    /**
     * Carrega as expressões (CustomExpression)
     *
     * @param attribute
     * @return
     */
    private void customExpressionsLoad() {
        listCustomExpressions.clear();
        for (int i = 0; i < states.size(); i++) {
            listCustomExpressions.add(states.get(i).getCustomExpression());
        }
    }

    /**
     * Atualiza a apresentação da expressão personalizada
     */
    private void updateCustomExpressionView() {
        jTextAreaCustomExpressionView.setText(editingCustomExpression.getApresentation());
    }

    /**
     * Gerencia Exibição da edição de expressões (BasicExpression)
     *
     * @param basicExpression
     * @param editing
     */
    private void basicExpressionEditionView(BasicExpression basicExpression, boolean editing) {
        basicExpressionEdition = new BasicExpressionEdition(graphEditor, basicExpression, editing, classElement);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - basicExpressionEdition.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - basicExpressionEdition.getHeight()) / 2;

        basicExpressionEdition.setLocation(x, y);
        basicExpressionEdition.setVisible(true);
    }

    /**
     * Exibe Dialogo para Alteração do Método de Elaboração do Dicionário de
     * estados
     *
     */
    public void methodElaborationView() {
        methodElaborationDictionary = new MethodElaborationDictionary(graphEditor, classElement);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - methodElaborationDictionary.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - methodElaborationDictionary.getHeight()) / 2;

        methodElaborationDictionary.setLocation(x, y);
        methodElaborationDictionary.setVisible(true);
    }

    /**
     * Exibe diálogo para válidação do Dicionário de estados
     */
    public void dictionaryOfStatesValidationView() {
        dictionaryOfStatesValidation = new DictionaryOfStatesValidation(graphEditor, classElement);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - dictionaryOfStatesValidation.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - dictionaryOfStatesValidation.getHeight()) / 2;

        dictionaryOfStatesValidation.setLocation(x, y);
        dictionaryOfStatesValidation.setVisible(true);
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

        listCustomExpressions = new LinkedList<CustomExpression>();
        listElements = new LinkedList<ElementExpression>();
        jPanelCenter = new javax.swing.JPanel();
        jPanelBuilder = new javax.swing.JPanel();
        jPanelBuilderSelectionState = new javax.swing.JPanel();
        jComboBoxStates = new javax.swing.JComboBox();
        jButtonValidationSyntax = new javax.swing.JButton();
        jButtonValidationExpression = new javax.swing.JButton();
        jPanelBuilderExpressions = new javax.swing.JPanel();
        jPanelBuilderExpressionsOff = new javax.swing.JPanel();
        jLabelSelectState = new javax.swing.JLabel();
        jPanelBuilderExpressionsOn = new javax.swing.JPanel();
        jPanelBuilderElementsControl = new javax.swing.JPanel();
        jComboBoxElements = new javax.swing.JComboBox();
        jButtonAddElement = new javax.swing.JButton();
        jButtonEditElement = new javax.swing.JButton();
        jButtonDeleteElement = new javax.swing.JButton();
        jButtonMoveDownElement = new javax.swing.JButton();
        jButtonMoveUpElement = new javax.swing.JButton();
        jPanelBuilderElementsView = new javax.swing.JPanel();
        jScrollPaneElements = new javax.swing.JScrollPane();
        jListElements = new javax.swing.JList();
        jPanelBuilderCustomEspressionView = new javax.swing.JPanel();
        jScrollPaneCustomExpressionView = new javax.swing.JScrollPane();
        jTextAreaCustomExpressionView = new javax.swing.JTextArea();
        jPanelView = new javax.swing.JPanel();
        jScrollPaneCustomExpressions = new javax.swing.JScrollPane();
        jTableCustomExpressions = new javax.swing.JTable();
        jPanelSouth = new javax.swing.JPanel();
        jButtonGenerateTrigger = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jSeparatorSouth = new javax.swing.JSeparator();
        jButtonParameters = new javax.swing.JButton();
        jButtonMethodElaboration = new javax.swing.JButton();

        listCustomExpressions = org.jdesktop.observablecollections.ObservableCollections.observableList(listCustomExpressions);

        listElements = org.jdesktop.observablecollections.ObservableCollections.observableList(listElements);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanelBuilder.setPreferredSize(new java.awt.Dimension(638, 352));
        jPanelBuilder.setRequestFocusEnabled(false);
        jPanelBuilder.setLayout(new java.awt.BorderLayout());

        jPanelBuilderSelectionState.setPreferredSize(new java.awt.Dimension(800, 60));

        jComboBoxStates.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBoxStates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStatesActionPerformed(evt);
            }
        });

        jButtonValidationSyntax.setToolTipText("Validação da Sintaxe");
        jButtonValidationSyntax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonValidationSyntaxActionPerformed(evt);
            }
        });

        jButtonValidationExpression.setToolTipText("Validação dos Atributos");
        jButtonValidationExpression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonValidationExpressionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBuilderSelectionStateLayout = new javax.swing.GroupLayout(jPanelBuilderSelectionState);
        jPanelBuilderSelectionState.setLayout(jPanelBuilderSelectionStateLayout);
        jPanelBuilderSelectionStateLayout.setHorizontalGroup(
            jPanelBuilderSelectionStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderSelectionStateLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jComboBoxStates, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButtonValidationSyntax, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonValidationExpression, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        jPanelBuilderSelectionStateLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonValidationExpression, jButtonValidationSyntax});

        jPanelBuilderSelectionStateLayout.setVerticalGroup(
            jPanelBuilderSelectionStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderSelectionStateLayout.createSequentialGroup()
                .addGroup(jPanelBuilderSelectionStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonValidationSyntax, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelBuilderSelectionStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelBuilderSelectionStateLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jButtonValidationExpression, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanelBuilderSelectionStateLayout.createSequentialGroup()
                            .addGap(13, 13, 13)
                            .addComponent(jComboBoxStates, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelBuilderSelectionStateLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonValidationExpression, jButtonValidationSyntax, jComboBoxStates});

        jPanelBuilder.add(jPanelBuilderSelectionState, java.awt.BorderLayout.NORTH);

        jPanelBuilderExpressions.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelBuilderExpressions.setLayout(new java.awt.CardLayout());

        jPanelBuilderExpressionsOff.setLayout(new java.awt.BorderLayout());

        jLabelSelectState.setForeground(new java.awt.Color(153, 153, 153));
        jLabelSelectState.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectState.setText("<Selecione um estado>");
        jPanelBuilderExpressionsOff.add(jLabelSelectState, java.awt.BorderLayout.CENTER);

        jPanelBuilderExpressions.add(jPanelBuilderExpressionsOff, "card0");

        jPanelBuilderExpressionsOn.setLayout(new java.awt.BorderLayout());

        jPanelBuilderElementsControl.setPreferredSize(new java.awt.Dimension(230, 180));

        jComboBoxElements.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Selecione o Elemento", "Expressão", "AND", "OR", "NOT", "(", ")" }));
        jComboBoxElements.setNextFocusableComponent(jButtonAddElement);
        jComboBoxElements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxElementsActionPerformed(evt);
            }
        });

        jButtonAddElement.setText("Adicionar");
        jButtonAddElement.setNextFocusableComponent(jComboBoxElements);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jComboBoxElements, org.jdesktop.beansbinding.ELProperty.create("${selectedItem!=null}"), jButtonAddElement, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddElementActionPerformed(evt);
            }
        });

        jButtonEditElement.setText("Editar");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListElements, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonEditElement, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonEditElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditElementActionPerformed(evt);
            }
        });

        jButtonDeleteElement.setText("Excluir");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListElements, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonDeleteElement, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonDeleteElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteElementActionPerformed(evt);
            }
        });

        jButtonMoveDownElement.setText("Mover para Baixo");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListElements, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonMoveDownElement, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonMoveDownElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveDownElementActionPerformed(evt);
            }
        });

        jButtonMoveUpElement.setText("Mover para Cima");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListElements, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), jButtonMoveUpElement, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButtonMoveUpElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveUpElementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBuilderElementsControlLayout = new javax.swing.GroupLayout(jPanelBuilderElementsControl);
        jPanelBuilderElementsControl.setLayout(jPanelBuilderElementsControlLayout);
        jPanelBuilderElementsControlLayout.setHorizontalGroup(
            jPanelBuilderElementsControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderElementsControlLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanelBuilderElementsControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonMoveUpElement, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jButtonMoveDownElement, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDeleteElement, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jButtonEditElement, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddElement, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxElements, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelBuilderElementsControlLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAddElement, jButtonDeleteElement, jButtonEditElement, jButtonMoveDownElement, jButtonMoveUpElement, jComboBoxElements});

        jPanelBuilderElementsControlLayout.setVerticalGroup(
            jPanelBuilderElementsControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderElementsControlLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jComboBoxElements, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDeleteElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonMoveUpElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMoveDownElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelBuilderExpressionsOn.add(jPanelBuilderElementsControl, java.awt.BorderLayout.WEST);

        jPanelBuilderElementsView.setPreferredSize(new java.awt.Dimension(350, 208));

        jScrollPaneElements.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jListElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListElements.setVerifyInputWhenFocusTarget(false);

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listElements, jListElements);
        bindingGroup.addBinding(jListBinding);

        jListElements.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListElementsMouseClicked(evt);
            }
        });
        jScrollPaneElements.setViewportView(jListElements);

        javax.swing.GroupLayout jPanelBuilderElementsViewLayout = new javax.swing.GroupLayout(jPanelBuilderElementsView);
        jPanelBuilderElementsView.setLayout(jPanelBuilderElementsViewLayout);
        jPanelBuilderElementsViewLayout.setHorizontalGroup(
            jPanelBuilderElementsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderElementsViewLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPaneElements, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
        jPanelBuilderElementsViewLayout.setVerticalGroup(
            jPanelBuilderElementsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderElementsViewLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPaneElements, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelBuilderExpressionsOn.add(jPanelBuilderElementsView, java.awt.BorderLayout.CENTER);

        jPanelBuilderCustomEspressionView.setPreferredSize(new java.awt.Dimension(650, 80));

        jScrollPaneCustomExpressionView.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneCustomExpressionView.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextAreaCustomExpressionView.setEditable(false);
        jTextAreaCustomExpressionView.setColumns(20);
        jTextAreaCustomExpressionView.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jTextAreaCustomExpressionView.setRows(5);
        jScrollPaneCustomExpressionView.setViewportView(jTextAreaCustomExpressionView);

        javax.swing.GroupLayout jPanelBuilderCustomEspressionViewLayout = new javax.swing.GroupLayout(jPanelBuilderCustomEspressionView);
        jPanelBuilderCustomEspressionView.setLayout(jPanelBuilderCustomEspressionViewLayout);
        jPanelBuilderCustomEspressionViewLayout.setHorizontalGroup(
            jPanelBuilderCustomEspressionViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBuilderCustomEspressionViewLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPaneCustomExpressionView, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelBuilderCustomEspressionViewLayout.setVerticalGroup(
            jPanelBuilderCustomEspressionViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBuilderCustomEspressionViewLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPaneCustomExpressionView, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jPanelBuilderExpressionsOn.add(jPanelBuilderCustomEspressionView, java.awt.BorderLayout.SOUTH);

        jPanelBuilderExpressions.add(jPanelBuilderExpressionsOn, "card1");

        jPanelBuilder.add(jPanelBuilderExpressions, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanelBuilder, java.awt.BorderLayout.NORTH);

        jPanelView.setPreferredSize(new java.awt.Dimension(638, 195));
        jPanelView.setLayout(new java.awt.BorderLayout());

        jTableCustomExpressions.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listCustomExpressions, jTableCustomExpressions);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${state.name}"));
        columnBinding.setColumnName("Estado");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${apresentation}"));
        columnBinding.setColumnName("Expressão");
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jTableCustomExpressions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCustomExpressionsMouseClicked(evt);
            }
        });
        jScrollPaneCustomExpressions.setViewportView(jTableCustomExpressions);
        if (jTableCustomExpressions.getColumnModel().getColumnCount() > 0) {
            jTableCustomExpressions.getColumnModel().getColumn(1).setResizable(false);
        }

        jPanelView.add(jScrollPaneCustomExpressions, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanelView, java.awt.BorderLayout.CENTER);

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

        jSeparatorSouth.setOrientation(javax.swing.SwingConstants.VERTICAL);

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

        javax.swing.GroupLayout jPanelSouthLayout = new javax.swing.GroupLayout(jPanelSouth);
        jPanelSouth.setLayout(jPanelSouthLayout);
        jPanelSouthLayout.setHorizontalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSouthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonMethodElaboration)
                .addGap(18, 18, 18)
                .addComponent(jButtonParameters)
                .addGap(18, 18, 18)
                .addComponent(jSeparatorSouth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jButtonGenerateTrigger, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelSouthLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonExit, jButtonGenerateTrigger, jButtonMethodElaboration, jButtonParameters});

        jPanelSouthLayout.setVerticalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSouthLayout.createSequentialGroup()
                .addGroup(jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelSouthLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparatorSouth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelSouthLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonGenerateTrigger, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonParameters)
                            .addComponent(jButtonMethodElaboration))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelSouthLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonExit, jButtonGenerateTrigger, jButtonMethodElaboration, jButtonParameters});

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        setBounds(0, 0, 718, 647);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Ação do Botão - Parâmetros
     *
     * @param evt
     */
    private void jButtonParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParametersActionPerformed
        graphEditor.parametersTriggersView(classElement);
    }//GEN-LAST:event_jButtonParametersActionPerformed

    /**
     * Ação do Botão - Sair
     *
     * @param evt
     */
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonExitActionPerformed

    /**
     * Ação do Botão - Gerar gatilhos
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
            dictionaryOfStatesValidationView();
        }
    }//GEN-LAST:event_jButtonGenerateTriggerActionPerformed

    /**
     * Ação de Botão - Alterar Método de elaboração
     *
     * @param evt
     */
    private void jButtonMethodElaborationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMethodElaborationActionPerformed
        this.methodElaborationView();
        if (classModel.isMethodElaborationCustom()) {
            // Permanece no Método atual
        } else {
            this.setVisible(false);
            graphEditor.dictionaryOfStatesMethodStandartView(classElement);
        }
    }//GEN-LAST:event_jButtonMethodElaborationActionPerformed

    /**
     * Gerencia cliques de mouse na tabela de expressões
     *
     * @param evt
     */
    private void jTableCustomExpressionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCustomExpressionsMouseClicked
        if (rowClickCustomExpressions == null) {
            // PRIMEIRO CLIQUE
            rowClickCustomExpressions = jTableCustomExpressions.getSelectedRow();
        } else {
            // SEGUNDO CLIQUE
            // SEGUNDO CLIQUE
            if (rowClickCustomExpressions == jTableCustomExpressions.getSelectedRow()) {
                // CLIQUE IGUAL
                jTableCustomExpressions.clearSelection();
                rowClickCustomExpressions = jTableCustomExpressions.getSelectedRow();
                selectedCustomExpression = null;
            } else {
                //CLIQUE DIFERENTE
                rowClickCustomExpressions = jTableCustomExpressions.getSelectedRow();
                selectedCustomExpression = listCustomExpressions.get(rowClickCustomExpressions);
            }
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jTableCustomExpressionsMouseClicked

    /**
     * Ação do jComboBox - Estado Selecionado
     *
     * @param evt
     */
    private void jComboBoxStatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStatesActionPerformed
        if (jComboBoxStates.getSelectedItem() != null) {
            if (jComboBoxStates.getSelectedIndex() != 0) {
                selectedState = getState(jComboBoxStates.getSelectedItem().toString());
                if (selectedState != null) {
                    builderCustomExpressionView(true);
                    editingCustomExpression = selectedState.getCustomExpression();
                    updateView();
                }
            } else {
                jButtonValidationSyntax.setVisible(false);
                jButtonValidationExpression.setVisible(false);
                builderCustomExpressionView(false);
            }
        }
    }//GEN-LAST:event_jComboBoxStatesActionPerformed

    /**
     * Ação do Botão - Editar Elemento
     *
     * @param evt
     */
    private void jButtonEditElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditElementActionPerformed
        if ((rowClickElements != -1) && (rowClickElements < listElements.size())) {
            editing = true;
            editingIndex = rowClickElements;
            previousEditingElementExpression = (ElementExpression) listElements.get(rowClickElements);
            jButtonAddElement.setText("Salvar");
            // Seta Para o tipo de elemento atual
            if (previousEditingElementExpression instanceof BasicExpression) {
                jComboBoxElements.setSelectedIndex(1);
            }
            if (previousEditingElementExpression instanceof OperatorAnd) {
                jComboBoxElements.setSelectedIndex(2);
            }
            if (previousEditingElementExpression instanceof OperatorOr) {
                jComboBoxElements.setSelectedIndex(3);
            }
            if (previousEditingElementExpression instanceof OperatorNot) {
                jComboBoxElements.setSelectedIndex(4);
            }
            if (previousEditingElementExpression instanceof OpenParenthesis) {
                jComboBoxElements.setSelectedIndex(5);
            }
            if (previousEditingElementExpression instanceof CloseParenthesis) {
                jComboBoxElements.setSelectedIndex(6);
            }
            jListElements.clearSelection();
            rowClickElements = -1;
        }
    }//GEN-LAST:event_jButtonEditElementActionPerformed

    /**
     * Ação do Botão - Mover para Cima elemento
     *
     * @param evt
     */
    private void jButtonMoveUpElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveUpElementActionPerformed
        int newSelectionIndex;
        if (rowClickElements != -1) {
            int first = 0;
            if (rowClickElements > first) {
                newSelectionIndex = rowClickElements - 1;
                Collections.swap(editingCustomExpression.getElements(), rowClickElements, rowClickElements - 1);
                updateView();
                jListElements.setSelectedIndex(newSelectionIndex);
                rowClickElements = newSelectionIndex;
            }
        }
    }//GEN-LAST:event_jButtonMoveUpElementActionPerformed

    /**
     * Gerencia cliques de mouse na lista de elementos
     *
     * @param evt
     */
    private void jListElementsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListElementsMouseClicked
        if (evt.getButton() == 1) {
            if (rowClickElements != -1) {
                // Limpa Seleção    
                if (rowClickElements == jListElements.getSelectedIndex()) {
                    jListElements.clearSelection();
                    rowClickElements = jListElements.getSelectedIndex();
                } else {
                    // Seleciona novo
                    rowClickElements = jListElements.getSelectedIndex();
                }
            } else {
                rowClickElements = jListElements.getSelectedIndex();
            }

            // Limpa a seleção
            if (rowClickElements == -1) {
                jListElements.clearSelection();
            }
        }

    }//GEN-LAST:event_jListElementsMouseClicked

    /**
     * Ação do JComboBox - Elementos
     *
     * @param evt
     */
    private void jComboBoxElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxElementsActionPerformed
        Integer selectedIndexElement = jComboBoxElements.getSelectedIndex();

        switch (selectedIndexElement) {
            case 1:
                editingElementExpression = new BasicExpression();
                jButtonAddElement.setEnabled(true);
                break;
            case 2:
                editingElementExpression = new OperatorAnd();
                jButtonAddElement.setEnabled(true);
                break;
            case 3:
                editingElementExpression = new OperatorOr();
                jButtonAddElement.setEnabled(true);
                break;
            case 4:
                editingElementExpression = new OperatorNot();
                jButtonAddElement.setEnabled(true);
                break;
            case 5:
                editingElementExpression = new OpenParenthesis();
                jButtonAddElement.setEnabled(true);
                break;
            case 6:
                editingElementExpression = new CloseParenthesis();
                jButtonAddElement.setEnabled(true);
                break;

            default:
                editingElementExpression = null;

        }

    }//GEN-LAST:event_jComboBoxElementsActionPerformed

    /**
     * Ação do Botão - Adicionar e ou salvar - Elemento
     *
     * @param evt
     */
    private void jButtonAddElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddElementActionPerformed
        // Verificação 
        if (jComboBoxElements.getSelectedItem() != null) {
            // Verifica a seleção
            if (jComboBoxElements.getSelectedIndex() > 0) {
                if (editingElementExpression != null) {
                    if (editingElementExpression instanceof BasicExpression) {
                        // Elemento Anterior BasicExpression
                        if (previousEditingElementExpression instanceof BasicExpression) {
                            editingBasicExpression = (BasicExpression) previousEditingElementExpression;
                            basicExpressionEditionView(editingBasicExpression, true);
                            editingElementExpression = editingBasicExpression;
                        } else {
                            editingBasicExpression = (BasicExpression) editingElementExpression;
                            basicExpressionEditionView(editingBasicExpression, false);
                        }
                        if (editingBasicExpression.getAttribute() != null && editingBasicExpression.getOperator() != null && editingBasicExpression.getValue() != null) {
                            addElementExpression();
                        } else {
                            System.out.println("Cancelou!");
                            JOptionPane.showMessageDialog(null,"Cancelou!");
                        }
                    } else {
                        addElementExpression();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Selecione um elemento!");
                jComboBoxElements.requestFocus();
            }
        }

    }//GEN-LAST:event_jButtonAddElementActionPerformed

    /**
     * Ação do Botão - Mover para Baixo elemento
     *
     * @param evt
     */
    private void jButtonMoveDownElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveDownElementActionPerformed
        int newSelectionIndex;
        if (rowClickElements != -1) {
            int last = listElements.size() - 1;
            if (rowClickElements < last) {
                newSelectionIndex = rowClickElements + 1;
                Collections.swap(editingCustomExpression.getElements(), rowClickElements, rowClickElements + 1);
                updateView();
                jListElements.setSelectedIndex(newSelectionIndex);
                rowClickElements = newSelectionIndex;
            }
        }
    }//GEN-LAST:event_jButtonMoveDownElementActionPerformed

    /**
     * Ação do Botão - Deletar elemento
     *
     * @param evt
     */
    private void jButtonDeleteElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteElementActionPerformed
        if ((rowClickElements != -1) && (rowClickElements < listElements.size())) {
            int userOption = JOptionPane.showConfirmDialog(graphEditor, "Excluir o Elemento?", "Exclusão",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (userOption == 0) {
                editingCustomExpression.removeElement((ElementExpression) listElements.get(rowClickElements));
                rowClickElements = -1;
                updateView();
            }
        }
    }//GEN-LAST:event_jButtonDeleteElementActionPerformed

    /**
     *
     * @param evt
     */
    private void jButtonValidationSyntaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonValidationSyntaxActionPerformed

    }//GEN-LAST:event_jButtonValidationSyntaxActionPerformed

    /**
     *
     * @param evt
     */
    private void jButtonValidationExpressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonValidationExpressionActionPerformed

    }//GEN-LAST:event_jButtonValidationExpressionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddElement;
    private javax.swing.JButton jButtonDeleteElement;
    private javax.swing.JButton jButtonEditElement;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonGenerateTrigger;
    private javax.swing.JButton jButtonMethodElaboration;
    private javax.swing.JButton jButtonMoveDownElement;
    private javax.swing.JButton jButtonMoveUpElement;
    private javax.swing.JButton jButtonParameters;
    private javax.swing.JButton jButtonValidationExpression;
    private javax.swing.JButton jButtonValidationSyntax;
    private javax.swing.JComboBox jComboBoxElements;
    private javax.swing.JComboBox jComboBoxStates;
    private javax.swing.JLabel jLabelSelectState;
    private javax.swing.JList jListElements;
    private javax.swing.JPanel jPanelBuilder;
    private javax.swing.JPanel jPanelBuilderCustomEspressionView;
    private javax.swing.JPanel jPanelBuilderElementsControl;
    private javax.swing.JPanel jPanelBuilderElementsView;
    private javax.swing.JPanel jPanelBuilderExpressions;
    private javax.swing.JPanel jPanelBuilderExpressionsOff;
    private javax.swing.JPanel jPanelBuilderExpressionsOn;
    private javax.swing.JPanel jPanelBuilderSelectionState;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelView;
    private javax.swing.JScrollPane jScrollPaneCustomExpressionView;
    private javax.swing.JScrollPane jScrollPaneCustomExpressions;
    private javax.swing.JScrollPane jScrollPaneElements;
    private javax.swing.JSeparator jSeparatorSouth;
    private javax.swing.JTable jTableCustomExpressions;
    private javax.swing.JTextArea jTextAreaCustomExpressionView;
    private java.util.List<br.com.upf.beans.CustomExpression> listCustomExpressions;
    private java.util.List listElements;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
