package org.seventyeight.web.extensions.filetype;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class ImageFileType extends FileType<ImageFileType> {

    public ImageFileType( MongoDocument document ) {
        super( document );
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        /* No op for now */
    }

    public static class ImageFileTypeDescriptor extends Descriptor<ImageFileType> {
        @Override
        public String getDisplayName() {
            return "Images";
        }
    }
}


