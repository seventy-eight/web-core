package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.markup.HtmlGenerator;
import org.seventyeight.markup.SimpleParser;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authorization.Ownable;
import org.seventyeight.web.extensions.MenuContributor;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ExtensionUtils;
import org.seventyeight.web.utilities.JsonException;
import org.seventyeight.web.utilities.JsonUtils;

import java.io.IOException;
import java.util.*;

/**
 *
 * This is the base implementation of a node in the tree.
 *
 * @author cwolfgang
 */
public abstract class AbstractNode<T extends AbstractNode<T>> extends PersistedNode implements TopLevelNode, Describable<T>, Ownable {

    private static Logger logger = LogManager.getLogger( AbstractNode.class );

    public static final String MODERATORS = "moderators";
    public static final String VIEWERS = "viewers";

    public enum Visibility {
        VISIBLE,
        INVISIBLE
    }

    protected Node parent;

    public AbstractNode( Node parent, MongoDocument document ) {
        super( document );

        this.parent = parent;
    }

    public String getIdentifier() {
        return document.get( "_id" ).toString();
    }

    public String getUrl() {
        return "/resource/" + getIdentifier() + "/";
    }

    public String getFileUrl() {
        return getUrl() + "file";
    }

    public String getConfigUrl() {
        return getUrl() + "configure";
    }

    public void addRelation(Relation relation, TopLevelNode node) {
        addRelation( relation, node.getIdentifier() );
    }

    public void addRelation(Relation relation, String id) {
        MongoDocument d = document.get( "relations" );
        if(d == null || d.isNull()) {
            d = new MongoDocument();
            document.set( "relations", d );
        }
        d.addToList( relation.getName(), id );
    }


    /*
    public void applyExtension(Class<? extends Extension> extensionClass) {
        logger.debug( "Layoutable: " + Core.getInstance().getExtensions( extensionClass ) );

        for( Extension d : Core.getInstance().getExtensions( extensionClass ) ) {
            if( d.isApplicable( this ) &&
                Core.getInstance().getTemplateManager().templateForClassExists( theme, platform, d.getClass(), template ) ) {
                ds.add( d );
            }
        }
    }
    */

    /**
     * Return a {@link List} of {@link org.seventyeight.web.model.AbstractExtension.ExtensionDescriptor}'s that are applicable and have a certain template.
     */
    public List<Layoutable> getLayoutableHavingTemplate( Theme theme, Theme.Platform platform, String template ) {
        List<Layoutable> ds = new ArrayList<Layoutable>(  );

        logger.debug( "Layoutable: " + Core.getInstance().getExtensions( Layoutable.class ) );
        //logger.debug( "LIST: " +  );

        for( Layoutable d : Core.getInstance().getExtensions( Layoutable.class ) ) {
            if( d.isApplicable( this ) &&
                Core.getInstance().getTemplateManager().templateForClassExists( theme, platform, d.getClass(), template ) ) {
                ds.add( d );
            }
        }

        return ds;
    }

    public List<AbstractExtension.ExtensionDescriptor<?>> getLayoutableHavingTemplate2( Theme theme, Theme.Platform platform, String template ) {
        List<AbstractExtension.ExtensionDescriptor<?>> ds = new ArrayList<AbstractExtension.ExtensionDescriptor<?>>(  );

        logger.debug( "DS: " + Core.getInstance().getExtensionDescriptors( Layoutable.class ) );

        for( Descriptor d : Core.getInstance().getExtensionDescriptors( Layoutable.class ) ) {
            if( (( AbstractExtension.ExtensionDescriptor)d).isApplicable( this ) &&
                Core.getInstance().getTemplateManager().templateForClassExists( theme, platform, d.getClazz(), template ) ) {
                ds.add( (AbstractExtension.ExtensionDescriptor<?>) d );
            }
        }

        return ds;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    public boolean isOwner( User user ) {
        logger.debug( "USER: " + user );
        logger.debug( "OWENR: " + getOwnerId() );
        return user.getIdentifier().equals( getOwnerId() );
    }

    public String getOwnerId() {
        return document.get( "owner" );
    }

    @Override
    public Descriptor<T> getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }

    /**
     * Save the document of the {@link Node}.
     */
    @Override
    public void save() {
        logger.debug( "Saving {}: {}", this, document );
        MongoDBCollection.get( getDescriptor().getCollectionName() ).save( document );
    }

    public void setOwner( User owner ) {
        logger.debug( "Setting owner to " + owner );
        document.set( "owner", owner.getIdentifier() );
    }

    public User getOwner() throws ItemInstantiationException, NotFoundException {
        return Core.getInstance().getNodeById( this, (String) document.get( "owner" ) );
    }

    public String getOwnerName() {
        return document.get( "owner" );
    }

    public String getType() {
        return document.get( "type", "unknown" );
    }

    public Date getCreated() {
        return getField( "created" );
    }

    public void update( User owner ) {
        update( owner, true );
    }

    public void update( User owner, boolean updateRevision ) {
        logger.debug( "Updating " + this );

        Date now = new Date();

        // Only set owner if not previously set and argument is not null.
        if( owner != null && document.get( "owner", null ) == null ) {
            logger.debug( "Owner was set to " + owner );
            setOwner( owner );
        }

        document.set( "updated", now );
        if(updateRevision) {
            document.set( "revision", getRevision() + 1 );
        }
    }

    public void setUpdated(Date date) {
        if(date != null) {
            document.set( "updated", date );
        } else {
            document.set( "updated", new Date() );
        }
    }

    public void setUpdatedCall() {
        setUpdatedCall( null );
    }

    protected void setUpdatedCall(Date date) {
        MongoDBQuery query = new MongoDBQuery().getId( this.getIdentifier() );
        MongoUpdate update;
        if(date == null) {
            update = new MongoUpdate().set( "updated", new Date() );
        } else {
            update = new MongoUpdate().set( "updated", date );
        }

        MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).update( query, update );
    }

    public Date getUpdated() {
        return getField( "updated", null );
    }

    public void setVisibility(Visibility visibility) {
        this.document.set("visibility", visibility.name());
    }

    public void setTitle( String title ) {
        document.set( "title", title );
    }

    public String getTitle() {
        String title = getField( "title", null );
        if( title != null ) {
            return title;
        } else {
            throw new IllegalStateException( "A node must have a title" );
        }
    }

    public void delete() {
        document.set( "deleted", new Date() );
    }

    public Date getDeleted() {
        return getField( "deleted" );
    }

    public Date getUpdatedOrCreated() {
        if(getUpdated() != null) {
            return getUpdated();
        } else {
            return getCreated();
        }
    }

    public Long getViews() {
        return getField( "views", 0l );
    }

    public void incrementViews() {
        document.set( "views", getViews() + 1 );
    }

    public int getRevision() {
        return getField( "revision", 1 );
    }

    public ObjectId getObjectId() {
        Object o = document.get( "_id" );
        logger.info( "OBJECT: " + o );
        return ObjectId.massageToObjectId( o.toString() );
    }

    public MongoDocument getUniqueIdentifier() {
        MongoDocument d = new MongoDocument().set( "type", ((NodeDescriptor)getDescriptor()).getType() ).set( "name", getTitle() );
        return d;
    }

    @PostMethod
    public void doConfigurationSubmit( Request request, Response response ) throws JsonException, ClassNotFoundException, SavingException, ItemInstantiationException, IOException {
        logger.debug( "Configuration submit" );

        JsonObject json = request.getJson();

        updateConfiguration( json );

        // Update user + revision
        update( request.getUser() );

        response.sendRedirect( getUrl() );
    }

    public void updateConfiguration(JsonObject json) throws ClassNotFoundException, ItemInstantiationException {

        // Default fields
        String title = JsonUtils.get( json, "title", null );
        if(title != null) {
            setField( "title", title );
        } else {
            throw new IllegalArgumentException( "Title not provided" );
        }

        // Access configuration
        if(json != null) {
            try {
                // access
                JsonObject accessObject = json.getAsJsonObject( "access" );
                if(accessObject != null) {
                    logger.debug( "THE ACCESS ARRAY: {}", accessObject );
                    Describable<?> describable = ExtensionUtils.handleExtensionConfiguration( accessObject, this );
                    logger.debug( "DESCRIBABABBABABA: {}", describable );
                    if(describable != null) {
                        document.set( "ACL", describable.getDocument() );
                    } else {
                        logger.debug( "ACL describable not set" );
                    }
                }
            } catch( NullPointerException e ) {
                logger.debug( "No json object provided" );
            }
        }

        // Update the node's extensions
        updateExtensions(json);

        // Update this nodes fields
        updateNode( json );

        postUpdate();
        save();

    }

    public void postUpdate() {
        document.set( "status", NodeDescriptor.Status.UPDATED.name() );
    }

    public NodeDescriptor.Status getStatus() {
        return NodeDescriptor.Status.valueOf( document.get( "status", "UPDATED" ) );
    }
    @Override
    public boolean equals( Object obj ) {
        if( obj == this ) {
            return true;
        }

        if( getClass().isInstance( obj ) ) {
            AbstractNode n = (AbstractNode) obj;
            if( getIdentifier().equals( n.getIdentifier() ) ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public void updateField( String collection, MongoUpdate update ) {
        MongoDBCollection.get( collection ).update( new MongoDBQuery().is( "_id", getObjectId() ), update );
    }

    /**
     * Get the first {@link Node} having the title.
     */
    public static <N extends Node> N getNodeByTitle( Node parent, String title ) {
        return getNodeByTitle( parent, title, null );
    }

    /**
     * Get the first {@link Node} having the title of the given type.
     * Type can be null and will then be disregarded.
     */
    public static <N extends Node> N getNodeByTitle( Node parent, String title, String type ) {
        MongoDBQuery q = new MongoDBQuery().is( "title", title );
        if( type != null && !type.isEmpty() ) {
            q.is( "type", type );
        }
        MongoDocument docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( q );
        logger.debug( "DOC IS: " + docs );

        if( docs != null ) {
            try {
                return (N) Core.getInstance().getNode( parent, docs );
            } catch( ItemInstantiationException e ) {
                logger.warn( e.getMessage() );
                return null;
            }
        } else {
            logger.debug( "The node " + title + " was not found" );
            return null;
        }
    }

    public static <N extends Node> List<N> getNodesByTitle( Node parent, String title, String type ) {
        MongoDBQuery q = new MongoDBQuery().is( "title", title );
        if( type != null && !type.isEmpty() ) {
            q.is( "type", type );
        }
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( q );
        logger.debug( "Docs are: " + docs );

        List<N> nodes = new ArrayList<N>( docs.size() );

        if( docs != null ) {
            for( MongoDocument doc : docs ) {
                try {
                    nodes.add( (N)Core.getInstance().getNode( parent, doc ) );
                } catch( ItemInstantiationException e ) {
                    /* TODO should this fail the entire method???? */
                    logger.error( e.getMessage() );
                }
            }
        } else {
            logger.debug( "Any node with title " + title + " was not found" );
        }

        return nodes;
    }

    public static <N extends Node> N getNodeById( Node parent, String id ) {
        MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).getDocumentById( id );
        if( doc != null ) {
            try {
                return (N) Core.getInstance().getNode( parent, doc );
            } catch( ItemInstantiationException e ) {
                logger.warn( e.getMessage() );
                return null;
            }
        } else {
            logger.debug( "The node with id " + id + " was not found" );
            return null;
        }
    }

    /* Texts */
    public static final String TEXTS_COLLECTION = "texts";
    private static SimpleParser textParser = new SimpleParser( new HtmlGenerator() );

    public enum TextType {
        markUp,
        html
    }

    public String getText( String type, String language ) {
        logger.debug( "Getting " + type + " for " + getIdentifier() );

        MongoDBQuery query = new MongoDBQuery().is( "identifier", getIdentifier() ).is( "type", type ).is( "language", language );
        MongoDocument doc = MongoDBCollection.get( TEXTS_COLLECTION ).findOne( query );

        if( doc == null || doc.isNull() ) {
            query = new MongoDBQuery().is( "identifier", getIdentifier() ).is( "type", type );
            doc = MongoDBCollection.get( TEXTS_COLLECTION ).findOne( query );

            if( doc == null || doc.isNull() ) {
                throw new IllegalStateException( language + " not found for " + type );
            }
        }

        return doc.getr( "texts" ).get( TextType.html.name(), "" );
    }

    /**
     * Set an internationalized text.
     * @param type The type of text, eg description
     * @param text The text itself
     * @param language
     */
    public void setText( String type, String text, String language ) {
        logger.debug( "Setting " + type + " for " + getIdentifier() );

        MongoDBQuery query = new MongoDBQuery().is( "identifier", getIdentifier() ).is( "type", type ).is( "language", language );
        MongoDocument doc = MongoDBCollection.get( TEXTS_COLLECTION ).findOne( query );

        if( doc == null || doc.isNull() ) {
            doc = createTextDocument( type, text, language );
        } else {
            updateTextDocument( doc, text );
        }

        MongoDBCollection.get( TEXTS_COLLECTION ).save( doc );
    }

    public void updateTextDocument( MongoDocument doc, String text ) {
        // Meta data
        doc.set( "revision", doc.get( "revision", 1 ) + 1 );
        doc.set( "updated", new Date() );

        StringBuilder output = textParser.parse( text );

        // Set the version of the parser and generator
        String version = textParser.getVersion() + ":" + textParser.getGeneratorVersion();
        doc.set( "textParserVersion", version );

        // Set the texts
        MongoDocument texts = doc.getSubDocument( "texts", null );
        if( texts == null || texts.isNull() ) {
            texts = new MongoDocument();
            doc.set( "texts", texts );
        }
        texts.set( TextType.markUp.name(), text );
        texts.set( TextType.html.name(), output.toString() );
    }

    /**
     * Create a text document
     */
    public MongoDocument createTextDocument( String type, String text, String language ) {
        logger.debug( "Creating text document" );

        MongoDocument doc = new MongoDocument();

        // Set meta data
        doc.set( "identifier", this.getIdentifier() );
        doc.set( "language", language );
        doc.set( "type", type );
        doc.set( "created", new Date() );
        doc.set( "revision", 1 );

        StringBuilder output = textParser.parse( text );

        // Set the version of the parser and generator
        String version = textParser.getVersion() + ":" + textParser.getGeneratorVersion();
        doc.set( "textParserVersion", version );

        // Set the texts
        MongoDocument texts = new MongoDocument();
        texts.set( TextType.markUp.name(), text );
        texts.set( TextType.html.name(), output.toString() );
        doc.set( "texts", texts );

        return doc;
    }

    public void doGetView(Request request, Response response) throws TemplateException, IOException {
        response.setRenderType( Response.RenderType.NONE );

        String template = request.getValue( "view", "simpleIndex" );

        response.getWriter().write( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, template + ".vm" ) );
    }


    /*
    public Menu getContributingViews( Request request ) {
        Menu menu = new Menu();

        //partitions.add( new ContributingView( "Main", "view", this ) );

        // Get extensions adding to the list
        for( MenuContributor pc : Core.getInstance().getExtensions( MenuContributor.class ) ) {
            pc.addContributingMenu( menu );
        }

        return menu;
    }
    */

    public Menu getMenu() {
        logger.debug( "Getting menu for {}", this );
        Menu menu = new Menu();

        for( MenuContributor pc : Core.getInstance().getExtensions( MenuContributor.class ) ) {
            logger.debug( "Menu contributor {}", pc );
            pc.addContributingMenu( this, menu );
            logger.debug( "ENDING::::.:.::::.:" );
        }

        return menu;
    }

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String toString() {
        //return getDisplayName();
        return getIdentifier();
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
