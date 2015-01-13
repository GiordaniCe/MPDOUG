/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the graphEditor.
 */
package br.com.upf.view.internalframe;

import br.com.upf.beans.BasicState;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.beans.StandartExpression;
import br.com.upf.beans.FinalState;
import br.com.upf.beans.InitialState;
import br.com.upf.beans.State;
import br.com.upf.beans.Transition;
import br.com.upf.jgraph.custom.StateGraph;
import br.com.upf.jgraph.custom.StateGraphComponent;
import br.com.upf.view.EditorKeyboardHandler;
import br.com.upf.view.GraphEditor;
import br.com.upf.view.StateEditorPopupMenu;
import br.com.upf.view.StateEditorToolBar;
import br.com.upf.view.StatePalette;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraphSelectionModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author GiordaniAntonio
 */
public class StateEditor extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 6181123989323951836L;
    
    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     *
     */
    private StateGraphComponent stateGraphComponent;

    /**
     *
     */
    private StateGraph stateGraph;

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
    private StatePalette palette;

    /**
     *
     */
    private StateEditorToolBar toolBar;

    /**
     *
     */
    private StateEditorPopupMenu popupMenu;

    /**
     *
     */
    private ArrayList<StandartExpression> arrayListExpressions;

    /**
     *
     */
    private GraphEditor graphEditor;

    /**
     *
     * Sinalizador indicando se o gráfico atual foi modificado
     */
    private boolean modified = false;

    /**
     *
     */
    private mxUndoManager undoManager;

    /**
     *
     */
    protected mxRubberband rubberband;

    /**
     *
     */
    protected mxKeyboardHandler keyboardHandler;

    /**
     * Histórico de comandos
     */
    protected mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
        @Override
        public void invoke(Object source, mxEventObject evt) {
            getUndoManager().undoableEditHappened((mxUndoableEdit) evt
                    .getProperty("edit"));
        }
    };

    /**
     *
     */
    protected mxEventSource.mxIEventListener changeTracker = new mxEventSource.mxIEventListener() {
        @Override
        public void invoke(Object source, mxEventObject evt) {
            setModified(true);
        }
    };

    /**
     * Creates new form DiagramaDeClassModels
     *
     * @param editor
     * @param element
     */
    public StateEditor(GraphEditor editor, ClassElement element) {
        super("Diagrama de Estados");
        initComponents();
        this.graphEditor = editor;
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.diagramOfStateModel = classModel.getDiagramOfStateModel();

        stateGraph = new StateGraph(diagramOfStateModel);
        stateGraphComponent = new StateGraphComponent(graphEditor, stateGraph, diagramOfStateModel);
        stateGraph.setStateGraphComponent(stateGraphComponent);

        createUndoManager();

        stateGraph.setResetViewOnRootChange(false);
        stateGraph.getModel().addListener(mxEvent.CHANGE, changeTracker);

        stateGraph.getModel().addListener(mxEvent.UNDO, undoHandler);
        stateGraph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt
                        .getProperty("edit")).getChanges();
                stateGraph.setSelectionCells(stateGraph
                        .getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        installGraphComponent();
        installPalette();
        installToolBar();

        installRepaintListener();
        installHandlers();
        installListeners();
        installSelectListener();
        setAlignColumnJTable();
    }

    /**
     * Configuração -Define o alinhamento do conteudo de células da tabela
     */
    private void setAlignColumnJTable() {
        TableColumn column;
        TableCellRenderer tcr = new AlinharCentro();
        for (int i = 0; i < jTableExpressions.getColumnCount(); i++) {
            column = jTableExpressions.getColumnModel().getColumn(i);
            column.setCellRenderer(tcr);
        }
    }

    private void installGraphComponent() {
        jPanelContent.add(BorderLayout.CENTER, stateGraphComponent);
    }

    private void installPalette() {
        palette = new StatePalette();
        addItemsPalette();
        jPanelPalette.add(BorderLayout.CENTER, palette);
    }

    private void installToolBar() {
        toolBar = new StateEditorToolBar(this, JToolBar.HORIZONTAL);
        jPanelNorth.add(BorderLayout.CENTER, toolBar);
    }

    private void addItemsPalette() {

        palette
                .addTemplate(
                        "Inicial",
                        new ImageIcon(
                                StateEditor.class
                                .getResource(IMAGE_PATH + "inicial.png")),
                        "inicial", 30, 30, new InitialState("Estado Inicial"));

        palette
                .addTemplate(
                        "Estado",
                        new ImageIcon(
                                StateEditor.class
                                .getResource(IMAGE_PATH + "rounded.png")),
                        "rounded=1", 100, 60, new State());

        palette
                .addTemplate(
                        "Final",
                        new ImageIcon(
                                StateEditor.class
                                .getResource(IMAGE_PATH + "final.png")),
                        "final;shape=doubleEllipse", 30, 30, new FinalState("Estado Final"));
    }

    private void createUndoManager() {
        undoManager = new mxUndoManager();
    }

    private void installHandlers() {
        rubberband = new mxRubberband(stateGraphComponent);
        keyboardHandler = new EditorKeyboardHandler(stateGraphComponent);
    }

    private void installRepaintListener() {
        stateGraphComponent.getGraph().addListener(mxEvent.REPAINT,
                new mxEventSource.mxIEventListener() {
                    @Override
                    public void invoke(Object source, mxEventObject evt) {
                        String buffer = (stateGraphComponent.getTripleBuffer() != null) ? ""
                                : " (unbuffered)";
                        mxRectangle dirty = (mxRectangle) evt
                        .getProperty("region");

                        if (dirty == null) {
                            status("Repaint all" + buffer);
                        } else {
                            status("Repaint: x=" + (int) (dirty.getX()) + " y="
                                    + (int) (dirty.getY()) + " w="
                                    + (int) (dirty.getWidth()) + " h="
                                    + (int) (dirty.getHeight()) + buffer);
                        }
                    }
                });
    }

    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            stateGraphComponent.zoomIn();
        } else {
            stateGraphComponent.zoomOut();
        }

        status(mxResources.get("scale") + ": "
                + (int) (100 * stateGraphComponent.getGraph().getView().getScale())
                + "%");
    }

    protected void showGraphPopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                stateGraphComponent);
        popupMenu = new StateEditorPopupMenu(StateEditor.this);
        popupMenu.show(stateGraphComponent, pt.x, pt.y);

        e.consume();
    }

    protected void mouseLocationChanged(MouseEvent e) {
        status(e.getX() + ", " + e.getY());
    }

    private void installSelectListener() {
        stateGraphComponent.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {

            @Override
            public void invoke(Object sender, mxEventObject evt) {
                if (sender instanceof mxGraphSelectionModel) {
                    mxGraphSelectionModel sm = (mxGraphSelectionModel) sender;

                    Object selecao[] = sm.getCells();

                    if (selecao != null) {
                        if (selecao.length == 1) {
                            mxCell cell = (mxCell) sm.getCell();
                            // Ativa painel de propriedades
                            displayJPanelProperties(true);

                            // Vértice
                            if (cell.isVertex()) {

                                if (cell.getValue() instanceof BasicState) {
                                    BasicState basicState = (BasicState) cell.getValue();
                                    displayJPanelBase(true);
                                    if (cell.getValue() instanceof State) {
                                        State state = (State) basicState;
                                        displayState(state);
                                    } else {
                                        if (cell.getValue() instanceof InitialState) {
                                            InitialState initialState = (InitialState) basicState;
                                            displayInitialState(initialState);
                                        } else {
                                            if (cell.getValue() instanceof FinalState) {
                                                FinalState finalState = (FinalState) basicState;
                                                displayFinalState(finalState);
                                            }
                                        }
                                    }
                                } else {
                                    // Não é instância de BasicState
                                }
                            }// Aresta
                            else {

                                if ((cell.getSource() != null) && (cell.getTarget() != null)) {
                                    if (cell.getValue() instanceof Transition) {
                                        Transition transition = (Transition) cell.getValue();
                                        displayTransition(transition);
                                    } else {
                                        // Não é instância de Transition
                                    }
                                } else {
                                    //Aresta não esta completamente conectada
                                }
                            }
                        } else {
                            displayJPanelProperties(false);
                        }
                    } else {
                        displayJPanelProperties(false);
                    }
                }
            }
        });
    }

    public void loadAttributes(ArrayList<StandartExpression> expressions) {
        listExpressions.clear();
        listExpressions.addAll(expressions);
    }

    private void displayJPanelProperties(Boolean selected) {
        CardLayout painel = (CardLayout) jPanelProperties.getLayout();
        if (selected) {
            painel.show(jPanelProperties, "card2");
        } else {
            painel.show(jPanelProperties, "card1");
        }
    }

    private void displayJPanelBase(Boolean state) {
        CardLayout painel = (CardLayout) jPanelBase.getLayout();
        if (state) {
            painel.show(jPanelBase, "card1");
        } else {
            painel.show(jPanelBase, "card2");
        }
    }

    private void displayjTabbedPaneProperties(Boolean state) {
        if (state) {
            jTabbedPaneProperties.setEnabledAt(0, true);
            jTabbedPaneProperties.setEnabledAt(1, true);
            jTabbedPaneProperties.setSelectedIndex(1);
        } else {
            jTabbedPaneProperties.setEnabledAt(0, true);
            jTabbedPaneProperties.setEnabledAt(1, false);
            jTabbedPaneProperties.setSelectedIndex(0);
        }
    }

    private void displayTransition(Transition t) {
        displayJPanelBase(false);
        displayjTabbedPaneProperties(false);
        jTextFieldConnectionName.setText(t.getName());
        jTextFieldConnectionSource.setText(t.getSource().getName());
        jTextFieldConnectionTarget.setText(t.getTarget().getName());
    }

    private void displayState(State s) {
        displayjTabbedPaneProperties(true);
        loadAttributes(s.getStandartExpressions());
        jTextFieldStateName.setText(s.getName());
    }

    private void displayInitialState(InitialState is) {
        displayjTabbedPaneProperties(false);
        jTextFieldStateName.setText(is.getName());
    }

    private void displayFinalState(FinalState fs) {
        displayjTabbedPaneProperties(false);
        jTextFieldStateName.setText(fs.getName());
    }

    public void status(String msg) {
        jLabelStatusInformation.setText(msg);
    }

    public void updateTitle() {
        this.setTitle("Diagrama de Estados - " + classModel.getName());
    }

    private void installListeners() {

        // Instala o popup menu no componente "gráfico" graph
        stateGraphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            /**
             *
             */
            @Override
            public void mousePressed(MouseEvent e) {
                // Alças menu de contexto no Mac, onde o gatilho está na mousePressed
                mouseReleased(e);
            }

            /**
             *
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showGraphPopupMenu(e);
                }
            }

        });

        // Instala um ouvinte de movimento do mouse para exibir a localização do mouse
        stateGraphComponent.getGraphControl().addMouseMotionListener(
                new MouseMotionListener() {

                    /*
                     * (non-Javadoc)
                     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
                     */
                    public void mouseDragged(MouseEvent e) {
                        mouseLocationChanged(e);
                    }

                    /*
                     * (non-Javadoc)
                     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
                     */
                    public void mouseMoved(MouseEvent e) {
                        mouseDragged(e);
                    }

                });
    }

    /**
     * Classe Interna - Utilizada no alinhamento do conteudo das colunas da
     * Tabela
     */
    class AlinharCentro extends DefaultTableCellRenderer {

        public AlinharCentro() {
            setHorizontalAlignment(CENTER); // ou LEFT, RIGHT, etc
        }
    }

    public Action bind(String name, final Action action) {
        return bind(name, action, null);
    }

    public Action bind(String name, final Action action, String iconUrl) {
        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
                StateEditor.class.getResource(iconUrl)) : null) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.actionPerformed(new ActionEvent(getStateGraphComponent(), e
                                        .getID(), e.getActionCommand()));
                    }
                };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
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

        listExpressions =  new LinkedList<br.com.upf.beans.StandartExpression>();
        jPanelPrincipal = new javax.swing.JPanel();
        jPanelNorth = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jSplitPaneCenter = new javax.swing.JSplitPane();
        jPanelWest = new javax.swing.JPanel();
        jSplitPaneWest = new javax.swing.JSplitPane();
        jPanelPalette = new javax.swing.JPanel();
        jPanelProperties = new javax.swing.JPanel();
        jPanelEmpty = new javax.swing.JPanel();
        jLabelNoSelect = new javax.swing.JLabel();
        jPanelFull = new javax.swing.JPanel();
        jTabbedPaneProperties = new javax.swing.JTabbedPane();
        jPanelBase = new javax.swing.JPanel();
        jPanelBaseState = new javax.swing.JPanel();
        jLabelStateName = new javax.swing.JLabel();
        jTextFieldStateName = new javax.swing.JTextField();
        jPanelBaseConnection = new javax.swing.JPanel();
        jLabelConnectionName = new javax.swing.JLabel();
        jTextFieldConnectionName = new javax.swing.JTextField();
        jLabelConnectionSource = new javax.swing.JLabel();
        jTextFieldConnectionSource = new javax.swing.JTextField();
        jLabelConnectionTarget = new javax.swing.JLabel();
        jTextFieldConnectionTarget = new javax.swing.JTextField();
        jPanelExpressions = new javax.swing.JPanel();
        jScrollPaneExpressions = new javax.swing.JScrollPane();
        jTableExpressions = new javax.swing.JTable();
        jPanelContent = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jLabelStatusInformation = new javax.swing.JLabel();

        listExpressions = org.jdesktop.observablecollections.ObservableCollections.observableList(listExpressions);

        setMaximizable(true);
        setPreferredSize(new java.awt.Dimension(410, 294));
        setRequestFocusEnabled(false);
        setVisible(true);

        jPanelPrincipal.setLayout(new java.awt.BorderLayout());

        jPanelNorth.setPreferredSize(new java.awt.Dimension(484, 25));
        jPanelNorth.setLayout(new java.awt.BorderLayout());
        jPanelPrincipal.add(jPanelNorth, java.awt.BorderLayout.PAGE_START);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jSplitPaneCenter.setDividerLocation(250);
        jSplitPaneCenter.setDividerSize(7);
        jSplitPaneCenter.setResizeWeight(0.1);

        jPanelWest.setPreferredSize(new java.awt.Dimension(150, 318));
        jPanelWest.setLayout(new java.awt.BorderLayout());

        jSplitPaneWest.setDividerLocation(80);
        jSplitPaneWest.setDividerSize(7);
        jSplitPaneWest.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneWest.setOneTouchExpandable(true);

        jPanelPalette.setLayout(new java.awt.BorderLayout());
        jSplitPaneWest.setTopComponent(jPanelPalette);

        jPanelProperties.setLayout(new java.awt.CardLayout());

        jPanelEmpty.setLayout(new java.awt.BorderLayout());

        jLabelNoSelect.setForeground(new java.awt.Color(102, 102, 102));
        jLabelNoSelect.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNoSelect.setText("<Selecione um Estado>");
        jPanelEmpty.add(jLabelNoSelect, java.awt.BorderLayout.CENTER);

        jPanelProperties.add(jPanelEmpty, "card1");

        jPanelFull.setLayout(new java.awt.BorderLayout());

        jTabbedPaneProperties.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        jPanelBase.setLayout(new java.awt.CardLayout());

        jLabelStateName.setText("Nome:");

        jTextFieldStateName.setEditable(false);
        jTextFieldStateName.setOpaque(false);

        javax.swing.GroupLayout jPanelBaseStateLayout = new javax.swing.GroupLayout(jPanelBaseState);
        jPanelBaseState.setLayout(jPanelBaseStateLayout);
        jPanelBaseStateLayout.setHorizontalGroup(
            jPanelBaseStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseStateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelStateName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldStateName, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelBaseStateLayout.setVerticalGroup(
            jPanelBaseStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseStateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBaseStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelStateName)
                    .addComponent(jTextFieldStateName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(171, Short.MAX_VALUE))
        );

        jPanelBase.add(jPanelBaseState, "card1");

        jLabelConnectionName.setText("Nome:");

        jTextFieldConnectionName.setEditable(false);
        jTextFieldConnectionName.setOpaque(false);

        jLabelConnectionSource.setText("Origem:");

        jTextFieldConnectionSource.setEditable(false);
        jTextFieldConnectionSource.setOpaque(false);

        jLabelConnectionTarget.setText("Destino:");

        jTextFieldConnectionTarget.setEditable(false);
        jTextFieldConnectionTarget.setOpaque(false);

        javax.swing.GroupLayout jPanelBaseConnectionLayout = new javax.swing.GroupLayout(jPanelBaseConnection);
        jPanelBaseConnection.setLayout(jPanelBaseConnectionLayout);
        jPanelBaseConnectionLayout.setHorizontalGroup(
            jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseConnectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelConnectionName)
                    .addComponent(jLabelConnectionSource)
                    .addComponent(jLabelConnectionTarget))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldConnectionTarget, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(jTextFieldConnectionSource, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldConnectionName, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanelBaseConnectionLayout.setVerticalGroup(
            jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseConnectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelConnectionName)
                    .addComponent(jTextFieldConnectionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelConnectionSource)
                    .addComponent(jTextFieldConnectionSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBaseConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelConnectionTarget)
                    .addComponent(jTextFieldConnectionTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelBase.add(jPanelBaseConnection, "card2");

        jTabbedPaneProperties.addTab("Base", jPanelBase);

        jPanelExpressions.setLayout(new java.awt.BorderLayout());

        jTableExpressions.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listExpressions, jTableExpressions);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${attribute}"));
        columnBinding.setColumnName("Atributo");
        columnBinding.setColumnClass(br.com.upf.beans.Attribute.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${operator}"));
        columnBinding.setColumnName("Operador");
        columnBinding.setColumnClass(br.com.upf.util.Operator.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${value}"));
        columnBinding.setColumnName("Valor");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPaneExpressions.setViewportView(jTableExpressions);
        if (jTableExpressions.getColumnModel().getColumnCount() > 0) {
            jTableExpressions.getColumnModel().getColumn(0).setResizable(false);
            jTableExpressions.getColumnModel().getColumn(1).setResizable(false);
            jTableExpressions.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanelExpressions.add(jScrollPaneExpressions, java.awt.BorderLayout.CENTER);

        jTabbedPaneProperties.addTab("Expressões", jPanelExpressions);

        jPanelFull.add(jTabbedPaneProperties, java.awt.BorderLayout.CENTER);

        jPanelProperties.add(jPanelFull, "card2");

        jSplitPaneWest.setRightComponent(jPanelProperties);

        jPanelWest.add(jSplitPaneWest, java.awt.BorderLayout.CENTER);

        jSplitPaneCenter.setLeftComponent(jPanelWest);

        jPanelContent.setLayout(new java.awt.BorderLayout());
        jSplitPaneCenter.setRightComponent(jPanelContent);

        jPanelCenter.add(jSplitPaneCenter, java.awt.BorderLayout.CENTER);

        jPanelPrincipal.add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(484, 25));
        jPanelSouth.setLayout(new java.awt.BorderLayout());
        jPanelSouth.add(jLabelStatusInformation, java.awt.BorderLayout.CENTER);

        jPanelPrincipal.add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelConnectionName;
    private javax.swing.JLabel jLabelConnectionSource;
    private javax.swing.JLabel jLabelConnectionTarget;
    private javax.swing.JLabel jLabelNoSelect;
    private javax.swing.JLabel jLabelStateName;
    private javax.swing.JLabel jLabelStatusInformation;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelBaseConnection;
    private javax.swing.JPanel jPanelBaseState;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPanel jPanelEmpty;
    private javax.swing.JPanel jPanelExpressions;
    private javax.swing.JPanel jPanelFull;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelPalette;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelProperties;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPaneExpressions;
    private javax.swing.JSplitPane jSplitPaneCenter;
    private javax.swing.JSplitPane jSplitPaneWest;
    private javax.swing.JTabbedPane jTabbedPaneProperties;
    private javax.swing.JTable jTableExpressions;
    private javax.swing.JTextField jTextFieldConnectionName;
    private javax.swing.JTextField jTextFieldConnectionSource;
    private javax.swing.JTextField jTextFieldConnectionTarget;
    private javax.swing.JTextField jTextFieldStateName;
    private java.util.List<br.com.upf.beans.StandartExpression> listExpressions;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public StateGraphComponent getStateGraphComponent() {
        return stateGraphComponent;
    }

    public void setStateGraphComponent(StateGraphComponent stateGraphComponent) {
        this.stateGraphComponent = stateGraphComponent;
    }

    public StateGraph getStateGraph() {
        return stateGraph;
    }

    public void setStateGraph(StateGraph stateGraph) {
        this.stateGraph = stateGraph;
    }

    public StatePalette getPalette() {
        return palette;
    }

    public void setPalette(StatePalette palette) {
        this.palette = palette;
    }

    public GraphEditor getGraphEditor() {
        return graphEditor;
    }

    public void setGraphEditor(GraphEditor graphEditor) {
        this.graphEditor = graphEditor;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public void setUndoManager(mxUndoManager undoManager) {
        this.undoManager = undoManager;
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClasseModel(ClassModel classeModel) {
        this.classModel = classeModel;
    }

    public DiagramOfStateModel getDiagramOfStateModel() {
        return diagramOfStateModel;
    }

    public void setDiagramOfStateModel(DiagramOfStateModel diagramOfStateModel) {
        this.diagramOfStateModel = diagramOfStateModel;
    }

    public ClassElement getClassElement() {
        return classElement;
    }

    public void setClassElement(ClassElement classElement) {
        this.classElement = classElement;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.classElement);
        hash = 19 * hash + Objects.hashCode(this.classModel);
        hash = 19 * hash + Objects.hashCode(this.diagramOfStateModel);
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
        final StateEditor other = (StateEditor) obj;
        if (!Objects.equals(this.classElement, other.classElement)) {
            return false;
        }
        if (!Objects.equals(this.classModel, other.classModel)) {
            return false;
        }
        if (!Objects.equals(this.diagramOfStateModel, other.diagramOfStateModel)) {
            return false;
        }
        return true;
    }

}
