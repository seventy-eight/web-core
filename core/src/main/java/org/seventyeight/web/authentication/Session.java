package org.seventyeight.web.authentication;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.Date;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.model.AbstractNodeItem;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.model.NodeItem;


public class Session extends AbstractNodeItem {
	
	private static Logger logger = Logger.getLogger( Session.class );

	public static final String __END_DATE = "end";

	public Session( NodeItem parent, MongoDocument document ) {
		super( parent, document );
	}

    @Override
    public String getDisplayName() {
        return "Session";
    }

    @Override
    public NodeItem getParent() {
        return null;
    }

    @Override
    public NodeItem getNode( String name ) {
        return null;
    }

/*
     public void bindToUser( User user ) {
         removeBindings();
         logger.debug( "Binding session to " + user );
         //user.getNode().createRelationshipTo( node, SessionEdge.session );
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

	public User getUser() {
        return null;
	}

    public void setHash( String hash ) {
        document.set( "hash", hash );
    }
	
	public String getHash() {
		return document.get( "hash", null );
	}

    public void setCreated() {
        document.set( "created", new Date().getTime() );
    }

    public void setEndDate( Date date ) {
        document.set( __END_DATE, date.getTime() );
    }
	
	public Date getEndingAsDate() {
		return new Date( (Long)document.get( __END_DATE ) );
	}


    public static class SessionsDescriptor extends Descriptor<Session> {

        @Override
        public String getDisplayName() {
            return "Session";
        }
    }
}
