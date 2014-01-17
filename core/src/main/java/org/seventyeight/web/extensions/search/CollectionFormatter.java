package org.seventyeight.web.extensions.search;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public class CollectionFormatter extends SearchFormatter {
    @Override
    public void format( MongoDocument document, Node node ) {
        document.set("SNADE", "boom!");
    }

    @Override
    public String getName() {
        return "collection";
    }
}
