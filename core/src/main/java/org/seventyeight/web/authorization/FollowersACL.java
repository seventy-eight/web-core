package org.seventyeight.web.authorization;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class FollowersACL extends ACL<FollowersACL> {
    public FollowersACL( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public boolean hasPermission( User user, Permission permission ) {
        if(getParent() instanceof AbstractNode ) {
            // Somehow get the followers
            //( (AbstractNode) getParent() ).getOwner().get
        }

        return false;
    }

    @Override
    public Permission getPermission( User user ) {
        return Permission.NONE;
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        /* Implementation is a no op */
    }

    @Override
    public String getDisplayName() {
        return null;  /* Implementation is a no op */
    }

    @Override
    public String getMainTemplate() {
        return null;  /* Implementation is a no op */
    }
}
