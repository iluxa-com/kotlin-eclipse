package org.jetbrains.kotlin.core.debug;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;
import org.jetbrains.annotations.NotNull;

public class KotlinLineBreakpoint extends JavaLineBreakpoint {
    public static final String KOTLIN_LINE_BREAKPOINT_MARKER = "org.jetbrains.kotlin.core.debug.lineBreakpointMarker";
    
    public KotlinLineBreakpoint(
            @NotNull IResource resource, 
            @NotNull String typeName,
            int lineNumber, int charStart, int charEnd, int hitCount,
            boolean add, 
            @NotNull Map<String, Object> attributes) throws DebugException {
        super(resource, typeName, lineNumber, charStart, charEnd, hitCount, add,
                attributes, KOTLIN_LINE_BREAKPOINT_MARKER);
    }
    
    public static KotlinLineBreakpoint createKotlinLineBreakpoint(
            @NotNull IResource resource,
            @NotNull String typeName, 
            int lineNumber, int charStart, int charEnd) throws DebugException {
        Map<String, Object> attributes = new HashMap<String, Object>(10);
        return new KotlinLineBreakpoint(resource, typeName, lineNumber, charStart, charEnd, 0, true, attributes);
    }
}
