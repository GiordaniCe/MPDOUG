/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JOptionPane;

/**
 *
 * @author GiordaniAntonio
 */
public class CustomExpression implements Serializable {

    private static final long serialVersionUID = 764819069468850170L;

    private State state;

    private ArrayList<ElementExpression> elements;

    public CustomExpression() {
        this.elements = new ArrayList<>();
    }

    public CustomExpression(State state) {
        this.state = state;
        this.elements = new ArrayList<>();
    }

    public CustomExpression(State state, ArrayList<ElementExpression> elements) {
        this.state = state;
        this.elements = elements;
    }

    private void updateCustomExpression() {

    }

    /**
     * Validação da expressão, verifica se o estado possui ao menos uma
     * sub-expressão (BasicExpression) envolvendo cada atributo monitorado.
     *
     * @param monitored
     * @return
     */
    public boolean validateExpression(ArrayList<Attribute> monitored) {
        ArrayList<Attribute> monitoredAttributes = monitored;
        BasicExpression currentBasicExpression;
        for (int index = 0; index < elements.size(); index++) {
            if (elements.get(index) instanceof BasicExpression) {
                currentBasicExpression = (BasicExpression) elements.get(index);
                for (int j = 0; j < monitoredAttributes.size(); j++) {
                    if (monitoredAttributes.get(j).equals(currentBasicExpression.getAttribute())) {
                        monitoredAttributes.remove(j);
                    }
                }
            }
        }
        if (monitoredAttributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * REGRAS PARA VALIDAÇÃO DA SINTAXE 1 - A expressão deve iniciar por um abre
     * parenteses '(' e acabar por um fecha parenteses ')'. 2 - Os operadores
     * And e or devem ser precedidos e sucedidos por uma expressão básica. 3 - O
     * operador Not deve ser sucedido por uma expressão básica. 4 - Para cada
     * parenteses aberto deve haver um fechando.
     *
     * @return
     */
    public boolean validateSyntax() {
        if (elements.size() < 3) {
            return false;
        } else {
            return validationRuleOne() && validationRuleTwo() && validationRuleThree() && validationRuleFour();
        }
    }

    /**
     * VALIDAÇÃO - REGRA 1
     *
     * Verifica se a expressão inicia com um parenteses '(' e termina com um
     * parenteses ')'.
     *
     * @return
     */
    private boolean validationRuleOne() {
        if ((elements.get(0) instanceof OpenParenthesis) && (elements.get(elements.size() - 1) instanceof CloseParenthesis)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * VALIDAÇÃO - REGRA 2
     *
     * @return
     */
    private boolean validationRuleTwo() {
        int open = 0;
        int close = 0;

        for (int index = 0; index < elements.size(); index++) {
            if (elements.get(index) instanceof OpenParenthesis) {
                open++;
            } else {
                if (elements.get(index) instanceof CloseParenthesis) {
                    close++;
                }
            }
        }

        if (open == close) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * VALIDAÇÃO - REGRA 3
     *
     * Verifica se os operadores and e or presentes na expressão são precedidos
     * e sucedidos por sub-expressões ou entao por abre e fecha parenteses
     * respectivamente.
     *
     * @return
     */
    private boolean validationRuleThree() {
        ElementExpression currentElementExpression;
        Integer first = 0;
        Integer last = elements.size() - 1;
        for (int index = 0; index < elements.size(); index++) {
            currentElementExpression = elements.get(index);
            if ((currentElementExpression instanceof OperatorAnd) || (currentElementExpression instanceof OperatorOr)) {
                // Verificação de indices válidos
                if (!((index == first) || (index == last))) {
                    // Verifica se o elemento anterior e posterior são expressões básicas
                    if ((elements.get(index - 1) instanceof BasicExpression) && (elements.get(index + 1) instanceof BasicExpression)) {
                        // O operador lógico é precedido e sucedido por expressões básicas
                    } else {
                        if ((elements.get(index - 1) instanceof CloseParenthesis) && (elements.get(index + 1) instanceof OpenParenthesis)) {
                            // O operador lógico é precedido e sucedido por parenteses
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * VALIDAÇÃO - REGRA 4
     *
     * Verifica se o operador Not é sucedido por uma expressão básica!
     *
     * @return
     */
    private boolean validationRuleFour() {
        int last = elements.size() - 1;
        for (int index = 0; index < elements.size(); index++) {
            if (elements.get(index) instanceof OperatorNot) {
                if (index != last) {
                    // Verifica se o Operador Not é sucedido por uma expressão ou então por um abre parenteses
                    if ((elements.get(index + 1) instanceof BasicExpression) || elements.get(index + 1) instanceof OpenParenthesis) {
                        // Operador lógico Not é sucedido por uma expressão básica ou po um Abre parenteses
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adiciona Elemento a expressão
     *
     * @param element
     * @return
     */
    public boolean addElement(ElementExpression element) {
        boolean result;
        result = elements.add(element);
        if (result) {
            updateCustomExpression();
        } else {
            updateCustomExpression();
            result = false;
        }

        return result;
    }

    /**
     * Adiciona Elemento a expressão
     *
     * @param element
     * @param index
     * @return
     */
    public boolean addElement(ElementExpression element, Integer index) {
        boolean result;
        result = elements.remove(elements.get(index));
        if (result) {
            elements.add(index, element);
            updateCustomExpression();
        } else {
            updateCustomExpression();
            result = false;
        }
        return result;
    }

    /**
     * Edita elemento da expressão
     *
     * @param element
     * @return
     */
    public boolean editElement(ElementExpression element) {
        int index = getIndexElements(element);
        if (index >= 0) {
            elements.remove(index);
            elements.add(index, element);
            updateCustomExpression();
            return true;
        } else {
            updateCustomExpression();
            return false;
        }
    }

    /**
     * Remove elemento da expressão
     *
     * @param element
     * @return
     */
    public boolean removeElement(ElementExpression element) {
        boolean result = elements.remove(element);
        updateCustomExpression();
        return result;
    }

    /**
     *
     * @param expression
     * @return
     */
    private Integer getIndexElements(ElementExpression element) {
        ElementExpression current;
        for (int i = 0; i < elements.size(); i++) {
            current = elements.get(i);
            if (current.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ArrayList<ElementExpression> getElements() {
        return elements;
    }

    public void setElements(ArrayList<ElementExpression> elements) {
        this.elements = elements;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.state);
        hash = 47 * hash + Objects.hashCode(this.elements);
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
        final CustomExpression other = (CustomExpression) obj;
        if (!Objects.equals(this.state, other.state)) {
            return false;
        }
        if (!Objects.equals(this.elements, other.elements)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Estado: " + state + " Expressão: " + elements;
    }

    /**
     * Apresenta a expressão completa
     *
     * @return
     */
    public String getApresentation() {
        return sqlNotationRepresentation();
    }

    public String sqlNotationRepresentation() {
        ElementExpression currentElementExpression;
        BasicExpression currentBasicExpression;
        OperatorAnd currentOperatorAnd;
        OperatorOr currentOperatorOr;
        OperatorNot currentOperatorNot;
        OpenParenthesis currentOpenParenthesis;
        CloseParenthesis currentCloseParenthesis;

        String result = "";

        for (int i = 0; i < elements.size(); i++) {
            currentElementExpression = elements.get(i);
            // Expressão (BasicExpression)
            if (currentElementExpression instanceof BasicExpression) {
                currentBasicExpression = (BasicExpression) currentElementExpression;
                result += currentBasicExpression.sqlRepresetation() + " ";
            } else {
                // Operador And (OperatorAnd)
                if (currentElementExpression instanceof OperatorAnd) {
                    currentOperatorAnd = (OperatorAnd) currentElementExpression;
                    result += currentOperatorAnd.getSqlValue() + " ";
                } else {
                    // Operador Or (OperatorOr)
                    if (currentElementExpression instanceof OperatorOr) {
                        currentOperatorOr = (OperatorOr) currentElementExpression;
                        result += currentOperatorOr.getSqlValue() + " ";
                    } else {
                        // Operador Not (OperatorNot)
                        if (currentElementExpression instanceof OperatorNot) {
                            currentOperatorNot = (OperatorNot) currentElementExpression;
                            result += currentOperatorNot.getSqlValue() + " ";
                        } else {
                            // Abre Parenteses
                            if (currentElementExpression instanceof OpenParenthesis) {
                                currentOpenParenthesis = (OpenParenthesis) currentElementExpression;
                                result += currentOpenParenthesis.getDefaultValue() + " ";
                            } else {
                                // Fecha Parenteses
                                if (currentElementExpression instanceof CloseParenthesis) {
                                    currentCloseParenthesis = (CloseParenthesis) currentElementExpression;
                                    result += currentCloseParenthesis.getDefaultValue() + " ";
                                } else {
                                    JOptionPane.showMessageDialog(null, "Elemento não Identificado" + currentElementExpression);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Retorna a representação SQL da expressão, conforme qualificador recebido
     * por parâmetro(New ou Old).
     *
     * @param qualifying
     * @return
     */
    public String sqlNotationRepresentation(String qualifying) {
        ElementExpression currentElementExpression;
        BasicExpression currentBasicExpression;
        OperatorAnd currentOperatorAnd;
        OperatorOr currentOperatorOr;
        OperatorNot currentOperatorNot;
        OpenParenthesis currentOpenParenthesis;
        CloseParenthesis currentCloseParenthesis;

        String result = "";

        for (int i = 0; i < elements.size(); i++) {
            currentElementExpression = elements.get(i);
            // Expressão (BasicExpression)
            if (currentElementExpression instanceof BasicExpression) {
                currentBasicExpression = (BasicExpression) currentElementExpression;
                result += currentBasicExpression.sqlRepresentation(qualifying) + " ";
            } else {
                // Operador And (OperatorAnd)
                if (currentElementExpression instanceof OperatorAnd) {
                    currentOperatorAnd = (OperatorAnd) currentElementExpression;
                    result += currentOperatorAnd.getSqlValue() + " ";
                } else {
                    // Operador Or (OperatorOr)
                    if (currentElementExpression instanceof OperatorOr) {
                        currentOperatorOr = (OperatorOr) currentElementExpression;
                        result += currentOperatorOr.getSqlValue() + " ";
                    } else {
                        // Operador Not (OperatorNot)
                        if (currentElementExpression instanceof OperatorNot) {
                            currentOperatorNot = (OperatorNot) currentElementExpression;
                            result += currentOperatorNot.getSqlValue() + " ";
                        } else {
                            // Abre Parenteses
                            if (currentElementExpression instanceof OpenParenthesis) {
                                currentOpenParenthesis = (OpenParenthesis) currentElementExpression;
                                result += currentOpenParenthesis.getDefaultValue() + " ";
                            } else {
                                // Fecha Parenteses
                                if (currentElementExpression instanceof CloseParenthesis) {
                                    currentCloseParenthesis = (CloseParenthesis) currentElementExpression;
                                    result += currentCloseParenthesis.getDefaultValue() + " ";
                                } else {
                                    JOptionPane.showMessageDialog(null, "Elemento não Identificado" + currentElementExpression);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Retorna a representação UML da Expressão
     *
     * @return
     */
    public String umlNotationRepresentation() {
        ElementExpression currentElementExpression;
        BasicExpression currentBasicExpression;
        OperatorAnd currentOperatorAnd;
        OperatorOr currentOperatorOr;
        OperatorNot currentOperatorNot;
        OpenParenthesis currentOpenParenthesis;
        CloseParenthesis currentCloseParenthesis;

        String result = "";

        for (int i = 0; i < elements.size(); i++) {
            currentElementExpression = elements.get(i);
            // Expressão (BasicExpression)
            if (currentElementExpression instanceof BasicExpression) {
                currentBasicExpression = (BasicExpression) currentElementExpression;
                result += currentBasicExpression.umlRepresetation() + " ";
            } else {
                // Operador And (OperatorAnd)
                if (currentElementExpression instanceof OperatorAnd) {
                    currentOperatorAnd = (OperatorAnd) currentElementExpression;
                    result += currentOperatorAnd.getUmlValue() + " ";
                } else {
                    // Operador Or (OperatorOr)
                    if (currentElementExpression instanceof OperatorOr) {
                        currentOperatorOr = (OperatorOr) currentElementExpression;
                        result += currentOperatorOr.getUmlValue() + " ";
                    } else {
                        // Operador Not (OperatorNot)
                        if (currentElementExpression instanceof OperatorNot) {
                            currentOperatorNot = (OperatorNot) currentElementExpression;
                            result += currentOperatorNot.getUmlValue() + " ";
                        } else {
                            // Abre Parenteses
                            if (currentElementExpression instanceof OpenParenthesis) {
                                currentOpenParenthesis = (OpenParenthesis) currentElementExpression;
                                result += currentOpenParenthesis.getDefaultValue() + " ";
                            } else {
                                // Fecha Parenteses
                                if (currentElementExpression instanceof CloseParenthesis) {
                                    currentCloseParenthesis = (CloseParenthesis) currentElementExpression;
                                    result += currentCloseParenthesis.getDefaultValue() + " ";
                                } else {
                                    JOptionPane.showMessageDialog(null, "Elemento não Identificado" + currentElementExpression);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

}
