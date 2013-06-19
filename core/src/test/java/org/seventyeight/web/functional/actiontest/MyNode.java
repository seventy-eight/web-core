package org.seventyeight.web.functional.actiontest;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public class MyNode extends AbstractNode<MyNode> {

    public MyNode( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public void setValue( String key, String value ) {
        document.set( key, value );
    }

    public String getValue( String key ) {
        return document.get( key );
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        MongoDocument doc = document.getSubDocument( "actions." + name, null );
        if( doc == null ) {
            doc = new MongoDocument();
            document.set( "actions", new MongoDocument().set( name, doc ) );
        }
        //return new MyAction( this, doc );
        return null;
    }

    @Override
    public String getMainTemplate() {
        return null;
    }
}