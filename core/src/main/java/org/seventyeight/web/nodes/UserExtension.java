package org.seventyeight.web.nodes;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.SavingException;

import java.util.List;

/**
 * @author cwolfgang
 */
public class UserExtension extends NodeExtension<UserExtension> {
    public UserExtension( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public static List<UserExtension> all() {
        return Core.getInstance().getExtensions( UserExtension.class );

    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
