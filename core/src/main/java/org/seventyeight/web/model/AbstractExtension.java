package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ExtensionGroup;

import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class AbstractExtension<T extends AbstractExtension<T>> extends PersistedNode implements Describable<T> {

    private static Logger logger = LogManager.getLogger( AbstractExtension.class );

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

    @Override
    public void save() {
        if(parent instanceof Savable) {
            ( (Savable) parent ).save();
        } else {
            throw new IllegalStateException( "Parent is not savable!!!!" );
        }
    }

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

        protected String mongoPath = EXTENSIONS + "." + getTypeName() + "." + getExtensionName() + ".";

        public abstract String getDisplayName();

        public abstract String getExtensionName();

        public abstract String getTypeName();

        public abstract ExtensionGroup getExtensionGroup();

        @Override
        public List<ExtensionGroup> getApplicableExtensions() {
            return Collections.emptyList();
        }

        /**
         * Get the class of the extensions base.
         */
        public abstract Class<T> getExtensionClass();

        @Override
        public Describable<T> getDescribable( Node parent, MongoDocument document ) throws ItemInstantiationException {
            logger.warn( "THE DESCRIBABABALE DOC IS {}", document );
            logger.warn( "EXTENSION JSON ID {}", getJsonId( getExtensionClass().getName() ) );
            MongoDocument d = document.getr( EXTENSIONS, getJsonId( getExtensionClass().getName() ) );
            logger.warn( "THE DESCRIBABABALE DOC IS {}", d );
            if(d != null && !d.isNull()) {
                return (Describable<T>) Core.getInstance().getNode( parent, d );
            } else {
                return null;
            }
        }

        @Override
        public Class<? extends AbstractExtension> getClazz() {
            return (Class<? extends AbstractExtension>) super.getClazz();
        }

        /**
         * Yes yes, I know....
         */
        public String getMongoPath() {
            return EXTENSIONS + "." + getTypeName() + "." + getExtensionName() + ".";
        }

        public MongoDocument getExtensionSubDocument( Documented parent ) {
            return parent.getDocument().getr( EXTENSIONS, getTypeName(), getExtensionName() );
        }

        public T getExtension( Documented parent ) throws ItemInstantiationException {
            MongoDocument d = parent.getDocument().getr( EXTENSIONS, getTypeName(), getExtensionName() );
            if( d.get( "class", null ) == null ) {
                d.set( "class", getId() );
            }

            logger.debug( "EXTENSION SUBDOC " + d );
            return Core.getInstance().getNode( (Node) parent, d );
        }

        public T getExtension( Descriptor parent ) throws ItemInstantiationException {
            MongoDocument d = new MongoDocument().set( "class", getId() );

            logger.debug( "EXTENSION SUBDOC " + d );
            return Core.getInstance().getNode( (Node) parent, d );
        }

        public boolean isApplicable( Node node ) {
            return true;
        }
    }


}
