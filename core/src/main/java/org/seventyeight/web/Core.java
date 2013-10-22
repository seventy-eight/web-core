package org.seventyeight.web;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.seventyeight.TokenList;
import org.seventyeight.database.mongodb.*;
import org.seventyeight.loader.Loader;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.utils.FileUtilities;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.actions.Get;
import org.seventyeight.web.actions.ResourceAction;
import org.seventyeight.web.authentication.*;
import org.seventyeight.web.handlers.template.TemplateManager;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.themes.Default;
import org.seventyeight.web.utilities.ExecuteUtils;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author cwolfgang
 */
public abstract class Core implements TopLevelNode, RootNode, Parent {

    private static Logger logger = Logger.getLogger( Core.class );

    public static final String NAME_FIELD = "title";

    public static final String MAIN_TEMPLATE = "org/seventyeight/web/main.vm";

    public static final String TEMPLATE_PATH_NAME = "templates";
    public static final String THEMES_PATH_NAME = "themes";
    public static final String PLUGINS_PATH_NAME = "plugins";

    /**
     * The instance of {@link Core}
     */
    protected static Core instance;

    protected TemplateManager templateManager = new TemplateManager();

    protected Authentication authentication = new SimpleAuthentication();
    protected SessionManager sessionManager = new SessionManager();

    protected Map<String, Searchable> searchables = new ConcurrentHashMap<String, Searchable>(  );

    protected ConcurrentHashMap<String, NaturalSearchable> naturalSearchables = new ConcurrentHashMap<String, NaturalSearchable>(  );

    /**
     * The default anonymous {@link User}
     */
    protected User anonymous;

    /**
     * The collection name for {@link Node}s
     */
    public static final String RESOURCES_COLLECTION_NAME = "resources";

    /**
     * The collection name for {@link Descriptor}s
     */
    public static final String DESCRIPTOR_COLLECTION_NAME = "descriptors";

    protected org.seventyeight.loader.ClassLoader classLoader = null;
    protected Loader pluginLoader;

    /* Database */
    protected MongoDBManager dbManager;
    protected MongoDatabase db;

    protected Menu mainMenu = new Menu();

    /**
     * A map of descriptors keyed by their supers class
     */
    protected Map<Class<?>, Descriptor<?>> descriptors = new HashMap<Class<?>, Descriptor<?>>();

    protected Map<String, Map<String, AbstractExtension.ExtensionDescriptor<?>>> extensionDescriptors = new HashMap<String, Map<String, AbstractExtension.ExtensionDescriptor<?>>>();

    /**
     * A map of interfaces corresponding to specific {@link Descriptor}s<br />
     * This is used to map an extension class/interface to those {@link Describable}s {@link Descriptor}s implementing them.
     */
    protected Map<Class, List<Descriptor>> descriptorList = new HashMap<Class, List<Descriptor>>();

    protected Map<Class<?>, List> extensionsList = new HashMap<Class<?>, List>();

    //protected ConcurrentMap<String, N>

    /**
     * A {@link Map} of top level actions, given by its name
     */
    //private ConcurrentMap<String, TopLevelGizmo> topLevelGizmos = new ConcurrentHashMap<String, TopLevelGizmo>();

    /**
     * The Map of top level {@link Node}s
     */
    protected ConcurrentMap<String, Node> children = new ConcurrentHashMap<String, Node>();

    /**
     * Default {@link Group} that has no one in it
     */
    protected Group noneGroup;

    /**
     * Default {@link Group} that has all in it
     */
    protected Group addGroup;

    /**
     * Path to the ...
     */
    protected File path;
    protected File orientdbPath;
    protected File pluginsPath;
    protected File uploadPath;

    protected File portraitPath;

    /**
     * This path contains the themes. Each theme in a sub directory
     */
    protected File themesPath;


    protected AbstractTheme defaultTheme = new Default();

    public static class Relations {
        public static final String EXTENSIONS = "extensions";
    }

    public Core( File path, String dbname ) throws CoreException {
        if( instance != null ) {
            throw new IllegalStateException( "Instance already defined" );
        }

        /* Initialize database */
        try {
            dbManager = new MongoDBManager( dbname );
        } catch( UnknownHostException e ) {
            throw new IllegalArgumentException( e );
        }
        db = dbManager.getDatabase();

        /* Initialize paths */
        this.path = path;
        this.uploadPath = new File( path, "upload" );
        this.portraitPath = new File( path, "portraits" );

        addDescriptor( new Session.SessionsDescriptor() );

        /* Default groups */
        // Group.de Maybe not???!?!?!?!

        /* Class loader */
        classLoader = new org.seventyeight.loader.ClassLoader( Thread.currentThread().getContextClassLoader() );
        this.pluginLoader = new Loader( classLoader );

        /* Mandatory */
        children.put( "get", new Get( this ) );  // This
        children.put( "resource", new ResourceAction() ); // Or that?

        instance = this;
    }

    public Core initialize() {

        /* TODO something with install */

        /* Other stuff */

        return this;
    }

    public static <T extends Core> T getInstance() {
        return (T) instance;
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

    /**
     * @deprecated ???
     */
    public <T extends Node> T createNode( Class<T> clazz, String collectioName ) throws ItemInstantiationException {
        logger.debug( "Creating " + clazz.getName() );

        MongoDBCollection collection = MongoDBCollection.get( collectioName ); // db.getCollection( NODE_COLLECTION_NAME );
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

    /**
     * @deprecated ???
     */
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
        String clazz = null;
        try {
            clazz = document.get( "class" );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Field \"class\" not found.", e );
        }


        if( clazz == null ) {
            logger.warn( "Class property not found" );
            throw new ItemInstantiationException( "\"class\" property not found for " + document );
        }
        //logger.debug( "ModelObject class: " + clazz );
        //logger.debug( "PARENT: " + parent );

        try {
            Class<PersistedObject> eclass = (Class<PersistedObject>) Class.forName(clazz, true, classLoader );
            Constructor<?> c = eclass.getConstructor( Node.class, MongoDocument.class );
            return (T) c.newInstance( parent, document );
        } catch( Exception e ) {
            logger.error( "Unable to get the class " + clazz );
            throw new ItemInstantiationException( "Unable to get the class " + clazz, e );
        }
    }

    public <T extends Node> T getNodeById( Node parent, String id ) throws ItemInstantiationException, NotFoundException {
        logger.debug( "Getting node by id: " + id );
        MongoDocument d = MongoDBCollection.get( RESOURCES_COLLECTION_NAME ).getDocumentById( id );

        if( d != null && !d.isNull() ) {
            PersistedObject obj = getItem( parent, d );

            return (T) obj;
        } else {
            throw new NotFoundException( "Could not find node with id " + id );
        }
    }

    @Override
    public Node getChild( String name ) {
        if( children.containsKey( name ) ) {
            return children.get( name );
        } else {
            return null;
        }
    }

    public void addNode( String urlName, Node node ) {
        children.put( urlName, node );
    }

    public Object resolve( String path ) {

        return null;
    }

    /**
     * Render the path from the URL
     */
    public void render( Request request, Response response ) throws CoreException {
        TokenList tokens = new TokenList( request.getRequestURI() );
        Node node = null;
        Exception exception = null;
        try {
            node = resolveNode( tokens );
            logger.debug("Found node " + node );
            if( !tokens.isEndsWithSlash() && tokens.isEmpty() ) {
                response.sendRedirect( request.getRequestURI() + "/" );
                return;
            }
        } catch( NotFoundException e ) {
            logger.debug( "Exception is set to " + e );
            exception = e;
        } catch( Exception e ) {
            throw new CoreException( e );
        }

        if( node instanceof Autonomous ) {
            logger.debug( node + " is autonomous" );
            try {
                ((Autonomous)node).autonomize( request, response );
            } catch( IOException e ) {
                throw new CoreException( e );
            }
            return;
        }

        /* Only try to find a valid view if there was a valid node found on the path */
        if( node != null ) {
                try {
                    if( tokens.isEmpty() && exception == null ) {
                        logger.debug( "Executing last node" );
                        ExecuteUtils.execute( request, response, node, "index" );
                        return;
                    }

                    renderObject( node, exception, request, response, tokens );
                } catch( CoreException ce ) {
                    throw ce;
                } catch( Exception e ) {
                    throw new CoreException( e.getMessage(), e );
                }
        } else {
            //Response.NOT_FOUND_404.render( request, response, exception );
            if( exception instanceof CoreException ) {
                throw (CoreException)exception;
            } else {
                throw new HttpException( exception ).setCode( 404 );
            }
        }

    }

    private void renderObject( Object obj, Exception exception, Request request, Response response, TokenList tokens ) throws Exception {
        logger.debug( "Rendering object " + obj + ", tokens: " + tokens );
        switch( tokens.left() ) {
                /* If the last token on the path is a valid node */
            case 0:
                ExecuteUtils.execute( request, response, obj, "index" );
                break;

                /* Typically, this happens if a node has an action, either as a view or doSomething */
            case 1:
                ExecuteUtils.execute( request, response, obj, tokens.next() );
                break;

                /* Generate a 404 if there are more tokens, because this means, that a valid node was not found */
            default:
                //Response.NOT_FOUND_404.render( request, response, exception );
                if( exception != null ) {
                    throw new CoreException( exception );
                } else {
                    throw new NotFoundException( request.getRequestURI() );
                }
        }
    }


    /**
     * Resolve the {@link org.seventyeight.web.model.Node}s from a path. <br />
     * @param tokens
     * @return The last valid {@link Node} on the path, adding the extra tokens, if any, to the the token list.
     */
    public Node resolveNode( TokenList tokens ) throws NotFoundException, UnsupportedEncodingException, ItemInstantiationException {
        //logger.debug( "Resolving " + path );
        //StringTokenizer tokenizer = new StringTokenizer( URLDecoder.decode( path, "ISO-8859-1" ), "/" );
        //StringTokenizer tokenizer = new StringTokenizer( path, "/" );

        Node current = this;
        Node next = null;
        Node last = this;

        while( tokens.hasMore() ) {
            String token = tokens.next();
            logger.debug( "Current: " + current );
            logger.debug( "Token  : " + token );

            /* Find a child node */

            next = null;
            if( current instanceof Parent ) {
                next = ((Parent)current).getChild( token );
            }
            logger.debug( "Found node is " + next );

            /* If there's no child, try an action */
            if( next == null ) {

                AbstractExtension.ExtensionDescriptor<?> d = extensionDescriptors.get( "action" ).get( token );
                logger.debug( "Found descriptor " + d + " for " + token );
                if( d != null ) {
                    if( d.isApplicable( current ) ) {
                        if( current instanceof PersistedObject ) {
                            //logger.debug( "CURRENT IS " + current );
                            next = (Node) d.getExtension( (PersistedObject) current );
                            logger.debug( "Found action is " + next );
                            //logger.debug( "TEMP PARENT: " + next.getParent() );
                        } else if( current instanceof Descriptor ) {
                            next = (Node) d.getExtension( (Descriptor) current );
                        }
                    }
                }

                /* If there ain't any actions */
                if( next == null ) {
                    tokens.backup();
                    break;
                }
            }

            if( next instanceof Autonomous ) {
                logger.debug( current + " is autonomous" );
                return next;
            }

            /*
            if( current instanceof Parent ) {
            } else {
                tokens.backup();
                break;
            }
            */

            /* TODO Something about authorization? */

            current = next;
            last = next;
        }

        return last;
    }

    /*
    public Object resolveObject( Actionable parent, LinkedList<String> tokens ) {
        Action action = null;
        Object lastObject = parent;
        //for( String token : tokens ) {
        while( !tokens.isEmpty() ) {
            String token = tokens.peek();
            logger.debug( "Popped " + token );

            action = parent.getAction( token );

            if( action == null ) {
                return lastObject;
            }

            if( action instanceof Actionable ) {
                parent = (Actionable) action;
            }

            lastObject = action;

            tokens.pop();
        }

        return lastObject;
    }
    */

    /*
    public Action getAction( Actionable actionable, String action ) {
        return null;
    }
    */

    public void addDescriptor( Descriptor<?> descriptor ) throws CoreException {
        logger.debug( "Adding " + descriptor + ", " + descriptor.getClazz() );
        this.descriptors.put( descriptor.getClazz(), descriptor );

        /* Determine if the descriptor has something to be loaded */
        descriptor.loadFromDisk();
        logger.debug( "Adding " + descriptor + ", " + descriptor.getClazz() );
        List<Class<?>> interfaces = ClassUtils.getInterfaces( descriptor.getClazz() );
        interfaces.addAll( ClassUtils.getClasses( descriptor.getClazz() ) );
        for( Class<?> i : interfaces ) {
            //logger.debug( "INTERFACE: " + i );
            List<Descriptor> list = null;
            if( !descriptorList.containsKey( i ) ) {
                descriptorList.put( i, new ArrayList<Descriptor>() );
            }
            list = descriptorList.get( i );

            list.add( descriptor );
        }

        if( descriptor instanceof ResourceDescriptor ) {
            ResourceDescriptor nd = (ResourceDescriptor) descriptor;
            children.put( nd.getType(), nd );
        }

        /* Find searchables */
        List<Searchable> ss = descriptor.getSearchables();
        for( Searchable s : ss ) {
            searchables.put( s.getMethodName(), s );
        }

        /**/
        if( descriptor instanceof NaturalSearchable ) {
            naturalSearchables.put( ( (NaturalSearchable) descriptor ).getType(), ( NaturalSearchable )descriptor );
        }

        if( descriptor instanceof AbstractExtension.ExtensionDescriptor ) {
            AbstractExtension.ExtensionDescriptor ed = (AbstractExtension.ExtensionDescriptor) descriptor;
            logger.debug( "Adding extension descriptor " + ed.getTypeName() + ", " + ed.getExtensionName() );
            if( !extensionDescriptors.containsKey( ed.getTypeName() ) ) {
                extensionDescriptors.put( ed.getTypeName(), new HashMap<String, AbstractExtension.ExtensionDescriptor<?>>() );
            }
            extensionDescriptors.get( ed.getTypeName() ).put( ed.getExtensionName(), ed );
        }

        /**/
        //descriptor.configureIndex( db );
    }

    public NaturalSearchable getNaturalSearchable( String type ) {
        return naturalSearchables.get( type );
    }

    public Map<String, NaturalSearchable> getNaturalSearchables() {
        return naturalSearchables;
    }

    public void addSearchable( Searchable s ) {
        searchables.put( s.getMethodName(), s );
    }

    public Map<String, Searchable> getSearchables() {
        return searchables;
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
     *
     *
     * @param clazz The interface in question
     * @return
     */
    public <T extends Descriptor> List<T> getExtensionDescriptors( Class clazz ) {
        if( descriptorList.containsKey( clazz ) ) {
            return (List<T>) descriptorList.get( clazz );
        } else {
            return Collections.emptyList();
        }
    }

    public Collection<Descriptor<?>> getAllDescriptors() {
            return descriptors.values();
    }

    public List<Descriptor> getCreatableDescriptors() {
        return getExtensionDescriptors( CreatableNode.class );
    }

    /**
     * Add an extension implementing a certain interface.
     */
    public <T> void addExtension( Class<T> clazz, Object extension ) {
        if( !extensionsList.containsKey( clazz ) ) {
            extensionsList.put( clazz, new ArrayList<T>() );
        }

        extensionsList.get( clazz ).add( extension );
    }

    /**
     * Get a list of extensions implementing a certain interface.
     */
    public <T> List<T> getExtensions( Class<T> type ) {
        if( extensionsList.containsKey( type ) ) {
            return extensionsList.get( type );
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Save the {@link TopLevelNode} given a {@link Node}.
     */
    public static void superSave( Node node ) {
        logger.debug( "Saving node " + node );

        while( node != null ) {
            if( node instanceof TopLevelNode ) {
                logger.debug( "Saving top level node " + node );
                ((TopLevelNode)node).save();
                return;
            }

            node = node.getParent();
        }

        throw new IllegalStateException( "No top level node to save" );
    }

    public static <T extends TopLevelNode> T superGet( Node node ) {
        logger.debug( "Super getting node " + node );

        while( node != null ) {
            if( node instanceof TopLevelNode ) {
                return (T) node;
            }

            node = node.getParent();
        }

        throw new IllegalStateException( "No top level node" );
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

    public void setAnonymous( User anonymous ) {
        this.anonymous = anonymous;
    }

    public User getAnonymousUser() {
        return anonymous;
    }

    private static final String NUMBERS_COLLECTION = "numbers";

    /**
     * Given a {@link org.seventyeight.web.model.ResourceDescriptor} a unique name is returned.
     * @param d ResourceDescriptor
     * @return
     */
    public synchronized String getUniqueName( ResourceDescriptor d ) {
        logger.debug( "Getting unique name for " + d.getType() );
        MongoDocument doc = MongoDBCollection.get( NUMBERS_COLLECTION ).findOne( new MongoDBQuery().getId( d.getType() ) );
        if( doc.isNull() ) {
            logger.debug( "Numbers for " + d.getType() + " was null, creating document" );
            doc = new MongoDocument(  ).set( "_id", d.getType() ).set( "number", 1 );
            //MongoDBCollection.get( NUMBERS_COLLECTION ).save( doc );
        }

        int i = doc.get( "number" );
        String name = d.getType() + "-" + i;
        doc.set( "number", ++i );

        MongoDBCollection.get( NUMBERS_COLLECTION ).save( doc );

        return name;
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
        logger.debug( "Getting theme file " + filename + " for " + theme );

        File themePath = new File( themesPath, theme.getName() );
        File themeFile = new File( themePath, filename );

        logger.debug( "Getting theme file " + themeFile + " for " + theme );

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

    public File getUploadPath() {
        return uploadPath;
    }

    public File getPortrataitPath() {
        return portraitPath;
    }

    public Menu getMainMenu() {
        return mainMenu;
    }

    @PostMethod
    public void doLogin( Request request, Response response ) throws AuthenticationException, IOException {

        String username = request.getValue( Authentication.__NAME_KEY );
        String password = request.getValue( Authentication.__PASS_KEY );
        logger.debug( "U: " + username + ", P:" + password );

        Session session = getAuthentication().login( username, password );

        session.save();

        Cookie c = new Cookie( Authentication.__SESSION_ID, session.getIdentifier() );
        c.setMaxAge( session.getTimeToLive() );
        response.addCookie( c );

        response.sendRedirect( request.getValue( "url", "/" ) );
    }

    public void doLogout( Request request, Response response ) throws IOException {
        logger.debug( "Logging out" );

        for( Cookie cookie : request.getCookies() ) {
            logger.debug( "Cookie: " + cookie.getName() + "=" + cookie.getValue() );
            if( cookie.getName().equals( Authentication.__SESSION_ID ) ) {
                cookie.setMaxAge( 0 );
                response.addCookie( cookie );

                /* Remove the session object */
                sessionManager.removeSession( cookie.getValue() );

                break;
            }
        }

        response.sendRedirect( "/" );
    }

    @Override
    public String getMainTemplate() {
        return TemplateManager.getUrlFromClass( Core.class, "main.vm" );
    }
}
