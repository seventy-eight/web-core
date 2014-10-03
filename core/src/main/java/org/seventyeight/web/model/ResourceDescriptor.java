package org.seventyeight.web.model;

import java.util.List;

import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.Tags;
import org.seventyeight.web.extensions.Tags.TagsDescriptor;

public abstract class ResourceDescriptor<T extends AbstractNode<T>> extends NodeDescriptor<T> {

	protected ResourceDescriptor(Node parent) {
		super(parent);
	}
	
	public List<TagsDescriptor> get(Core core) {
		return core.getExtensionDescriptors(Tags.class);
	}
}
