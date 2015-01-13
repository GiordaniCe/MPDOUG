/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class DiagramOfStateModel implements Serializable {

    private static final long serialVersionUID = 4781332016509777747L;

    /**
     *
     */
    private InitialState initialState;

    /**
     *
     */
    private ArrayList<State> states;

    /**
     *
     */
    private ArrayList<FinalState> finalStates;

    /**
     *
     */
    private ArrayList<Transition> transitions;

    /**
     * Representa o primeiro estado seguinte ao Estado inicial do Objeto.
     * Representa o estado em que o Objeto deve estar ao iniciar seu ciclo de
     * vida.
     */
    private State firstState;

    /**
     * Representa o ultimos estado em que se encontra o Objeto antes de seguir
     * para o estado Final. Representa o estado em que o Objeto deve estar antes
     * de seguir para um estado final.
     */
    private ArrayList<State> lastStates;

    /**
     * Validação contém estado inicial
     */
    private boolean containsInitialState;

    /**
     * Validação do número mínimo de estados
     */
    private boolean containsInternalStates;

    /**
     *
     */
    private boolean containsFinalStates;

    /**
     * Indica se o nome dos estados são válidos
     */
    private boolean checkValidStatesName;

    /**
     * Indica se o nome dos estados são únicos
     */
    private boolean checkUniqueStatesName;

    /**
     * Indica se o nome das transições são válidas
     */
    private boolean checkValidTransitionsName;

    /**
     * Indica se o nome das transições são únicos
     */
    private boolean checkUniqueTransitionsName;

    /**
     * Indica se contém Transição inicial
     */
    private boolean containsInitialTransition;

    /**
     * Indica se contém as Transições internas
     */
    private boolean containsInternalTransitions;

    /**
     * Indica se contém as Transições finais
     */
    private boolean containsFinalTransitions;

    /**
     *
     */
    private boolean modified;

    /**
     *
     */
    private boolean initialized;

    /**
     * Construtor Padrão
     */
    public DiagramOfStateModel() {
        this.initialState = null;
        this.states = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.containsInitialState = false;
        this.containsInternalStates = false;
        this.containsFinalStates = false;
        this.checkValidStatesName = false;
        this.checkUniqueStatesName = false;
        this.containsInitialTransition = false;
        this.containsInternalTransitions = false;
        this.containsFinalTransitions = false;
        this.firstState = null;
        this.lastStates = new ArrayList<>();
        this.modified = false;
        this.initialized = false;
    }

    /**
     * Atualiza
     *
     * @return
     */
    private boolean updateDiagramOfStateModel() {
        modified = false;
        return validate();
    }

    /**
     * Valida o Diagrama de Estados
     *
     * @return
     */
    public boolean validate() {
        boolean statesResult = validationStates();
        boolean transitionsResult = validationTransitions();
        return statesResult && transitionsResult;
    }

    /**
     * Verifica se o diagrama possui: 1. Um estado inicial; 2. Dóis ou mais
     * estados internos; 3. Nome válido para cada estado(Diferente de 'New
     * State');
     *
     * @return
     */
    private boolean validationStates() {
        //VERIFICA ESTADO INICIAL
        if (initialState instanceof InitialState) {
            containsInitialState = true;
        } else {
            containsInitialState = false;
        }

        // VERIFICA O NOME DOS ESTADOS
        if (states.isEmpty()) {
            checkValidStatesName = false;
            checkUniqueStatesName = false;
        } else {
            checkValidStatesName = verifyIsValidStateName();
            if (checkValidStatesName) {
                checkUniqueStatesName = verifyIsUniqueStateName();
            } else {
                checkUniqueStatesName = false;
            }
        }

        // VERIFICA O NOME DAS TRANSIÇÕES
        if (transitions.isEmpty()) {
            checkValidTransitionsName = false;
            checkUniqueTransitionsName = false;
        } else {
            checkValidTransitionsName = verifyIsValidTransitionsName();
        }

        // VERIFICA NÚMERO DE ESTADOS INTERNOS
        if (states.size() > 1) {
            containsInternalStates = true;
        } else {
            containsInternalStates = false;
        }

        // VERIFICA SE CONTÉM ESTADO FINAL
        if (finalStates.isEmpty()) {
            containsFinalStates = false;
        } else {
            containsFinalStates = true;
        }

        // RESULTADO ATUALIZADO
        return containsInitialState && containsInternalStates && checkValidStatesName && checkUniqueStatesName && checkValidTransitionsName;
    }

    /**
     * Verificação - Nome do estado válido
     *
     * @return
     */
    private boolean verifyIsValidStateName() {
        for (int i = 0; i < states.size(); i++) {
            if (!states.get(i).isCheckName()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verificação - Nome do estado único
     *
     * @return
     */
    private boolean verifyIsUniqueStateName() {
        for (int i = 0; i < states.size(); i++) {
            State currentState = states.get(i);
            for (int j = 0; j < states.size(); j++) {
                if (j != i) {
                    if (currentState.getName().equalsIgnoreCase(states.get(j).getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Verificação - Nome do estado válido
     *
     * @return
     */
    private boolean verifyIsValidTransitionsName() {
        for (int i = 0; i < transitions.size(); i++) {
            if (!transitions.get(i).isCheckName()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verificação - Nome do estado único
     *
     * @return
     */
    private boolean verifyIsUniqueTransitionsName() {
        for (int i = 0; i < transitions.size(); i++) {
            Transition currentTransition = transitions.get(i);
            for (int j = 0; j < transitions.size(); j++) {
                if (j != i) {
                    if (currentTransition.getName().equalsIgnoreCase(transitions.get(j).getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Verifica se o diagrama possui: 1. Uma transição Inicial; 2. Uma ou mais
     * transições de entrada e saída para cada estado interno; 3. Uma transição
     * Final para cada Estado Final;
     *
     * @return
     */
    private boolean validationTransitions() {
        State currentState;
        FinalState currentFinalState;
        Transition currentTransition;

        // VERIFICAÇÃO TRANSIÇÕES INTERNAS
        if (transitions.isEmpty()) {
            containsInternalTransitions = false;
        } else {
            containsInternalTransitions = true;
            for (int i = 0; i < states.size(); i++) {
                currentState = states.get(i);
                boolean source = false;
                boolean target = false;

                for (int j = 0; j < transitions.size(); j++) {
                    currentTransition = transitions.get(j);

                    if (currentState.equals(currentTransition.getSource())) {
                        source = true;
                    }
                    if (currentState.equals(currentTransition.getTarget())) {
                        target = true;
                    }
                }

                // Caso não seja o primeiro estado válido
                if (!(source && target)) {
                    containsInternalTransitions = false;
                }
            }
        }

        // VERIFICAÇÂO TRANSIÇÕES FINAIS
        if (finalStates.isEmpty()) {
            containsFinalTransitions = false;
        } else {
            if (transitions.isEmpty()) {
                containsFinalTransitions = false;
            } else {
                int count = 0;
                for (int j = 0; j < transitions.size(); j++) {
                    currentTransition = transitions.get(j);
                    if (currentTransition.isFinalTransition()) {
                        count++;
                    }
                }
                if (finalStates.size() == count) {
                    containsFinalTransitions = true;
                } else {
                    containsFinalTransitions = false;
                }
            }
        }

        // RESULTADO ATUALIZADO
        if (containsFinalStates) {
            if (containsInitialTransition && containsInternalTransitions && containsFinalTransitions) {
                return true;
            } else {
                return false;
            }
        } else {
            if (containsInitialTransition && containsInternalTransitions) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean availabilityStateName(String newName) {
        String currentName;
        for (int i = 0; i < states.size(); i++) {
            currentName = states.get(i).getName();
            if (newName.equals(currentName)) {
                return false;
            }
        }
        return true;
    }

    public boolean availabilityTransitionName(String newName) {
        String currentName;
        for (int i = 0; i < transitions.size(); i++) {
            currentName = transitions.get(i).getName();
            if (newName.equals(currentName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adiciona Estado inicial
     *
     * @param initial
     * @return
     */
    public boolean addInitialState(InitialState initial) {
        // Verificação - Estado inicial é unico
        if (containsInitialState) {
            return false;
        } else {
            this.initialState = initial;
            updateDiagramOfStateModel();
            return true;
        }
    }

    /**
     * Remove Estado inicial
     *
     * @return
     */
    public boolean removeInitalState() {
        if (containsInitialState) {
            initialState = null;
            updateDiagramOfStateModel();
        }
        return true;
    }

    /**
     * Adiciona Estado
     *
     * @param s
     * @return
     */
    public boolean addState(State s) {
        boolean result = states.add(s);
        updateDiagramOfStateModel();
        return result;
    }

    /**
     * Remove Estado
     *
     * @param s
     * @return
     */
    public boolean removeState(State s) {
        boolean result = states.remove(s);
        updateDiagramOfStateModel();
        return result;
    }

    /**
     * Adiciona Estado final
     *
     * @param fs
     * @return
     */
    public boolean addFinalState(FinalState fs) {
        boolean result = finalStates.add(fs);
        updateDiagramOfStateModel();
        return result;
    }

    /**
     * Remove Estado Final
     *
     * @param fs
     * @return
     */
    public boolean removeFinalState(FinalState fs) {
        boolean result = finalStates.remove(fs);
        updateDiagramOfStateModel();
        return result;
    }

    /**
     * Adiciona Transição
     *
     * @param t
     * @return
     */
    public boolean addTransition(Transition t) {
        boolean result;
        // TRANSIÇÃO INICIAL
        if (t.isInitialTransition()) {
            result = transitions.add(t);
            if (result) {
                firstState = (State) t.getTarget();
                containsInitialTransition = true;
                updateDiagramOfStateModel();
            }
            return result;
        } else {
            // TRANSIÇÃO FINAL
            if (t.isFinalTransition()) {
                result = transitions.add(t);
                if (result) {
                    lastStates.add((State) t.getSource());
                    updateDiagramOfStateModel();
                }
                return result;
            } else {
                // TRANSIÇÃO INTERNA
                if (t.isInternalTransition()) {
                    result = transitions.add(t);
                    if (result) {
                        State stateSource = (State) t.getSource();
                        State stateTarget = (State) t.getTarget();
                        stateSource.addNextState(stateTarget);
                        updateDiagramOfStateModel();
                    }
                    return result;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Remove Transição
     *
     * @param t
     * @return
     */
    public boolean removeTransition(Transition t) {
        boolean result;
        // TRANSIÇÃO INICIAL
        if (t.isInitialTransition()) {
            result = transitions.remove(t);
            if (result) {
                firstState = null;
                containsInitialTransition = false;
                updateDiagramOfStateModel();
            }
            return result;
        } else {
            // TRANSIÇÃO FINAL
            if (t.isFinalTransition()) {
                result = transitions.remove(t);
                if (result) {
                    lastStates.remove(t.getSource());
                    updateDiagramOfStateModel();
                }
                return result;
            } else {
                // TRANSIÇÃO INTERNA
                if (t.isInternalTransition()) {
                    result = transitions.remove(t);
                    if (result) {
                        State stateSource = (State) t.getSource();
                        State stateTarget = (State) t.getTarget();
                        stateSource.removeNextState(stateTarget);
                        updateDiagramOfStateModel();
                    }
                    return result;
                } else {
                    return false;
                }
            }
        }
    }

    private Integer getIndexStates(State s) {
        State current;
        for (int i = 0; i < states.size(); i++) {
            current = states.get(i);
            if (current.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    private Integer getIndexFinalStates(FinalState fs) {
        FinalState current;
        for (int i = 0; i < finalStates.size(); i++) {
            current = finalStates.get(i);
            if (current.equals(fs)) {
                return i;
            }
        }
        return -1;
    }

    private Integer getIndexTransitions(Transition t) {
        Transition current;
        for (int i = 0; i < transitions.size(); i++) {
            current = transitions.get(i);
            if (current.equals(t)) {
                return i;
            }
        }
        return -1;
    }

    public InitialState getInitialState() {
        return initialState;
    }

    public void setInitialState(InitialState initialState) {
        this.initialState = initialState;
        containsInitialState = true;
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public void setStates(ArrayList<State> states) {
        this.states = states;
    }

    public ArrayList<FinalState> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(ArrayList<FinalState> finalStates) {
        this.finalStates = finalStates;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

    public boolean isContainsInitialState() {
        return containsInitialState;
    }

    public void setContainsInitialState(boolean containsInitialState) {
        this.containsInitialState = containsInitialState;
    }

    public State getFirstState() {
        return firstState;
    }

    public void setFirstState(State firstState) {
        this.firstState = firstState;
    }

    public ArrayList<State> getLastStates() {
        return lastStates;
    }

    public void setLastStates(ArrayList<State> lastStates) {
        this.lastStates = lastStates;
    }

    public boolean isContainsInternalStates() {
        return containsInternalStates;
    }

    public void setContainsInternalStates(boolean containsInternalStates) {
        this.containsInternalStates = containsInternalStates;
    }

    public boolean isContainsFinalStates() {
        return containsFinalStates;
    }

    public void setContainsFinalStates(boolean containsFinalStates) {
        this.containsFinalStates = containsFinalStates;
    }

    public boolean isCheckValidStatesName() {
        return checkValidStatesName;
    }

    public void setCheckValidStatesName(boolean checkValidStatesName) {
        this.checkValidStatesName = checkValidStatesName;
    }

    public boolean isContainsInitialTransition() {
        return containsInitialTransition;
    }

    public void setContainsInitialTransition(boolean containsInitialTransition) {
        this.containsInitialTransition = containsInitialTransition;
    }

    public boolean isContainsInternalTransitions() {
        return containsInternalTransitions;
    }

    public void setContainsInternalTransitions(boolean containsInternalTransitions) {
        this.containsInternalTransitions = containsInternalTransitions;
    }

    public boolean isContainsFinalTransitions() {
        return containsFinalTransitions;
    }

    public void setContainsFinalTransitions(boolean containsFinalTransitions) {
        this.containsFinalTransitions = containsFinalTransitions;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isCheckUniqueStatesName() {
        return checkUniqueStatesName;
    }

    public void setCheckUniqueStatesName(boolean checkUniqueStatesName) {
        this.checkUniqueStatesName = checkUniqueStatesName;
    }

    public boolean isCheckValidTransitionsName() {
        return checkValidTransitionsName;
    }

    public void setCheckValidTransitionsName(boolean checkValidTransitionNames) {
        this.checkValidTransitionsName = checkValidTransitionNames;
    }

    public boolean isCheckUniqueTransitionsName() {
        return checkUniqueTransitionsName;
    }

    public void setCheckUniqueTransitionsName(boolean checkUniqueTransitionName) {
        this.checkUniqueTransitionsName = checkUniqueTransitionName;
    }

}
