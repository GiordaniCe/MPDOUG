/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.jgraph.custom;

import java.text.NumberFormat;
import br.com.upf.beans.BasicState;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.beans.FinalState;
import br.com.upf.beans.InitialState;
import br.com.upf.beans.State;
import br.com.upf.beans.Transition;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxConnectionConstraint;
import com.mxgraph.view.mxGraph;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class StateGraph extends mxGraph {

    public static final NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * Holds the edge to be used as a template for inserting new edges.
     */
    protected Object edgeTemplate;

    /**
     *
     */
    private StateGraphComponent stateGraphComponent;

    /**
     *
     */
    private DiagramOfStateModel diagramOfStatesModel;

    /**
     *
     */
    private boolean containsStateGraphComponent;

    /**
     * Permite arrastar uma aresta mudando a origem ou destino da mesma. Padrão
     * false
     */
    private boolean editEdgeSourceAndTarget;

    public StateGraph(DiagramOfStateModel diagramModel) {
        this.diagramOfStatesModel = diagramModel;
        this.containsStateGraphComponent = false;
        editEdgeSourceAndTarget = false;
    }

    public void setEdgeTemplate(Object template) {
        edgeTemplate = template;
    }

    public boolean isEditEdgeSourceAndTarget() {
        return editEdgeSourceAndTarget;
    }

    public void setEditEdgeSourceAndTarget(boolean editEdgeSourceAndTarget) {
        this.editEdgeSourceAndTarget = editEdgeSourceAndTarget;
    }

    public StateGraphComponent getStateGraphComponent() {
        return stateGraphComponent;
    }

    public void setStateGraphComponent(StateGraphComponent stateGraphComponent) {
        this.stateGraphComponent = stateGraphComponent;
        this.containsStateGraphComponent = true;
    }

    public DiagramOfStateModel getDiagramOfStatesModel() {
        return diagramOfStatesModel;
    }

    public void setDiagramOfStatesModel(DiagramOfStateModel diagramOfStatesModel) {
        this.diagramOfStatesModel = diagramOfStatesModel;
    }

    public boolean isContainsStateGraphComponent() {
        return containsStateGraphComponent;
    }

    public void setContainsStateGraphComponent(boolean containsStateGraphComponent) {
        this.containsStateGraphComponent = containsStateGraphComponent;
    }

    @Override
    public String getToolTipForCell(Object cell) {
        String tip = "<html>";
        mxGeometry geo = getModel().getGeometry(cell);
        mxCellState state = getView().getState(cell);

        if (getModel().isEdge(cell)) {
            tip += "points={";

            if (geo != null) {
                List<mxPoint> points = geo.getPoints();

                if (points != null) {
                    Iterator<mxPoint> it = points.iterator();

                    while (it.hasNext()) {
                        mxPoint point = it.next();
                        tip += "[x=" + numberFormat.format(point.getX())
                                + ",y=" + numberFormat.format(point.getY())
                                + "],";
                    }

                    tip = tip.substring(0, tip.length() - 1);
                }
            }

            tip += "}<br>";
            tip += "absPoints={";

            if (state != null) {

                for (int i = 0; i < state.getAbsolutePointCount(); i++) {
                    mxPoint point = state.getAbsolutePoint(i);
                    tip += "[x=" + numberFormat.format(point.getX())
                            + ",y=" + numberFormat.format(point.getY())
                            + "],";
                }

                tip = tip.substring(0, tip.length() - 1);
            }

            tip += "}";
        } else {
            tip += "geo=[";

            if (geo != null) {
                tip += "x=" + numberFormat.format(geo.getX()) + ",y="
                        + numberFormat.format(geo.getY()) + ",width="
                        + numberFormat.format(geo.getWidth()) + ",height="
                        + numberFormat.format(geo.getHeight());
            }

            tip += "]<br>";
            tip += "state=[";

            if (state != null) {
                tip += "x=" + numberFormat.format(state.getX()) + ",y="
                        + numberFormat.format(state.getY()) + ",width="
                        + numberFormat.format(state.getWidth())
                        + ",height="
                        + numberFormat.format(state.getHeight());
            }

            tip += "]";
        }

        mxPoint trans = getView().getTranslate();

        tip += "<br>scale=" + numberFormat.format(getView().getScale())
                + ", translate=[x=" + numberFormat.format(trans.getX())
                + ",y=" + numberFormat.format(trans.getY()) + "]";
        tip += "</html>";

        return tip;
        //return "Giordani";

    }

    @Override
    public Object createEdge(Object parent, String id, Object value,
            Object source, Object target, String style) {
        return super.createEdge(parent, id, new Transition(), source, target, style);
    }

    @Override
    public Object connectCell(Object edge, Object terminal, boolean source, mxConnectionConstraint constraint) {

        if (editEdgeSourceAndTarget) {
            mxCell cellEdge = (mxCell) edge;
            mxCell sourceOrigin = (mxCell) cellEdge.getSource();
            mxCell targetOrigin = (mxCell) cellEdge.getTarget();

            mxCell newCell = (mxCell) terminal;

            if (cellEdge.getValue() instanceof Transition) {
                Transition t = (Transition) cellEdge.getValue();
                if (source) {
                    // Modificou Origem
                    if (newCell.getValue() instanceof BasicState) {
                        BasicState bs = (BasicState) newCell.getValue();
                        t.setSource(bs);
                    }

                } else {
                    // Modificou Alvo
                    if (newCell.getValue() instanceof BasicState) {
                        BasicState bs = (BasicState) newCell.getValue();
                        t.setTarget(bs);
                    }
                }
            }
            return super.connectCell(edge, terminal, source, constraint); //To change body of generated methods, choose Tools | Templates.
        } else {
            stateGraphComponent.refresh();
            return null;
        }
    }

    @Override
    public void cellsAdded(Object[] cells, Object parent, Integer index,
            Object source, Object target, boolean absolute, boolean constrain) {
        mxCell cell = null;
        mxCell cellSource;
        mxCell cellTarget;

        State state;
        InitialState initialState;
        FinalState finalState;

        BasicState stateSource;
        BasicState stateTarget;

        Transition transition;

        boolean valid = false;
        // Verificação - Quantidade de Cells recebidos no Array
        if (cells.length == 1) {
            if (cells[0] instanceof mxCell) {

                // Carrega a Cell selecionada
                cell = (mxCell) cells[0];

                // Verificação - Vértice
                if (cell.isVertex()) {

                    // Verificação -  Instância de State
                    if (cell.getValue() instanceof State) {
                        state = (State) cell.getValue();

                        if (diagramOfStatesModel.addState(state)) {
                            valid = true;
                        } else {
                            JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("errorWhenAddingTheStateModel"),
                                    mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            valid = false;
                        }
                    } else {
                        // ADICIONA ESTADO INICIAL
                        if (cell.getValue() instanceof InitialState) {
                            initialState = (InitialState) cell.getValue();
                            // VERIFICA SE O MODELO JÁ POSSUI UM ESTADO INICIAL
                            if (diagramOfStatesModel.isContainsInitialState()) {
                                JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("TheStateDiagramAllowsOnlyOneInitialState"),
                                        mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            } else {
                                // ADICIONA O ESTADO INICIAL AO MODELO
                                if (diagramOfStatesModel.addInitialState(initialState)) {
                                    valid = true;
                                } else {
                                    JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("errorWhenAddingTheStateModel"),
                                            mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                    valid = false;
                                }
                            }
                        } else {
                            // ADICIONA ESTADO FINAL
                            if (cell.getValue() instanceof FinalState) {
                                valid = true;
                                finalState = (FinalState) cell.getValue();

                                if (diagramOfStatesModel.addFinalState(finalState)) {
                                    valid = true;
                                } else {
                                    JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("errorWhenAddingTheStateModel"),
                                            mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                    valid = false;
                                }
                            }
                        }
                    }
                } else {

                    // Verificação - Origem e Alvo não nulos
                    if (source != null && target != null) {
                        if (cell.getValue() instanceof Transition) {
                            transition = (Transition) cell.getValue();

                            // Verificação - Instância de mxCell
                            if (source instanceof mxCell && target instanceof mxCell) {
                                cellSource = (mxCell) source;
                                cellTarget = (mxCell) target;
                                valid = true;

                                // INICIO DA VERIFICAÇÃO
                                // Verificação - Transição com origem em estado final
                                if (cellSource.getValue() instanceof FinalState) {
                                    JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("InvalidTransition"),
                                            mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                    stateGraphComponent.refresh();
                                    valid = false;
                                } else {
                                    // Verificação - Transição com destino em estado inicial
                                    if (cellTarget.getValue() instanceof InitialState) {
                                        JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("InvalidTransition"),
                                                mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                        stateGraphComponent.refresh();
                                        valid = false;
                                    } else {
                                        // Verificação - Transição do estado inicial ao final
                                        if ((cellSource.getValue() instanceof InitialState) && (cellTarget.getValue() instanceof FinalState)) {
                                            JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("InvalidTransition"),
                                                    mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                            stateGraphComponent.refresh();
                                            valid = false;
                                        } else {
                                            if ((cellSource.getValue() instanceof InitialState)) {
                                                if (diagramOfStatesModel.isContainsInitialTransition()) {
                                                    JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("InvalidTransition"),
                                                            mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                                    stateGraphComponent.refresh();
                                                    valid = false;
                                                }
                                            }
                                        }

                                    }
                                }

                                if (valid) {
                                    // Define Origem e Alvo da transição
                                    transition.setSource((BasicState) cellSource.getValue());
                                    transition.setTarget((BasicState) cellTarget.getValue());
                                    transition.generateClassification();

                                    // Define o nome padrão para a transição
                                    transition.setName("Nova transição");

                                    // Adiciona a transição ao modelo
                                    if (!diagramOfStatesModel.addTransition(transition)) {
                                        JOptionPane.showMessageDialog(stateGraphComponent, mxResources.get("errorWhenAddingTransitionToModel"),
                                                mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                        stateGraphComponent.refresh();
                                        valid = false;
                                    }
                                }
                            }
                        }
                    } else {
                        // Não permite arestas desconectadas
                        valid = false;
                        stateGraphComponent.refresh();
                    }
                }
            }
        }
        if (valid) {
            cells[0] = cell;
            super.cellsAdded(cells, parent, index, source, target, absolute, constrain); //To change body of generated methods, choose Tools | Templates.
        } else {
            //System.out.println("Operação Inválida!");
        }
    }

    @Override
    public void cellsRemoved(Object[] cells) {

        int tam = cells.length;
        for (int i = 0; i < tam; i++) {
            if (cells[i] instanceof mxCell) {
                mxCell currentCell = (mxCell) cells[i];

                if (currentCell.isVertex()) {
                    if (currentCell.getValue() instanceof State) {
                        State state = (State) currentCell.getValue();
                        boolean result = diagramOfStatesModel.removeState(state);
                        if (result) {
                            //JOptionPane.showMessageDialog(graphComponent, "Estado '" + state.getNome() + "' removido com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(stateGraphComponent,
                                    "Error removing state element!", "Alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (currentCell.getValue() instanceof InitialState) {
                        InitialState initialState = (InitialState) currentCell.getValue();
                        boolean result = diagramOfStatesModel.removeInitalState();
                        if (result) {
                            //JOptionPane.showMessageDialog(graphComponent, "Estado Inicial '" + initialState.getNome() + "' removido com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(stateGraphComponent,
                                    "Error removing state initial element!", "Alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (currentCell.getValue() instanceof FinalState) {
                        FinalState finalState = (FinalState) currentCell.getValue();
                        boolean result = diagramOfStatesModel.removeFinalState(finalState);
                        if (result) {
                            //JOptionPane.showMessageDialog(graphComponent, "Estado Final '" + finalState.getNome() + "' removido com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(stateGraphComponent,
                                    "Error removing state final element!", "Alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    if (currentCell.getValue() instanceof Transition) {
                        Transition transition = (Transition) currentCell.getValue();
                        boolean result = diagramOfStatesModel.removeTransition(transition);
                        if (result) {
                            //JOptionPane.showMessageDialog(graphComponent, "Transição '" + transition.getNome() + "' removido com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(stateGraphComponent,
                                    "Error removing transition element!", "Alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    // No Modelo do Jgraph Cada Célula é representada por três objetos,
                    // como na classe DiagramsOfStates é representado por apenas um objeto, 
                    // se fez necessário essa adaptação para funcionar a funcionalidade de deletar de múltiplas arestas.
                    tam = tam - 2;
                }
            }
        }

        super.cellsRemoved(cells);
    }

}
