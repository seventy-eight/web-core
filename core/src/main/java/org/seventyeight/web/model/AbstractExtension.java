package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class AbstractExtension<T extends AbstractExtension<T>> extends PersistedObject implements Describable<T> {

    private static Logger logger = Logger.getLogger( AbstractExtension.class );

    protected Node parent;

    public AbstractExtension( Node parent, MongoDocument document ) {
        super( document );

        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    /*
    public AbstractExtension( Node node ) {
        super();
        if( node instanceof Documented ) {
            MongoDocument d = ((Documented)node).getDocument().getr( EXTENSIONS, getTypeName(), getExtensionName() );
            this.setDocument( d );
        } else {
            this.setDocument( new MongoDocument(  ) );
        }
    }
    */

    public List<Action> getActions( AbstractNode<?> node ) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Descriptor<T> getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }

    /**
     *
     * @param <T>
     */
    public abstract static class ExtensionDescriptor<T extends AbstractExtension<T>> extends Descriptor<T> {

        public abstract String getDisplayName();

        public abstract String getExtensionName();

        public abstract String getTypeName();

        public T get( AbstractNode node ) throws ItemInstantiationException {
            MongoDocument d = node.getDocument().getr( EXTENSIONS, getTypeName(), getExtensionName() );
            if( d.get( "class", null ) == null ) {
                d.set( "class", getId() );
            }
            //logger.debug( "FROM NODE " + node );
            return Core.getInstance().getItem( node, d );
        }

        public boolean isApplicable( Node node ) {
            return true;
        }
    }


}
