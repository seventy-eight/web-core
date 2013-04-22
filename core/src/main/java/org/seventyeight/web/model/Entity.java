package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class Entity<T extends Entity<T>> extends AbstractNode<T> implements CreatableNode, Portraitable {

    private static Logger logger = Logger.getLogger( Entity.class );

    public Entity( Node parent, MongoDocument document ) {
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
    public Node getParent() {
        return parent;
    }

    @Override
    public Node getChild( String name ) {
        return null;
    }

}
