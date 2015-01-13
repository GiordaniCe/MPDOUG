/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view;

import br.com.upf.beans.ClassElement;
import br.com.upf.beans.DiagramOfClassModel;
import br.com.upf.jgraph.custom.ClassGraphComponent;
import br.com.upf.util.Operator;
import br.com.upf.util.RelationTypeUMLForSQL;
import br.com.upf.view.dialog.DictionaryOfStatesMethodCustom;
import br.com.upf.view.dialog.DictionaryOfStatesMethodStandart;
import br.com.upf.view.dialog.MethodElaborationDictionary;
import br.com.upf.view.dialog.ParametersTable;
import br.com.upf.view.dialog.ParametersTrigger;
import br.com.upf.view.dialog.TableCodeView;
import br.com.upf.view.dialog.TriggersCodeView;
import br.com.upf.view.dialog.validation.DiagramOfStatesValidation;
import br.com.upf.view.dialog.validation.DictionaryOfStatesValidation;
import br.com.upf.view.internalframe.ClassEditor;
import br.com.upf.view.internalframe.StateEditor;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author GiordaniAntonio
 */
public class GraphEditor extends javax.swing.JFrame {

    private static final long serialVersionUID = -6981729349080289529L;

    /**
     * Adds required resources for i18n
     */
    static {
        try {
            mxResources.add("br/com/upf/resources/editor");
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     *
     */
    private ArrayList<Operator> operators;

    /**
     *
     */
    private ArrayList<RelationTypeUMLForSQL> relationShipTypes;

    /**
     *
     */
    private CustomMenuBar menuBarCustom;

    /**
     *
     */
    private CustomToolBar toolBar;

    /**
     *
     */
    private DiagramOfClassModel diagramOfClassModel;

    /**
     *
     */
    private ClassGraphComponent diagramOfClassGraph;

    /**
     *
     */
    private ClassEditor classEditor;

    /**
     *
     */
    private mxGraphComponent currentGraphComponent;

    /**
     *
     */
    private mxGraph currentGraph;

    /**
     *
     */
    private StateEditor stateEditor;

    /**
     *
     */
    private DictionaryOfStatesMethodCustom dictionaryOfStatesMethodCustom;

    /**
     *
     */
    private DictionaryOfStatesMethodStandart dictionaryOfStatesMethodStandart;

    /**
     *
     */
    private MethodElaborationDictionary methodElaborationDictionary;

    /**
     *
     */
    private ParametersTable parametersTableView;

    /**
     *
     */
    private TableCodeView tableCodeView;

    /**
     *
     */
    private ParametersTrigger parametersTriggersView;

    /**
     *
     */
    private TriggersCodeView triggersCodeView;

    /**
     * VALIDAÇÃO - Diagrama de estados
     */
    private DiagramOfStatesValidation diagramOfStatesValidation;

    /**
     * VALIDAÇÃO - Dicionário de estados
     */
    private DictionaryOfStatesValidation dictionaryOfStatesValidation;

    /**
     *
     */
    private ClassElement classElement;

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     * Flag indicating whether the current graph has been modified
     */
    private boolean modified = false;

    /**
     * Creates new form GraphEditor
     */
    public GraphEditor() {
        initComponents();
        installMenuBar();
        loadOperators();
        loadRelationShipTypes();
        classEditor = null;
        stateEditor = null;

        menuBarCustom.enableProjectMenuBar();

        updateTitle();
    }

    private void updateTitle() {
          this.setTitle("MPDOUG");
    }

    /**
     * Carrega os Operadores
     */
    private void loadOperators() {
        operators = new ArrayList<>();
        operators.add(new Operator("IGUAL", "=", "==", true, true, false));
        operators.add(new Operator("DIFERENTE", "!=", "!=", true, true, false));
        operators.add(new Operator("MAIOR", ">", ">", true, false, false));
        operators.add(new Operator("MENOR", "<", "<", true, false, false));
        operators.add(new Operator("MAIOR E IGUAL", ">=", ">=", true, false, false));
        operators.add(new Operator("MENOR E IGUAL", "<=", "<=", true, false, false));

        operators.add(new Operator("E", "AND", "&&", false, false, true));
        operators.add(new Operator("OU", "OR", "||", false, false, true));
        operators.add(new Operator("NAO", "NOT", "!", false, false, true));
    }

    /**
     * Carrega Relação de correspondencia entre Tipos UML e SQL
     */
    private void loadRelationShipTypes() {
        relationShipTypes = new ArrayList<>();

        // Tipos String
        relationShipTypes.add(new RelationTypeUMLForSQL("String", "VARCHAR"));

        // Tipos Char
        relationShipTypes.add(new RelationTypeUMLForSQL("Char", "VARCHAR"));

        // Tipos Boolean
        relationShipTypes.add(new RelationTypeUMLForSQL("Boolean", "BOOLEAN"));

        // Tipos Integer
        relationShipTypes.add(new RelationTypeUMLForSQL("Integer", "INT"));

        // Tipos Double
        relationShipTypes.add(new RelationTypeUMLForSQL("Double", "DOUBLE PRECISION"));

        // Tipos Float
        relationShipTypes.add(new RelationTypeUMLForSQL("Float", "FLOAT"));
    }

    /**
     * Instala o MenuBar
     */
    private void installMenuBar() {
        menuBarCustom = new CustomMenuBar(this);
        this.setJMenuBar(menuBarCustom);
    }

    /**
     * Reseta o GraphEditor
     */
    public void resetGraphEditor() {
        classEditor.setVisible(false);
        jDesktopPaneWorkArea.removeAll();
        classEditor = null;
        stateEditor = null;
        menuBarCustom.enableProjectMenuBar();
    }

    /**
     * Cria um Novo Diagrama de Classes
     *
     * @param name
     * @param display
     */
    public void newClassEditor(String name, boolean display) {
        diagramOfClassModel = new DiagramOfClassModel(name);
        classEditor = new ClassEditor(this, diagramOfClassModel.getName(), diagramOfClassModel);
        installClassEditor();
        if (display) {
            classEditorView();
        }
    }

    /**
     * Adiciona o ClassEditor (JInternalFrame) ao JDesktopPane.
     */
    private void installClassEditor() {
        classEditor.setSize(jDesktopPaneWorkArea.getSize());
        jDesktopPaneWorkArea.add(classEditor);
    }

    /**
     * Exibe o ClassEditor (Editor de Diagrama de Classes)
     */
    public void classEditorView() {
        if (jDesktopPaneWorkArea.getSelectedFrame() instanceof StateEditor) {
            StateEditor currentEditor = (StateEditor) jDesktopPaneWorkArea.getSelectedFrame();
            currentEditor.setVisible(false);
        }

        jDesktopPaneWorkArea.setSelectedFrame(classEditor);
        classEditor.setVisible(true);
        menuBarCustom.enableClassMenuBar();

        try {
            classEditor.setMaximum(true);
        } catch (PropertyVetoException ex) {
            System.out.println("ERRO");
        }
    }

    /**
     * Cria um Diagrama de Estados para a Classe
     *
     * @param classElement
     */
    public void newStateEditor(ClassElement classElement) {
        // Recupera o Elemento 
        stateEditor = classElement.getStateEditor();
        classElement.getClassModel().setInitializedDiagramOfStateModel(true);
        installStateEditor();
    }

    /**
     * Adiciona o StateEditor (JInternalFrame) ao JDesktopPane.
     */
    private void installStateEditor() {
        stateEditor.setSize(jDesktopPaneWorkArea.getSize());
        jDesktopPaneWorkArea.add(stateEditor);
    }

    /**
     * Exibe o StateEditor (Editor de Diagrama de Estados) da Classe
     */
    public void stateEditorView(StateEditor editor) {

        if (jDesktopPaneWorkArea.getSelectedFrame() instanceof ClassEditor) {
            ClassEditor currentEditor = (ClassEditor) jDesktopPaneWorkArea.getSelectedFrame();
            currentEditor.setVisible(false);
        }

        JInternalFrame frames[] = jDesktopPaneWorkArea.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] instanceof StateEditor) {
                StateEditor currentStateEditor = (StateEditor) frames[i];
                if (currentStateEditor.equals(editor)) {
                    jDesktopPaneWorkArea.setSelectedFrame(currentStateEditor);
                    currentStateEditor.setVisible(true);
                    menuBarCustom.enableStateMenuBar();
                    currentStateEditor.updateTitle();
                }
            }
        }

        try {
            stateEditor.setMaximum(true);
        } catch (PropertyVetoException ex) {
            System.out.println("ERRO");
        }
    }

    /**
     * Exibe Dialogo - Elaborador de dicionario de estados Método Custom
     *
     * @param element
     */
    public void dictionaryOfStatesMethodCustomView(ClassElement element) {
        dictionaryOfStatesMethodCustom = new DictionaryOfStatesMethodCustom(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - dictionaryOfStatesMethodCustom.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - dictionaryOfStatesMethodCustom.getHeight()) / 2;

        dictionaryOfStatesMethodCustom.setLocation(x, y);
        dictionaryOfStatesMethodCustom.setVisible(true);
    }

    /**
     * Exibe Dialogo - Elaborador de dicionario de estados Método Standart
     *
     * @param element
     */
    public void dictionaryOfStatesMethodStandartView(ClassElement element) {
        dictionaryOfStatesMethodStandart = new DictionaryOfStatesMethodStandart(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - dictionaryOfStatesMethodStandart.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - dictionaryOfStatesMethodStandart.getHeight()) / 2;

        dictionaryOfStatesMethodStandart.setLocation(x, y);
        dictionaryOfStatesMethodStandart.setVisible(true);
    }

    /**
     * Exibe Dialogo - Seleção de Método de elaboração do Dicionário de estados.
     *
     * @param element
     */
    public void methodElaborationDictionaryView(ClassElement element) {
        methodElaborationDictionary = new MethodElaborationDictionary(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - methodElaborationDictionary.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - methodElaborationDictionary.getHeight()) / 2;

        methodElaborationDictionary.setLocation(x, y);
        methodElaborationDictionary.setVisible(true);
    }

    /**
     * Exibe dialogo com dados da validação do diagrama de estados.
     *
     * @param element
     */
    public void diagramOfStatesValidationView(ClassElement element) {
        diagramOfStatesValidation = new DiagramOfStatesValidation(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - diagramOfStatesValidation.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - diagramOfStatesValidation.getHeight()) / 2;

        diagramOfStatesValidation.setLocation(x, y);
        diagramOfStatesValidation.setVisible(true);
    }

    /**
     * Exibe dialogo com dados da validação do dicionário de estados
     *
     * @param element
     */
    public void dictionaryOfStatesValidationView(ClassElement element) {
        dictionaryOfStatesValidation = new DictionaryOfStatesValidation(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - dictionaryOfStatesValidation.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - dictionaryOfStatesValidation.getHeight()) / 2;

        dictionaryOfStatesValidation.setLocation(x, y);
        dictionaryOfStatesValidation.setVisible(true);
    }

    /**
     * Exibe o dialogo - Parâmetros da Tabela
     *
     * @param element
     */
    public void parametersTableView(ClassElement element) {
        parametersTableView = new ParametersTable(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - parametersTableView.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - parametersTableView.getHeight()) / 2;

        parametersTableView.setLocation(x, y);
        parametersTableView.setVisible(true);
    }

    /**
     * Exibe o dialogo - Código da Tabela
     *
     * @param element
     */
    public void tableCodeView(ClassElement element) {
        tableCodeView = new TableCodeView(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - tableCodeView.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - tableCodeView.getHeight()) / 2;

        tableCodeView.setLocation(x, y);
        tableCodeView.setVisible(true);
    }

    /**
     * Exibe o dialogo - Parâmetros dos Gatilhos
     *
     * @param element
     */
    public void parametersTriggersView(ClassElement element) {
        parametersTriggersView = new ParametersTrigger(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - parametersTriggersView.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - parametersTriggersView.getHeight()) / 2;

        parametersTriggersView.setLocation(x, y);
        parametersTriggersView.setVisible(true);
    }

    /**
     * Exibe o dialogo - Código dos Gatilhos
     *
     * @param element
     */
    public void triggersCodeView(ClassElement element) {
        triggersCodeView = new TriggersCodeView(this, element);

        // Centers inside the application Dialog
        int x = this.getX() + (this.getWidth() - triggersCodeView.getWidth()) / 2;
        int y = this.getY() + (this.getHeight() - triggersCodeView.getHeight()) / 2;

        triggersCodeView.setLocation(x, y);
        triggersCodeView.setVisible(true);
    }

    /**
     *
     * @param name
     * @param action
     * @return
     */
    public Action bind(String name, final Action action) {
        return bind(name, action, null);
    }

    /**
     *
     * @param name
     * @param action
     * @param iconUrl
     * @return
     */
    public Action bind(String name, final Action action, String iconUrl) {
        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
                GraphEditor.class.getResource(iconUrl)) : null) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.actionPerformed(new ActionEvent(GraphEditor.this, e.getID(), e.getActionCommand()));
                    }
                };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

    /**
     * Fecha a janela
     */
    public void exit() {

        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBackGround = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPaneActivated = new javax.swing.JPanel();
        jDesktopPaneWorkArea = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelBackGround.setLayout(new java.awt.BorderLayout());

        jPanelSouth.setPreferredSize(new java.awt.Dimension(400, 15));
        jPanelSouth.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanelSouth.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanelBackGround.add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        jPanelCenter.setLayout(new java.awt.CardLayout());

        jPaneActivated.setLayout(new java.awt.BorderLayout());

        jDesktopPaneWorkArea.setBackground(new java.awt.Color(255, 255, 255));
        jDesktopPaneWorkArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout jDesktopPaneWorkAreaLayout = new javax.swing.GroupLayout(jDesktopPaneWorkArea);
        jDesktopPaneWorkArea.setLayout(jDesktopPaneWorkAreaLayout);
        jDesktopPaneWorkAreaLayout.setHorizontalGroup(
            jDesktopPaneWorkAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1198, Short.MAX_VALUE)
        );
        jDesktopPaneWorkAreaLayout.setVerticalGroup(
            jDesktopPaneWorkAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 783, Short.MAX_VALUE)
        );

        jPaneActivated.add(jDesktopPaneWorkArea, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPaneActivated, "card1");

        jPanelBackGround.add(jPanelCenter, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelBackGround, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(1218, 847));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane jDesktopPaneWorkArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPaneActivated;
    private javax.swing.JPanel jPanelBackGround;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelSouth;
    // End of variables declaration//GEN-END:variables

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public javax.swing.JDesktopPane getjDesktopPaneWorkArea() {
        return jDesktopPaneWorkArea;
    }

    public void setjDesktopPaneWorkArea(javax.swing.JDesktopPane jDesktopPaneWorkArea) {
        this.jDesktopPaneWorkArea = jDesktopPaneWorkArea;
    }

    public DiagramOfClassModel getDiagramOfClassModel() {
        return diagramOfClassModel;
    }

    public void setDiagramOfClassModel(DiagramOfClassModel diagramOfClassModel) {
        this.diagramOfClassModel = diagramOfClassModel;
    }

    public ClassEditor getClassEditor() {
        return classEditor;
    }

    public void setClassEditor(ClassEditor classEditor) {
        this.classEditor = classEditor;
    }

    public StateEditor getStateEditor() {
        return stateEditor;
    }

    public void setStateEditor(StateEditor stateEditor) {
        this.stateEditor = stateEditor;
    }

    public ArrayList<Operator> getOperators() {
        return operators;
    }

    public void setOperators(ArrayList<Operator> operators) {
        this.operators = operators;
    }

    public ArrayList<RelationTypeUMLForSQL> getRelationShipTypes() {
        return relationShipTypes;
    }

    public void setRelationShipTypes(ArrayList<RelationTypeUMLForSQL> relationShipTypes) {
        this.relationShipTypes = relationShipTypes;
    }

    public CustomMenuBar getMenuBarCustom() {
        return menuBarCustom;
    }

    public void setMenuBarCustom(CustomMenuBar menuBarCustom) {
        this.menuBarCustom = menuBarCustom;
    }

    public mxGraphComponent getCurrentGraphComponent() {
        return currentGraphComponent;
    }

    public void setCurrentGraphComponent(mxGraphComponent currentGraphComponent) {
        this.currentGraphComponent = currentGraphComponent;
    }

}
