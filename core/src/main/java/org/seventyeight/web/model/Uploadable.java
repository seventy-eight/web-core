package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface Uploadable extends Describable<Uploadable> {

    public UploadableDescriptor getDescriptor();
}
