package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.utils.Date;
import org.seventyeight.web.Core;
import org.seventyeight.web.User;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 19-02-13
 *         Time: 13:45
 */
public abstract class AbstractItem extends AbstractModelObject implements Authorizer, Describable {

    private static Logger logger = Logger.getLogger( AbstractItem.class );

    public static final String MODERATORS = "moderators";
    public static final String VIEWERS = "viewers";

    public AbstractItem( MongoDocument document ) {
        super( document );
    }

    public boolean isOwner( User user ) {
        return true;
    }

    @Override
    public Authorization getAuthorization( User user ) throws AuthorizationException {

        /* First check ownerships */
        try {
            if( isOwner( user ) ) {
                return Authorization.MODERATE;
            }
        } catch( PersistenceException e ) {
            logger.warn( e );
        }

        List<MongoDocument> docs = document.getList( MODERATORS );
        for( MongoDocument d : docs ) {
            Authoritative a = null;
            try {
                a = (Authoritative) getItem( d );
            } catch( ItemInstantiationException e ) {
                throw new AuthorizationException( e );
            }
            if( a.isAuthoritative( user ) ) {
                return Authorization.MODERATE;
            }
        }

        List<MongoDocument> viewers = document.getList( VIEWERS );
        for( MongoDocument d : docs ) {
            Authoritative a = null;
            try {
                a = (Authoritative) getItem( d );
            } catch( ItemInstantiationException e ) {
                throw new AuthorizationException( e );
            }
            if( a.isAuthoritative( user ) ) {
                return Authorization.VIEW;
            }
        }

        logger.debug( "None of the above" );
        return Authorization.NONE;
    }

    public Date getCreatedAsDate() {
        return new Date( (Long)getField( "created" ) );
    }

    public Long getCreated() {
        return getField( "created" );
    }

    public void update() {
        document.set( "updated", new Date().getTime() );
    }

    public Date getUpdatedAsDate() {
        Long l = getField( "updated", null );
        if( l != null ) {
            return new Date( l );
        } else {
            return null;
        }
    }

    public Long getUpdated() {
        return getField( "updated", null );
    }


    public void delete() {
        document.set( "deleted", new Date().getTime() );
    }


    public Date getDeletedAsDate() {
        Long l = getField( "deleted", null );
        if( l != null ) {
            return new Date( l );
        } else {
            return null;
        }
    }

    public Long getDeleted() {
        return getField( "deleted" );
    }


    public Long getViews() {
        return getField( "views", 0l );
    }

    public void incrementViews() {
        document.set( "views", getViews() + 1 );
    }

    public int getRevision() {
        return getField( "revision", 1 );
    }

    public ObjectId getObjectId() {
        return document.get( "_id" );
    }

    @Override
    public Descriptor<?> getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }

    public void updateField( String collection, MongoUpdate update ) {
        MongoDBCollection.get( collection ).update( update, new MongoDBQuery().is( "_id", getObjectId() ) );
    }
}
