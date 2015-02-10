package org.jetbrains.kotlin.aspects.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;
import org.jetbrains.kotlin.idea.JetFileType;

public aspect KotlinSourceLookupAspect {

	pointcut getSourceName(Object object) : 
				args(object) 
				&& execution(String JavaSourceLookupParticipant.getSourceName(Object));

	String around(Object object) throws CoreException : getSourceName(object) {
		String sourceName = proceed(object);
		if (sourceName.endsWith(JetFileType.INSTANCE.getDefaultExtension())) {
			return KotlinSourceLookupNavigator.findKotlinSourceFile(sourceName);
		}
		
		return sourceName;
	}
}