/*
 * Created on Jul 20, 2004
 *
 * @author Fabio Zadrozny
 */
package org.python.pydev.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.python.pydev.core.ExtensionHelper;

/**
 * @author Fabio Zadrozny
 */
public class PyShowOutline extends PyAction{

    protected IEditorActionDelegate registered;
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        IEditorActionDelegate participant = getParticipant();
        if(participant != null){
            participant.run(action);
        }
    }
    
    public void setActiveEditor(IAction action, IEditorPart targetEditor){
        IEditorActionDelegate participant = getParticipant();
        if(participant != null){
            participant.setActiveEditor(action, targetEditor);
        }
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        IEditorActionDelegate participant = getParticipant();
        if(participant != null){
            participant.selectionChanged(action, selection);
        }
    }


    protected IEditorActionDelegate getParticipant() {
        if(registered != null){
            return registered;
        }
        
        registered = (IEditorActionDelegate) ExtensionHelper.getParticipant(getExtensionName());
        return registered;
    }

    protected String getExtensionName() {
        return ExtensionHelper.PYDEV_QUICK_OUTLINE;
    }

}
