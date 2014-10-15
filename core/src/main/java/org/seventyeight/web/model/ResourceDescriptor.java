package org.seventyeight.web.model;

import java.util.ArrayList;
import java.util.List;

import org.seventyeight.web.Core;
import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.authorization.ACL.ACLDescriptor;
import org.seventyeight.web.extensions.AbstractPortrait;
import org.seventyeight.web.extensions.AbstractPortrait.AbstractPortraitDescriptor;
import org.seventyeight.web.extensions.Event;
import org.seventyeight.web.extensions.Event.EventDescriptor;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.extensions.Tags;
import org.seventyeight.web.extensions.Tags.TagsDescriptor;
import org.seventyeight.web.model.Action.ActionDescriptor;

public abstract class ResourceDescriptor<T extends AbstractNode<T>> extends NodeDescriptor<T> {

	protected ResourceDescriptor(Node parent) {
		super(parent);
	}
	
	public List<ACLDescriptor<?>> getACLDescriptors(Core core) {
		return core.getExtensionDescriptors(ACL.class);
	}
	
	public Class<ACL> getACLClass() {
		return ACL.class;
	}
	
	public List<TagsDescriptor> getTagsDescriptors(Core core) {
		return core.getExtensionDescriptors(Tags.class);
	}
	
	public Class<Tags> getTagsClass() {
		return Tags.class;
	}
	
	public List<EventDescriptor> getEventDescriptors(Core core) {
		return core.getExtensionDescriptors(Event.class);
	}
	
	public Class<Event> getEventClass() {
		return Event.class;
	}
	
	public List<AbstractPortraitDescriptor> getPortraitDescriptors(Core core) {
		return core.getExtensionDescriptors(AbstractPortrait.class);
	}
	
	public Class<AbstractPortrait> getPortraitClass() {
		return AbstractPortrait.class;
	}
	
	public List<NodeDescriptor<?>> getNodeDescriptors(Core core) {
		return core.getExtensionDescriptors(NodeExtension.class);
	}
	
	public Class<NodeExtension> getExtensionClass() {
		return NodeExtension.class;
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
