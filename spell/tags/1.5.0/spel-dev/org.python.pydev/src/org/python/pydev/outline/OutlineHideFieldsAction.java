package org.python.pydev.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.python.pydev.core.bundle.ImageCache;
import org.python.pydev.parser.jython.SimpleNode;
import org.python.pydev.parser.jython.ast.Attribute;
import org.python.pydev.parser.jython.ast.ClassDef;
import org.python.pydev.parser.jython.ast.Name;
import org.python.pydev.parser.visitors.scope.ASTEntry;
import org.python.pydev.ui.UIConstants;

/**
 * Action that will hide the fields in the outline
 * 
 * @author laurent.dore
 */
public class OutlineHideFieldsAction extends AbstractOutlineFilterAction {

    private static final String PREF_HIDE_FIELDS = "org.python.pydev.OUTLINE_HIDE_FIELDS";

    public OutlineHideFieldsAction(PyOutlinePage page, ImageCache imageCache) {
        super("Hide Fields", page, imageCache, PREF_HIDE_FIELDS, UIConstants.FIELDS_HIDE_ICON);
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
                    if (token instanceof Attribute) {
                        return false;
                    }
                    else if (token instanceof Name) {
                        if (parentElement instanceof ParsedItem) {
                            ParsedItem parentItem = (ParsedItem) parentElement;
                            if (parentItem != null) {
                                ASTEntry ast = parentItem.getAstThis();
                                if (ast != null) {
                                    SimpleNode parentToken = ast.node;
                                    
                                    if (parentToken instanceof ClassDef) {
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
