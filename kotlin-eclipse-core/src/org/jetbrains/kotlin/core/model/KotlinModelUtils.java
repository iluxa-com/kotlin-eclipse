package org.jetbrains.kotlin.core.model;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jetbrains.annotations.NotNull;

public class KotlinModelUtils {
    public static void excludeKotlinFilesFromOutput(@NotNull IJavaProject javaProject) {
        String excludeFiles = javaProject.getOption(JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, true);
        if (excludeFiles.contains("*.kt")) {
            if (!excludeFiles.isEmpty()) {
                excludeFiles.concat(",");
            }
            excludeFiles.concat("*.kt");
        }
    }
}
