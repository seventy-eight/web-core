package org.seventyeight.web.model;

public interface Describable<T extends Describable> extends Savable, Documented {
	public Descriptor<T> getDescriptor();
}
