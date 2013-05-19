package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.nodes.BaseUser;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

/**
 * @author cwolfgang
 */
public class Profile extends BaseUser<Profile> {

    private static Logger logger = Logger.getLogger( Profile.class );

    public Profile( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String toString() {
        return "Profile[" + getDisplayName() + "]";
    }

    public static Profile getProfileByUsername( String username, Node parent ) throws NotFoundException {
        MongoDocument doc = BaseUser.getUserDocumentByUsername( username );
        if( doc != null ) {
            return new Profile( parent, doc );
        } else {
            throw new NotFoundException( "The profile " + username + " was not found" );
        }
    }

    public static class ProfileDescriptor extends BaseUserDescriptor<Profile> {

        @Override
        public Node getChild( String name ) throws NotFoundException {
            return getProfileByUsername( name, this );
        }

        @Override
        public String getDisplayName() {
            return "Profile";
        }

        @Override
        public String getType() {
            return "profile";
        }

        @Override
        public void save( Request request, Response response ) {
            /**/
        }
    }
}
