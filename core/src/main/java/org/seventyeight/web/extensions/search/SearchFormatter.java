package org.seventyeight.web.extensions.search;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.ExtensionPoint;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public abstract class SearchFormatter implements ExtensionPoint {
    public abstract void format( MongoDocument document, final Node node );
    public abstract String getName();
}
