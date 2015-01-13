/*
 * $Id: JTableRenderer.java,v 1.1 2012/11/15 13:26:46 gaudenz Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package br.com.upf.view;

import br.com.upf.beans.DiagramOfClassModel;
import br.com.upf.view.internalframe.ClassEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.view.mxGraph;
import javax.swing.JOptionPane;

/**
 * @author Administrator
 *
 */
public class JTableRenderer extends JComponent {

    /**
     *
     */
    private static final long serialVersionUID = 2106746763664760745L;

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     *
     */
    protected static JTableRenderer dragSource = null;

    /**
     *
     */
    protected static int sourceRow = 0;

    /**
     *
     */
    protected Object cell;

    /**
     *
     */
    protected mxGraphComponent graphContainer;

    /**
     *
     */
    protected mxGraph graph;

    /**
     *
     */
    public JTable table;

    /**
     *
     */
    private ClassEditor classEditor;

    /**
     *
     */
    private DiagramOfClassModel diagramOfClassModel;

    /**
     *
     */
    private JPanel jPanelCenter;

    /**
     *
     */
    private JPanel jPanelSouth;

    /**
     *
     */
    private JPanel jPanelNorth;

    private JLabel jLabelName;

    private JLabel jlabelResize;

    /**
     * Construtor da classe
     *
     * @param cell
     * @param graphContainer
     * @param classEditor
     * @param diagramModel
     */
    @SuppressWarnings("serial")
    public JTableRenderer(final Object cell,
            final mxGraphComponent graphContainer, ClassEditor classEditor, DiagramOfClassModel diagramModel) {
        // Atributos recebidos pela chamada ao método são atribuidos à variáveis da classe
        this.cell = cell;
        this.graphContainer = graphContainer;
        this.graph = graphContainer.getGraph();
        this.classEditor = classEditor;
        this.diagramOfClassModel = diagramModel;

        // Define layout 
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(ShadowBorder
                .getSharedInstance(), BorderFactory
                .createBevelBorder(BevelBorder.RAISED)));

        // Define a barra de título
        jPanelNorth = new JPanel();
        jPanelNorth.setBackground(Color.PINK); // new Color(149, 173, 239)
        jPanelNorth.setOpaque(true);
        jPanelNorth.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        jPanelNorth.setLayout(new BorderLayout());

        // Configura o texto ao centro da barra de titulo
        jLabelName = new JLabel("Nova Classe"); //String.valueOf(graph.getLabel(cell))
        jLabelName.setForeground(Color.WHITE);
        jLabelName.setFont(jPanelNorth.getFont().deriveFont(Font.BOLD, 11));
        jLabelName.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));
        jPanelNorth.add(jLabelName, BorderLayout.CENTER);
        add(jPanelNorth, BorderLayout.NORTH);

        jPanelCenter = new JPanel(new BorderLayout());
        add(jPanelCenter, BorderLayout.CENTER);

        // Configura um Jlabel para redimensionar o componente
        jlabelResize = new JLabel(new ImageIcon(JTableRenderer.class
                .getResource(IMAGE_PATH + "resize.gif")));
        jlabelResize.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
        jlabelResize.setVisible(true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(jlabelResize, BorderLayout.EAST);
        // Adiciona ao componente
        add(panel, BorderLayout.SOUTH);

        ResizeHandler resizeHandler = new ResizeHandler();
        jlabelResize.addMouseListener(resizeHandler);
        jlabelResize.addMouseMotionListener(resizeHandler);

        // Define a dimensão mínima
        setMinimumSize(new Dimension(100, 100));
    }

    /**
     * Implements an event redirector for the specified handle index, where 0 is
     * the top right, and 1-7 are the top center, rop right, middle left, middle
     * right, bottom left, bottom center and bottom right, respectively. Default
     * index is 7 (bottom right).
     */
    public class ResizeHandler implements MouseListener, MouseMotionListener {

        protected int index;

        public ResizeHandler() {
            this(7);
        }

        public ResizeHandler(int index) {
            this.index = index;
        }

        public void mouseClicked(MouseEvent e) {
            // ignore
        }

        public void mouseEntered(MouseEvent e) {
            // ignore
        }

        public void mouseExited(MouseEvent e) {
            // ignore
        }

        public void mousePressed(MouseEvent e) {
            // Selects to create a handler for resizing
            if (!graph.isCellSelected(cell)) {
                graphContainer.selectCellForEvent(cell, e);
            }

            // Initiates a resize event in the handler
            mxCellHandler handler = graphContainer.getSelectionCellsHandler().getHandler(
                    cell);

            if (handler != null) {
                // Starts the resize at index 7 (bottom right)
                handler.start(SwingUtilities.convertMouseEvent((Component) e
                        .getSource(), e, graphContainer.getGraphControl()),
                        index);
                e.consume();
            }
        }

        public void mouseReleased(MouseEvent e) {
            graphContainer.getGraphControl().dispatchEvent(
                    SwingUtilities.convertMouseEvent((Component) e.getSource(),
                            e, graphContainer.getGraphControl()));
        }

        public void mouseDragged(MouseEvent e) {
            graphContainer.getGraphControl().dispatchEvent(
                    SwingUtilities.convertMouseEvent((Component) e.getSource(),
                            e, graphContainer.getGraphControl()));
        }

        public void mouseMoved(MouseEvent e) {
            // ignore
        }
    }

    /**
     * Método para criar um vertice JTableRendered
     */
    public static JTableRenderer getVertex(Component component) {
        while (component != null) {
            if (component instanceof JTableRenderer) {
                return (JTableRenderer) component;
            }
            component = component.getParent();
        }

        return null;
    }

}
