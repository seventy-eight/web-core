package org.seventyeight.web.model;

import java.util.ArrayList;
import java.util.List;

import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.Tags;
import org.seventyeight.web.extensions.Tags.TagsDescriptor;

public abstract class ResourceDescriptor<T extends AbstractNode<T>> extends NodeDescriptor<T> {

	protected ResourceDescriptor(Node parent) {
		super(parent);
	}
	
	public List<TagsDescriptor> getTagsDescriptors(Core core) {
		return core.getExtensionDescriptors(Tags.class);
	}

	/*
	@Override
	public List<List<Descriptor<?>>> getApplicableExtensions(Core core) {
		List<List<Descriptor<?>>> list = new ArrayList<List<Descriptor<?>>>();
		list.addAll(getTagDescriptors(core));
		
		return list;
	}
	*/
	
}
