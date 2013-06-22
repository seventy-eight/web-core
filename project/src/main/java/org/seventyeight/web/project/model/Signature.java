package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.extensions.PermanentExtension;

/**
 * @author cwolfgang
 */
public class Signature extends PermanentExtension<Signature> {
    public Signature( MongoDocument document ) {
        super( document );
    }
}
