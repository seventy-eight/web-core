package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class GravatarPortrait implements UserPortrait {

    protected User user;

    protected MongoDocument document;

    public GravatarPortrait( MongoDocument document ) {
        this.document = document;
    }

    @Override
    public String getUrl() {
        return null;  /* Implementation is a no op */
    }

    @Override
    public void setUser( User user ) {
      this.user = user;
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }
}
