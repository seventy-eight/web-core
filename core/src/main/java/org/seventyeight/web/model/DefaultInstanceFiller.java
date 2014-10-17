package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

public interface DefaultInstanceFiller {
	public boolean isApplicable(Descriptor<?> d);
	public void fill(Core core, MongoDocument document);
}
