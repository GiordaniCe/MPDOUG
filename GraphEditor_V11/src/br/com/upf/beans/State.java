/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import br.com.upf.util.Operator;
import br.com.upf.view.GraphEditor;
import com.mxgraph.util.mxResources;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class State extends BasicState implements Serializable{

    private static final long serialVersionUID = 8837202049189155529L;

    /**
     * Contém as expressões que definem o estado quanto ao valor de seus
     * atributos.
     */
    private ArrayList<StandartExpression> standartExpressions;

    /**
     * Expressão Personalizada
     */
    private CustomExpression customExpression;

    /**
     * Contém os estados que iniciam uma transicao que termina no estado atual.
     */
    private ArrayList<State> nextStates;

    /**
     * Construtor Padrão
     */
    public State() {
        this("Novo Estado");
    }

    /**
     * Construtor
     *
     * @param nome
     */
    public State(String nome) {
        super(nome);
        this.standartExpressions = new ArrayList<StandartExpression>();
        this.customExpression = new CustomExpression(this);
        this.nextStates = new ArrayList<State>();
        this.nextStates.add(this);
    }

    private void updateState() {
        updateBasicState();
    }

    public boolean validate() {
        updateState();
        if (checkName) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<StandartExpression> getStandartExpressions() {
        return standartExpressions;
    }

    public void setStandartExpressions(ArrayList<StandartExpression> standartExpressions) {
        this.standartExpressions = standartExpressions;
    }

    public CustomExpression getCustomExpression() {
        return customExpression;
    }

    public void setCustomExpression(CustomExpression customExpression) {
        this.customExpression = customExpression;
    }

    public ArrayList<State> getNextStates() {
        return nextStates;
    }

    public void setNextStates(ArrayList<State> nextStates) {
        this.nextStates = nextStates;
    }

    /**
     * Valida uma nova expressão
     *
     * @param graphEditor
     * @param newExpression
     * @return
     */
    public boolean validateNewExpression(GraphEditor graphEditor, StandartExpression newExpression) {
        if (newExpression != null) {

            Attribute newAttribute = newExpression.getAttribute();
            Operator newOperator = newExpression.getOperator();
            String newValue = newExpression.getValue();

            StandartExpression expressionOne;
            Operator operatorExpressionOne;
            String valueExpressionOne;

            ArrayList<StandartExpression> expressionsContainsAttribute = new ArrayList<>();

            // Busca as expressões que contém o mesmo atributo da nova expressão
            for (int i = 0; i < standartExpressions.size(); i++) {
                StandartExpression currentExpression = standartExpressions.get(i);
                if (currentExpression.getAttribute().equals(newAttribute)) {
                    expressionsContainsAttribute.add(currentExpression);
                }
            }

            if (expressionsContainsAttribute.isEmpty()) {
                return true;
            } else {
                if (expressionsContainsAttribute.size() == 1) {
                    expressionOne = expressionsContainsAttribute.get(0);
                    operatorExpressionOne = expressionOne.getOperator();

                    if (operatorExpressionOne.isEquality()) {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("InvalidExpressionInTheContextOfTheState"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    } else {
                        if (newOperator.isComparative()) {
                            if (((operatorExpressionOne.getName().equalsIgnoreCase("MAIOR")
                                    || operatorExpressionOne.getName().equalsIgnoreCase("MAIOR E IGUAL"))
                                    && (newOperator.getName().equalsIgnoreCase("MENOR")
                                    || newOperator.getName().equalsIgnoreCase("MENOR E IGUAL")))
                                    || ((operatorExpressionOne.getName().equalsIgnoreCase("MENOR")
                                    || operatorExpressionOne.getName().equalsIgnoreCase("MENOR E IGUAL"))
                                    && (newOperator.getName().equalsIgnoreCase("MAIOR")
                                    || newOperator.getName().equalsIgnoreCase("MAIOR E IGUAL")))) {
                                return true;
                            } else {
                                JOptionPane.showMessageDialog(graphEditor, mxResources.get("InvalidExpressionInTheContextOfTheState"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                                return false;
                            }
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("InvalidExpressionInTheContextOfTheState"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }

                    }
                } else {
                    if (expressionsContainsAttribute.size() == 2) {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("InvalidExpressionInTheContextOfTheState"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(graphEditor, mxResources.get("expressionNull"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Valida a edição da expressão
     *
     * @param graphEditor
     * @param oldExpression
     * @param newExpression
     * @return
     */
    public boolean validateEditionExpression(GraphEditor graphEditor, StandartExpression oldExpression, StandartExpression newExpression) {
        if (newExpression != null && oldExpression != null) {
            //JOptionPane.showMessageDialog(graphEditor, "old: " + oldExpression + " New: " + newExpression);

            Attribute newAttribute = newExpression.getAttribute();
            Operator newOperator = newExpression.getOperator();
            Operator oldOperator = oldExpression.getOperator();
            ArrayList<StandartExpression> expressionsContainsAttribute = new ArrayList<>();

            // Busca as expressões que contém o mesmo atributo da nova expressão
            for (int i = 0; i < standartExpressions.size(); i++) {
                StandartExpression currentExpression = standartExpressions.get(i);

                if (currentExpression.getAttribute().equals(newAttribute)) {
                    expressionsContainsAttribute.add(currentExpression);
                }
            }

            // CONTÉM UMA EXPRESSÃO
            if (expressionsContainsAttribute.size() == 1) {
                return true;
            } else {
                // CONTÉM DUAS EXPRESSÕES
                if (newOperator.isEquality()) {
                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("EditingInvalidExpressionInTheContextOfTheState"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    return false;
                } else {
                    if (((oldExpression.getOperator().getName().equalsIgnoreCase("MAIOR")
                            || oldExpression.getOperator().getName().equalsIgnoreCase("MAIOR E IGUAL"))
                            && (newExpression.getOperator().getName().equalsIgnoreCase("MAIOR")
                            || newExpression.getOperator().getName().equalsIgnoreCase("MAIOR E IGUAL")))
                            || ((oldExpression.getOperator().getName().equalsIgnoreCase("MENOR")
                            || oldExpression.getOperator().getName().equalsIgnoreCase("MENOR E IGUAL"))
                            && (newExpression.getOperator().getName().equalsIgnoreCase("MENOR")
                            || newExpression.getOperator().getName().equalsIgnoreCase("MENOR E IGUAL")))) {
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("EditingInvalidExpressionInTheContextOfTheState"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(graphEditor, mxResources.get("expressionNull"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Adiciona Expressão
     *
     * @param expression
     * @return
     */
    public boolean addExpression(StandartExpression expression) {
        boolean result;
        result = standartExpressions.add(expression);
        if (result) {
            updateState();
        } else {
            updateState();
            result = false;
        }

        return result;
    }

    /**
     * Edita Expressão
     *
     * @param expression
     * @return
     */
    public boolean editExpression(StandartExpression expression) {
        int index = getIndexExpressions(expression);
        if (index >= 0) {
            standartExpressions.remove(index);
            standartExpressions.add(index, expression);
            updateState();
            return true;
        } else {
            updateState();
            return false;
        }
    }

    /**
     * Remove Expressão
     *
     * @param expression
     * @return
     */
    public boolean removeExpression(StandartExpression expression) {
        boolean result = standartExpressions.remove(expression);
        updateState();
        return result;
    }

    /**
     * Adiciona estado Anterior
     *
     * @param state
     * @return
     */
    public boolean addNextState(State state) {
        boolean containsState = false;
        boolean result;
        for (int i = 0; i < nextStates.size(); i++) {
            if (nextStates.get(i).equals(state)) {
                containsState = true;
            }
        }
        if (!containsState) {
            result = nextStates.add(state);
            updateState();
        } else {
            result = false;
            updateState();
        }
        return result;
    }

    /**
     * Edita estado Anterior
     *
     * @param state
     * @return
     */
    public boolean editNextState(State state) {
        int index = getIndexNextStates(state);
        if (index >= 0) {
            nextStates.remove(index);
            nextStates.add(index, state);
            updateState();
            return true;
        } else {
            updateState();
            return false;
        }
    }

    /**
     * Remove estado Anterior
     *
     * @param state
     * @return
     */
    public boolean removeNextState(State state) {
        boolean result;
        result = nextStates.remove(state);
        updateState();
        return result;
    }

    /**
     *
     * @param expression
     * @return
     */
    private Integer getIndexExpressions(StandartExpression expression) {
        StandartExpression current;
        for (int i = 0; i < standartExpressions.size(); i++) {
            current = standartExpressions.get(i);
            if (current.equals(expression)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *
     * @param state
     * @return
     */
    private Integer getIndexNextStates(State state) {
        State current;
        for (int i = 0; i < nextStates.size(); i++) {
            current = nextStates.get(i);
            if (current.equals(state)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }

}
