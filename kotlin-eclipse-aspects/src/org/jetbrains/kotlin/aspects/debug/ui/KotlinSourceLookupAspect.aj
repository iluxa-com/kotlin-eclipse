package org.jetbrains.kotlin.aspects.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.debug.KotlinSourceLookupNavigator;
import org.jetbrains.kotlin.idea.JetFileType;

public aspect KotlinSourceLookupAspect {

	pointcut getSourceName(Object object) : 
				args(object) 
				&& execution(String JavaSourceLookupParticipant.getSourceName(Object));

	String around(Object object) throws CoreException : getSourceName(object) {
		String sourceName = proceed(object);
		if (sourceName.endsWith(JetFileType.INSTANCE.getDefaultExtension())) {
			IJavaStackFrame frame = getStackFrame(object);
			IPath kotlinSourcePath = KotlinSourceLookupNavigator.INSTANCE.findKotlinSourceFile(frame);
			
			return kotlinSourcePath != null ? kotlinSourcePath.toOSString() : sourceName;
		}
		
		return sourceName;
	}
	
	@Nullable
	private IJavaStackFrame getStackFrame(Object stackFrame) {
		if (stackFrame instanceof IAdaptable) {
			return (IJavaStackFrame) ((IAdaptable) stackFrame).getAdapter(IJavaStackFrame.class);
		}
		
		return null;
	}
}