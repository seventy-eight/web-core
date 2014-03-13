package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.orm.SimpleORM;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

public abstract class Descriptor<T extends Describable<T>> extends Configurable implements Node {
	
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
		return createSubDocument( title, null );
	}
	*/

    //public abstract T newInstance( String title ) throws ItemInstantiationException;

    public T newInstance(CoreRequest request) throws ItemInstantiationException {
        logger.debug( "New instance for " + clazz );

        // Mandatory
        String title = request.getValue( "title" );
        if(title == null) {
            throw new IllegalArgumentException( "Title must be provided" );
        }

        return createSubDocument( title, this );
    }

    protected T createSubDocument( String title, Node parent ) throws ItemInstantiationException {
        logger.debug( "Creating sub document " + clazz.getName() );

        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<T> c = clazz.getConstructor( Node.class, MongoDocument.class );
            instance = c.newInstance( parent, document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        document.set( "title", title );
        document.set( "class", clazz.getName() );

        return instance;
    }

    public String getCollectionName() {
        return DATA_COLLECTION;
    }

    /**
     * Get the descriptors for
     * @return
     */
    public List<Class> getExtensionClasses() {
        return Collections.emptyList();
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

    public String getConfigurationPage( Request request ) throws TemplateException, NotFoundException {
        return getConfigurationPage( request, null );
    }

    public String getConfigurationPage( Request request, Describable<?> describable ) throws TemplateException, NotFoundException {
        VelocityContext c = new VelocityContext();
        c.put( "class", getClazz().getName() );
        c.put( "descriptor", this );

        if( describable != null ) {
            logger.debug( "Extension is " + describable );
            c.put( "enabled", true );
            c.put( "configuration", Core.getInstance().getTemplateManager().getRenderer( request ).setContext( c ).renderObject( describable, "config.vm" ) );
        } else {
            logger.debug( "Preparing EMPTY " + getClazz() );
            c.put( "enabled", false );
            c.put( "configuration", Core.getInstance().getTemplateManager().getRenderer( request ).setContext( c ).renderClass( getClazz(), "config.vm" ) );
        }

        return Core.getInstance().getTemplateManager().getRenderer( request ).setContext( c ).render( "org/seventyeight/web/model/descriptorpage.vm" );
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


}
