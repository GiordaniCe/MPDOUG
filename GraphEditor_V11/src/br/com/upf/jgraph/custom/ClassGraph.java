/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.jgraph.custom;

import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.DiagramOfClassModel;
import br.com.upf.view.GraphEditor;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class ClassGraph extends mxGraph {

    /**
     *
     */
    private DiagramOfClassModel diagramOfClassModel;

    /**
     *
     */
    private ClassGraphComponent classGraphComponent;

    /**
     *
     */
    private GraphEditor graphEditor;

    /**
     *
     * @param editor
     * @param diagramModel
     */
    public ClassGraph(GraphEditor editor, DiagramOfClassModel diagramModel) {
        super();
        this.setCellsResizable(false);
        this.setCellsEditable(false);
        this.graphEditor = editor;
        this.diagramOfClassModel = diagramModel;

    }

    /**
     *
     * @param cell
     * @param collapse
     * @return
     */
    @Override
    public boolean isCellFoldable(Object cell, boolean collapse) {
        return model.isVertex(cell);
    }

    @Override
    public void cellsAdded(Object[] cells, Object parent, Integer index,
            Object source, Object target, boolean absolute, boolean constrain) {

        mxCell cell = null;

        ClassModel classModel;
        ClassElement classElement;

        // Verificação - Quantidade de Cells recebidos no Array
        if (cells.length == 1) {
            if (cells[0] instanceof mxCell) {

                // Carrega a Cell selecionada
                cell = (mxCell) cells[0];

                // Verificação - Vértice
                if (cell.isVertex()) {

                    // Verificação -  Instância de State
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();

                        classElement = new ClassElement(graphEditor, classModel);
                        diagramOfClassModel.addClassElement(classElement);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(classGraphComponent, cells.length);
        }

        cells[0] = cell;
        super.cellsAdded(cells, parent, index, source, target, absolute, constrain); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void cellsRemoved(Object[] cells) {

        int tam = cells.length;
        for (int i = 0; i < tam; i++) {
            if (cells[i] instanceof mxCell) {
                mxCell currentCell = (mxCell) cells[i];

                if (currentCell.isVertex()) {
                    if (currentCell.getValue() instanceof ClassModel) {
                        ClassModel classModel = (ClassModel) currentCell.getValue();
                        boolean result = diagramOfClassModel.removeClassElement(classModel);
                        if (result) {
                            //JOptionPane.showMessageDialog(null, "Classe '" + classModel.getNome() + "' removida com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Error removing class element!", "Alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        super.cellsRemoved(cells);
    }

    public DiagramOfClassModel getDiagramOfClassModel() {
        return diagramOfClassModel;
    }

    public void setDiagramOfClassModel(DiagramOfClassModel diagramOfClassModel) {
        this.diagramOfClassModel = diagramOfClassModel;
    }

    public ClassGraphComponent getClassGraphComponent() {
        return classGraphComponent;
    }

    public void setClassGraphComponent(ClassGraphComponent classGraphComponent) {
        this.classGraphComponent = classGraphComponent;
    }

    public GraphEditor getGraphEditor() {
        return graphEditor;
    }

    public void setGraphEditor(GraphEditor graphEditor) {
        this.graphEditor = graphEditor;
    }

}
