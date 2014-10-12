package org.seventyeight.web.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public AbstractExtension( Core core, Node parent, MongoDocument document ) {
        super( core, document );

        this.parent = parent;
    }

    public PersistedNode getParent() {
        return (PersistedNode) parent;
    }

    public String getThisIdentifier() {
        Node p = parent;
        while(p != null) {
            if(p instanceof AbstractNode) {
                return ( (AbstractNode) p ).getIdentifier();
            }

            p = p.getParent();
        }

        throw new IllegalStateException( this + " does not have a parent with an identifier" );
    }

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
        return core.getDescriptor( getClass() );
    }
    
    public MongoDocument getDocument(Documented parent) {
    	ExtensionDescriptor<?> d = core.getDescriptor(getClass());
    	return d.getExtensionDocument(parent);
    }

    /**
     *
     * @param <T>
     */
    public abstract static class ExtensionDescriptor<T extends AbstractExtension<T>> extends Descriptor<T> {

        protected ExtensionDescriptor() {
            super();
        }

        public abstract String getExtensionName();

        public abstract ExtensionGroup getExtensionGroup();

        /*
        @Override
        public List<ExtensionGroup> getApplicableExtensions( Core core ) {
            return Collections.emptyList();
        }
        */

        public boolean isOmnipresent() {
            return false;
        }

        /**
         * Get the class of the extensions base.
         */
        public abstract Class<?> getExtensionClass();

        public String getExtensionClassId() {
            return getExtensionClass().getName();
        }

        public String getExtensionClassJsonId() {
            return getJsonId( getExtensionClassId() );
        }

        public Describable<T> getDescribable( Core core, PersistedNode node ) throws ItemInstantiationException {
            return getDescribable( core, node, node.getDocument() );
        }

        @Override
        public Describable<T> getDescribable( Core core, Node parent, MongoDocument document ) throws ItemInstantiationException {
            logger.warn( "THE DESCRIBABABALE DOC IS {}", document );
            logger.warn( "EXTENSION JSON ID {}", getJsonId( getExtensionClass().getName() ) );
            MongoDocument d = document.getr2( EXTENSIONS, getJsonId( getExtensionClass().getName() ) );
            logger.warn( "THE DESCRIBABABALE DOC IS {}", d );
            if(d != null && !d.isNull() && d.get( "class", null ) != null ) {
                return (Describable<T>) core.getNode( parent, d );
            } else {
                return null;
            }
        }

        @Override
        public Class<? extends AbstractExtension> getClazz() {
            return (Class<? extends AbstractExtension>) super.getClazz();
        }

        /**
         * Get the extension {@link MongoDocument} given a {@link Documented} parent.
         * @param parent A {@link Documented} parent. Typically a {@link Node}.
         * @return A {@link MongoDocument}, can be null.
         */
        public MongoDocument getExtensionDocument( Documented parent ) {
            MongoDocument d = parent.getDocument().getr2( EXTENSIONS, getExtensionClassJsonId() );
            logger.info( "RETRIEVED EXTENSION DOCUMENT for {} IS:", getId() );
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println( gson.toJson( d ) );
            if(d != null && !d.isNull() && canHaveMultiple()) {
                d = d.get( getJsonId() );
            }

            return d;
        }
        
        public void setExtensionDocument(Documented d, MongoDocument extension) {
        	MongoDocument ed = d.getDocument().get(EXTENSIONS);
        	if(ed == null || ed.isNull()) {
        		ed = new MongoDocument();
        		d.getDocument().set(EXTENSIONS, ed);
        	}
        	
        	if(canHaveMultiple()) {
        		if(ed.contains(getExtensionClassJsonId()) && !ed.getList(getExtensionClassJsonId()).contains(extension)) {
        			ed.addToList(getExtensionClassJsonId(), extension);
        		} else {
        			ed.addToList(getExtensionClassJsonId(), extension);
        		}
        	} else {
        		//d.getDocument().set(EXTENSIONS, new MongoDocument().set(getExtensionClassJsonId(), extension));
        		ed.set(getExtensionClassJsonId(), extension);
        	}
        }

        /**
         * Get instantiated extension given a {@link Documented} parent.
         *
         * @param core
         * @param parent
         * @return
         * @throws ItemInstantiationException
         */
        public T getExtension( Core core, Documented parent ) throws ItemInstantiationException {
        	logger.debug("Extension '{}' for parent: {}, DOC: {}", this, parent, parent.getDocument());
            MongoDocument d = getExtensionDocument( parent );
            logger.debug( "EXTENSION SUBDOC " + d );

            //
            if(isOmnipresent()) {
                if(d == null || d.isNull()) {
                    d = new MongoDocument().set( "class", getId() );
                    setExtensionDocument(parent, d);
                }

                if(d.get( "class", null ) == null) {
                    d.set( "class", getId() );
                }
            } else {
            	if(d == null) {
            		return null;
            	}
            }

            if(!d.get( "class" ).equals( getId() )) {
                throw new IllegalStateException( d.get( "class" ) + " is not equal to " + getId() );
            }

            return core.getNode( (Node) parent, d );
        }

        /**
         * Return a new bare boned instantiation of T.
         *
         * @param core
         * @param parent
         * @return
         * @throws ItemInstantiationException
         */
        public T getExtension( Core core, Descriptor parent ) throws ItemInstantiationException {
            MongoDocument d = new MongoDocument().set( "class", getId() );

            logger.debug( "EXTENSION SUBDOC " + d );
            return core.getNode( (Node) parent, d );
        }

        public boolean isApplicable( Node node ) {
            return true;
        }

        public boolean canHaveMultiple() {
            return false;
        }

        public String getPostConfigurationPage() {
            // Default implementation is a no op
            return null;
        }
    }


}
