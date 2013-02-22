package org.seventyeight.web.authentication;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.Date;
import org.seventyeight.web.User;
import org.seventyeight.web.model.Entity;


public class Session extends Entity {
	
	private static Logger logger = Logger.getLogger( Session.class );

	public static final String __END_DATE = "end";

	public Session( MongoDocument document ) {
		super( document );
	}

    @Override
    public String getDisplayName() {
        return "Session";
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


	public User getUser() {
        return null;
	}
	
	public String getHash() {
		return document.get( "hash", null );
	}
	
	public Date getEndingAsDate() {
		return new Date( (Long)document.get( __END_DATE ) );
	}
}
