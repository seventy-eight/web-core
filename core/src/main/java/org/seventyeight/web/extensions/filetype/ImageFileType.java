package org.seventyeight.web.extensions.filetype;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

/**
 * @author cwolfgang
 */
public class ImageFileType extends FileType<ImageFileType> {

    private static Logger logger = LogManager.getLogger( ImageFileType.class );

    public ImageFileType( MongoDocument document ) {
        super( document );
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        /* No op for now */
    }

    public static class ImageFileTypeDescriptor extends Descriptor<ImageFileType> implements Node {

        private Integer thumbWidth;

        private Integer thumbHeight;

        @Override
        public String getDisplayName() {
            return "Images";
        }

        @Override
        public Node getParent() {
            return null;
        }

        @Override
        public String getMainTemplate() {
            return Core.MAIN_TEMPLATE;
        }

        @Override
        public void save( Request request, Response response ) {
            thumbWidth = Integer.parseInt( request.getValue( "twidth", "80" ) );
            thumbHeight = Integer.parseInt( request.getValue( "theight", "120" ) );
        }
    }
}


