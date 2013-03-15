package org.seventyeight.web.model;

public interface Describable extends Savable, Documented {
	public Descriptor<?> getDescriptor();
}
