/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;
import java.util.Objects;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class Transition implements Serializable {

    private static final long serialVersionUID = 7811814343827104978L;

    /**
     *
     */
    protected static int idStatic = 0;

    /**
     *
     */
    public static final String UPDATE = "UPDATE";

    /**
     *
     */
    public static final String INSERT = "INSERT";

    /**
     *
     */
    public static final String DELETE = "DELETE";

    /**
     * Identificador da transição
     */
    private int id;

    /**
     * Nome da transição
     */
    private String name;

    /**
     * Origem da transição
     */
    private BasicState source;

    /**
     * Alvo da transição
     */
    private BasicState target;

    /**
     * Operação
     */
    private String operation;

    /**
     *
     */
    private boolean initialTransition;

    /**
     *
     */
    private boolean finalTransition;

    /**
     *
     */
    private boolean internalTransition;

    /**
     *
     */
    private DiagramOfStateModel diagramOfStateModel;

    /**
     *
     */
    private boolean checkName;

    /**
     * Gera id
     *
     * @return
     */
    private static int generateId() {
        return idStatic++;
    }

    /**
     * Construtor Padrão
     */
    public Transition() {
        this("");
    }

    /**
     * Construtor
     *
     * @param nome
     */
    public Transition(String nome) {
        this(nome, null, null);
    }

    /**
     * Construtor
     *
     * @param nome
     * @param source
     * @param target
     */
    public Transition(String nome, BasicState source, BasicState target) {
        this.name = nome;
        this.source = source;
        this.target = target;
        this.id = generateId();
        this.initialTransition = false;
        this.finalTransition = false;
        this.internalTransition = false;
        this.operation = "";
    }

    protected void updateTransition() {
        // VERIFICA O NOME
        if (name.equals("Nova Transição")) {
            checkName = false;
        } else {
            checkName = true;
        }
    }

    public boolean validate() {
        return checkName;
    }

    /**
     * Classifica a transição quanto ao tipo no diagrama(INICIAL, FINAL,
     * INTERNA) e o tipo de operacao no banco de dados ( INSERT, UPDATE, DELETE)
     *
     */
    public void generateClassification() {
        if (source instanceof InitialState) {
            initialTransition = true;
            finalTransition = false;
            internalTransition = false;
            operation = "INSERT";
        } else {
            if (target instanceof FinalState) {
                initialTransition = false;
                finalTransition = true;
                internalTransition = false;
                operation = "DELETE";
            } else {
                initialTransition = false;
                finalTransition = false;
                internalTransition = true;
                operation = "UPDATE";
            }
        }
    }

    public boolean isInitialTransition() {
        return initialTransition;
    }

    public void setInitialTransition(boolean initialTransition) {
        this.initialTransition = initialTransition;
    }

    public boolean isFinalTransition() {
        return finalTransition;
    }

    public void setFinalTransition(boolean finalTransition) {
        this.finalTransition = finalTransition;
    }

    public boolean isInternalTransition() {
        return internalTransition;
    }

    public void setInternalTransition(boolean internalTransition) {
        this.internalTransition = internalTransition;
    }

    public BasicState getTarget() {
        return target;
    }

    public void setTarget(BasicState target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateTransition();
    }

    public BasicState getSource() {
        return source;
    }

    public void setSource(BasicState source) {
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public boolean isCheckName() {
        return checkName;
    }

    public void setCheckName(boolean checkName) {
        this.checkName = checkName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.source);
        hash = 59 * hash + Objects.hashCode(this.target);
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
        final Transition other = (Transition) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
