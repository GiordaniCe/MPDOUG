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
public class DiagramOfClassModel implements Serializable {

    private static final long serialVersionUID = 6448011716010301292L;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private ArrayList<ClassElement> elements;

    /**
     * Construtor Padrão
     */
    public DiagramOfClassModel() {
        this("New Diagram");
    }

    /**
     * Construtor
     *
     * @param name
     */
    public DiagramOfClassModel(String name) {
        this.name = name;
        elements = new ArrayList<>();
    }

    /**
     * Adiciona ClasseElement
     *
     * @param element
     * @return
     */
    public boolean addClassElement(ClassElement element) {
        boolean result = elements.add(element);
        return result;
    }

    /**
     * Retorna ClassElement com o Identificador informado
     *
     * @param id
     * @return
     */
    public ClassElement getClassElement(Integer id) {
        ClassElement current;
        for (int i = 0; i < elements.size(); i++) {
            current = elements.get(i);
            if (current.getId() == id) {
                return current;
            }
        }
        return null;
    }

    /**
     * Edita ClassElement
     *
     * @param element
     * @return
     */
    public boolean editClassElement(ClassElement element) {
        int index = getIndexClassElement(element);
        if (index >= 0) {
            elements.remove(index);
            elements.add(index, element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove ClassElement informada
     *
     * @param element
     * @return
     */
    public boolean removeClassElement(ClassElement element) {
        boolean result = elements.remove(element);
        return result;
    }

    /**
     * Remove ClassElement informando a ClassModel
     *
     * @param classModel
     * @return
     */
    public boolean removeClassElement(ClassModel classModel) {
        ClassElement classElement = getIndexClassElement(classModel);
        boolean result = elements.remove(classElement);
        return result;
    }

    private Integer getIndexClassElement(ClassElement element) {
        ClassElement current;
        for (int i = 0; i < elements.size(); i++) {
            current = elements.get(i);
            if (current.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    private ClassElement getIndexClassElement(ClassModel classModel) {
        ClassElement current;
        for (int i = 0; i < elements.size(); i++) {
            current = elements.get(i);
            if (current.getId() == classModel.getId()) {
                return current;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ClassElement> getElements() {
        return elements;
    }

    public void setElements(ArrayList<ClassElement> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.elements);
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
        final DiagramOfClassModel other = (DiagramOfClassModel) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.elements, other.elements)) {
            return false;
        }
        return true;
    }

}
