package org.python.copiedfromeclipsesrc;

import org.python.pydev.core.docutils.WordUtils;

public class JDTNotAvailableException extends RuntimeException {
    
    public JDTNotAvailableException() {
        super(
                WordUtils.wrap("The selected operation could not be completed because the JDT plugin was not found in your configuration. " +
                "The JDT (java) plugin is required for operations that need a java interpreter (mainly for jython development). " +
                "So, if you want to use pydev to develop for JYTHON, please install the jdt plugin (available in the Eclipse SDK). " +
                "If that's not the case, please close any project you have configured as being a jython project.", 75)
                );
    }

}
