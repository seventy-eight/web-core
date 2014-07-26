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
public abstract class UserExtension extends NodeExtension<UserExtension> {
    public UserExtension( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    public static List<UserExtension> all(Core core) {
        return core.getExtensions( UserExtension.class );

    }

    @Override
    public String getMainTemplate() {
        return null;  /* Implementation is a no op */
    }
}
