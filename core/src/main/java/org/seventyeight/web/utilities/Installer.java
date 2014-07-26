package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.installers.UserInstall;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.SavingException;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class Installer {

    private static Logger logger = LogManager.getLogger( Installer.class );

    private Core core;

    public Installer(Core core) {
        this.core = core;
    }

    public void install() throws ItemInstantiationException, ClassNotFoundException, SavingException, DatabaseException {

        //Core.getInstance().getDatabase().remove();
        logger.debug( "-------------------------------------------------------" );

        logger.info( "Installing users" );
        UserInstall adminInstall = new UserInstall( core, "wolle", "wolle@ejbyurterne.dk" );
        adminInstall.install();
        adminInstall.after();

        UserInstall aInstall = new UserInstall( core, "anonymous", "a@ejbyurterne.dk" ).setVisibility( false );
        //User anonymous = installUser( "anonymous", false );
        aInstall.install();
        aInstall.after();
        core.setAnonymous( aInstall.getValue() );
        logger.fatal( "------- Setting anonymous, {} - {}", aInstall.getValue(), core.getAnonymousUser() );

        logger.info( "Installing groups" );
        //Group admins = installGroup( "Admins", admin );
        //admins.addMember( admin );

        MongoDocument d = new MongoDocument().set( "type", 1 ).set( "title", 1 );
        MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).createIndex( "title", d );

    }

}
