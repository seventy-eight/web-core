package org.seventyeight.web.extensions;

import org.seventyeight.web.model.AbstractExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ExtensionGroup {

    private String name;

    private boolean multiple = false;

    private List<AbstractExtension.ExtensionDescriptor<?>> descriptors = new ArrayList<AbstractExtension.ExtensionDescriptor<?>>();

    public ExtensionGroup( String name ) {
        this.name = name;
    }

    public ExtensionGroup( String name, boolean multiple ) {
        this.name = name;
        this.multiple = multiple;
    }

    public String getName() {
        return name;
    }

    public void addDescriptor(AbstractExtension.ExtensionDescriptor<?> descriptor) {
        descriptors.add( descriptor );
    }

    public List<AbstractExtension.ExtensionDescriptor<?>> getDescriptors() {
        return descriptors;
    }

    @Override
    public String toString() {
        return name;
    }
}
