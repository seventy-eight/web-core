package org.seventyeight.web.widgets;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;

import java.util.ArrayList;
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

    public List<Activity> getActivities(Request request) throws NotFoundException, ItemInstantiationException {
        MongoDocument sort = new MongoDocument().set( "date", -1 );
        MongoDBQuery query = new MongoDBQuery();
        List<MongoDocument> docs = MongoDBCollection.get( Activity.ACTIVITY_COLLECTION ).find( query, 0, 10, sort );
        List<Activity> nodes = new ArrayList<Activity>( docs.size() );

        for( MongoDocument d : docs ) {
            //AbstractNode<?> n = Core.getInstance().getNodeById( this, d.getIdentifier() );
            //d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );
            //nodes.add( n );

            nodes.add( new Activity( d ) );
        }

        return nodes;
    }
}
