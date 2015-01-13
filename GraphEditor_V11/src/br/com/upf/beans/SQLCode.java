/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import br.com.upf.view.GraphEditor;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class SQLCode implements Serializable {

    /**
     * Referência ao editor
     */
    private GraphEditor graphEditor;

    /**
     * Contém o elemento classe
     */
    private ClassElement classElement;

    /**
     * Contém a classe
     */
    private ClassModel classModel;

    /**
     * Contém o diagrama de estados
     */
    private DiagramOfStateModel diagramOfStateModel;

    /**
     * Contém os Eventos
     */
    private ArrayList<Transition> events;

    /**
     * Contém os estados
     */
    private ArrayList<State> states;

    /**
     * Contém o estado em que o diagrama se encontra após a transição de estado
     * inicial
     */
    private State initialState;

    /**
     * Contém todos os estados que originam transições para estados finais
     */
    private ArrayList<State> finalStates;

    /**
     * Contém os atributos da classe
     */
    private ArrayList<Attribute> attributes;

    /**
     * Contém os atributos identificadores da classe
     */
    private ArrayList<Attribute> attributesIdentifiers;

    /**
     * Contém o nome da Tabela
     */
    private String nameTable;

    /**
     * Contém o codigo da Tabela
     */
    private String table;

    /**
     * Contém o Código da Trigger Insert
     */
    private String triggerInsert;

    /**
     * Contém o Código da Trigger Update
     */
    private String triggerUpdate;

    /**
     * Contém o Código da Trigger Delete
     */
    private String triggerDelete;

    /**
     * Contém o nome da Classe
     */
    private String nameClass;

    /**
     * Contém o nome do Projeto
     */
    private String nameProject;

    private boolean validation;

    private boolean generate;

    private String msgValidation;

    /**
     * Contrutor - Padrão
     */
    public SQLCode() {

    }

    /**
     * Construtor com Argumentos
     *
     * @param editor
     * @param element
     */
    public SQLCode(GraphEditor editor, ClassElement element) {
        this.graphEditor = editor;
        this.classElement = element;
        this.classModel = classElement.getClassModel();
        this.diagramOfStateModel = classModel.getDiagramOfStateModel();
        this.attributes = classModel.getAttributes();

        this.events = new ArrayList<>();
        this.states = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.table = "";
        this.triggerInsert = "";
        this.triggerUpdate = "";
        this.triggerDelete = "";
    }

    /**
     * Gera SQL
     *
     * @return
     */
    public boolean generate() {
        updateInformation();
        if (validation()) {
            createTable();
            createTriggers();
        } else {
            JOptionPane.showMessageDialog(graphEditor, "Erro ao Validar:\n" + msgValidation);
        }
        return true;
    }

    /**
     * Atualiza os Dados
     */
    private void updateInformation() {
        this.nameClass = classModel.getName().toUpperCase();
        this.attributesIdentifiers = classModel.getIdentifiersAttributes();

        // Carrega as transições 
        events.clear();
        events.addAll(diagramOfStateModel.getTransitions());

        // Carrega os estados
        states.clear();
        states.addAll(diagramOfStateModel.getStates());

        computeNextStates();
        searchInitialState();
        searchFinalStates();

    }

    /**
     * Calcula as transições permitidas para cada estado
     */
    private void computeNextStates() {
        Transition transition;
        BasicState source, target;
        State stateSource, stateTarget;

        /**
         * Coloca o proprio estado como Próximo, dessa maneira o sistema permite
         * executar uma atualização permanecendo no mesmo estado.
         */
        for (int index = 0; index < states.size(); index++) {
            states.get(index).addNextState(states.get(index));
        }

        for (int index = 0; index < events.size(); index++) {
            transition = events.get(index);
            source = transition.getSource();
            target = transition.getTarget();
            if (transition.isInternalTransition()) {
                stateSource = (State) source;
                stateTarget = (State) target;
                stateSource.addNextState(stateTarget);
            } else {
                // TRANSIÇÃO INICIAL OU FINAL
            }
        }
    }

    /**
     * Encontra o estado inicial do Diagrama
     */
    private void searchInitialState() {
        Transition transition;
        for (int index = 0; index < events.size(); index++) {
            transition = events.get(index);
            if (transition.isInitialTransition()) {
                initialState = (State) transition.getTarget();
            }
        }
    }

    /**
     * Encontra os estado's' finais do Diagrama
     */
    private void searchFinalStates() {
        Transition transition;
        finalStates.clear();
        for (int index = 0; index < events.size(); index++) {
            transition = events.get(index);
            if (transition.isFinalTransition()) {
                finalStates.add((State) transition.getSource());
            }
        }
    }

    /**
     * Gerencia a Validação
     *
     * @return
     */
    private boolean validation() {
        msgValidation = "";

        if (validationClassModel() && validationDiagramOfStates() && validationBind()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Valida a classe
     *
     * @return
     */
    private boolean validationClassModel() {
        return true;
    }

    /**
     * Valida o Diagrama de Estados
     *
     * @return
     */
    private boolean validationDiagramOfStates() {
        return true;
    }

    /**
     * Valida a vinculação
     *
     * @return
     */
    private boolean validationBind() {
        return true;
    }

    /**
     * Cria a Tabela
     *
     * @return
     */
    private boolean createTable() {

        Attribute currentAttribute;

        nameTable = nameClass.toUpperCase();

        table = "CREATE TABLE " + nameTable + "\n";
        table += "(";

        for (int index = 0; index < attributes.size(); index++) {
            currentAttribute = attributes.get(index);

            table += "\n" + "\t";
            table += currentAttribute.getName().toUpperCase() + " ";

            if (currentAttribute.getSqlType().equalsIgnoreCase("VARCHAR")) {
                table += "VARCHAR(" + currentAttribute.getSize() + ")";
            } else {
                table += currentAttribute.getSqlType();
            }

            if (currentAttribute.isUniqueColumn()) {
                table += "\t" + "UNIQUE";
            }

            if (currentAttribute.isNotNull()) {
                table += "\t" + "NOT NULL";
            }

            table += ",";

        }

        table += "\n";
        table += "PRIMARY KEY (";

        for (int index = 0; index < attributesIdentifiers.size(); index++) {
            currentAttribute = attributesIdentifiers.get(index);
            table += currentAttribute.getName();
            if (index != (attributesIdentifiers.size() - 1)) {
                table += "," + " ";
            }
        }
        table += ")" + "\n";
        table += ");";

        return true;
    }

    private void createTriggers() {
        createInsertTrigger();
        createUpdateTrigger();
        if (finalStates.size() >= 1) {
            createDeleteTrigger();
        }
    }

    /**
     * Cria o Gatilho Insert
     *
     * @return
     */
    private boolean createInsertTrigger() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;

        nameFunction = nameClass.toUpperCase() + "_INSERT_FUNCTION()";
        nameTrigger = nameClass.toUpperCase() + "_INSERT_TRIGGER";

        triggerInsert = "-- TRIGGER OF INSERT --" + "\n";
        triggerInsert += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerInsert += "RETURNS TRIGGER AS" + "\n";
        triggerInsert += "$$" + "\n";
        triggerInsert += "BEGIN" + "\n";

        triggerInsert += "\t" + "IF( NOT(" + getStateSyntaxSQL(initialState, "NEW") + ") ) THEN" + "\n";
        triggerInsert += "\t" + "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE INCLUSÃO RECUSADA';" + "\n";
        triggerInsert += "\t" + "END IF;" + "\n";
        triggerInsert += "RETURN NEW;" + "\n";

        triggerInsert += "END;" + "\n";
        triggerInsert += "$$ " + "LANGUAGE plpgsql;";

        triggerInsert += "\n" + "\n";
        triggerInsert += "CREATE TRIGGER " + nameTrigger + " BEFORE INSERT" + "\n";
        triggerInsert += "ON " + nameTable + " FOR EACH ROW" + "\n";
        triggerInsert += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n\n";

        return true;
    }

    /**
     * Cria o Gatilho Update
     *
     * @return
     */
    private boolean createUpdateTrigger() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;
        Attribute currentAttribute;

        nameFunction = nameClass.toUpperCase() + "_UPDATE_FUNCTION()";
        nameTrigger = nameClass.toUpperCase() + "_UPDATE_TRIGGER";

        triggerUpdate = "-- TRIGGER OF UPDATE --" + "\n";
        triggerUpdate += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerUpdate += "RETURNS TRIGGER AS" + "\n";
        triggerUpdate += "$$" + "\n";
        triggerUpdate += "BEGIN" + "\n";

        triggerUpdate += computeUpdateTrigger(0);

        triggerUpdate += "RETURN NEW;" + "\n";

        triggerUpdate += "END;" + "\n";
        triggerUpdate += "$$ " + "LANGUAGE plpgsql;";

        triggerUpdate += "\n" + "\n";
        triggerUpdate += "CREATE TRIGGER " + nameTrigger + " BEFORE UPDATE" + " OF ";

        int contMonitorable = 0;
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isMonitorable()) {
                if (contMonitorable != 0) {
                    triggerUpdate += ", "; //Acredito que aqui esta o erro 
                }
                triggerUpdate += attributes.get(i).getName().toUpperCase();
                contMonitorable++;
//                if ((attributes.size() > 1) && (i < attributes.size() - 1)) {
//                    triggerUpdate += ", "; //Acredito que aqui esta o erro 
//                }
            }
        }
        triggerUpdate += "\n" + "ON " + nameTable + " FOR EACH ROW" + "\n";
        triggerUpdate += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n\n";

        return true;
    }

    /**
     * Trigger of Delete
     *
     * @return
     */
    private boolean createDeleteTrigger() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;

        nameFunction = nameClass.toUpperCase() + "_DELETE_FUNCTION()";
        nameTrigger = nameClass.toUpperCase() + "_DELETE_TRIGGER";

        triggerDelete = "-- TRIGGER OF DELETE --" + "\n";
        triggerDelete += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerDelete += "RETURNS TRIGGER AS" + "\n";
        triggerDelete += "$$" + "\n";
        triggerDelete += "BEGIN" + "\n";

        triggerDelete += computeDeleteTrigger(0);

        triggerDelete += "RETURN OLD;" + "\n";

        triggerDelete += "END;" + "\n";
        triggerDelete += "$$ " + "LANGUAGE plpgsql;";

        triggerDelete += "\n" + "\n";
        triggerDelete += "CREATE TRIGGER " + nameTrigger + " BEFORE DELETE" + "\n";
        triggerDelete += "ON " + nameTable + " FOR EACH ROW" + "\n";
        triggerDelete += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n\n";

        return true;
    }

    /**
     * Compute Trigger of Update
     *
     * @param index
     * @return
     */
    private String computeUpdateTrigger(Integer index) {
        State currentState;
        ArrayList<State> currentNextStates;
        ArrayList<StandartExpression> currentExpressions;
        String expression = "\n";

        if (index < states.size()) {
            currentState = states.get(index);
            currentNextStates = currentState.getNextStates();
            currentExpressions = currentState.getStandartExpressions();

            expression += "IF( " + getStateSyntaxSQL(currentState, "OLD") + " ) THEN \n";
            if (currentNextStates.size() >= 1) {
                expression += "\t" + "IF( NOT(" + getNextStatesSyntaxSQL(currentNextStates) + ") ) THEN \n";
                expression += "\t" + "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE ATUALIZAÇÃO RECUSADA';" + "\n";
                expression += "\t" + "END IF;" + "\n";
            } else {
                expression += "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE ATUALIZAÇÃO RECUSADA';" + "\n";
            }
            expression += "ELSE" + "\n";
            expression += computeUpdateTrigger(index + 1);
            expression += "END IF;" + "\n";
        } else {
            return expression;
        }

        return expression;
    }

    /**
     * Compute Trigger of Delete
     *
     * @param index
     * @return
     */
    private String computeDeleteTrigger(Integer index) {
        State currentState;
        String expression = "\n";
        Integer last = finalStates.size() - 1;

        if (index < finalStates.size()) {
            currentState = finalStates.get(index);

            expression += "IF( NOT(" + getStateSyntaxSQL(currentState, "OLD") + ") ) THEN \n";
            if (index == last) {
                expression += "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE EXCLUSÃO RECUSADA';" + "\n";
            } else {
                expression += computeDeleteTrigger(index + 1);
            }
            expression += "END IF;" + "\n";

        } else {
            return expression;
        }

        return expression;
    }

    /**
     *
     * @param nextStates
     * @return
     */
    private String getNextStatesSyntaxSQL(ArrayList<State> nextStates) {
        // Declaração e inicialização - variáveis locais
        String expression = "";
        ArrayList<State> currentNextStates = nextStates;
        State currentState;
        Integer first = 0;
        Integer last = currentNextStates.size() - 1;

        for (int i = 0; i < currentNextStates.size(); i++) {
            currentState = currentNextStates.get(i);
            if (i == first) {
                if (currentNextStates.size() > 1) {
                    expression += "(";
                }
            } else {
                expression += ") OR (";
            }

            expression += getStateSyntaxSQL(currentState, "NEW");

            if ((currentNextStates.size() > 1) && (i == last)) {
                {
                    expression += ")";
                }
            }
        }

        return expression;
    }

    /**
     *
     * @param state
     * @return
     */
    private String getStateSyntaxSQL(State state, String type) {
        // Declaração e inicialização - variáveis locais
        State currentState = state;
        ArrayList<StandartExpression> currentExpressions = currentState.getStandartExpressions();
        StandartExpression currentExpression;
        Integer first = 0;
        Integer last = currentExpressions.size() - 1;
        String syntax = "";

        for (int i = 0; i < currentExpressions.size(); i++) {
            currentExpression = currentExpressions.get(i);
            if (i == first) {
                if (currentExpressions.size() > 1) {
                    syntax += "(";
                }
            } else {
                syntax += ") AND (";
            }

            syntax += type + "." + currentExpression.getAttribute() + " "
                    + currentExpression.getOperator() + " ";

            if (currentExpression.getAttribute().getUmlType().equalsIgnoreCase("STRING")) {
                syntax += "'" + currentExpression.getValue() + "'";
            } else {
                if (currentExpression.getAttribute().getUmlType().equalsIgnoreCase("BOOLEAN")) {
                    syntax += "'" + currentExpression.getValue() + "'";
                } else {
                    syntax += currentExpression.getValue();
                }
            }

            if ((currentExpressions.size() > 1) && (i == last)) {
                {
                    syntax += ")";
                }
            }
        }

        return syntax;
    }

    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTriggerInsert() {
        return triggerInsert;
    }

    public void setTriggerInsert(String triggerInsert) {
        this.triggerInsert = triggerInsert;
    }

    public String getTriggerUpdate() {
        return triggerUpdate;
    }

    public void setTriggerUpdate(String triggerUpdate) {
        this.triggerUpdate = triggerUpdate;
    }

    public String getTriggerDelete() {
        return triggerDelete;
    }

    public void setTriggerDelete(String triggerDelete) {
        this.triggerDelete = triggerDelete;
    }

    public ArrayList<Transition> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Transition> events) {
        this.events = events;
    }

    public ArrayList<State> getDictionaryOfStates() {
        return states;
    }

    public void setDictionaryOfStates(ArrayList<State> dictionaryOfStates) {
        this.states = dictionaryOfStates;
    }

}
