/*
 * Created on Apr 30, 2006
 */
package org.python.pydev.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;


/**
 * Used if the interface also wants to be notified of input changes.
 * 
 * This is an extension to the IPyEditListener
 */
public interface IPyEditListener3 {

    /**
     * Called when the input of the editor is changed.
     * 
     * @param edit the editor that had the input changed
     * @param oldInput the old input of the editor
     * @param input the new input of the editor
     * @param monitor the monitor for the job that's making the notifications
     */
    void onInputChanged(PyEdit edit, IEditorInput oldInput, IEditorInput input, IProgressMonitor monitor);

}
