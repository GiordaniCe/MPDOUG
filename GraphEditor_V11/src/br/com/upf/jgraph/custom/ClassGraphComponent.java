/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.jgraph.custom;

import br.com.upf.beans.DiagramOfClassModel;
import br.com.upf.view.GraphEditor;
import br.com.upf.view.ClassView;
import br.com.upf.view.internalframe.ClassEditor;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 *
 * @author GiordaniAntonio
 */
public class ClassGraphComponent extends mxGraphComponent {

    private static final long serialVersionUID = -8656656642208103024L;

    private GraphEditor graphEditor;

    private ClassEditor classEditor;

    private DiagramOfClassModel diagramOfClassModel;

    /**
     * Construtor
     *
     * @param editor
     * @param classEditor
     * @param diagramModel
     * @param graph
     */
    public ClassGraphComponent(GraphEditor editor, ClassEditor classEditor, DiagramOfClassModel diagramModel, mxGraph graph) {
        super(graph);
        this.graphEditor = editor;
        this.classEditor = classEditor;
        this.diagramOfClassModel = diagramModel;

        mxGraphView graphView = new mxGraphView(graph) {

            /**
             *
             */
            public void updateFloatingTerminalPoint(mxCellState edge,
                    mxCellState start, mxCellState end, boolean isSource) {
                int col = getColumn(edge, isSource);

                if (col >= 0) {
                    double y = getColumnLocation(edge, start, col);
                    boolean left = start.getX() > end.getX();

                    if (isSource) {
                        double diff = Math.abs(start.getCenterX()
                                - end.getCenterX())
                                - start.getWidth() / 2 - end.getWidth() / 2;

                        if (diff < 40) {
                            left = !left;
                        }
                    }

                    double x = (left) ? start.getX() : start.getX()
                            + start.getWidth();
                    double x2 = (left) ? start.getX() - 20 : start.getX()
                            + start.getWidth() + 20;

                    int index2 = (isSource) ? 1
                            : edge.getAbsolutePointCount() - 1;
                    edge.getAbsolutePoints().add(index2, new mxPoint(x2, y));

                    int index = (isSource) ? 0
                            : edge.getAbsolutePointCount() - 1;
                    edge.setAbsolutePoint(index, new mxPoint(x, y));
                } else {
                    super.updateFloatingTerminalPoint(edge, start, end,
                            isSource);
                }
            }
        };

        this.setGridVisible(true);
        this.setConnectable(false);
        this.getViewport().setOpaque(true);
        this.getViewport().setBackground(new Color(255, 255, 255)); // Azul Claro

        this.getGraph().setCellsResizable(false);

        this.setConnectable(false);
        this.getGraphHandler().setCloneEnabled(false);
        this.getGraphHandler().setImagePreview(false);

    }

    /**
     * Disables folding icons.
     *
     * @param state
     * @return
     */
    @Override
    public ImageIcon getFoldingIcon(mxCellState state) {
        return null;
    }

    /**
     *
     * @param state
     * @param isSource
     * @return
     */
    public int getColumn(mxCellState state, boolean isSource) {
        if (state != null) {
            if (isSource) {
                return mxUtils.getInt(state.getStyle(), "sourceRow", -1);
            } else {
                return mxUtils.getInt(state.getStyle(), "targetRow", -1);
            }
        }

        return -1;
    }

    /**
     *
     * @param edge
     * @param terminal
     * @param column
     * @return
     */
    public int getColumnLocation(mxCellState edge, mxCellState terminal,
            int column) {
        Component[] c = components.get(terminal.getCell());
        int y = 0;

        if (c != null) {
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof ClassView) {
                    ClassView vertex = (ClassView) c[i];

                    JTable table = vertex.table;
                    JViewport viewport = (JViewport) table.getParent();
                    double dy = -viewport.getViewPosition().getY();
                    y = (int) Math.max(terminal.getY() + 22, terminal.getY()
                            + Math.min(terminal.getHeight() - 20, 30 + dy
                                    + column * 16));
                }
            }
        }

        return y;
    }

    /**
     *
     * @param state
     * @return
     */
    @Override
    public Component[] createComponents(mxCellState state) {
        if (getGraph().getModel().isVertex(state.getCell())) {
            return new Component[]{new ClassView(state.getCell(), this, classEditor, diagramOfClassModel)};
        }

        return null;
    }

    public GraphEditor getGraphEditor() {
        return graphEditor;
    }

    public void setGraphEditor(GraphEditor graphEditor) {
        this.graphEditor = graphEditor;
    }

}
