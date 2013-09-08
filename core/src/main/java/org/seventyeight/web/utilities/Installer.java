package org.seventyeight.web.utilities;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.SavingException;

/**
 * @author cwolfgang
 */
public class Installer {

    private static Logger logger = Logger.getLogger( Installer.class );

    private Core core;

    public Installer() {
        this.core = Core.getInstance();
    }

    public void install() throws ItemInstantiationException, ClassNotFoundException, SavingException {

        Core.getInstance().getDatabase().remove();

        logger.info( "Installing users" );
        User admin = installUser( "wolle", true );
        User anonymous = installUser( "anonymous", false );
        Core.getInstance().setAnonymous( anonymous );

        logger.info( "Installing groups" );
        //Group admins = installGroup( "Admins", admin );
        //admins.addMember( admin );

        MongoDocument d = new MongoDocument().set( "type", 1 ).set( "title", 1 );
        MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).createIndex( "title", d );

    }

    public User installUser( String name, boolean visible ) throws ItemInstantiationException, ClassNotFoundException, SavingException {
        User user = (User) core.getDescriptor( User.class ).newInstance( name );

        Parameters p = new Parameters();
        p.put( "username", name );

        if( visible ) {
            user.setVisible( visible );
        }

        user.save( p, null );

        return user;
    }

    public Group installGroup( String name, User owner ) throws ItemInstantiationException, ClassNotFoundException, SavingException {
        Group group = (Group) core.getDescriptor( Group.class ).newInstance( "" );

        Parameters p = new Parameters();
        p.put( "name", name );
        p.setUser( owner );

        group.save( p, null );

        return group;
    }
}
