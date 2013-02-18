package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:14
 */
public abstract class AbstractItem implements Item {

    public static final String EXTENSIONS = "extensions";

    protected MongoDocument document;

    public AbstractItem( MongoDocument document ) {
        this.document = document;
    }

    public List<AbstractExtension> getExtensions() throws ItemInstantiationException {
        List<MongoDocument> docs = document.getList( EXTENSIONS );
        List<AbstractExtension> extensions = new ArrayList<AbstractExtension>( docs.size() );

        for( MongoDocument doc : docs ) {
            extensions.add( (AbstractExtension) Core.getInstance().getItem( doc ) );
        }

        return extensions;
    }
}
