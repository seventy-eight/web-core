package org.seventyeight.web.functional.actiontest;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

/**
 * @author cwolfgang
 */
public class MyAction implements Action {

    private Node parent;
    private MongoDocument document;

    protected MyAction( Node parent, MongoDocument document ) {
        this.parent = parent;
        this.document = document;
    }

    public MongoDocument getDocument() {
        return document;
    }

    public void setValue( String key, String value ) {
        document.set( key, value );
    }

    public String getValue( String key ) {
        return document.get( key );
    }

    @Override
    public String getUrlName() {
        return "mya";
    }
    @Override
    public String getDisplayName() {
        return "Mya";
    }

}