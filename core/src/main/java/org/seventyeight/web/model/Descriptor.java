package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ExtensionGroup;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.servlet.Request;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Descriptor<T extends Describable<T>> extends Configurable {
	
	private static Logger logger = LogManager.getLogger( Descriptor.class );

    public static final String DATA_COLLECTION = "data";
	
	protected transient Class<T> clazz;
	
	protected Descriptor() {
		clazz = (Class<T>) getClass().getEnclosingClass();
		logger.debug( "Descriptor class is " + clazz );
	}

    public List<String> getRequiredJavascripts() {
        return Collections.EMPTY_LIST;
    }
	
	public abstract String getDisplayName();

    /*
	public T newInstance( String title ) throws ItemInstantiationException {
		logger.debug( "New instance for " + clazz );
		return create( title, null );
	}
	*/

    //public abstract T newInstance( String title ) throws ItemInstantiationException;

    public T newInstance(CoreRequest request, Node parent) throws ItemInstantiationException {
        // Mandatory
        String title = request.getValue( "title" );
        if(title == null) {
            throw new IllegalArgumentException( "Title must be provided" );
        }

        return newInstance( request, parent, title );
    }

    public T newInstance(CoreRequest request, Node parent, String title) throws ItemInstantiationException {
        logger.debug( "New instance for " + clazz );

        return create( title, parent );
    }

    protected T newInstance(String title, Node parent) throws ItemInstantiationException {
        logger.debug( "New instance for " + clazz );
        return create( title, parent );
    }

    protected T create( String title, Node parent ) throws ItemInstantiationException {
        logger.debug( "Creating document " + clazz.getName() );

        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<T> c = clazz.getConstructor( Node.class, MongoDocument.class );
            instance = c.newInstance( parent, document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        //document.set( "title", title );
        document.set( "class", clazz.getName() );

        return instance;
    }

    public Map<String, String> getSearchKeyMap() {
        return Collections.EMPTY_MAP;
    }

    public String getCollectionName() {
        return DATA_COLLECTION;
    }

    /**
     * Get the class of the {@link Descriptor}s {@link Describable}.
     * @return
     */
	public Class<? extends Describable> getClazz() {
		return clazz;
	}

    public String getId() {
        return getClazz().getName();
    }

    public String getJsonId() {
        return getId().replace( '.', '-' );
    }

    public static String getJsonId(String id) {
        return id.replace( '.', '-' );
    }

    /**
     * When instantiated the descriptor can configure an index
     */
    public void configureIndex() {
        /* Default implementation is a no op */
    }

    public boolean isMandatory() {
        return false;
    }

    public List<Searchable> getSearchables() {
        return Collections.EMPTY_LIST;
    }

    public String getConfigurationPage( Request request ) throws TemplateException, NotFoundException, ItemInstantiationException {
        return getConfigurationPage( request, null, null );
    }

    public String getConfigurationPage( Request request, Describable<?> describable ) throws TemplateException, NotFoundException, ItemInstantiationException {
        return getConfigurationPage( request, describable, null );
    }

    public String getConfigurationPage( Request request, Describable<?> describable, String groupName ) throws TemplateException, NotFoundException, ItemInstantiationException {
        VelocityContext c = new VelocityContext();
        c.put( "class", getClazz().getName() );
        c.put( "descriptor", this );
        c.put( "groupName", groupName );

        if( describable != null ) {
            logger.debug( "Extension is " + describable );
            c.put( "enabled", true );
            c.put( "configuration", Core.getInstance().getTemplateManager().getRenderer( request ).setContext( c ).renderClass( describable, getClazz(), "config.vm" ) );
        } else {
            logger.debug( "Preparing EMPTY " + getClazz() );
            c.put( "enabled", false );
            c.put( "configuration", Core.getInstance().getTemplateManager().getRenderer( request ).setContext( c ).renderClass( getClazz(), "config.vm" ) );
        }

        return Core.getInstance().getTemplateManager().getRenderer( request ).setContext( c ).render( "org/seventyeight/web/model/descriptorpage.vm" );
    }

    public Describable<T> getDescribable(Node parent, MongoDocument document) throws ItemInstantiationException {
        return (Describable<T>) Core.getInstance().getNode( parent, document );
    }

    public String getRelationType() {
        return Core.Relations.EXTENSIONS;
    }

    /**
     * Determine whether to remove data items on configure.
     * @return
     */
    public boolean doRemoveDataItemOnConfigure() {
        return false;
    }

    public String getEnctype() {
        return "application/x-www-form-urlencoded";
    }

    public boolean hasGlobalConfiguration() {
        //Core.getInstance().getTemplateManager().getTemplateFromClass(  )
        return true;
    }

    /**
     * @return A {@link List} of {@link ExtensionGroup}'s defining the applicable extensions.
     */
    public abstract List<ExtensionGroup> getApplicableExtensions();
}
