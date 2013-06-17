package org.seventyeight.web.actions;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Nodes implements Action, Node {

    private static Logger logger = Logger.getLogger( Nodes.class );

    public List<Node> getNodes( int offset, int number ) {
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( new MongoDBQuery(), offset, number );

        List<Node> nodes = new ArrayList<Node>( docs.size() );

        for( MongoDocument doc : docs ) {
            try {
                nodes.add( (Node) Core.getInstance().getItem( this, doc ) );
            } catch( ItemInstantiationException e ) {
                logger.warn( e.getMessage() );
            }
        }

        return nodes;
    }

    @Override
    public String getUrlName() {
        return "nodes";
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Nodes";
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
