package org.seventyeight.web.authentication;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.model.ItemInstantiationException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SessionManager implements Node {
	
	private static Logger logger = Logger.getLogger( SessionManager.class );

    public static final String SESSIONS = "sessions";
    public static final String SESSION = "session";
    public static final String SESSIONS_COLLECTION_NAME = "sessions";

	public SessionManager() {
        //db.createIndex( INDEX_SESSIONS, IndexType.UNIQUE, IndexValueType.STRING );
		//logger.debug( "Initializing session index" );
	}

	public Session createSession( User user, Date date, int ttl ) throws ItemInstantiationException, AuthenticationException {
		logger.debug( "Creating session for " + user + ", " + ttl );

        /*
		String hash = "";
		try {
			hash = Utils.md5( user.getUsername() + date.getTime() );
		} catch( NoSuchAlgorithmException e ) {
			logger.warn( "Unable to create session: " + e.getMessage() );
			throw new AuthenticationException( e );
		}
		*/
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		calendar.add( Calendar.SECOND, ttl );

		//Session session = createSessionNode( hash, calendar.getTime() );

        Session.SessionsDescriptor descriptor = Core.getInstance().getDescriptor( Session.class );
        Session session = descriptor.newInstance( "Session for " + user.getDisplayName(), this );
        session.getDocument().set( "created", new Date() );
        session.setEndDate( calendar.getTime() );
        //session.setCreated();
        session.setTimeToLive( ttl );
        //session.setHash( hash );
        session.setUser( user );

		return session;
	}
	
	public Session getSession( String hash ) {
		logger.debug( "Getting session for " + hash );

        MongoDBQuery query = new MongoDBQuery().getId( hash );
        List<MongoDocument> docs = MongoDBCollection.get( SESSIONS_COLLECTION_NAME ).find( query );

        logger.debug( "DOCS: " + docs );

        Session actual = null;
        for( MongoDocument doc : docs ) {
            Session session = new Session( Core.getInstance(), doc );
            logger.debug( "Comparing " + session.getEndingAsDate() + " with " + new Date() + "(" + session.getCreated() + ")" );
            if( session.getEndingAsDate().after( new Date() ) ) {
                logger.debug( "A valid session found" );
                actual = session;
                break;
            } else {
                logger.debug( "Session has expired" );
                /* TODO remove session??? */
            }
        }

		return actual;
	}
	
	public void removeSession( String hash ) {
        logger.debug( "[Removing sessions] " + hash );
        MongoDBCollection.get( SESSIONS_COLLECTION_NAME ).remove( new MongoDBQuery().getId( hash ) );
	}

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Session manager";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }
}
