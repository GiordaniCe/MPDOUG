/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.jgraph.custom;

import br.com.upf.beans.BasicState;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.beans.Transition;
import br.com.upf.view.GraphEditor;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Point;
import java.util.EventObject;
import org.w3c.dom.Document;

/**
 *
 * @author GiordaniAntonio
 */
public class StateGraphComponent extends mxGraphComponent {

    private static final long serialVersionUID = 1768494718756327474L;

    private GraphEditor graphEditor;

    private DiagramOfStateModel diagramOfStateModel;

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/resources/";

    public StateGraphComponent(GraphEditor editor, mxGraph graph, DiagramOfStateModel model) {
        super(graph);
        this.diagramOfStateModel = model;
        this.graphEditor = editor;

        setPageVisible(false);
        setGridVisible(true);
        setToolTips(true);
        setEnterStopsCellEditing(false);

        graph.setCellsCloneable(false);
        graph.setCellsResizable(true);
        graph.setMultigraph(false);
        graph.setAllowDanglingEdges(false);
        graph.setSplitEnabled(false);

        // Loads the defalt stylesheet from an external file
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.loadDocument(StateGraphComponent.class.getResource(
                IMAGE_PATH + "default-style.xml").toString());
        codec.decode(doc.getDocumentElement(), graph.getStylesheet());

        // Sets the background to white
        getViewport().setOpaque(false);
        getViewport().setBackground(Color.white);
        setTransferHandler(new mxGraphTransferHandler());

    }

    @Override
    public void startEditingAtCell(Object cell, EventObject evt) {
        if (cell == null) {
            cell = graph.getSelectionCell();

            if (cell != null && !graph.isCellEditable(cell)) {
                cell = null;
            }
        }

        if (cell != null) {
            eventSource.fireEvent(new mxEventObject(mxEvent.START_EDITING,
                    "cell", cell, "event", evt));
            cellEditor.startEditing(cell, evt);
        }
    }

    @Override
    public Object labelChanged(Object cell, Object value, EventObject evt) {
        mxIGraphModel model = graph.getModel();

        // Verificação - Instância de mxCell
        if (cell instanceof mxCell) {
            mxCell c = (mxCell) cell;

            // Verificação - Vértice
            if (c.isVertex()) {

                // Verificação - Instância de BasicState
                if (c.getValue() instanceof BasicState) {
                    BasicState e = (BasicState) c.getValue();
                    String newName = (String) value;

                    // O NOVO NOME É DIFERENTE DO ANTIGO?
                    if (!(e.getName().equalsIgnoreCase(newName))) {
                        // NOME ESTA DISPONIVEL?
                        if (diagramOfStateModel.availabilityStateName(newName)) {
                            e.setName((String) value);
                            model.beginUpdate();
                            try {
                                graph.cellLabelChanged(cell, e, graph.isAutoSizeCell(cell));
                                eventSource.fireEvent(new mxEventObject(mxEvent.LABEL_CHANGED,
                                        "cell", cell, "value", value, "event", evt));
                            } finally {
                                model.endUpdate();
                            }
                        } else {
//                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("ThereIsAlreadyAStateWithThisName"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    // Fim Verificação - Instância de BasicState
                } else {
                    System.out.println("Não é Instância de BasicState!");
                }

            } // Fim Verificação - Vértice
            if (c.isEdge()) {

                // Verificação - Instância Transition
                if (c.getValue() instanceof Transition) {
                    Transition t = (Transition) c.getValue();
                    String newName = (String) value;

                    // O NOVO NOME É DIFERENTE DO ANTIGO?
                    if (!(t.getName().equalsIgnoreCase(newName))) {
                        t.setName((String) value);
                        model.beginUpdate();
                        try {
                            graph.cellLabelChanged(cell, t, graph.isAutoSizeCell(cell));
                            eventSource.fireEvent(new mxEventObject(mxEvent.LABEL_CHANGED,
                                    "cell", cell, "value", value, "event", evt));
                        } finally {
                            model.endUpdate();
                        }
                    }
                } else {
                    System.out.println(c.getValue().getClass());
                    System.out.println("Não é Instância de Transicao!");
                }
            }
        } // Fim Verificação - Instância de mxCell
        return cell;
    }

    @Override
    public Object[] importCells(Object[] cells, double dx, double dy,
            Object target, Point location) {
        if (target == null && cells.length == 1 && location != null) {
            target = getCellAt(location.x, location.y);

            if (target instanceof mxICell && cells[0] instanceof mxICell) {
                mxICell targetCell = (mxICell) target;
                mxICell dropCell = (mxICell) cells[0];

                if (targetCell.isVertex() == dropCell.isVertex()
                        || targetCell.isEdge() == dropCell.isEdge()) {
                    mxIGraphModel model = graph.getModel();
                    model.setStyle(target, model.getStyle(cells[0]));
                    graph.setSelectionCell(target);

                    return null;
                }
            }
        }

        return super.importCells(cells, dx, dy, target, location);
    }
}
