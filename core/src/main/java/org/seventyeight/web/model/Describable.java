package org.seventyeight.web.model;

public interface Describable<T extends Describable<T>> extends Savable, Documented {
	public Descriptor<T> getDescriptor();
}
