package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 24-02-13
 *         Time: 21:47
 */
public abstract class Entity extends AbstractNodeItem implements CreatableNode {

    private static Logger logger = Logger.getLogger( Entity.class );

    public Entity( NodeItem parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public List<Action> getActions() {
        logger.debug( "Getting actions for " + this );

        List<Action> actions = new ArrayList<Action>();
        List<AbstractExtension> extensions = getExtensions();

        /* For all extensions */
        for( AbstractExtension e : extensions ) {
            actions.addAll( e.getActions() );
        }

        logger.debug( "Found: " + actions );
        return actions;
    }

    @Override
    public NodeItem getParent() {
        return parent;
    }

    @Override
    public NodeItem getNode( String name ) {
        return null;
    }

}
