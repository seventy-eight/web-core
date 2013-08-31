package org.seventyeight.web.extensions.filetype;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Describable;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.model.ExtensionPoint;

/**
 * @author cwolfgang
 */
public abstract class FileType<T extends FileType<T>> implements ExtensionPoint, Describable<T> {

    protected MongoDocument document;

    public FileType( MongoDocument document ) {
        this.document = document;
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }

    @Override
    public Descriptor<T> getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }
}
