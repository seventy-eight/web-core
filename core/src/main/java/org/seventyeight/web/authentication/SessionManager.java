package org.seventyeight.web.authentication;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.Utils;
import org.seventyeight.web.Core;
import org.seventyeight.web.User;
import org.seventyeight.web.model.ItemInstantiationException;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SessionManager {
	
	private static Logger logger = Logger.getLogger( SessionManager.class );

    public static final String SESSIONS = "sessions";
    public static final String SESSION = "session";

	public SessionManager() {
        //db.createIndex( INDEX_SESSIONS, IndexType.UNIQUE, IndexValueType.STRING );
		//logger.debug( "Initializing session index" );
	}
	
	private Session createSessionNode( String hash, Date endDate ) throws ItemInstantiationException {
		logger.debug( "Creating new session" );
		
		Session session = Core.getInstance().createItem( Session.class, SESSIONS );
		session.getDocument().set( "hash", hash );
        session.getDocument().set( "created", new Date().getTime() );
        session.getDocument().set( Session.__END_DATE, endDate.getTime() );

		return session;
	}
	
	public Session createSession( User user, Date date, int ttl ) throws ItemInstantiationException, AuthenticationException {
		logger.debug( "Creating session for " + user + ", " + ttl );
		String hash = "";
		try {
			hash = Utils.md5( user.getUsername() + date.getTime() );
		} catch( NoSuchAlgorithmException e ) {
			logger.warn( "Unable to create session: " + e.getMessage() );
			throw new AuthenticationException( e );
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		calendar.add( Calendar.HOUR_OF_DAY, ttl );
		
		//Session session = createSessionNode( hash, calendar.getTime() );

        Session.SessionsDescriptor descriptor = Core.getInstance().getDescriptor( Session.class );
        Session session = descriptor.newInstance();
        session.setEndDate( new org.seventyeight.utils.Date( calendar.getTime().getTime() ) );
        session.setCreated();
        session.setHash( hash );
        session.setUser( user );
		
		return session;
	}
	
	public Session getSession( String hash ) {
		logger.debug( "Getting session for " + hash );

        MongoDBQuery query = new MongoDBQuery().is( "hash", hash );
        List<MongoDocument> docs = new MongoDBCollection( SESSIONS ).find( query );

        Session actual = null;
        for( MongoDocument doc : docs ) {
            Session session = new Session( doc );
            logger.debug( "Comparing " + session.getEndingAsDate().getTime() + " with " + new Date().getTime() );
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
        new MongoDBCollection( SESSIONS ).remove( new MongoDBQuery().is( "hash", hash ) );
	}
}
