package org.jetbrains.kotlin.ui.debug;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.load.kotlin.PackageClassUtils;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.JetClass;
import org.jetbrains.kotlin.psi.JetFile;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public class KotlinLineBreakpointAdapter implements IToggleBreakpointsTarget {
    @Override
    public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
        ITextEditor editor = getEditor(part);
        if (editor != null) {
            IResource resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
            int lineNumber = ((ITextSelection) selection).getStartLine() + 1;
            IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
            String typeName = getTypeName(document, lineNumber, (IFile) resource);
            
            IJavaLineBreakpoint existingBreakpoint = findKotlinBreakpoint(resource, lineNumber);
            if (existingBreakpoint != null) {
                existingBreakpoint.delete();
            } else {
                Map<String, Object> attributes = new HashMap<String, Object>(10);
                int charstart = -1, charend = -1;
                try {
                    IRegion line = document.getLineInformation(lineNumber - 1);
                    charstart = line.getOffset();
                    charend = charstart + line.getLength();
                }   
                catch (BadLocationException e) {
                    KotlinLogger.logAndThrow(e);
                }
                
                
                IJavaLineBreakpoint br = JDIDebugModel.createLineBreakpoint(resource, typeName, lineNumber, charstart, charend, 0, true, attributes);
                DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(br);
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
    private IJavaLineBreakpoint findKotlinBreakpoint(IResource resource, int lineNumber) throws CoreException {
        String markerType = KotlinLineBreakpoint.KOTLIN_LINE_BREAKPOINT_MARKER;
        IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
        IBreakpoint[] breakpoints = manager.getBreakpoints(JDIDebugModel.getPluginIdentifier());
        for (IBreakpoint breakpoint : breakpoints) {
            if (!(breakpoint instanceof KotlinLineBreakpoint)) {
                continue;
            }
            
            KotlinLineBreakpoint kotlinLineBreakpoint = (KotlinLineBreakpoint) breakpoint;
            IMarker marker = kotlinLineBreakpoint.getMarker();
            if (marker != null && marker.exists() && markerType.equals(marker.getType())) {
                if (kotlinLineBreakpoint.getLineNumber() == lineNumber) {
                    return kotlinLineBreakpoint;
                }
            }
        }
        
        return null;
    }
    
    @NotNull
    private String getTypeName(@NotNull IDocument document, int lineNumber, @NotNull IFile file) {
        JetFile kotlinParsedFile = KotlinPsiManager.getKotlinParsedFile(file);
        assert kotlinParsedFile != null;
        
        String typeName = null;
        try {
            typeName = findNearestType(document.getLineOffset(lineNumber - 1), kotlinParsedFile).asString();
        } catch (BadLocationException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        assert typeName != null;
        return typeName;
    }
    
    @NotNull
    private FqName findNearestType(int offset, @NotNull JetFile jetFile) {
        PsiElement element = jetFile.findElementAt(offset);
        JetClass jetClass = null;
        do {
            jetClass = PsiTreeUtil.getParentOfType(element, JetClass.class);
        } while (jetClass != null && jetClass.getFqName() == null);
        
        FqName fqName = null;
        if (jetClass != null) {
            fqName = jetClass.getFqName();
        } else {
            FqName packageFqName = jetFile.getPackageFqName();
            fqName = PackageClassUtils.getPackageClassFqName(packageFqName);
        }
        
        assert fqName != null : "FqName for non-empty file should not be null";
        
        return fqName;
    }
    
    @Nullable
    private ITextEditor getEditor(@NotNull IWorkbenchPart part) {
        if (part instanceof ITextEditor) {
            return (ITextEditor) part;
        }
        
        return (ITextEditor) part.getAdapter(ITextEditor.class);
    }
}