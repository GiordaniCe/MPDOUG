package br.com.upf.view;

import br.com.upf.beans.ClassModel;
import br.com.upf.view.internalframe.StateEditor;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

public class StateEditorPopupMenu extends JPopupMenu {

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     *
     */
    private static final long serialVersionUID = -3132749140550242191L;

    public StateEditorPopupMenu(StateEditor editor) {
        boolean selected = !editor.getStateGraphComponent().getGraph()
                .isSelectionEmpty();

        add(
                editor.bind(mxResources.get("delete"), mxGraphActions
                        .getDeleteAction(),
                        IMAGE_PATH + "delete.gif"))
                .setEnabled(selected);

        addSeparator();

        add(editor.bind(mxResources.get("selectVertices"), mxGraphActions
                .getSelectVerticesAction()));
        add(editor.bind(mxResources.get("selectEdges"), mxGraphActions
                .getSelectEdgesAction()));

        addSeparator();

        add(editor.bind(mxResources.get("selectAll"), mxGraphActions
                .getSelectAllAction()));
    }

}
