package org.seventyeight.web.functional.actiontest;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

/**
 * @author cwolfgang
 */
public class MyAction implements Node {

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
    public Node getParent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDisplayName() {
        return "Mya";
    }

    @Override
    public String getMainTemplate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}