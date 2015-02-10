package org.jetbrains.kotlin.aspects.debug.ui;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.JetFile;

public class KotlinSourceLookupNavigator {
	public static String findKotlinSourceFile(String fullPath) {
		IPath path = new Path(fullPath);
		String fileName = path.lastSegment();
		
		return some(fileName, path.removeLastSegments(1)).toOSString();
	}
	
	private static IPath some(String fileName, IPath packageNamePath) {
		for (IFile ktFile : KotlinPsiManager.INSTANCE.getAllFiles()) {
			if (ktFile.getName().equals(fileName)) {
				JetFile jetFile = KotlinPsiManager.INSTANCE.getParsedFile(ktFile);
				List<Name> packageNames = jetFile.getPackageFqName().pathSegments();
				if (areEqualsPackages(packageNames, packageNamePath.segments())) {
					return ktFile.getProjectRelativePath();
				}
			}
		}
		
		return null;
		
	}
	
	private static boolean areEqualsPackages(List<Name> ktPackageSegments, String[] segments) {
		if (ktPackageSegments.size() != segments.length) {
			return false;
		}
		
		for (int i = 0; i < ktPackageSegments.size(); ++i) {
			if (!ktPackageSegments.get(i).asString().equals(segments[i])) {
				return false;
			}
		}
		
		return true;
	}
}
