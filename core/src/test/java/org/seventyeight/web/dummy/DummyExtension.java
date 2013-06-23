package org.seventyeight.web.dummy;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.SavingException;

/**
 * @author cwolfgang
 */
public class DummyExtension extends NodeExtension {

    public DummyExtension( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        String name = request.getValue( "name", "The Name" );
        document.set( "name", name );
    }
}
