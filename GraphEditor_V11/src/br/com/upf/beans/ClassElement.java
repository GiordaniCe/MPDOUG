/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.beans;

import br.com.upf.view.ClassView;
import br.com.upf.view.GraphEditor;
import br.com.upf.view.internalframe.StateEditor;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author GiordaniAntonio
 */
public class ClassElement implements Serializable {

    private static final long serialVersionUID = 9219070299480682120L;

    /**
     *
     */
    private static int staticId = -1;

    public static int generateStaticId() {
        staticId++;
        return staticId;
    }

    /**
     *
     */
    private int id;

    /**
     *
     */
    private ClassModel classModel;
    
    /**
     * 
     */
    private ClassView classView;

    /**
     *
     */
    private StateEditor stateEditor;

    /**
     *
     */
    private GraphEditor graphEditor;

    /**
     * Contrutor Padrão
     */
    public ClassElement() {
    }

    /**
     * Construtor
     *
     * @param editor
     * @param classe
     */
    public ClassElement(GraphEditor editor, ClassModel classe) {
        this.classModel = classe;
        this.graphEditor = editor;
        this.id = generateStaticId();
        this.classModel.setId(id);

        this.stateEditor = new StateEditor(graphEditor, this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }

    public StateEditor getStateEditor() {
        return stateEditor;
    }

    public void setStateEditor(StateEditor stateEditor) {
        this.stateEditor = stateEditor;
    }

    public void setGraphEditor(GraphEditor graphEditor) {
        this.graphEditor = graphEditor;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.classModel);
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
        final ClassElement other = (ClassElement) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.classModel, other.classModel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return classModel.getName();
    }

    public ClassView getClassView() {
        return classView;
    }

    public void setClassView(ClassView classView) {
        this.classView = classView;
    }

}
