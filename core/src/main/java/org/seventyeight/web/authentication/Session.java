package org.seventyeight.web.authentication;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class Session extends AbstractNode<Session> {
	
	private static Logger logger = LogManager.getLogger( Session.class );

	public static final String __END_DATE = "end";

	public Session( Node parent, MongoDocument document ) {
		super( parent, document );
	}

    @Override
    public String getDisplayName() {
        return "Session";
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

/*
     public void bindToUser( User user ) {
         removeBindings();
         logger.debug( "Binding session to " + user );
         //user.getChild().createRelationshipTo( node, SessionEdge.session );
         user.createRelation( this, SessionEdge.session );
     }

     public void removeBindings() {
         logger.debug( "Removing all bindings for session" );
         List<Edge> edges = node.getEdges( SessionEdge.session, Direction.INBOUND );
         //Iterator<Relationship> i = node.getRelationships( SessionEdge.session ).iterator();

         for( Edge e : edges ) {
             e.remove();
         }
     }
     */

    public void setUser( User user ) {
        document.set( "user", user.getUsername() );
    }

	public String getUser() {
        return document.get( "user" );
	}

    /*
    public void setHash( String hash ) {
        document.set( "hash", hash );
    }
	
	public String getHash() {
		return document.get( "hash", null );
	}
	*/

    public void setTimeToLive( int seconds ) {
        this.document.set( "ttl", seconds );
    }

    public int getTimeToLive() {
        return document.get( "ttl" );
    }

    /*
    public void setCreated() {
        document.set( "created", new Date().getTime() );
    }
    */

    public void setEndDate( Date date ) {
        document.set( __END_DATE, date );
    }
	
	public Date getEndingAsDate() {
		//return new Date( (Long)document.get( __END_DATE ) );
        return document.get( __END_DATE );
	}

    public Date getCreated() {
        //return new Date( (Long)document.get( __END_DATE ) );
        return document.get( "created" );
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
      /* Implementation is a no op */
    }

    public static class SessionsDescriptor extends Descriptor<Session> {

        @Override
        public String getDisplayName() {
            return "Session";
        }

        @Override
        public String getCollectionName() {
            return SessionManager.SESSIONS_COLLECTION_NAME;
        }

        @Override
        public List<String> getApplicableExtensions() {
            return Collections.emptyList();
        }

        public Session newInstance(String title, Node parent) throws ItemInstantiationException {
            return super.create( title, parent );
        }
    }
}
