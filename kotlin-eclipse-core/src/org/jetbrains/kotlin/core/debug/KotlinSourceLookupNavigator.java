package org.jetbrains.kotlin.core.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.log.KotlinLogger;

public class KotlinSourceLookupNavigator {
	public static final KotlinSourceLookupNavigator INSTANCE = new KotlinSourceLookupNavigator();
	
	private KotlinSourceLookupNavigator() {
	}
	
	@Nullable
	public IPath findKotlinSourceFile(@NotNull IJavaStackFrame frame) {
		try {
			IFile kotlinSourceFile = findKotlinFileByClass(frame);
			return kotlinSourceFile != null ? kotlinSourceFile.getProjectRelativePath() : null;
		} catch (CoreException e) {
			KotlinLogger.logAndThrow(e);
		}
		
		return null;
	}
	
	@Nullable
	private IFile findKotlinFileByClass(@NotNull IJavaStackFrame frame) throws CoreException {
//		IJavaProject javaProject = JavaDebugUtils.resolveJavaProject(frame);
	    String declaringTypeName = frame.getDeclaringTypeName();
	    ISourceLocator sourceLocator = frame.getLaunch().getSourceLocator();
	    if (sourceLocator instanceof ISourceLookupDirector) {
            ISourceLookupDirector lookupDirector = (ISourceLookupDirector) sourceLocator;
            lookupDirector.setFindDuplicates(true);
            for (ISourceContainer container: lookupDirector.getSourceContainers()) {
                Object[] elements = container.findSourceElements(frame.getSourceName());
                Object[] classElements = container.findSourceElements(declaringTypeName);
                int x = 1;
            }
	        
	    }
		
		
//		for (IFile kotlinFile : KotlinPsiManager.INSTANCE.getFilesByProject(javaProject.getProject())) {
//			if (kotlinFile.getName().equals(sourceName)) {
//				JetFile jetFile = KotlinPsiManager.INSTANCE.getParsedFile(kotlinFile);
//				if (jetFile.getPackageFqName().equals(declaringPackage)) {
//					return kotlinFile;
//				}
//			}
//		}
		
		return null;
	}
	
}
