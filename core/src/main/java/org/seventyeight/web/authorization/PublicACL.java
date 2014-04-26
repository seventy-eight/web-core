package org.seventyeight.web.authorization;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.ExtensionGroup;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

import java.util.List;

/**
 * @author cwolfgang
 */
public class PublicACL extends ACL<PublicACL> {

    public PublicACL( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public List<Authorizable> getAuthorized( Permission permission ) {
        return null;  /* Implementation is a no op */
    }

    @Override
    public Permission getPermission( User user ) {
        return Permission.WRITE;
    }

    @Override
    public String getDisplayName() {
        return "Public ACL";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
      /* Implementation is a no op */
    }

    public static class PublicACLDescriptor extends ACLDescriptor<PublicACL> {

        @Override
        public String getDisplayName() {
            return "Public ACL";
        }

        @Override
        public List<ExtensionGroup> getApplicableExtensions() {
            return null;  /* Implementation is a no op */
        }
    }
}
