package org.jetbrains.kotlin.ui.debug;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.debug.KotlinLineBreakpoint;

public class KotlinLineBreakpointAdapter implements IToggleBreakpointsTarget {
    @Override
    public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
        ITextEditor editor = getEditor(part);
        if (editor != null) {
            IResource resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
            int lineNumber = ((ITextSelection) selection).getStartLine() + 1;
            
            IBreakpoint breakpoint = getLineBreakpointAt(lineNumber, resource);
            if (breakpoint != null) {
                breakpoint.delete();
            } else {
                DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(new KotlinLineBreakpoint(resource, lineNumber));
            }
        }
    }
    
    @Override
    public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
        // TODO Auto-generated method stub
        return true;
    }
    
    @Override
    public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
        // TODO Auto-generated method stub
    }
    
    @Override
    public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
        // TODO Auto-generated method stub
        return true;
    }
    
    @Override
    public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
        // TODO Auto-generated method stub
    }
    
    @Override
    public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
        // TODO Auto-generated method stub
        return true;
    }
    
    @Nullable
    private ITextEditor getEditor(@NotNull IWorkbenchPart part) {
        if (part instanceof ITextEditor) {
            return (ITextEditor) part;
        }
        
        return (ITextEditor) part.getAdapter(ITextEditor.class);
    }
    
    @Nullable
    private IBreakpoint getLineBreakpointAt(int lineNumber, @NotNull IResource resource) throws CoreException {
        IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
                .getBreakpoints(KotlinLineBreakpoint.MODEL_IDENTIFIER);
        for (IBreakpoint breakpoint : breakpoints) {
            if (resource.equals(breakpoint.getMarker().getResource())) {
                if (breakpoint instanceof ILineBreakpoint) {
                    if (((ILineBreakpoint) breakpoint).getLineNumber() == lineNumber) {
                        return breakpoint;
                    }
                }
            }
        }
        
        return null;
    }
}