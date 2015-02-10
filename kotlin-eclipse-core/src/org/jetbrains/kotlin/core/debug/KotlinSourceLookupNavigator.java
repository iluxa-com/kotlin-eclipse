package org.jetbrains.kotlin.core.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.name.FqName;

public class KotlinSourceLookupNavigator {
	public static final KotlinSourceLookupNavigator INSTANCE = new KotlinSourceLookupNavigator();
	
	private KotlinSourceLookupNavigator() {
	}
	
	@Nullable
	public IPath findKotlinSourceFile(@NotNull IJavaStackFrame frame) {
		try {
			IFile kotlinSourceFile = findKotlinFileByClass(frame);
			return kotlinSourceFile != null ? kotlinSourceFile.getProjectRelativePath() : null;
		} catch (DebugException e) {
			KotlinLogger.logAndThrow(e);
		}
		
		return null;
	}
	
	@Nullable
	private IFile findKotlinFileByClass(@NotNull IJavaStackFrame frame) throws DebugException {
//		IJavaProject javaProject = JavaDebugUtils.resolveJavaProject(frame);
		String sourceName = frame.getSourceName();    
		FqName declaringPackage = new FqName(frame.getDeclaringTypeName()).parent();
		
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
