package org.seventyeight.web.nodes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.GetMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authorization.Authorizable;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Group extends Resource<Group> implements Authorizable {

    private static Logger logger = LogManager.getLogger( Group.class );

    public static final String GROUPS = "groups";
    public static final String GROUP = "group";

    public Group( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
        JsonElement usersElement = jsonData.get( "users" );
        if(usersElement.isJsonNull()) {

        } else {
            JsonArray usersArray = usersElement.getAsJsonArray();
            //List<String> artists = new ArrayList<String>( artistsArray.size() );
            for( JsonElement k : usersArray) {
                try {
                    User user = core.getNodeById( this, k.getAsString() );
                    addMember( user );
                } catch( Exception e ) {
                    logger.log( Level.WARN, "Unable to add " + k, e );
                }
            }

            //document.set( "members", artists );
        }
    }

    protected String getGroupType() {
        return GROUPS;
    }

    public List<AbstractNode<?>> getActivities(Request request) throws NotFoundException, ItemInstantiationException {
        logger.debug( "Getting activities for {}", this );
        MongoDocument sort = new MongoDocument().set( "updated", -1 );
        MongoDBQuery query = new MongoDBQuery().notExists( "parent" ).is( "ACL.read", getIdentifier() );

        logger.debug( "Query is {}", query );

        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, 0, 10, sort );
        List<AbstractNode<?>> nodes = new ArrayList<AbstractNode<?>>( docs.size() );

        for( MongoDocument d : docs ) {
            AbstractNode<?> n = core.getNodeById( this, d.getIdentifier() );
            //d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );
            nodes.add( n );

            //nodes.add( new Activity( d ) );
        }

        return nodes;
    }
    
    public static List<String> getGroupIds(User user) {
    	List<String> gs = user.getDocument().getObjectList2("groups");
    	return gs;
    }

    /**
     * Add a {@link User} to this {@link Group}.
     */
    public void addMember( User user ) {
        if( !isMember( user ) ) {
            logger.debug( "Adding " + user + " to " + this );
            //document.addToList( getGroupType(), user.getIdentifier() );
            user.getDocument().addToList( getGroupType(), getIdentifier() );
            user.save();
        } else {
            logger.debug( user + " is already a member of " + this );
        }
    }

    /**
     * Determine whether or not the {@link User} is member of this {@link Group}.
     */
    public boolean isMember( User user ) {
        List<Group> groups = user.getGroups();
        logger.debug( "User is in {}", groups );

        for( Group g : groups ) {
            if( this.equals( g ) ) {
                return true;
            }
        }

        return false;
    }

    public List<User> getMembers() {
        logger.debug( "Getting members for " + this );

        MongoDBQuery q = new MongoDBQuery().is( GROUPS, getIdentifier() );
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( q );

        logger.debug( "DOCS ARE: " + docs );

        List<User> users = new ArrayList<User>( docs.size() );

        for( MongoDocument d : docs ) {
            users.add( new User( core, this, d ) );
        }

        return users;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

    public static class GroupDescriptor extends ResourceDescriptor<Group> {

        public GroupDescriptor( Node parent ) {
            super( parent );
        }

        @Override
        public String getDisplayName() {
            return GROUP;
        }

        @Override
        public String getType() {
            return GROUP;
        }
        
        @GetMethod
        public void doGetGroups(Request request, Response response) throws IOException {
            response.setRenderType( Response.RenderType.NONE );

            String term = request.getValue( "term", "" );

            if( term.length() > 1 ) {
                MongoDBQuery query = new MongoDBQuery().is( "type", "group" ).regex( "title", "(?i)" + term + ".*" );

                PrintWriter writer = response.getWriter();
                writer.print( MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, 0, 10 ) );
            } else {
                response.getWriter().write( "{}" );
            }
        }

    }
}
