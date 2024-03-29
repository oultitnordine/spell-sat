package org.python.pydev.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.python.pydev.core.bundle.ImageCache;
import org.python.pydev.parser.jython.SimpleNode;
import org.python.pydev.parser.jython.ast.Import;
import org.python.pydev.parser.jython.ast.ImportFrom;
import org.python.pydev.parser.visitors.scope.ASTEntryWithChildren;
import org.python.pydev.ui.UIConstants;

/**
 * Action that will hide the imports in the outline
 * 
 * @author Fabio
 */
public class OutlineHideImportsAction extends AbstractOutlineFilterAction {

    private static final String PREF_HIDE_IMPORTS = "org.python.pydev.OUTLINE_HIDE_IMPORTS";


    public OutlineHideImportsAction(PyOutlinePage page, ImageCache imageCache) {
        super("Hide Imports", page, imageCache, PREF_HIDE_IMPORTS, UIConstants.IMPORT_ICON);
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
                    ASTEntryWithChildren astThis = item.getAstThis();
                    if(astThis != null){
                        SimpleNode n = astThis.node;
                        if (n instanceof ImportFrom || n instanceof Import) {
                            return false;
                        }
                    }
                }
                return true;
            }

        };
    }

}
