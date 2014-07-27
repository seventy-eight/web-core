package org.seventyeight.web.functional.actiontest;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class MyNode extends AbstractNode<MyNode> implements Parent {

    public MyNode( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
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

    @Override
    public void updateNode( JsonObject jsonData ) {
      /* Implementation is a no op */
    }
}
