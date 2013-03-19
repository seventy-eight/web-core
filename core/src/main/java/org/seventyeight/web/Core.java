package org.seventyeight.web;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBManager;
import org.seventyeight.database.mongodb.MongoDatabase;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.loader.Loader;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.utils.FileUtilities;
import org.seventyeight.web.actions.Get;
import org.seventyeight.web.actions.NewContent;
import org.seventyeight.web.authentication.Authentication;
import org.seventyeight.web.authentication.SessionManager;
import org.seventyeight.web.authentication.SimpleAuthentication;
import org.seventyeight.web.extensions.footer.Footer;
import org.seventyeight.web.handlers.template.TemplateManager;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.StaticFiles;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.nodes.Users;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.themes.Default;
import org.seventyeight.web.utilities.ExecuteUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:16
 */
public class Core extends Actionable implements Node, RootNode {

    private static Logger logger = Logger.getLogger( Core.class );

    public static final String TEMPLATE_PATH_NAME = "templates";
    public static final String THEMES_PATH_NAME = "themes";
    public static final String PLUGINS_PATH_NAME = "plugins";

    private static Core instance;

    private TemplateManager templateManager = new TemplateManager();

    private Authentication authentication = new SimpleAuthentication();
    private SessionManager sessionManager = new SessionManager();

    private User anonymous;

    public static final String NODE_COLLECTION_NAME = "nodes";

    private org.seventyeight.loader.ClassLoader classLoader = null;
    private Loader pluginLoader;

    /* Database */
    private MongoDBManager dbManager;
    private MongoDatabase db;

    //
    /**
     * A map of descriptors keyed by their super class
     */
    private Map<Class<?>, Descriptor<?>> descriptors = new HashMap<Class<?>, Descriptor<?>>();

    /**
     * A map of interfaces corresponding to specific {@link Descriptor}s<br />
     * This is used to map an extension class/interface to those {@link Describable}s {@link Descriptor}s implementing them.
     */
    private Map<Class, List<Descriptor>> descriptorList = new HashMap<Class, List<Descriptor>>();

    private Map<Class, List<Descriptor>> entityDescriptorList = new HashMap<Class, List<Descriptor>>();

    /**
     * A {@link Map} of top level actions, given by its name
     */
    //private ConcurrentMap<String, TopLevelGizmo> topLevelGizmos = new ConcurrentHashMap<String, TopLevelGizmo>();

    /**
     * A map of first level {@link org.seventyeight.web.model.Node}s registered to the core
     */
    private ConcurrentMap<String, Node> items = new ConcurrentHashMap<String, Node>();

    /**
     * The list of top level {@link Action}s
     */
    private List<Action> actions = new CopyOnWriteArrayList<Action>();

    /**
     * Path to the ...
     */
    private File path;
    private File orientdbPath;
    private File pluginsPath;
    private File uploadPath;

    /**
     * This path contains the themes. Each theme in a sub directory
     */
    private File themesPath;


    private AbstractTheme defaultTheme = new Default();

    public static class Relations {
        public static final String EXTENSIONS = "extensions";
    }

    public Core( File path, String dbname ) {
        if( instance != null ) {
            throw new IllegalStateException( "Instance already defined" );
        }

        try {
            dbManager = new MongoDBManager( dbname );
        } catch( UnknownHostException e ) {
            throw new IllegalArgumentException( e );
        }
        db = dbManager.getDatabase();
        this.path = path;

        /* Mandatory top level Actions */
        actions.add( new StaticFiles() );
        actions.add( new NewContent( this ) );
        actions.add( new Get( this ) );

        items.put( "user", new Users( this ) );

        /* Class loader */
        classLoader = new org.seventyeight.loader.ClassLoader( Thread.currentThread().getContextClassLoader() );
        this.pluginLoader = new Loader( classLoader );

        /**/
        addDescriptor( new User.UserDescriptor() );

        /* test */
        addDescriptor( new Footer.FooterDescriptor() );

        instance = this;
    }

    public static Core getInstance() {
        return instance;
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Root";
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public <T extends Node> T createNode( Class<T> clazz ) throws ItemInstantiationException {
        logger.debug( "Creating " + clazz.getName() );

        MongoDBCollection collection = MongoDBCollection.get( NODE_COLLECTION_NAME ); // db.getCollection( NODE_COLLECTION_NAME );
        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<T> c = clazz.getConstructor( Node.class, MongoDocument.class );
            instance = c.newInstance( this, document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        document.set( "class", clazz.getName() );
        collection.save( document );

        return instance;
    }

    public <T extends Documented> T createSubDocument( Class<T> clazz ) throws ItemInstantiationException {
        logger.debug( "Creating sub item " + clazz.getName() );

        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<T> c = clazz.getConstructor( MongoDocument.class );
            instance = c.newInstance( document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        document.set( "class", clazz.getName() );

        return instance;
    }

    public <T extends Documented> T getSubDocument( MongoDocument document ) throws ItemInstantiationException {
        String clazz = (String) document.get( "class" );

        if( clazz == null ) {
            logger.warn( "Class property not found" );
            throw new ItemInstantiationException( "\"class\" property not found for " + document );
        }
        logger.debug( "ModelObject class: " + clazz );

        try {
            Class<Documented> eclass = (Class<Documented>) Class.forName(clazz, true, classLoader );
            Constructor<?> c = eclass.getConstructor( MongoDocument.class );
            return (T) c.newInstance( document );
        } catch( Exception e ) {
            logger.error( "Unable to get the class " + clazz );
            throw new ItemInstantiationException( "Unable to get the class " + clazz, e );
        }
    }



    /**
     * Get an object from a {@link MongoDocument}
     * @param document
     * @param <T>
     * @return
     * @throws ItemInstantiationException
     */
    public <T extends PersistedObject> T getItem( Node parent, MongoDocument document ) throws ItemInstantiationException {
        String clazz = (String) document.get( "class" );

        if( clazz == null ) {
            logger.warn( "Class property not found" );
            throw new ItemInstantiationException( "\"class\" property not found for " + document );
        }
        logger.debug( "ModelObject class: " + clazz );

        try {
            Class<PersistedObject> eclass = (Class<PersistedObject>) Class.forName(clazz, true, classLoader );
            Constructor<?> c = eclass.getConstructor( Node.class, MongoDocument.class );
            return (T) c.newInstance( parent, document );
        } catch( Exception e ) {
            logger.error( "Unable to get the class " + clazz );
            throw new ItemInstantiationException( "Unable to get the class " + clazz, e );
        }
    }

    public Node getNodeById( Node parent, String id ) throws ItemInstantiationException {
        logger.debug( "Getting node by id: " + id );
        MongoDocument d = MongoDBCollection.get( NODE_COLLECTION_NAME ).getDocumentById( id );

        PersistedObject obj = getItem( parent, d );

        return (Node) obj;
    }

    @Override
    public Node getChild( String name ) {
        if( items.containsKey( name ) ) {
            return items.get( name );
        } else {
            return null;
        }
    }

    public void addNode( String urlName, Node node ) {
        items.put( urlName, node );
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    public Object resolve( String path ) {

        return null;
    }

    /**
     * Render the path from the URL
     */
    public void render( Request request, Response response ) throws Exception {
        LinkedList<String> tokens = new LinkedList<String>();
        Node node = null;
        Exception exception = null;
        try {
            node = resolveItem( request.getRequestURI(), tokens );

            if( tokens.isEmpty() ) {
                ExecuteUtils.execute( request, response, node, "index" );
                return;
            }
        } catch( NotFoundException e ) {
            logger.debug( "Exception is set to " + e );
            exception = e;
        }

        /* End of the line, render the node it self with index */
        if( node instanceof Actionable ) {
            Actionable a = (Actionable) node;
            Object action = null;
            try {
                action = resolveAction( request, response, a, tokens );
            } catch( Exception e ) {
                Response.NOT_FOUND_404.render( request, response, exception );
            }

            if( action != null ) {
                if( action instanceof Autonomous ) {
                   /* Don't do anything */
                } else {

                    try {
                        switch( tokens.size() ) {
                            case 0:
                                ExecuteUtils.execute( request, response, action, "index" );
                                break;

                            case 1:
                                ExecuteUtils.execute( request, response, action, tokens.get( 0 ) );
                                break;

                            default:
                                Response.NOT_FOUND_404.render( request, response, exception );
                        }
                    } catch( NotFoundException e ) {
                        logger.log( Level.WARN, "", e );
                        Response.NOT_FOUND_404.render( request, response, exception );
                    }
                }
            } else {
                Response.NOT_FOUND_404.render( request, response, exception );
            }

        } else {
            Response.NOT_FOUND_404.render( request, response, exception );
        }
    }


    /**
     * Resolve the {@link org.seventyeight.web.model.Node}s from a path
     * @param path
     * @param tokens
     * @return
     */
    public Node resolveItem( String path, List<String> tokens ) throws NotFoundException {
        logger.debug( "Resolving " + path );
        StringTokenizer tokenizer = new StringTokenizer( path, "/" );

        Node current = this;
        Node last = this;

        while( tokenizer.hasMoreTokens() ) {
            String token = tokenizer.nextToken();
            logger.debug( "Url name: " + token );

            current = current.getChild( token );

            if( current == null ) {
                tokens.add( token );
                break;
            }

            /* TODO Something about authorization? */

            last = current;
        }

        while( tokenizer.hasMoreTokens() ) {
            tokens.add( tokenizer.nextToken() );
        }

        return last;
    }

    public Object resolveAction( Request request, Response response, Actionable actionable, Queue<String> urlNames ) throws ItemInstantiationException, IOException {
        logger.debug( "Resolving actions" );

        String urlName = "index";

        Object object = null;
        while( ( urlName = urlNames.peek() ) != null ) {
            logger.debug( "Url name: " + urlName );

            object = actionable.getDynamic( urlName );
            logger.debug( "Found object is " + object );

            if( object == null ) {
                break;
            }

            /* Pop the name */
            urlNames.remove();

            /**/
            if( object instanceof Autonomous ) {
                logger.debug( object + " is autonomous" );
                ((Autonomous)object).autonomize( urlName, request, response );
                return object;
            }

            if( object instanceof Actionable ) {
                actionable = (Actionable) object;
            } else {
                break;
            }
        }

        return object;
    }

    public Action getAction( Actionable actionable, String action ) {
        return null;
    }


    public void addDescriptor( Descriptor<?> descriptor ) {
        this.descriptors.put( descriptor.getClazz(), descriptor );

        List<Class<?>> interfaces = ClassUtils.getInterfaces( descriptor.getClazz() );
        for( Class<?> i : interfaces ) {
            logger.debug( "INTERFACE: " + i );
            List<Descriptor> list = null;
            if( !descriptorList.containsKey( i ) ) {
                descriptorList.put( i, new ArrayList<Descriptor>() );
            }
            list = descriptorList.get( i );

            list.add( descriptor );
        }

        /**/
        //descriptor.configureIndex( db );
    }

    public Descriptor<?> getDescriptor( String className ) throws ClassNotFoundException {
        return getDescriptor( Class.forName( className ) );
    }

    public <T extends Descriptor> T getDescriptor( Class<?> clazz ) {
        logger.debug( "Getting descriptor for " + clazz );

        if( descriptors.containsKey( clazz ) ) {
            return (T) descriptors.get( clazz );
        } else {
            return null;
        }
    }

    public List<Descriptor> getExtensionDescriptors( String clazz ) throws ClassNotFoundException {
        return getExtensionDescriptors( Class.forName( clazz ) );
    }

    /**
     * Get a list of {@link Descriptor}s whose {@link Describable} implements the given interface
     * @param clazz The interface in question
     * @return
     */
    public List<Descriptor> getExtensionDescriptors( Class clazz ) {
        if( descriptorList.containsKey( clazz ) ) {
            return descriptorList.get( clazz );
        } else {
            return Collections.emptyList();
        }
    }

    public List<Descriptor> getCreatableDescriptors() {
        return getExtensionDescriptors( CreatableNode.class );
    }

    public AbstractTheme getDefaultTheme() {
        return defaultTheme;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }


    public int getNextSequence( String sequence ) {

        return 1;
    }

    public void setAnonymous( User anonymous ) {
        this.anonymous = anonymous;
    }

    public User getAnonymousUser() {
        return anonymous;
    }


    /**
     * From the given path, get all jars and extract them to their directories
     * @param basePath
     * @return
     * @throws IOException
     */
    public static List<File> extractPlugins( File basePath ) throws IOException {
        logger.debug( "Extracting plugins to " + PLUGINS_PATH_NAME );
        File path = new File( basePath, PLUGINS_PATH_NAME );
        File themes = new File( basePath, THEMES_PATH_NAME );

        FileUtils.deleteDirectory( themes );
        themes.mkdir();

        File[] files = path.listFiles( FileUtilities.getExtension( "jar" ) );

        List<File> plugins = new ArrayList<File>();
        for( File f : files ) {
            logger.debug( "Extracting plugin " + f );
            String p = f.getName();
            p = p.substring( 0, ( p.length() - 4 ) );
            File op = new File( path, p );
            FileUtils.deleteDirectory( op );

            FileUtilities.extractArchive( f, op );
            plugins.add( op );

            /* Copy any theme directory to path */
            File pthemes = new File( op, "themes" );
            if( pthemes.exists() ) {
                logger.debug( "Copying themes from " + pthemes + " to " + themes );
                FileUtils.copyDirectory( pthemes, themes );
            }
        }

        return plugins;
    }

    public File getThemeFile( AbstractTheme theme, String filename ) throws IOException {
        File themePath = new File( themesPath, theme.getName() );
        File themeFile = new File( themePath, filename );

        if( themeFile.exists() ) {
            return themeFile;
        }

        throw new IOException( "Theme file " + themeFile + " does not exist" );
    }

    public void setThemesPath( File path ) {
        this.themesPath = path;
    }

    public org.seventyeight.loader.ClassLoader getClassLoader() {
        return classLoader;
    }

    public void getPlugins( List<File> plugins ) {
        for( File p : plugins ) {
            logger.debug( "Plugin " + p );
            try {
                /* Maybe check for classes directory */
                pluginLoader.load( p, "" );
            } catch( Exception e ) {
                logger.error( "Unable to load " + p, e );
            }
        }
    }

    public File getPath() {
        return path;
    }
}
