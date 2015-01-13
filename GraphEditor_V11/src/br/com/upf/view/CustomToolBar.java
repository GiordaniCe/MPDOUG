/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.upf.view;

import br.com.upf.model.Actions.NewClassEditorAction;
import com.mxgraph.util.mxResources;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;

/**
 *
 * @author GiordaniAntonio
 */
public class CustomToolBar extends JToolBar {

    public CustomToolBar(GraphEditor editor) {
        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);
        setOrientation(JToolBar.HORIZONTAL);
        
        add(editor.bind(mxResources.get("new"), new NewClassEditorAction(),
				"/br/com/upf/images/new.gif"));
    }

}
