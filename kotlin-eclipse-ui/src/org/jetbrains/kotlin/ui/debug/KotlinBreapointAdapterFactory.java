package org.jetbrains.kotlin.ui.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.jetbrains.kotlin.ui.editors.KotlinEditor;

public class KotlinBreapointAdapterFactory implements IAdapterFactory {
    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        if (adaptableObject instanceof KotlinEditor) {
            if (adapterType == IToggleBreakpointsTarget.class) {
                return new KotlinLineBreakpointAdapter();
            }
        }
        
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IToggleBreakpointsTarget.class };
    }
}
