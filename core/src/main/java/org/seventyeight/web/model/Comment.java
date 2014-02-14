package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;

import java.util.Date;

/**
 * @author cwolfgang
 */
public class Comment extends AbstractNode<Comment> {

    public static final String TITLE_FIELD = "title";
    public static final String TEXT_FIELD = "text";
    public static final String USER_FIELD = "user";
    public static final String DATE_FIELD = "date";
    public static final String RESOURCE_FIELD = "resource";
    public static final String PARENT_FIELD = "parent";

    public Comment(Node parent, MongoDocument document) {
        super(parent, document);
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        /* Implementation is a no op */
    }

    public static Comment create(Resource<?> resource, User user, AbstractNode<?> parent, String title, String text) throws ItemInstantiationException {
        MongoDocument document = new MongoDocument();
        document.set( USER_FIELD, user.getIdentifier() );
        document.set( DATE_FIELD, new Date() );
        document.set( TEXT_FIELD, text );
        document.set( RESOURCE_FIELD, resource.getIdentifier() );
        document.set( PARENT_FIELD, parent.getIdentifier() );

        CommentDescriptor cd = Core.getInstance().getDescriptor( Comment.class );
        Comment instance = cd.newInstance( title, resource );

        return instance;
    }

    public Comment setText(String text) {
        document.set( TEXT_FIELD, text );
        return this;
    }

    public static class CommentDescriptor extends Descriptor<Comment> {

        @Override
        public String getDisplayName() {
            return "Comment";
        }
    }
}
