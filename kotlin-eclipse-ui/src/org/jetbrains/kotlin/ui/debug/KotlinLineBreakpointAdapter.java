package org.jetbrains.kotlin.ui.debug;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
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
import org.jetbrains.kotlin.core.debug.KotlinLineBreakpoint;
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
            JetFile kotlinParsedFile = KotlinPsiManager.getKotlinParsedFile((IFile) resource);
            assert kotlinParsedFile != null;
            
            String typeName = null;
            try {
                typeName = findNearestTopType(document.getLineOffset(lineNumber - 1), kotlinParsedFile).asString();
            } catch (BadLocationException e1) {
                KotlinLogger.logAndThrow(e1);
            }
            
            IJavaLineBreakpoint existingBreakpoint = JDIDebugModel.lineBreakpointExists(resource, typeName, lineNumber);
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
                    JDIDebugUIPlugin.log(e);
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
    
    @NotNull
    private FqName findNearestTopType(int offset, @NotNull JetFile jetFile) {
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