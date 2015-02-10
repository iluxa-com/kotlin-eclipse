package org.jetbrains.kotlin.aspects.debug.ui;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.JetFile;
import org.jetbrains.kotlin.psi.JetSimpleNameExpression;

public class KotlinSourceLookupNavigator {
	public static String findKotlinFile(String name) {
		IPath path = new Path(name);
		String lastSegment = path.lastSegment();
		if (name.endsWith(".kt")) {
			return some(lastSegment, path.removeLastSegments(1)).toOSString();
		}
		
		return name;
	}
	
	private static IPath some(String fileName, IPath packageNamePath) {
		for (IFile file : KotlinPsiManager.INSTANCE.getAllFiles()) {
			if (file.getName().equals(fileName)) {
				JetFile jetFile = KotlinPsiManager.INSTANCE.getParsedFile(file);
				List<Name> packageNames = jetFile.getPackageFqName().pathSegments();
				if (packageNamePath.segmentCount() == packageNames.size()) {
					boolean eq = true;
					for (int i = 0; i < packageNamePath.segmentCount(); ++i) {
						if (!packageNamePath.segment(i).equals(packageNames.get(i).getIdentifier())) {
							eq = false;
							break;
						}
					}
					
					if (eq) {
						return file.getFullPath();
					}
				}
			}
		}
		
		return null;
		
	}
}
