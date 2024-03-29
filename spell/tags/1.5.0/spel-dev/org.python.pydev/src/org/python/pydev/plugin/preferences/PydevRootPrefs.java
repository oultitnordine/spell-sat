package org.python.pydev.plugin.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.python.pydev.core.docutils.StringUtils;
import org.python.pydev.plugin.PydevPlugin;

public class PydevRootPrefs  extends PreferencePage implements IWorkbenchPreferencePage{


    public PydevRootPrefs() {
        setDescription(StringUtils.format("Pydev version: %s", PydevPlugin.version)); 
    }

    protected Control createContents(Composite parent) {
        return parent;
    }

    public void init(IWorkbench workbench) {
    }
}
