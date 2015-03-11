package org.jetbrains.kotlin.core.resolve.lang.java.structure

import org.eclipse.jdt.core.dom.IBinding
import org.jetbrains.kotlin.name.Name
import org.eclipse.jdt.core.IJavaProject
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument
import org.eclipse.jdt.core.dom.IAnnotationBinding
import org.eclipse.jdt.core.dom.IVariableBinding
	
public abstract class EclipseJavaAnnotationArgument<T : IBinding>(javaElement: T) : 
	EclipseJavaElement<T>(javaElement), JavaAnnotationArgument {
	
	override val name: Name?
		get() = Name.identifier(getBinding().getName())
	
	default object {
		fun create(value: Any, name: Name, javaProject: IJavaProject): JavaAnnotationArgument {
			return when (value) {
				is IAnnotationBinding -> EclipseJavaAnnotationAsAnnotationArgument(value, name)
				is IVariableBinding -> EclipseJavaReferenceAnnotationArgument(value)
				is Array<Any> -> EclipseJavaArrayAnnotationArgument(value, name, javaProject) 
				is Class<*> -> EclipseJavaClassObjectAnnotationArgument(value, name, javaProject)
				is String -> EclipseJavaLiteralAnnotationArgument(value, name)
				else -> throw IllegalArgumentException("Wrong annotation argument: $value")
			}
		}
	}
} 