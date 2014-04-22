package org.seventyeight.web.extensions;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

import java.io.File;
import java.io.IOException;

/**
 * @author cwolfgang
 */
public abstract class UserPortrait extends AbstractExtension<UserPortrait> {

    private static Logger logger = LogManager.getLogger( UserPortrait.class );

    public static final int SMALL_SIZE = 80;
    public static final int LARGE_SIZE = 150;

    public UserPortrait( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public User getUser() {
        return (User) parent;
    }

    public File getPortraitPath() {
        File p = new File( Core.getInstance().getPortrataitPath(), getUser().getIdentifier() );
        if( !p.exists() ) {
            try {
                FileUtils.forceMkdir( p );
            } catch( IOException e ) {
                logger.warn( "Unable to create path", e );
                return null;
            }
        }

        return p;
    }

    public abstract String getUrl();

    public static abstract class UserPortraitDescriptor extends ExtensionDescriptor<UserPortrait> {
        @Override
        public String getTypeName() {
            return "portrait";
        }

        @Override
        public Class<UserPortraitDescriptor> getExtensionClass() {
            return UserPortraitDescriptor.class;
        }

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( getClazz(), "User portrait" );
        }
    }
}
