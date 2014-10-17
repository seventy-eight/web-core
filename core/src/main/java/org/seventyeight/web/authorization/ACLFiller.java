package org.seventyeight.web.authorization;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.authorization.PublicACL.PublicACLDescriptor;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.DefaultInstanceFiller;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.model.ItemInstantiationException;

public class ACLFiller implements DefaultInstanceFiller {
	
	private static Logger logger = LogManager.getLogger(ACLFiller.class);
	
	private static final String ACLField = Descriptor.getJsonId(ACL.class.getName());

	@Override
	public boolean isApplicable(Descriptor<?> d) {
		return true;
	}

	@Override
	public void fill(Core core, MongoDocument document) {
		MongoDocument doc;
		if(document.contains(AbstractExtension.EXTENSIONS)) {
			logger.debug("DOES CONTAIN EXTENSIONS");
			doc = document.get(AbstractExtension.EXTENSIONS);
		} else {
			logger.debug("DOES NOT CONTAIN EXTENSIONS");
			doc = new MongoDocument();
			document.set(AbstractExtension.EXTENSIONS, doc);
		}
		
		if(doc.contains(ACLField)) {
			logger.debug("Has ACL! skipping.");
			return;
		}
		
    	PublicACLDescriptor d = core.getDescriptor(PublicACL.class);
    	
		try {
			PublicACL instance = d.newInstance(core, null);
			instance.updateNode(null);
			logger.debug("FILLING");
			doc.set(ACLField, instance.getDocument());
		} catch (ItemInstantiationException e) {
			logger.log(Level.ERROR, "Unable to apply filler", e);
		}
	}
}
