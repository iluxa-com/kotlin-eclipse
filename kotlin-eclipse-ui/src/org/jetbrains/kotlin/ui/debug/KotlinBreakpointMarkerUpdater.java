package org.jetbrains.kotlin.ui.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;
import org.jetbrains.kotlin.core.debug.KotlinLineBreakpoint;

public class KotlinBreakpointMarkerUpdater implements IMarkerUpdater {

    @Override
    public String getMarkerType() {
        return KotlinLineBreakpoint.KOTLIN_LINE_BREAKPOINT_MARKER;
    }

    @Override
    public String[] getAttribute() {
        return new String[] { IMarker.LINE_NUMBER };
    }

    @Override
    public boolean updateMarker(IMarker marker, IDocument document, Position position) {
        // TODO Auto-generated method stub
        return true;
    }
}
