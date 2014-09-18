package org.jetbrains.kotlin.core.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

public class KotlinLineBreakpoint extends LineBreakpoint {
    public static final String MODEL_IDENTIFIER = "org.jetbrains.kotlin.core.debug.model";
    public static final String ID_MARKER = "org.jetbrains.kotlin.core.debug.lineBreakpoint";
    
    public KotlinLineBreakpoint(IResource resource, int lineNumber) throws CoreException {
        IMarker marker = resource.createMarker(ID_MARKER);
        setMarker(marker);
        setEnabled(true);
        ensureMarker().setAttribute(IMarker.LINE_NUMBER, lineNumber);
        ensureMarker().setAttribute(IBreakpoint.ID, MODEL_IDENTIFIER);
    }
    
    @Override
    public String getModelIdentifier() {
        return MODEL_IDENTIFIER;
    }
}
