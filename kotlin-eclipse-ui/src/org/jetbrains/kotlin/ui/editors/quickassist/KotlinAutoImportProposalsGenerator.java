package org.jetbrains.kotlin.ui.editors.quickassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.eclipse.ui.utils.EditorUtil;
import org.jetbrains.kotlin.ui.editors.AnnotationManager;
import org.jetbrains.kotlin.ui.editors.DiagnosticAnnotation;
import org.jetbrains.kotlin.ui.editors.DiagnosticAnnotationUtil;
import org.jetbrains.kotlin.ui.editors.KotlinEditor;
import org.jetbrains.kotlin.ui.editors.quickfix.KotlinSearchTypeRequestor;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;

public class KotlinAutoImportProposalsGenerator extends KotlinQuickAssistProposalsGenerator {

    @Override
    @NotNull
    protected List<KotlinQuickAssistProposal> getProposals(@NotNull KotlinEditor kotlinEditor,
            @NotNull PsiElement psiElement) {
        Collection<IType> typeResolutions = Collections2.filter(findAllTypes(psiElement.getText()), new Predicate<IType>() {
            
            @Override
            public boolean apply(IType type) {
                try {
                    return Flags.isPublic(type.getFlags());
                } catch (JavaModelException e) {
                    KotlinLogger.logAndThrow(e);
                }
                
                return false;
            }
        });
        
        List<KotlinQuickAssistProposal> assistProposals = Lists.newArrayList();
        for (IType type : typeResolutions) {
            assistProposals.add(new KotlinAutoImportAssistProposal(type));
        }
        
        return assistProposals;
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement psiElement) {
        KotlinEditor editor = getActiveEditor();
        if (editor == null) {
            return false;
        }
        
        int caretOffset = getCaretOffset(editor);
        DiagnosticAnnotation annotation = DiagnosticAnnotationUtil.INSTANCE.getAnnotationByOffset(editor, caretOffset);
        if (annotation != null) {
            return annotation.isQuickFixable();
        }
        
        IMarker marker = DiagnosticAnnotationUtil.INSTANCE.getMarkerByOffset(EditorUtil.getFile(editor), caretOffset);
        if (marker != null) {
            return marker.getAttribute(AnnotationManager.IS_QUICK_FIXABLE, false);
        }
        
        return false;
    }
    
    @NotNull
    private List<IType> findAllTypes(@NotNull String typeName) {
        IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        List<IType> searchCollector = new ArrayList<IType>();
        TypeNameMatchRequestor requestor = new KotlinSearchTypeRequestor(searchCollector);
        try {
            SearchEngine searchEngine = new SearchEngine(); // make static?
            searchEngine.searchAllTypeNames(null, 
                    SearchPattern.R_EXACT_MATCH, 
                    typeName.toCharArray(), 
                    SearchPattern.R_EXACT_MATCH, 
                    IJavaSearchConstants.TYPE, 
                    scope, 
                    requestor,
                    IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
                    null);
        } catch (CoreException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return searchCollector;
    }
}
