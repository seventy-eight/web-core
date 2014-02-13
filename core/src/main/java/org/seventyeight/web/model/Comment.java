package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.nodes.User;

import java.util.Date;

/**
 * @author cwolfgang
 */
public class Comment extends PersistedObject {

    public static final String TEXT_FIELD = "text";
    public static final String USER_FIELD = "user";
    public static final String DATE_FIELD = "date";
    public static final String RESOURCE_FIELD = "resource";

    protected MongoDocument document;

    public Comment(MongoDocument document) {
        this.document = document;
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        /* Implementation is a no op */
    }

    public static Comment create(Resource<?> resource, User user, String text) {
        MongoDocument document = new MongoDocument();
        document.set( USER_FIELD, user.getIdentifier() );
        document.set( DATE_FIELD, new Date() );
        document.set( TEXT_FIELD, text );
        document.set( RESOURCE_FIELD, resource.getIdentifier() );

        Comment comment = new Comment( document );
        return comment;
    }

    public Comment setText(String text) {
        document.set( TEXT_FIELD, text );
        return this;
    }

}
