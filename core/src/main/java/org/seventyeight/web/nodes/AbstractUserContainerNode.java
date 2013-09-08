package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class AbstractUserContainerNode<C extends User, T extends AbstractUserContainerNode<C, T>> extends Resource<T> {

    private static Logger logger = Logger.getLogger( AbstractUserContainerNode.class );

    public AbstractUserContainerNode( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public abstract String getField();

    public abstract C getElement( MongoDocument d );

    public String getCollectionName() {
        return Core.NODE_COLLECTION_NAME;
    }

    protected List<C> getElements() {
        logger.debug( "Getting elements for " + this );

        MongoDBQuery q = new MongoDBQuery().is( getField(), getIdentifier() );
        List<MongoDocument> docs = MongoDBCollection.get( getCollectionName() ).find( q );

        logger.debug( "DOCS ARE: " + docs );

        List<C> elements = new ArrayList<C>( docs.size() );

        for( MongoDocument d : docs ) {
            elements.add( getElement( d ) );
        }

        return elements;
    }

    protected List<T> getElementsForUser( C user ) {
        logger.debug( "Listing elements for " + this );

        List<MongoDocument> docs = document.getMappedList( getField(), getCollectionName() );

        List<T> groups = new ArrayList<T>( docs.size() );

        for( MongoDocument d : docs ) {
            //groups.add( new Group( this, d ) );
        }

        return groups;
    }

    /**
     * Determine whether or not the {@link User} is member of this {@link Group}.
     */
    protected boolean isMember( C c ) {
        List<Group> groups = c.getGroups();

        for( Group g : groups ) {
            if( this.equals( g ) ) {
                return true;
            }
        }

        return false;
    }
}
