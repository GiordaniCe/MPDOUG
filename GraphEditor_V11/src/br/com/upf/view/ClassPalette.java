/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

/**
 *
 * @author GiordaniAntonio
 */
public class ClassPalette extends javax.swing.JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 7771113885935187066L;

    /**
     *
     */
    protected JLabel selectedEntry = null;

    /**
     *
     */
    protected mxEventSource eventSource = new mxEventSource(this);

    /**
     * Creates new form ClassPalette
     */
    public ClassPalette() {
        initComponents();

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                clearSelection();
            }
        }
        );

        setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                return true;
            }
        });
        
        setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
    }

    /**
     *
     */
    public void clearSelection() {
        setSelectionEntry(null, null);
    }

    /**
     *
     * @param entry
     * @param t
     */
    public void setSelectionEntry(JLabel entry, mxGraphTransferable t) {
        JLabel previous = selectedEntry;
        selectedEntry = entry;

        if (previous != null) {
            previous.setBorder(null);
            previous.setOpaque(false);
        }

        if (selectedEntry != null) {
            selectedEntry.setBorder(ShadowBorder.getSharedInstance());
            selectedEntry.setOpaque(true);
        }

        eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry",
                selectedEntry, "transferable", t, "previous", previous));
    }

    /**
     *
     * @param width
     */
    public void setPreferredWidth(int width) {
        int cols = Math.max(1, width / 55);
        setPreferredSize(new Dimension(width,
                (getComponentCount() * 55 / cols) + 30));
        revalidate();
    }

    /**
     *
     * @param name
     * @param icon
     * @param style
     * @param width
     * @param height
     * @param value
     */
    public void addEdgeTemplate(final String name, ImageIcon icon,
            String style, int width, int height, Object value) {
        mxGeometry geometry = new mxGeometry(0, 0, width, height);
        geometry.setTerminalPoint(new mxPoint(0, height), true);
        geometry.setTerminalPoint(new mxPoint(width, 0), false);
        geometry.setRelative(true);

        mxCell cell = new mxCell(value, geometry, style);
        cell.setEdge(true);

        addTemplate(name, icon, cell);
    }

    /**
     *
     * @param name
     * @param icon
     * @param style
     * @param width
     * @param height
     * @param value
     */
    public void addTemplate(final String name, ImageIcon icon, String style,
            int width, int height, Object value) {
        mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height),
                style);
        cell.setVertex(true);

        addTemplate(name, icon, cell);
    }

    /**
     *
     * @param name
     * @param icon
     * @param cell
     */
    public void addTemplate(final String name, ImageIcon icon, mxCell cell) {
        mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
        final mxGraphTransferable t = new mxGraphTransferable(
                new Object[]{cell}, bounds);

        // Scales the image if it's too large for the library
        // Ajusta a imagem se for muito grande para a biblioteca
        if (icon != null) {
            if (icon.getIconWidth() > 32 || icon.getIconHeight() > 32) {
                icon = new ImageIcon(icon.getImage().getScaledInstance(32, 32,
                        0));
            }
        }

        final JLabel entry = new JLabel(icon);
        entry.setPreferredSize(new Dimension(50, 50));
        entry.setBackground(ClassPalette.this.getBackground().brighter());
        entry.setFont(new Font(entry.getFont().getFamily(), 0, 10));

        entry.setVerticalTextPosition(JLabel.BOTTOM);
        entry.setHorizontalTextPosition(JLabel.CENTER);
        entry.setIconTextGap(0);

        entry.setToolTipText(name);
        entry.setText(name);

        entry.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                setSelectionEntry(entry, t);
            }

        });

        // Install the handler for dragging nodes into a graph
        // Instale o manipulador para arrastar os nós em um gráfico
        DragGestureListener dragGestureListener = new DragGestureListener() {
            /**
             *
             */
            public void dragGestureRecognized(DragGestureEvent e) {
                e
                        .startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),
                                t, null);

            }

        };

        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(entry,
                DnDConstants.ACTION_COPY, dragGestureListener);

        add(entry);
    }

    /**
     * @param eventName
     * @param listener
     * @see com.mxgraph.util.mxEventSource#addListener(java.lang.String,
     * com.mxgraph.util.mxEventSource.mxIEventListener)
     */
    public void addListener(String eventName, mxEventSource.mxIEventListener listener) {
        eventSource.addListener(eventName, listener);
    }

    /**
     * @return whether or not event are enabled for this palette
     * @see com.mxgraph.util.mxEventSource#isEventsEnabled()
     */
    public boolean isEventsEnabled() {
        return eventSource.isEventsEnabled();
    }

    /**
     * @param listener
     * @see
     * com.mxgraph.util.mxEventSource#removeListener(com.mxgraph.util.mxEventSource.mxIEventListener)
     */
    public void removeListener(mxEventSource.mxIEventListener listener) {
        eventSource.removeListener(listener);
    }

    /**
     * @param eventName
     * @param listener
     * @see com.mxgraph.util.mxEventSource#removeListener(java.lang.String,
     * com.mxgraph.util.mxEventSource.mxIEventListener)
     */
    public void removeListener(mxEventSource.mxIEventListener listener, String eventName) {
        eventSource.removeListener(listener, eventName);
    }

    /**
     * @param eventsEnabled
     * @see com.mxgraph.util.mxEventSource#setEventsEnabled(boolean)
     */
    public void setEventsEnabled(boolean eventsEnabled) {
        eventSource.setEventsEnabled(eventsEnabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(150, 150));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
