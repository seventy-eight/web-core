package org.seventyeight.web.authorization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.JsonException;
import org.seventyeight.web.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 *
 * {
 *  read: [],
 *  write: [],
 *  admin: []
 * }
 *
 */
public class BasicResourceBasedSecurity extends ACL<BasicResourceBasedSecurity> {

    private static Logger logger = LogManager.getLogger( BasicResourceBasedSecurity.class );

    private Permission permission = null;

    public BasicResourceBasedSecurity( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    /*
    @Override
    public List<Authorizable> getAuthorized( Permission permission ) {
        List<Authorizable> list = new ArrayList<Authorizable>(  );
        List<String> l = document.get( permission.getDbname() );
        for( String id : l ) {
            try {
                list.add( Core.getInstance().<Authorizable>getNodeById( getParent(), id ) );
            } catch( Exception e ) {
                logger.log( Level.ERROR, "Unable to get " + id, e );
            }
        }

        return list;
    }
    */

    @Override
    public boolean hasPermission( User user, Permission permission ) {
        List<String> admins = document.get( permission.getDbname() );
        for( String admin : admins ) {
            if( user.getIdentifier().equals( admin ) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Permission getPermission( User user ) {
        if( permission != null ) {
            permission = _getPermission( user );
        }
        return permission;
    }

    private Permission _getPermission( User user ) {
        /* Owner? */
        if( getParent() instanceof Ownable ) {
            logger.debug( "Parent is ownable." );
            if( ( (Ownable) getParent() ).isOwner( user ) ) {
                return Permission.ADMIN;
            }
        }

        /* Admin rights first */
        if( hasPermission( user, Permission.ADMIN ) ) {
            return Permission.ADMIN;
        }

        /* Write access */
        if( hasPermission( user, Permission.WRITE ) ) {
            return Permission.WRITE;
        }

        /* Read access */
        if( hasPermission( user, Permission.READ ) ) {
            return Permission.READ;
        }

        logger.debug( user + " has no permissions at all" );
        return Permission.NONE;
    }

    @Override
    public String toString() {
        return "BRBS permission, " + permission;
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        logger.debug( "Updating {}, {}", this, jsonData );
        // Access
        if(jsonData != null) {
            // access
            JsonObject accessArray = jsonData.getAsJsonObject( "access" );
            logger.debug( "ACCESS IS {}", accessArray );

            List<String> reads = new ArrayList<String>( accessArray.getAsJsonArray( "read" ).size() );
            for( JsonElement k : accessArray.getAsJsonArray( "read" )) {
                reads.add( k.getAsString() );
            }

            document.set( "read", reads );
        }
    }

    public static class BasicResourceBasedSecurityDescriptor extends Descriptor<BasicResourceBasedSecurity> {

        @Override
        public String getDisplayName() {
            return "Basic resource based security";
        }
    }
}
