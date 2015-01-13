package br.com.upf.view;

import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.model.Actions.ClassModelAddAttributeAction;
import br.com.upf.model.Actions.ClassModelDeleteAttributeAction;
import br.com.upf.model.Actions.ClassModelEditAttributeAction;
import br.com.upf.model.Actions.ClassModelRenameAction;
import br.com.upf.view.internalframe.ClassEditor;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

public class ClassEditorPopupMenu extends JPopupMenu {

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     *
     */
    private static final long serialVersionUID = -3132749140550242191L;

    /**
     *
     */
    private ClassEditor classEditor;

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
    private ClassView classView;

    /**
     *
     */
    private JMenuItem menuItemClassModelRename;

    /**
     *
     */
    private JMenuItem menuItemClassModelAddAttribute;

    /**
     *
     */
    private JMenuItem menuItemClassModelEditAttribute;

    /**
     *
     */
    private JMenuItem menuItemClassModelDeleteAttribute;

    /**
     *
     */
    private JMenuItem menuItemCut;

    /**
     *
     */
    private JMenuItem menuItemCopy;

    /**
     *
     */
    private JMenuItem menuItemPaste;

    /**
     *
     */
    private JMenuItem menuItemDelete;

    /**
     *
     */
    private JMenuItem menuItemSelectVertices;

    /**
     *
     */
    private JMenuItem menuItemSelectEdges;

    /**
     *
     */
    private JMenuItem menuItemSelectAll;

    /**
     *
     * @param editor
     * @param element
     */
    public ClassEditorPopupMenu(ClassEditor editor, ClassElement element) {
        this.classEditor = editor;
        this.classElement = element;

        if (classElement != null) {
            this.classView = classElement.getClassView();
        }

        loadMenuView();

        add(menuItemClassModelAddAttribute);
        add(menuItemClassModelEditAttribute);
        addSeparator();
        add(menuItemClassModelDeleteAttribute);
        addSeparator();
        add(menuItemClassModelRename);
        addSeparator();
        add(menuItemCut);
        add(menuItemCopy);
        add(menuItemPaste);
        addSeparator();
        add(menuItemDelete);
//        addSeparator();
//        add(menuItemSelectVertices);
//        add(menuItemSelectEdges);
//        add(menuItemSelectAll);

        updateContext();

    }

    /**
     *
     */
    private void loadMenuView() {

        menuItemClassModelAddAttribute = new JMenuItem(classEditor.bind(mxResources.get("addAttribute"), new ClassModelAddAttributeAction(), IMAGE_PATH + "plus.png"));

        menuItemClassModelEditAttribute = new JMenuItem(classEditor.bind(mxResources.get("editAttribute"), new ClassModelEditAttributeAction(), IMAGE_PATH + "editar.gif"));

        menuItemClassModelDeleteAttribute = new JMenuItem(classEditor.bind(mxResources.get("deleteAttribute"), new ClassModelDeleteAttributeAction(), IMAGE_PATH + "delete.gif"));

        menuItemClassModelRename = new JMenuItem(classEditor.bind(mxResources.get("rename"), new ClassModelRenameAction()));

        menuItemCut = new JMenuItem(classEditor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), IMAGE_PATH + "cut.gif"));

        menuItemCopy = new JMenuItem(classEditor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), IMAGE_PATH + "copy.gif"));

        menuItemPaste = new JMenuItem(classEditor.bind(mxResources.get("paste"), TransferHandler.getPasteAction(), IMAGE_PATH + "paste.gif"));

        menuItemDelete = new JMenuItem(classEditor.bind(mxResources.get("delete"), mxGraphActions.getDeleteAction(), IMAGE_PATH + "delete.gif"));

        menuItemSelectVertices = new JMenuItem(classEditor.bind(mxResources.get("selectVertices"), mxGraphActions.getSelectVerticesAction()));

        menuItemSelectEdges = new JMenuItem(classEditor.bind(mxResources.get("selectEdges"), mxGraphActions.getSelectEdgesAction()));

        menuItemSelectAll = new JMenuItem(classEditor.bind(mxResources.get("selectAll"), mxGraphActions.getSelectAllAction()));
    }

    /**
     *
     */
    private void updateContext() {
        if (classElement != null) {
            if (classView.getSelectedAttribute() == null) {
                menuItemClassModelEditAttribute.setEnabled(false);
                menuItemClassModelDeleteAttribute.setEnabled(false);
            }
        } else {
            menuItemClassModelAddAttribute.setEnabled(false);
            menuItemClassModelEditAttribute.setEnabled(false);
            menuItemClassModelDeleteAttribute.setEnabled(false);
            menuItemClassModelEditAttribute.setEnabled(false);
            menuItemClassModelRename.setEnabled(false);
            menuItemCut.setEnabled(false);
            menuItemCopy.setEnabled(false);
            menuItemDelete.setEnabled(false);

        }
    }

}
