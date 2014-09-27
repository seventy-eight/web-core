package org.seventyeight.web.widgets;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.servlet.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class MyActivitiesWidget extends Widget {

    @Override
    public Node getParent() {
        return null;  /* Implementation is a no op */
    }

    @Override
    public String getDisplayName() {
        return "My activities";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public String getName() {
        return "My activities widget";
    }

    public List<AbstractNode<?>> getActivities(Request request) throws NotFoundException, ItemInstantiationException {
    	// Groups
    	List<String> groupIds = Group.getGroupIds(request.getUser());
    	groupIds.add(request.getUser().getIdentifier());
    	groupIds.add(ACL.ALL);
    	
        MongoDocument sort = new MongoDocument().set( "updated", -1 );
        MongoDBQuery query = new MongoDBQuery().notExists( "parent" ).in("ACL.read", groupIds);
        query.notExists( "relations." + Relation.BasicRelation.CREATED_FOR.getName() );
        query.notEquals( "visibility", AbstractNode.Visibility.INVISIBLE.name() );
        
        System.out.println("BUUM: " + query);
        
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, 0, 10, sort );
        List<AbstractNode<?>> nodes = new ArrayList<AbstractNode<?>>( docs.size() );

        Core core = request.getCore();

        for( MongoDocument d : docs ) {
            AbstractNode<?> n = core.getNodeById( this, d.getIdentifier() );
            //d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );
            nodes.add( n );

            //nodes.add( new Activity( d ) );
        }

        return nodes;
    }
}
