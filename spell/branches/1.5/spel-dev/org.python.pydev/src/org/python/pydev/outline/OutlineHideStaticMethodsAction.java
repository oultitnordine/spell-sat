package org.python.pydev.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.python.pydev.core.bundle.ImageCache;
import org.python.pydev.parser.jython.SimpleNode;
import org.python.pydev.parser.jython.ast.FunctionDef;
import org.python.pydev.parser.jython.ast.Name;
import org.python.pydev.parser.jython.ast.decoratorsType;
import org.python.pydev.ui.UIConstants;

/**
 * Action that will hide the static methods in the outline
 * 
 * Note: This only works with the 'staticmethod' decorator.
 * 
 * @author laurent.dore
 */
public class OutlineHideStaticMethodsAction extends AbstractOutlineFilterAction {

    private static final String PREF_HIDE_STATICMETHODS = "org.python.pydev.OUTLINE_HIDE_STATICMETHODS";

    public OutlineHideStaticMethodsAction(PyOutlinePage page, ImageCache imageCache) {
        super("Hide Static Methods", page, imageCache, PREF_HIDE_STATICMETHODS, UIConstants.STATIC_MEMBER_HIDE_ICON);
    }

    /**
     * @return the filter used to hide comments
     */
    @Override
    protected ViewerFilter createFilter() {
        return new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof ParsedItem) {
                    ParsedItem item = (ParsedItem) element;

                    SimpleNode token = item.getAstThis().node;

                    //String name = null;
                    if (token instanceof FunctionDef) {
                        FunctionDef functionDefToken = (FunctionDef) token;
                        if(functionDefToken.decs != null){
                            for (decoratorsType decorator : functionDefToken.decs) {
                                if (decorator.func instanceof Name) {
                                    Name decoratorFuncName = (Name) decorator.func;
                                    if (decoratorFuncName.id.equals("staticmethod")) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
        };
    }
}
