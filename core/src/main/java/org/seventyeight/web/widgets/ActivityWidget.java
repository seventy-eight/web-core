package org.seventyeight.web.widgets;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Widget;

import java.util.List;

/**
 * @author cwolfgang
 */
public class ActivityWidget extends Widget {

    @Override
    public Node getParent() {
        return null;  /* Implementation is a no op */
    }

    @Override
    public String getDisplayName() {
        return "Activity";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public String getName() {
        return "Activity widget";
    }

    public List<MongoDocument> getActivities() {
        MongoDocument sort = new MongoDocument().set( "updated", -1 );
        MongoDBQuery query = new MongoDBQuery();
        return MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( query, 0, 10, sort );
    }
}
