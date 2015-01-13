/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author GiordaniAntonio
 */
public class ClassModel implements Serializable {

    private static final long serialVersionUID = 6280466054898505842L;

    /**
     *
     */
    private int id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private boolean checkName;

    /**
     *
     */
    private ArrayList<Attribute> attributes;

    /**
     *
     */
    private boolean containsAttributes;

    /**
     *
     */
    private ArrayList<Method> methods;

    /**
     *
     */
    private String tableName;

    /**
     *
     */
    private String tableCode;

    /**
     *
     */
    private boolean generatedTableCode;

    /**
     *
     */
    private boolean updatedTableCode;

    /**
     *
     */
    private String triggersCode;

    /**
     *
     */
    private boolean generatedTriggersCode;

    /**
     *
     */
    private boolean updatedTriggersCode;

    /**
     *
     */
    private DiagramOfStateModel diagramOfStateModel;

    /**
     *
     */
    private boolean initializedDiagramOfStateModel;

    /**
     *
     */
    private boolean initializedDictionaryOfStates;

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
     * Contém os Eventos
     */
    private ArrayList<Transition> transitions;

    /**
     * Contém os estados
     */
    private ArrayList<State> states;

    /**
     *
     */
    private State initialState;

    /**
     *
     */
    private ArrayList<State> finalStates;

    /**
     * Contém o estado em que o diagrama se encontra após a transição de estado
     * inicial
     */
    private State firstState;

    /**
     * Contém todos os estados que originam transições para estados finais
     */
    private ArrayList<State> lastStates;

    /**
     * Metodolodia de Elaboração de Dicionário de estados
     *
     * TRUE - Custom
     *
     * FALSE - Standart
     *
     */
    private boolean methodElaborationCustom;

    /**
     * Construtor Padrão
     */
    public ClassModel() {
        this("Nova Classe");
    }

    /**
     * Construtor
     *
     * @param name
     */
    public ClassModel(String name) {
        attributes = new ArrayList<>();
        methods = new ArrayList<>();
        this.name = name;
        this.tableCode = "";
        this.generatedTableCode = false;
        this.updatedTableCode = false;
        this.triggersCode = "";
        this.generatedTriggersCode = false;
        this.updatedTriggersCode = false;
        this.methodElaborationCustom = false;
        this.diagramOfStateModel = new DiagramOfStateModel();
        this.states = diagramOfStateModel.getStates();
        this.transitions = diagramOfStateModel.getTransitions();
        this.initializedDiagramOfStateModel = false;
        this.initializedDictionaryOfStates = false;
        this.finalStates = new ArrayList<State>();
        this.checkName = false;
        this.containsAttributes = false;
    }

    private boolean updateClassModel() {
        //  VERFICA SE EXISTEM ATRIBUTOS
        if (attributes.isEmpty()) {
            containsAttributes = false;
        } else {
            containsAttributes = true;
        }

        // VERIFICA O NOME DA CLASSE
        if (name.equalsIgnoreCase("Nova Classe")) {
            checkName = false;
        } else {
            checkName = true;
        }
        return true;
    }

    public boolean validate() {
        updateClassModel();
        if (this.containsAttributes && this.checkName) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Valida o Dicionário de estados
     *
     * @return
     */
    public boolean validateDictionaryOfStates() {
        if (methodElaborationCustom) {
            return validateDictionaryOfStatesMethodCustom();
        } else {
            return validateDictionaryOfStatesMethodStandart();
        }
    }

    /**
     * Valida o dicionário de estados - Custom
     *
     * @return
     */
    public boolean validateDictionaryOfStatesMethodCustom() {
        CustomExpression currentCustomExpression;
        for (int i = 0; i < states.size(); i++) {
            currentCustomExpression = states.get(i).getCustomExpression();
            if ((currentCustomExpression.validateSyntax()) && (currentCustomExpression.validateExpression(getMonitorableAttributes()))) {
                // Estado válidado
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Valida o dicionário de estados - Standart
     *
     * @return
     */
    public boolean validateDictionaryOfStatesMethodStandart() {
        //states = diagramOfStateModel.getStates();
        // PERCORRE TODOS OS ATRIBUTOS
        for (int i = 0; i < attributes.size(); i++) {
            Attribute currentAttribute = attributes.get(i);
            boolean validAttribute = true;

            if (currentAttribute.isMonitorable()) {
                // PERCORRE TODOS OS ESTADOS
                for (int j = 0; j < states.size(); j++) {
                    State currentState = states.get(j);
                    ArrayList<StandartExpression> currentExpressions = currentState.getStandartExpressions();
                    ArrayList<StandartExpression> expressionsContainsAttribute = new ArrayList<>();

                    // PERCORRE TODAS AS EXPRESSÕES DO ESTADO CORRENTE
                    for (int k = 0; k < currentExpressions.size(); k++) {
                        StandartExpression currentExpression = currentExpressions.get(k);
                        if (currentExpression.getAttribute().equals(currentAttribute)) {
                            expressionsContainsAttribute.add(currentExpression);
                        }
                    }
                    // VERIFICA QUANTAS EXPRESSÕES RELACIONADAS AO ATRIBUTO EXISTEM NO ESTADO
                    if (expressionsContainsAttribute.size() > 0) {
                        // estado correto
                    } else {
                        validAttribute = false;
                    }
                }
            }
            if (!validAttribute) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gera Código da Tabela
     *
     * @return
     */
    public String generateTableCode() {
        ArrayList<Attribute> identifiers = getIdentifiersAttributes();
        Attribute currentAttribute;

        tableName = getName().toUpperCase().replace(" ", "_");

        tableCode = "CREATE TABLE " + tableName + "\n";
        tableCode += "(";

        for (int index = 0; index < attributes.size(); index++) {
            currentAttribute = attributes.get(index);

            tableCode += "\n" + "\t";
            tableCode += currentAttribute.getName().toUpperCase() + " ";

            if (currentAttribute.getSqlType().equalsIgnoreCase("VARCHAR")) {
                tableCode += "VARCHAR(" + currentAttribute.getSize() + ")";
            } else {
                tableCode += currentAttribute.getSqlType();
            }

            if (currentAttribute.isUniqueColumn()) {
                tableCode += "\t" + "UNIQUE";
            }

            if (currentAttribute.isNotNull()) {
                tableCode += "\t" + "NOT NULL";
            }

            tableCode += ",";

        }

        tableCode += "\n";
        tableCode += "PRIMARY KEY (";

        for (int index = 0; index < identifiers.size(); index++) {
            currentAttribute = identifiers.get(index);
            tableCode += currentAttribute.getName();
            if (index != (identifiers.size() - 1)) {
                tableCode += "," + " ";
            }
        }
        tableCode += ")" + "\n";
        tableCode += ");";

        setGeneratedTableCode(true);
        setUpdatedTableCode(true);
        return tableCode;
    }

    /**
     * Gera Código do Gatilho
     *
     * @return
     */
    public String generateTriggerCode() {
        firstState = diagramOfStateModel.getFirstState();
        lastStates = diagramOfStateModel.getLastStates();
        tableName = getName().toUpperCase().replace(" ", "_");

        triggersCode = createTriggerInsert();
        triggersCode += "\n";
        triggersCode += createTriggerUpdate();
        triggersCode += "\n";
        triggersCode += createTriggerDelete();

        setGeneratedTriggersCode(true);
        setUpdatedTriggersCode(true);

        return triggersCode;
    }

    /**
     * Cria Trigger Insert
     *
     * @return
     */
    private String createTriggerInsert() {
        if (methodElaborationCustom) {
            return createInsertTriggerMethodCustom();
        } else {
            return createInsertTriggerMethodStandart();
        }
    }

    /**
     * Cria o Código do Gatilho Insert - Método Custom
     *
     * @return
     */
    private String createInsertTriggerMethodCustom() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;

        nameFunction = tableName + "_INSERT_FUNCTION()";
        nameTrigger = tableName + "_INSERT_TRIGGER";

        triggerInsert = "-- TRIGGER OF INSERT --" + "\n";
        triggerInsert += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerInsert += "RETURNS TRIGGER AS" + "\n";
        triggerInsert += "$$" + "\n";
        triggerInsert += "BEGIN" + "\n";

        triggerInsert += "\t" + "IF( NOT" + firstState.getCustomExpression().sqlNotationRepresentation("NEW") + ") THEN" + "\n";
        triggerInsert += "\t" + "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE INCLUSÃO RECUSADA';" + "\n";
        triggerInsert += "\t" + "END IF;" + "\n";
        triggerInsert += "RETURN NEW;" + "\n";

        triggerInsert += "END;" + "\n";
        triggerInsert += "$$ " + "LANGUAGE plpgsql;";

        triggerInsert += "\n" + "\n";
        triggerInsert += "CREATE TRIGGER " + nameTrigger + " BEFORE INSERT" + "\n";
        triggerInsert += "ON " + tableName + " FOR EACH ROW" + "\n";
        triggerInsert += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n";

        return triggerInsert;
    }

    /**
     * Cria o Código do Gatilho Insert - Método Standart
     *
     * @return
     */
    private String createInsertTriggerMethodStandart() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;

        nameFunction = tableName + "_INSERT_FUNCTION()";
        nameTrigger = tableName + "_INSERT_TRIGGER";

        triggerInsert = "-- TRIGGER OF INSERT --" + "\n";
        triggerInsert += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerInsert += "RETURNS TRIGGER AS" + "\n";
        triggerInsert += "$$" + "\n";
        triggerInsert += "BEGIN" + "\n";

        triggerInsert += "\t" + "IF( NOT(" + getStateSyntaxSQLMethodStandart(firstState, "NEW") + ") ) THEN" + "\n";
        triggerInsert += "\t" + "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE INCLUSÃO RECUSADA';" + "\n";
        triggerInsert += "\t" + "END IF;" + "\n";
        triggerInsert += "RETURN NEW;" + "\n";

        triggerInsert += "END;" + "\n";
        triggerInsert += "$$ " + "LANGUAGE plpgsql;";

        triggerInsert += "\n" + "\n";
        triggerInsert += "CREATE TRIGGER " + nameTrigger + " BEFORE INSERT" + "\n";
        triggerInsert += "ON " + tableName + " FOR EACH ROW" + "\n";
        triggerInsert += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n";

        return triggerInsert;
    }

    /**
     * Cria Trigger Update
     *
     * @return
     */
    private String createTriggerUpdate() {
        if (methodElaborationCustom) {
            return createUpdateTriggerMethodCustom();
        } else {
            return createUpdateTriggerMethodStandart();
        }
    }

    /**
     * Trigger of Update - Método Custom
     *
     * @return
     */
    private String createUpdateTriggerMethodCustom() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;
        Attribute currentAttribute;

        nameFunction = tableName + "_UPDATE_FUNCTION()";
        nameTrigger = tableName + "_UPDATE_TRIGGER";

        triggerUpdate = "-- TRIGGER OF UPDATE --" + "\n";
        triggerUpdate += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerUpdate += "RETURNS TRIGGER AS" + "\n";
        triggerUpdate += "$$" + "\n";
        triggerUpdate += "BEGIN" + "\n";

        triggerUpdate += computeUpdateTriggerMethodCustom(0);

        triggerUpdate += "RETURN NEW;" + "\n";

        triggerUpdate += "END;" + "\n";
        triggerUpdate += "$$ " + "LANGUAGE plpgsql;";

        triggerUpdate += "\n" + "\n";
        triggerUpdate += "CREATE TRIGGER " + nameTrigger + " BEFORE UPDATE" + " OF ";

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isMonitorable()) {
                triggerUpdate += attributes.get(i).getName().toUpperCase();
                if ((attributes.size() > 1) && (i < attributes.size() - 1)) {
                    triggerUpdate += ", ";
                }
            }
        }
        triggerUpdate += "\n" + "ON " + tableName + " FOR EACH ROW" + "\n";
        triggerUpdate += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n";

        return triggerUpdate;
    }

    /**
     * Trigger of Update - Método Standart
     *
     * @return
     */
    private String createUpdateTriggerMethodStandart() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;
        Attribute currentAttribute;

        nameFunction = tableName + "_UPDATE_FUNCTION()";
        nameTrigger = tableName + "_UPDATE_TRIGGER";

        triggerUpdate = "-- TRIGGER OF UPDATE --" + "\n";
        triggerUpdate += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerUpdate += "RETURNS TRIGGER AS" + "\n";
        triggerUpdate += "$$" + "\n";
        triggerUpdate += "BEGIN" + "\n";

        triggerUpdate += computeUpdateTriggerMethodStandart(0);

        triggerUpdate += "RETURN NEW;" + "\n";

        triggerUpdate += "END;" + "\n";
        triggerUpdate += "$$ " + "LANGUAGE plpgsql;";

        triggerUpdate += "\n" + "\n";
        triggerUpdate += "CREATE TRIGGER " + nameTrigger + " BEFORE UPDATE" + " OF ";

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isMonitorable()) {
                triggerUpdate += attributes.get(i).getName().toUpperCase();
                if ((attributes.size() > 1) && (i < attributes.size() - 1)) {
                    triggerUpdate += ", ";
                }
            }
        }
        triggerUpdate += "\n" + "ON " + tableName + " FOR EACH ROW" + "\n";
        triggerUpdate += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n";

        return triggerUpdate;
    }

    /**
     * Cria Trigger Delete
     *
     * @return
     */
    private String createTriggerDelete() {
        if (methodElaborationCustom) {
            return createDeleteTriggerMethodCustom();
        } else {
            return createDeleteTriggerMethodStandart();
        }
    }

    /**
     * Trigger of Delete - Método Custom
     *
     * @return
     */
    private String createDeleteTriggerMethodCustom() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;

        nameFunction = tableName + "_DELETE_FUNCTION()";
        nameTrigger = tableName + "_DELETE_TRIGGER";

        triggerDelete = "-- TRIGGER OF DELETE --" + "\n";
        triggerDelete += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerDelete += "RETURNS TRIGGER AS" + "\n";
        triggerDelete += "$$" + "\n";
        triggerDelete += "BEGIN" + "\n";

        triggerDelete += computeDeleteTriggerMethodCustom(0);

        triggerDelete += "RETURN OLD;" + "\n";

        triggerDelete += "END;" + "\n";
        triggerDelete += "$$ " + "LANGUAGE plpgsql;";

        triggerDelete += "\n" + "\n";
        triggerDelete += "CREATE TRIGGER " + nameTrigger + " BEFORE DELETE" + "\n";
        triggerDelete += "ON " + tableName + " FOR EACH ROW" + "\n";
        triggerDelete += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n";

        return triggerDelete;
    }

    /**
     * Trigger of Delete - Método Standart
     *
     * @return
     */
    private String createDeleteTriggerMethodStandart() {
        // Declaração e inicialização - variáveis locais
        String nameFunction, nameTrigger;

        nameFunction = tableName + "_DELETE_FUNCTION()";
        nameTrigger = tableName + "_DELETE_TRIGGER";

        triggerDelete = "-- TRIGGER OF DELETE --" + "\n";
        triggerDelete += "CREATE OR REPLACE FUNCTION " + nameFunction + "\n";
        triggerDelete += "RETURNS TRIGGER AS" + "\n";
        triggerDelete += "$$" + "\n";
        triggerDelete += "BEGIN" + "\n";

        triggerDelete += computeDeleteTriggerMethodStandart(0);

        triggerDelete += "RETURN OLD;" + "\n";

        triggerDelete += "END;" + "\n";
        triggerDelete += "$$ " + "LANGUAGE plpgsql;";

        triggerDelete += "\n" + "\n";
        triggerDelete += "CREATE TRIGGER " + nameTrigger + " BEFORE DELETE" + "\n";
        triggerDelete += "ON " + tableName + " FOR EACH ROW" + "\n";
        triggerDelete += "EXECUTE PROCEDURE " + nameFunction + ";" + "\n";

        return triggerDelete;
    }

    /**
     * Compute Trigger of Update - Método Custom
     *
     * @param index
     * @return
     */
    private String computeUpdateTriggerMethodCustom(Integer index) {
        State currentState;
        ArrayList<State> currentNextStates;
        ArrayList<StandartExpression> currentExpressions;
        String expression = "\n";

        if (index < states.size()) {
            currentState = states.get(index);
            currentNextStates = currentState.getNextStates();
            currentExpressions = currentState.getStandartExpressions();

            expression += "IF( " + currentState.getCustomExpression().sqlNotationRepresentation("OLD") + " ) THEN \n";
            if (currentNextStates.size() >= 1) {
                expression += "\t" + "IF( NOT(" + getNextStatesSyntaxSQLMethodCustom(currentNextStates) + ") ) THEN \n";
                expression += "\t" + "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE ATUALIZAÇÃO RECUSADA';" + "\n";
                expression += "\t" + "END IF;" + "\n";
            } else {
                expression += "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE ATUALIZAÇÃO RECUSADA';" + "\n";
            }
            expression += "ELSE" + "\n";
            expression += computeUpdateTriggerMethodCustom(index + 1);
            expression += "END IF;" + "\n";
        } else {
            return expression;
        }

        return expression;
    }

    /**
     * Compute Trigger of Update - Método Standart
     *
     * @param index
     * @return
     */
    private String computeUpdateTriggerMethodStandart(Integer index) {
        State currentState;
        ArrayList<State> currentNextStates;
        ArrayList<StandartExpression> currentExpressions;
        String expression = "\n";

        if (index < states.size()) {
            currentState = states.get(index);
            currentNextStates = currentState.getNextStates();
            currentExpressions = currentState.getStandartExpressions();

            expression += "IF( " + getStateSyntaxSQLMethodStandart(currentState, "OLD") + " ) THEN \n";
            if (currentNextStates.size() >= 1) {
                expression += "\t" + "IF( NOT(" + getNextStatesSyntaxSQLMethodStandart(currentNextStates) + ") ) THEN \n";
                expression += "\t" + "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE ATUALIZAÇÃO RECUSADA';" + "\n";
                expression += "\t" + "END IF;" + "\n";
            } else {
                expression += "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE ATUALIZAÇÃO RECUSADA';" + "\n";
            }
            expression += "ELSE" + "\n";
            expression += computeUpdateTriggerMethodStandart(index + 1);
            expression += "END IF;" + "\n";
        } else {
            return expression;
        }

        return expression;
    }

    /**
     * Compute Trigger of Delete - Método Custom
     *
     * @param index
     * @return
     */
    private String computeDeleteTriggerMethodCustom(Integer index) {
        State currentState;
        String expression = "\n";
        Integer last = lastStates.size() - 1;
        if (index < lastStates.size()) {
            currentState = lastStates.get(index);

            expression += "IF( NOT" + currentState.getCustomExpression().sqlNotationRepresentation("OLD") + ") THEN \n";
            if (index == last) {
                expression += "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE EXCLUSÃO RECUSADA';" + "\n";
            } else {
                expression += computeDeleteTriggerMethodCustom(index + 1);
            }
            expression += "END IF;" + "\n";

        } else {
            return expression;
        }

        return expression;
    }

    /**
     * Compute Trigger of Delete - Método Standart
     *
     * @param index
     * @return
     */
    private String computeDeleteTriggerMethodStandart(Integer index) {
        State currentState;
        String expression = "\n";
        Integer last = lastStates.size() - 1;
        if (index < lastStates.size()) {
            currentState = lastStates.get(index);

            expression += "IF( NOT(" + getStateSyntaxSQLMethodStandart(currentState, "OLD") + ") ) THEN \n";
            if (index == last) {
                expression += "\t" + "RAISE EXCEPTION 'OPERAÇÃO DE EXCLUSÃO RECUSADA';" + "\n";
            } else {
                expression += computeDeleteTriggerMethodStandart(index + 1);
            }
            expression += "END IF;" + "\n";

        } else {
            return expression;
        }

        return expression;
    }

    /**
     * Calcula a expressão de próximos estados permitidos - Método Custom
     *
     * @param nextStates
     * @return
     */
    private String getNextStatesSyntaxSQLMethodCustom(ArrayList<State> nextStates) {
        String expression = "";
        ArrayList<State> currentNextStates = nextStates;
        State currentState;
        Integer first = 0;

        for (int i = 0; i < currentNextStates.size(); i++) {
            currentState = currentNextStates.get(i);
            if (i != first) {
                expression += "OR ";
            }

            expression += currentState.getCustomExpression().sqlNotationRepresentation("NEW");
        }

        return expression;
    }

    /**
     * Calcula a expressão de próximos estados permitidos - Método Standart
     *
     * @param nextStates
     * @return
     */
    private String getNextStatesSyntaxSQLMethodStandart(ArrayList<State> nextStates) {
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

            expression += getStateSyntaxSQLMethodStandart(currentState, "NEW");

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
    private String getStateSyntaxSQLMethodStandart(State state, String type) {
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

    /**
     * Retorna os atributos identificadores
     *
     * @return
     */
    public ArrayList<Attribute> getIdentifiersAttributes() {
        ArrayList<Attribute> identifiers = new ArrayList<>();

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isIdentifier()) {
                identifiers.add(attributes.get(i));
            }
        }
        return identifiers;
    }

    /**
     * Retorna os atributos monitoráveis
     *
     * @return
     */
    public ArrayList<Attribute> getMonitorableAttributes() {
        ArrayList<Attribute> monitorable = new ArrayList<>();

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).isMonitorable()) {
                monitorable.add(attributes.get(i));
            }
        }
        return monitorable;
    }

    /**
     * Adiciona Atributo
     *
     * @param attribute
     * @return
     */
    public boolean addAttribute(Attribute attribute) {
        boolean result;
        if (checkUniquenessAdd(attribute)) {
            result = attributes.add(attribute);
            if (result) {
                // Inserção efetuada com sucesso
                updateClassModel();
                return true;
            } else {
                // O attribute nao foi inserido
                updateClassModel();
                return false;
            }
        } else {
            // Atributo já existe
            updateClassModel();
            return false;
        }
    }

    /**
     * Edita Atributo
     *
     * @param attribute
     * @param index
     * @return
     */
    public boolean editAttribute(Attribute attribute, int index) {

        if (checkUniquenessEdit(attribute, index)) {
            attributes.remove(index);
            attributes.add(index, attribute);
            updateClassModel();
            return true;
        } else {
            // Atributo ja existe
            updateClassModel();
            return false;
        }
    }

    /**
     * Remove Atributo
     *
     * @param attribute
     * @return
     */
    public boolean removeAttribute(Attribute attribute) {
        boolean result = attributes.remove(attribute);
        updateClassModel();
        return result;
    }

    /**
     * Verifica a unicidade do Atributo ao Adicionar
     *
     * @param resumeAttribute
     * @return
     */
    public boolean checkUniquenessAdd(Attribute newAttribute) {
        Attribute a;
        for (int i = 0; i < attributes.size(); i++) {
            a = attributes.get(i);
            if (a.getResume().equals(newAttribute.getResume())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica a unicidade do Atributo ao Editar
     *
     * @return
     */
    public boolean checkUniquenessEdit(Attribute newAttribute, int index) {
        Attribute a;
        for (int i = 0; i < attributes.size(); i++) {
            if (i != index) {
                a = attributes.get(i);
                if (a.getResume().equals(newAttribute.getResume())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Remove Atributo
     *
     * @return
     */
    public boolean removeAttribute(int index) {
        Object resultado = attributes.remove(index);

        if (resultado != null) {
            // remoção efetuada com sucesso
            updateClassModel();
            return true;
        } else {
            // Não existe a chave, portanto o objeto não foi removido
            updateClassModel();
            return false;
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public Attribute getAttribute(int index) {
        Attribute resultado = attributes.get(index);

        if (resultado != null) {
            // Objeto encontrado
            return resultado;
        } else {
            // A chave passada por parâmetro não existe no Mapa
            return null;
        }
    }

    /**
     *
     * @param resumeAttribute
     * @return o Atributo
     */
    public Attribute getAttribute(String resumeAttribute) {
        Attribute a;
        for (int i = 0; i < attributes.size(); i++) {
            a = attributes.get(i);
            if (a.getResume().equals(resumeAttribute)) {
                return a;
            }
        }
        return null;
    }

    /**
     *
     * @param attribute
     * @return o indice do attribute no array
     */
    public int getIndex(Attribute attribute) {
        Attribute a;
        for (int i = 0; i < attributes.size(); i++) {
            a = attributes.get(i);
            if (a.getResume().equals(attribute.getResume())) {
                return i;
            }
        }
        return 0;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.attributes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassModel other = (ClassModel) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.attributes, other.attributes)) {
            return false;
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateClassModel();
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
        updateClassModel();
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    public void setMethods(ArrayList<Method> methods) {
        this.methods = methods;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public boolean isGeneratedTableCode() {
        return generatedTableCode;
    }

    public void setGeneratedTableCode(boolean generatedTableCode) {
        this.generatedTableCode = generatedTableCode;
    }

    public String getTriggersCode() {
        return triggersCode;
    }

    public void setTriggersCode(String triggersCode) {
        this.triggersCode = triggersCode;
    }

    public boolean isGeneratedTriggersCode() {
        return generatedTriggersCode;
    }

    public void setGeneratedTriggersCode(boolean generatedTriggersCode) {
        this.generatedTriggersCode = generatedTriggersCode;
    }

    public DiagramOfStateModel getDiagramOfStateModel() {
        return diagramOfStateModel;
    }

    public void setDiagramOfStateModel(DiagramOfStateModel diagramOfStateModel) {
        this.diagramOfStateModel = diagramOfStateModel;
    }

    public boolean isInitializedDiagramOfStateModel() {
        return initializedDiagramOfStateModel;
    }

    public void setInitializedDiagramOfStateModel(boolean initializedDiagramOfStateModel) {
        this.initializedDiagramOfStateModel = initializedDiagramOfStateModel;
    }

    public boolean isUpdatedTableCode() {
        return updatedTableCode;
    }

    public void setUpdatedTableCode(boolean updatedTableCode) {
        this.updatedTableCode = updatedTableCode;
    }

    /**
     * Refere-se a modificação do diagrama de estados
     *
     * @return
     */
    public boolean isUpdatedTriggersCode() {
        this.updatedTriggersCode = diagramOfStateModel.isModified();
        return updatedTriggersCode;
    }

    /**
     * refere-se a modificação do diagrama de estados
     *
     * @param updatedTriggersCode
     */
    public void setUpdatedTriggersCode(boolean updatedTriggersCode) {
        this.updatedTriggersCode = updatedTriggersCode;
        diagramOfStateModel.setModified(this.updatedTriggersCode);
    }

    public boolean isCheckName() {
        return checkName;
    }

    public void setCheckName(boolean checkName) {
        this.checkName = checkName;
    }

    public boolean isContainsAttributes() {
        return containsAttributes;
    }

    public void setContainsAttributes(boolean containsAttributes) {
        this.containsAttributes = containsAttributes;
    }

    public boolean isInitializedDictionaryOfStates() {
        return initializedDictionaryOfStates;
    }

    public void setInitializedDictionaryOfStates(boolean initializedDictionaryOfStates) {
        this.initializedDictionaryOfStates = initializedDictionaryOfStates;
    }

    public boolean isMethodElaborationCustom() {
        return methodElaborationCustom;
    }

    public void setMethodElaborationCustom(boolean methodElaborationCustom) {
        this.methodElaborationCustom = methodElaborationCustom;
    }
}
