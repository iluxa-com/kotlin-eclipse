package org.jetbrains.kotlin.aspects.debug.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;

public class KotlinSourceLookupNavigator {
	public static String findKotlinFile(String name) {
		IPath path = new Path(name);
		String lastSegment = path.lastSegment();
		return name;
	}
}
