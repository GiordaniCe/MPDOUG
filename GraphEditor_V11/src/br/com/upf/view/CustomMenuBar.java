/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view;

import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.model.Actions.CreateDiagramOfStatesAction;
import br.com.upf.model.Actions.CreateTableCodeAction;
import br.com.upf.model.Actions.ExitAction;
import br.com.upf.model.Actions.NewClassEditorAction;
import br.com.upf.model.Actions.OpenAction;
import br.com.upf.model.Actions.SaveAction;
import br.com.upf.model.Actions.CreateDictionaryOfStatesAction;
import br.com.upf.model.Actions.CreateTriggersCodeAction;
import br.com.upf.model.Actions.ViewDiagramOfClassAction;
import br.com.upf.model.Actions.ViewDiagramOfStatesAction;
import br.com.upf.model.Actions.ViewTableCodeAction;
import br.com.upf.model.Actions.DiagramOfStatesValidationAction;
import br.com.upf.model.Actions.DictionaryOfStatesValidationAction;
import br.com.upf.model.Actions.ViewTriggersCodeAction;
import br.com.upf.view.internalframe.ClassEditor;
import br.com.upf.view.internalframe.StateEditor;
import com.mxgraph.util.mxResources;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class CustomMenuBar extends JMenuBar {

    private static final long serialVersionUID = -1867292049962118942L;

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     * Menu Arquivo
     */
    protected JMenu menuFile;

    protected JMenuItem menuItemNew;

    protected JMenuItem menuItemOpenFile;

    protected JMenuItem menuItemSave;

    protected JMenuItem menuItemSaveAs;

    protected JMenuItem menuItemExit;

    /**
     * Menu Visualizar
     */
    protected JMenu menuView;

    protected JMenuItem menuItemViewTable;

    protected JMenuItem menuItemViewTriggers;

    protected JMenuItem menuItemViewDictionaryOfStates;

    protected JMenuItem menuItemViewDiagramOfStates;

    protected JMenuItem menuItemViewDiagramOfClass;

    /**
     * Menu Ferramentas
     */
    protected JMenu menuTools;

    protected JMenu subMenuCreate;

    protected JMenuItem menuItemCreateDiagramOfStates;

    protected JMenuItem menuItemCreateDictionaryOfStates;

    protected JMenuItem menuItemCreateTable;

    protected JMenuItem menuItemCreateTriggers;

    protected JMenu subMenuValidate;

    protected JMenuItem menuItemValidateDiagramOfStates;

    protected JMenuItem menuItemValidateDictionaryOfStates;

    /**
     *
     */
    protected GraphEditor graphEditor;

    /**
     *
     */
    protected ClassEditor classEditor;

    /**
     *
     */
    protected StateEditor stateEditor;

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
    protected JDesktopPane workArea;

    /**
     * Construtor Padrão
     *
     * @param editor
     */
    public CustomMenuBar(GraphEditor editor) {
        this.graphEditor = editor;
        this.workArea = editor.getjDesktopPaneWorkArea();

        loadMenuFile();
        loadMenuView();
        loadMenuTools();

        add(menuFile);
        add(menuView);
        add(menuTools);
    }

    /**
     * Carrega Menu Arquivo
     */
    private void loadMenuFile() {
        menuFile = new JMenu(mxResources.get("file"));

        menuItemNew = new JMenuItem(graphEditor.bind(mxResources.get("new"), new NewClassEditorAction(), IMAGE_PATH + "new.gif"));

        menuItemOpenFile = new JMenuItem(graphEditor.bind(mxResources.get("openFile"), new OpenAction(), IMAGE_PATH + "open.gif"));

        menuItemSave = new JMenuItem(graphEditor.bind(mxResources.get("save"), new SaveAction(false), IMAGE_PATH + "save.gif"));

        menuItemSaveAs = new JMenuItem(graphEditor.bind(mxResources.get("saveAs"), new SaveAction(true), IMAGE_PATH + "save.gif"));

        menuItemExit = new JMenuItem(graphEditor.bind(mxResources.get("exit"), new ExitAction()));

        menuFile.add(menuItemNew);
        menuFile.add(menuItemOpenFile);
        menuFile.addSeparator();
        menuFile.add(menuItemSave);
        menuFile.add(menuItemSaveAs);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);

    }

    /**
     * Carrega Menu Visualizar
     */
    private void loadMenuView() {
        menuView = new JMenu(mxResources.get("view"));

        menuItemViewTable = new JMenuItem(graphEditor.bind(mxResources.get("table"), new ViewTableCodeAction()));

        menuItemViewTriggers = new JMenuItem(graphEditor.bind(mxResources.get("triggers"), new ViewTriggersCodeAction()));

        menuItemViewDiagramOfStates = new JMenuItem(graphEditor.bind(mxResources.get("diagramOfStates"), new ViewDiagramOfStatesAction()));

        menuItemViewDiagramOfClass = new JMenuItem(graphEditor.bind(mxResources.get("diagramOfClass"), new ViewDiagramOfClassAction()));

        menuItemViewDictionaryOfStates = new JMenuItem(graphEditor.bind(mxResources.get("stateDictionary"), new CreateDictionaryOfStatesAction()));

        menuView.add(menuItemViewTable);
        menuView.add(menuItemViewTriggers);
        menuView.addSeparator();
        menuView.add(menuItemViewDictionaryOfStates);
        menuView.addSeparator();
        menuView.add(menuItemViewDiagramOfStates);
        menuView.add(menuItemViewDiagramOfClass);
    }

    /**
     * Carrega Menu Ferramentas
     */
    private void loadMenuTools() {
        menuTools = new JMenu(mxResources.get("tools"));

        subMenuCreate = new JMenu(mxResources.get("create"));

        menuItemCreateDiagramOfStates = new JMenuItem(graphEditor.bind(mxResources.get("diagramOfStates"), new CreateDiagramOfStatesAction()));

        menuItemCreateDictionaryOfStates = new JMenuItem(graphEditor.bind(mxResources.get("stateDictionary"), new CreateDictionaryOfStatesAction()));

        menuItemCreateTable = new JMenuItem(graphEditor.bind(mxResources.get("table"), new CreateTableCodeAction()));

        menuItemCreateTriggers = new JMenuItem(graphEditor.bind(mxResources.get("triggers"), new CreateTriggersCodeAction()));

        subMenuValidate = new JMenu(mxResources.get("validate"));

        menuItemValidateDiagramOfStates = new JMenuItem(graphEditor.bind(mxResources.get("stateDiagram"), new DiagramOfStatesValidationAction()));

        menuItemValidateDictionaryOfStates = new JMenuItem(graphEditor.bind(mxResources.get("stateDictionary"), new DictionaryOfStatesValidationAction()));

        // MENU CREATE
        subMenuCreate.add(menuItemCreateDiagramOfStates);
        subMenuCreate.add(menuItemCreateDictionaryOfStates);
        subMenuCreate.addSeparator();
        subMenuCreate.add(menuItemCreateTable);
        subMenuCreate.add(menuItemCreateTriggers);

        // MENU VALIDATE
        subMenuValidate.add(menuItemValidateDiagramOfStates);
        subMenuValidate.add(menuItemValidateDictionaryOfStates);

        menuTools.add(subMenuCreate);
        menuTools.add(subMenuValidate);

    }

    /**
     * Ativa Menu Modo Projeto
     */
    public void enableProjectMenuBar() {

        this.classEditor = null;
        this.stateEditor = null;

        // ----- Menu Arquivo
        menuFile.setVisible(true);
        menuFile.setEnabled(true);
        menuItemNew.setEnabled(true);
        menuItemOpenFile.setEnabled(true);
        menuItemSave.setEnabled(false);
        menuItemSaveAs.setEnabled(false);
        menuItemExit.setEnabled(true);

        // ------ Menu Visualizar
        menuView.setEnabled(false);
        menuItemViewTable.setEnabled(false);
        menuItemViewTriggers.setEnabled(false);
        menuItemViewDiagramOfStates.setEnabled(false);
        menuItemViewDiagramOfClass.setEnabled(false);

        // ----- Menu Ferramentas
        menuTools.setVisible(true);
        menuTools.setEnabled(false);
        menuItemCreateDiagramOfStates.setEnabled(false);
        menuItemCreateTable.setEnabled(false);
        menuItemCreateTriggers.setEnabled(false);
        menuItemValidateDiagramOfStates.setEnabled(false);
    }

    /**
     * Ativa Menu Modo Diagrama de Classes
     */
    public void enableClassMenuBar() {
        this.classEditor = (ClassEditor) workArea.getSelectedFrame();
        updateContexClassEditor(classEditor.getSelectedClassElement());
    }

    /**
     * Ativa Menu Modo Diagrama de Estados
     */
    public void enableStateMenuBar() {
        this.stateEditor = (StateEditor) workArea.getSelectedFrame();
        updateContextStateEditor(stateEditor.getClassElement());
    }

    /**
     * Atualiza o Menu de acordo com o Contexto Atual do Diagrama de Classes
     *
     * @param element
     */
    public void updateContexClassEditor(ClassElement element) {

        // ----- Menu Arquivo
        menuFile.setEnabled(true);
        menuItemNew.setEnabled(false);
        menuItemOpenFile.setEnabled(true);
        menuItemSave.setEnabled(true);
        menuItemSaveAs.setEnabled(true);
        menuItemExit.setEnabled(true);

        // ------ Menu Visualizar
        menuView.setEnabled(true);
        menuItemViewTable.setEnabled(false);
        menuItemViewTriggers.setEnabled(false);
        menuItemViewDictionaryOfStates.setEnabled(false);
        menuItemViewDiagramOfStates.setEnabled(false);
        menuItemViewDiagramOfStates.setVisible(true);
        menuItemViewDiagramOfClass.setEnabled(false);
        menuItemViewDiagramOfClass.setVisible(false);

        // ----- Menu Ferramentas
        menuTools.setEnabled(true);
        subMenuCreate.setEnabled(true);
        subMenuValidate.setEnabled(true);
        menuItemCreateDiagramOfStates.setEnabled(false);
        menuItemCreateDictionaryOfStates.setEnabled(false);
        menuItemCreateTable.setEnabled(false);
        menuItemCreateTriggers.setEnabled(false);
        menuItemValidateDiagramOfStates.setEnabled(false);
        menuItemValidateDictionaryOfStates.setEnabled(false);

        if (element != null) {
            this.classElement = element;
            this.classModel = classElement.getClassModel();
            this.diagramOfStateModel = classModel.getDiagramOfStateModel();

            // VIEW
            if (classModel.isGeneratedTableCode()) {
                menuItemViewTable.setEnabled(true);
            }

            if (classModel.isGeneratedTriggersCode()) {
                menuItemViewTriggers.setEnabled(true);
            }
            if (classModel.isInitializedDiagramOfStateModel()) {
                menuItemViewDiagramOfStates.setEnabled(true);
            }
            if (classModel.isInitializedDictionaryOfStates()) {
                menuItemViewDictionaryOfStates.setEnabled(true);
            }

            // TOOLS
            menuItemCreateTable.setEnabled(true);
            menuItemCreateTriggers.setEnabled(true);
            menuItemCreateDiagramOfStates.setEnabled(true);
            menuItemCreateDictionaryOfStates.setEnabled(true);
            if (classModel.validate()) {
                if (diagramOfStateModel.isInitialized()) {
                    menuItemValidateDiagramOfStates.setEnabled(true);
                    menuItemCreateDiagramOfStates.setEnabled(false);
                }
                if (classModel.isInitializedDictionaryOfStates()) {
                    menuItemValidateDictionaryOfStates.setEnabled(true);
                    menuItemCreateDictionaryOfStates.setEnabled(false);
                }
            }
        }
    }

    /**
     * Atualiza o Menu de acordo com o Contexto Atual do Diagrama de Estados
     *
     * @param element
     */
    public void updateContextStateEditor(ClassElement element) {

        // ----- Menu Arquivo
        menuFile.setEnabled(true);
        menuItemNew.setEnabled(false);
        menuItemOpenFile.setEnabled(false);
        menuItemSave.setEnabled(false);
        menuItemSaveAs.setEnabled(false);
        menuItemExit.setEnabled(true);

        // ------ Menu Visualizar
        menuView.setEnabled(true);
        menuItemViewTable.setEnabled(false);
        menuItemViewTriggers.setEnabled(false);
        menuItemViewDictionaryOfStates.setEnabled(false);
        menuItemViewDiagramOfStates.setEnabled(false);
        menuItemViewDiagramOfStates.setVisible(false);
        menuItemViewDiagramOfClass.setEnabled(false);
        menuItemViewDiagramOfClass.setVisible(true);

        // ----- Menu Ferramentas
        menuTools.setEnabled(true);
        subMenuCreate.setEnabled(true);
        subMenuValidate.setEnabled(true);
        menuItemCreateDiagramOfStates.setEnabled(false);
        menuItemCreateDictionaryOfStates.setEnabled(false);
        menuItemCreateTable.setEnabled(false);
        menuItemCreateTriggers.setEnabled(false);
        menuItemValidateDiagramOfStates.setEnabled(false);
        menuItemValidateDictionaryOfStates.setEnabled(false);

        if (element != null) {
            this.classElement = element;
            this.classModel = classElement.getClassModel();
            this.diagramOfStateModel = classModel.getDiagramOfStateModel();

            // VIEW
            menuItemViewDiagramOfClass.setEnabled(true);
            if (classModel.isInitializedDictionaryOfStates()) {
                menuItemViewDictionaryOfStates.setEnabled(true);
            }

            if (classModel.validate()) {
                menuItemCreateTriggers.setEnabled(true);
                if (diagramOfStateModel.isInitialized()) {
                    menuItemValidateDiagramOfStates.setEnabled(true);
                } else {
                    menuItemCreateDiagramOfStates.setEnabled(true);
                }
                if (classModel.isInitializedDictionaryOfStates()) {
                    menuItemValidateDictionaryOfStates.setEnabled(true);
                } else {
                    menuItemCreateDictionaryOfStates.setEnabled(true);
                }
            }
        }
    }

    public ClassElement getClassElement() {
        return classElement;
    }

    public void setClassElement(ClassElement classElement) {
        this.classElement = classElement;
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }

}
