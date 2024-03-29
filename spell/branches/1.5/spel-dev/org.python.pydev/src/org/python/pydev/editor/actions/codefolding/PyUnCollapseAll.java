/*
 * Created on Jul 22, 2004
 *
 * @author Fabio Zadrozny
 */
package org.python.pydev.editor.actions.codefolding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.python.pydev.editor.codefolding.PyProjectionAnnotation;

/**
 * @author Fabio Zadrozny
 */
public class PyUnCollapseAll extends PyFoldingAction {

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        final ProjectionAnnotationModel model = getModel();
        
        if (model != null) {
            
            Iterator iter = getAnnotationsIterator(model, true);
            
            
            if(iter != null){
                //we just want to expand the roots, and we are working only with the collapsed sorted by offset.
                
                List elements = new ArrayList(); //used to know the context
                while (iter.hasNext()) {
                    PyProjectionAnnotation element = (PyProjectionAnnotation) iter.next();
                    
                    //special case, we have none in our context
                    if(elements.size() == 0){
                        model.expand(element);
                        elements.add(element);
                    
                    } else{
                        if(isInsideLast(element, elements, model)){
                            //ignore
                            
                        }else{
                            //ok, the one in the top has to be collapsed ( and this one added )
                            model.expand(element);
                            elements.add(element);
                        }
                    }
                }
            }
        }
        
    }

}
