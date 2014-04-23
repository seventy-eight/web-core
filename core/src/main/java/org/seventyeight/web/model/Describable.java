package org.seventyeight.web.model;

public interface Describable<T extends Describable<T>> extends Savable, Documented, Node {
	public Descriptor<T> getDescriptor();
}
