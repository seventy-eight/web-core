package org.seventyeight.web.extensions;

import org.seventyeight.web.model.AbstractExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ExtensionGroup {

    private String name;

    private Class<? extends AbstractExtension.ExtensionDescriptor> clazz;

    enum Type {
        one,
        single,
        multiple
    }

    private Type type;

    private List<AbstractExtension.ExtensionDescriptor> descriptors = new ArrayList<AbstractExtension.ExtensionDescriptor>();

    public ExtensionGroup( Class<? extends AbstractExtension.ExtensionDescriptor> clazz, String name ) {
        this.name = name;
        this.clazz = clazz;
        type = Type.one;
    }

    public ExtensionGroup( Class<? extends AbstractExtension.ExtensionDescriptor> clazz, String name, boolean multiple ) {
        this.name = name;
        this.clazz = clazz;
        if(multiple) {
            this.type = Type.multiple;
        } else {
            this.type = Type.single;
        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void addDescriptor(AbstractExtension.ExtensionDescriptor<?> descriptor) {
        descriptors.add( descriptor );
    }

    public List<AbstractExtension.ExtensionDescriptor> getDescriptors() {
        return descriptors;
    }

    public Class<? extends AbstractExtension.ExtensionDescriptor> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return name;
    }
}
