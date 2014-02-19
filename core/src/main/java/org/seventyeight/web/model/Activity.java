package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;

import java.util.Date;

/**
 * @author cwolfgang
 */
public class Activity implements Documented {

    public static final String ACTIVITY_COLLECTION = "activities";

    private static Logger logger = LogManager.getLogger( Activity.class );

    public interface ActivityType {
        public String getName();
    }

    public enum DefaultTypes implements ActivityType {
        COMMENTED,
        CREATED;

        @Override
        public String getName() {
            return name();
        }
    }

    protected MongoDocument document;

    public Activity(MongoDocument document) {
        this.document = document;
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }

    public Resource<?> getResource() throws NotFoundException, ItemInstantiationException {
        return Core.getInstance().getNodeById( null, (String) document.get("resource") );
    }

    public String getType() {
        return document.get( "type" );
    }

    public Date getDate() {
        return document.get( "date" );
    }

    public User getUser() throws NotFoundException, ItemInstantiationException {
        return Core.getInstance().getNodeById( null, document.get("user", "") );
    }

    public static Activity create(User user, ActivityType type, Resource<?> resource) {
        MongoDocument document = new MongoDocument().set( "user", user.getIdentifier() ).set( "type", type.getName() ).set( "date", new Date() ).set( "resource", resource.getIdentifier() );

        logger.debug( "Saving document, {}", document );
        MongoDBCollection.get( ACTIVITY_COLLECTION ).save( document );

        return new Activity( document );
    }
}
