/*
 * Created on Jul 19, 2004
 *
 * @author Fabio Zadrozny
 */
package org.python.pydev.editor.actions.codefolding;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.python.pydev.core.docutils.PySelection;
import org.python.pydev.editor.actions.PyAction;

/**
 * This could be also called expand...
 * 
 * @author Fabio Zadrozny
 */
public class PyUnCollapse extends PyAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        PySelection ps = new PySelection(getTextEditor());

        ProjectionAnnotationModel model = (ProjectionAnnotationModel) getTextEditor()
                .getAdapter(ProjectionAnnotationModel.class);

        if (model != null) {
            model.expandAll(ps.getAbsoluteCursorOffset(), ps.getSelLength());
        }

    }

}