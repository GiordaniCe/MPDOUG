/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the graphEditor.
 */
package br.com.upf.view.internalframe;

import br.com.upf.beans.Attribute;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.DiagramOfClassModel;
import br.com.upf.jgraph.custom.ClassGraph;
import br.com.upf.jgraph.custom.ClassGraphComponent;
import br.com.upf.view.ClassEditorPopupMenu;
import br.com.upf.view.ClassEditorToolBar;
import br.com.upf.view.ClassPalette;
import br.com.upf.view.CustomMenuBar;
import br.com.upf.view.EditorKeyboardHandler;
import br.com.upf.view.GraphEditor;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
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
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
public class ClassEditor extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 577339445580882643L;

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     *
     */
    private CustomMenuBar menuBarCustom;

    /**
     *
     */
    private ClassGraphComponent classGraphComponent;

    /**
     *
     */
    private ClassGraph classGraph;

    /**
     *
     */
    private DiagramOfClassModel diagramOfClassModel;
    /**
     *
     */
    private ClassPalette palette;

    /**
     *
     */
    private ClassEditorToolBar toolBar;

    /**
     *
     */
    private ClassEditorPopupMenu popupMenu;

    /**
     *
     */
    private GraphEditor graphEditor;

    /**
     *
     */
    private ArrayList<Attribute> arrayListAttributes;

    /**
     *
     */
    private ClassElement selectedClassElement;

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
     *
     */
    protected File currentFile;

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
     * Creates new form ClassDiagramEditor
     *
     * @param editor
     * @param tittle
     * @param diagramModel
     */
    public ClassEditor(GraphEditor editor, String tittle, DiagramOfClassModel diagramModel) {
        super("Diagrama de Classes - " + tittle);
        initComponents();
        this.graphEditor = editor;
        this.diagramOfClassModel = diagramModel;
        this.menuBarCustom = graphEditor.getMenuBarCustom();
        this.selectedClassElement = null;

        this.classGraph = new ClassGraph(graphEditor, diagramOfClassModel);
        this.classGraphComponent = new ClassGraphComponent(graphEditor, this, diagramOfClassModel, classGraph);
        this.classGraph.setClassGraphComponent(classGraphComponent);

        createUndoManager();

        classGraph.setResetViewOnRootChange(false);
        classGraph.getModel().addListener(mxEvent.CHANGE, changeTracker);

        classGraph.getModel().addListener(mxEvent.UNDO, undoHandler);
        classGraph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt
                        .getProperty("edit")).getChanges();
                classGraph.setSelectionCells(classGraph
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
        setNameHeaderColumnJTable();

        jTabbedPaneClassDescription.setEnabledAt(1, false);

    }

    private void updateTitle(String title) {
        this.setTitle(title);
    }

    /**
     * Guarda a seleção atual, limpa-a e a recarrega,Atualizando assim o MenuBar
     */
    public void refreshClassSelected() {
        if (classGraphComponent.getGraph().getSelectionModel().getCells().length == 1) {
            Object select = classGraphComponent.getGraph().getSelectionModel().getCell();
            classGraphComponent.getGraph().getSelectionModel().clear();
            classGraphComponent.getGraph().getSelectionModel().setCell(select);
        }
    }

    /**
     * Configuração - Define o alinhamento do cabeçalho e conteudo das células
     * da tabela
     */
    private void setAlignColumnJTable() {
        TableColumn column;
        TableCellRenderer tcr = new AlinharCentro();
        for (int i = 0; i < jTableAttributes.getColumnCount(); i++) {
            column = jTableAttributes.getColumnModel().getColumn(i);
            column.setCellRenderer(tcr);
            column.setHeaderRenderer(tcr);
        }
    }

    /**
     * Configuração - Define o nome das colunas no cabeçalho da Tabela
     */
    private void setNameHeaderColumnJTable() {
        jTableAttributes.getColumnModel().getColumn(0).setHeaderValue(mxResources.get("visibility"));
        jTableAttributes.getColumnModel().getColumn(1).setHeaderValue(mxResources.get("type"));
        jTableAttributes.getColumnModel().getColumn(2).setHeaderValue(mxResources.get("name"));
    }

    private void installGraphComponent() {
        jPanelContent.add(BorderLayout.CENTER, classGraphComponent);
    }

    private void installPalette() {
        palette = new ClassPalette();
        addItemsPalette();
        jPanelWestPalette.add(BorderLayout.CENTER, palette);
    }

    private void installToolBar() {
        toolBar = new ClassEditorToolBar(this, JToolBar.HORIZONTAL);
        jPanelNorth.add(BorderLayout.CENTER, toolBar);
    }

    private void addItemsPalette() {
        mxCell classeTemplate = new mxCell(new ClassModel(), new mxGeometry(0, 0,
                200, 200), null);
        classeTemplate.getGeometry().setAlternateBounds(
                new mxRectangle(0, 0, 200, 100));
        
        classeTemplate.setVertex(true);

        getPalette()
                .addTemplate(
                        "Classe",
                        new ImageIcon(
                                ClassEditor.class
                                .getResource(IMAGE_PATH + "rectangle.png")),
                        classeTemplate);

    }

    private void createUndoManager() {
        undoManager = new mxUndoManager();
    }

    private void installHandlers() {
        rubberband = new mxRubberband(classGraphComponent);
        keyboardHandler = new EditorKeyboardHandler(classGraphComponent);
    }

    private void installRepaintListener() {
        classGraphComponent.getGraph().addListener(mxEvent.REPAINT,
                new mxEventSource.mxIEventListener() {
                    @Override
                    public void invoke(Object source, mxEventObject evt) {
                        String buffer = (classGraphComponent.getTripleBuffer() != null) ? ""
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
            classGraphComponent.zoomIn();
        } else {
            classGraphComponent.zoomOut();
        }

        status(mxResources.get("scale") + ": "
                + (int) (100 * classGraphComponent.getGraph().getView().getScale())
                + "%");
    }

    /**
     *
     * @param e
     */
    protected void showGraphPopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                classGraphComponent);
        popupMenu = new ClassEditorPopupMenu(ClassEditor.this, selectedClassElement);
        popupMenu.show(classGraphComponent, pt.x, pt.y);

        e.consume();
    }

    /**
     * Método que define as novas coordenadas do mouse na barra de status
     */
    protected void mouseLocationChanged(MouseEvent e) {
        status(e.getX() + ", " + e.getY());
    }

    public void status(String msg) {
        jLabelStatusInformation.setText(msg);
    }

    private void installSelectListener() {
        classGraphComponent.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {

            @Override
            public void invoke(Object sender, mxEventObject evt) {
                if (sender instanceof mxGraphSelectionModel) {
                    mxGraphSelectionModel sm = (mxGraphSelectionModel) sender;

                    Object selecao[] = sm.getCells();

                    if (selecao != null) {
                        if (selecao.length == 1) {
                            mxCell cell = (mxCell) sm.getCell();

                            if (cell.getValue() instanceof ClassModel) {
                                ClassModel model = (ClassModel) cell.getValue();
                                ClassElement element = diagramOfClassModel.getClassElement(model.getId());
                                setSelectedClassElement(element);
                                loadAttributes(model.getAttributes());
                                displayInformation(true);
                                menuBarCustom.updateContexClassEditor(element);
                            } else {
                                menuBarCustom.updateContexClassEditor(null);
                                setSelectedClassElement(null);
                            }

                        } else {
                            displayInformation(false);
                            menuBarCustom.updateContexClassEditor(null);
                            setSelectedClassElement(null);
                        }

                    } else {
                        displayInformation(false);
                        menuBarCustom.updateContexClassEditor(null);
                        setSelectedClassElement(null);
                    }
                }
            }
        });
    }

    private void displayInformation(boolean value) {
        CardLayout paineis = (CardLayout) jPanelWestAttributes.getLayout();

        if (value) {
            paineis.show(jPanelWestAttributes, "card1");
        } else {
            paineis.show(jPanelWestAttributes, "card2");
        }

    }

    public void loadAttributes(ArrayList<Attribute> attributes) {
        listAttributes.clear();
        listAttributes.addAll(attributes);
    }

    /**
     * Instalador de ouvintes
     */
    private void installListeners() {

        // Instala o popup menu no componente "gráfico" classGraph
        classGraphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            /**
             *
             */
            public void mousePressed(MouseEvent e) {
                // Alças menu de contexto no Mac, onde o gatilho está na mousePressed
                mouseReleased(e);
            }

            /**
             *
             */
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showGraphPopupMenu(e);
                }
            }

        });

        // Instala um ouvinte de movimento do mouse para exibir a localização do mouse
        classGraphComponent.getGraphControl().addMouseMotionListener(
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
                ClassEditor.class.getResource(iconUrl)) : null) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.actionPerformed(new ActionEvent(getGraphComponent(), e
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

        listAttributes = new LinkedList<Attribute>();
        jPanelPrincipal = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jLabelStatusInformation = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jSplitPaneCenter = new javax.swing.JSplitPane();
        jPanelWest = new javax.swing.JPanel();
        jSplitPaneWest = new javax.swing.JSplitPane();
        jPanelWestPalette = new javax.swing.JPanel();
        jPanelWestAttributes = new javax.swing.JPanel();
        jPanelEmpty = new javax.swing.JPanel();
        jLabelEmpty = new javax.swing.JLabel();
        jPanelFull = new javax.swing.JPanel();
        jTabbedPaneClassDescription = new javax.swing.JTabbedPane();
        jPanelAttributes = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTableAttributes = new javax.swing.JTable();
        jPanelMethods = new javax.swing.JPanel();
        jPanelContent = new javax.swing.JPanel();
        jPanelNorth = new javax.swing.JPanel();

        listAttributes =  org.jdesktop.observablecollections.ObservableCollections.observableList(listAttributes);

        setClosable(true);
        setMaximizable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanelPrincipal.setLayout(new java.awt.BorderLayout());

        jPanelSouth.setPreferredSize(new java.awt.Dimension(394, 25));
        jPanelSouth.setLayout(new java.awt.BorderLayout());

        jLabelStatusInformation.setForeground(new java.awt.Color(255, 255, 255));
        jLabelStatusInformation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelStatusInformation.setOpaque(true);
        jPanelSouth.add(jLabelStatusInformation, java.awt.BorderLayout.CENTER);

        jPanelPrincipal.add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jSplitPaneCenter.setDividerLocation(250);
        jSplitPaneCenter.setDividerSize(7);
        jSplitPaneCenter.setResizeWeight(0.1);
        jSplitPaneCenter.setToolTipText("");
        jSplitPaneCenter.setFocusCycleRoot(true);
        jSplitPaneCenter.setPreferredSize(new java.awt.Dimension(150, 210));

        jPanelWest.setPreferredSize(new java.awt.Dimension(150, 208));
        jPanelWest.setLayout(new java.awt.BorderLayout());

        jSplitPaneWest.setDividerLocation(80);
        jSplitPaneWest.setDividerSize(7);
        jSplitPaneWest.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneWest.setToolTipText("");
        jSplitPaneWest.setOneTouchExpandable(true);
        jSplitPaneWest.setPreferredSize(new java.awt.Dimension(100, 208));

        jPanelWestPalette.setMinimumSize(new java.awt.Dimension(100, 80));
        jPanelWestPalette.setPreferredSize(new java.awt.Dimension(100, 80));
        jPanelWestPalette.setLayout(new java.awt.BorderLayout());
        jSplitPaneWest.setTopComponent(jPanelWestPalette);

        jPanelWestAttributes.setLayout(new java.awt.CardLayout());

        jPanelEmpty.setLayout(new java.awt.BorderLayout());

        jLabelEmpty.setForeground(new java.awt.Color(153, 153, 153));
        jLabelEmpty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEmpty.setText("<Selecione uma Classe>");
        jLabelEmpty.setToolTipText("");
        jPanelEmpty.add(jLabelEmpty, java.awt.BorderLayout.CENTER);

        jPanelWestAttributes.add(jPanelEmpty, "card2");

        jPanelFull.setLayout(new java.awt.BorderLayout());

        jPanelAttributes.setLayout(new java.awt.BorderLayout());

        jTableAttributes.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listAttributes, jTableAttributes);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${visibility}"));
        columnBinding.setColumnName("Visibilidade");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${umlType}"));
        columnBinding.setColumnName("Tipo");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Nome");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPaneTable.setViewportView(jTableAttributes);
        if (jTableAttributes.getColumnModel().getColumnCount() > 0) {
            jTableAttributes.getColumnModel().getColumn(0).setResizable(false);
            jTableAttributes.getColumnModel().getColumn(1).setResizable(false);
            jTableAttributes.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanelAttributes.add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        jTabbedPaneClassDescription.addTab("Atributos", jPanelAttributes);
        jTabbedPaneClassDescription.addTab("Métodos", jPanelMethods);

        jTabbedPaneClassDescription.setSelectedComponent(jPanelAttributes);

        jPanelFull.add(jTabbedPaneClassDescription, java.awt.BorderLayout.CENTER);

        jPanelWestAttributes.add(jPanelFull, "card1");

        jSplitPaneWest.setRightComponent(jPanelWestAttributes);

        jPanelWest.add(jSplitPaneWest, java.awt.BorderLayout.CENTER);

        jSplitPaneCenter.setLeftComponent(jPanelWest);

        jPanelContent.setLayout(new java.awt.BorderLayout());
        jSplitPaneCenter.setRightComponent(jPanelContent);

        jPanelCenter.add(jSplitPaneCenter, java.awt.BorderLayout.CENTER);

        jPanelPrincipal.add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelNorth.setPreferredSize(new java.awt.Dimension(394, 25));
        jPanelNorth.setLayout(new java.awt.BorderLayout());
        jPanelPrincipal.add(jPanelNorth, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanelPrincipal, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        setBounds(0, 0, 500, 400);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        graphEditor.resetGraphEditor();
    }//GEN-LAST:event_formInternalFrameClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelEmpty;
    private javax.swing.JLabel jLabelStatusInformation;
    private javax.swing.JPanel jPanelAttributes;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPanel jPanelEmpty;
    private javax.swing.JPanel jPanelFull;
    private javax.swing.JPanel jPanelMethods;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JPanel jPanelWestAttributes;
    private javax.swing.JPanel jPanelWestPalette;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JSplitPane jSplitPaneCenter;
    private javax.swing.JSplitPane jSplitPaneWest;
    private javax.swing.JTabbedPane jTabbedPaneClassDescription;
    private javax.swing.JTable jTableAttributes;
    private java.util.List<Attribute> listAttributes;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * Define o arquivo atual
     *
     * @param file
     */
    public void setCurrentFile(File file) {
        File oldValue = currentFile;
        currentFile = file;

        firePropertyChange("currentFile", oldValue, file);
        String fileName = currentFile.getName();

        updateTitle("Diagrama de Classes - " + fileName);
    }

    /**
     * Retorna o arquivo atual
     *
     * @return
     */
    public File getCurrentFile() {
        return currentFile;
    }

    public ClassGraphComponent getGraphComponent() {
        return classGraphComponent;
    }

    public void setGraphComponent(ClassGraphComponent classGraphComponent) {
        this.classGraphComponent = classGraphComponent;
    }

    public ClassGraph getGraph() {
        return classGraph;
    }

    public void setGraph(ClassGraph classGraph) {
        this.classGraph = classGraph;
    }

    public ClassPalette getPalette() {
        return palette;
    }

    public void setPalette(ClassPalette palette) {
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

    public ClassElement getSelectedClassElement() {
        return selectedClassElement;
    }

    public void setSelectedClassElement(ClassElement classElement) {
        this.selectedClassElement = classElement;
    }

}
