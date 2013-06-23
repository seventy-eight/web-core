package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.NodeExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class Entity<T extends Entity<T>> extends AbstractNode<T> implements CreatableNode, Portraitable, Parent {

    private static Logger logger = Logger.getLogger( Entity.class );

    public Entity( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public List<AbstractExtension> getExtensions() {
        List<AbstractExtension> es = super.getExtensions( NodeExtension.class );
        //es.addAll( Core.getInstance().getExtensions( PermanentExtension.class ) );
        return es;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public Node getChild( String name ) {
        return null;
    }
}
