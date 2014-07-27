package org.seventyeight.web.extensions;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;

import java.io.File;
import java.io.IOException;

/**
 * @author cwolfgang
 */
public abstract class AbstractPortrait extends AbstractExtension<AbstractPortrait> {

    private static Logger logger = LogManager.getLogger( AbstractPortrait.class );

    public static final int SMALL_SIZE = 80;
    public static final int LARGE_SIZE = 150;

    public AbstractPortrait( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    public Resource<?> getResource() {
        return (Resource<?>) parent;
    }

    public File getPortraitPath() {
        File p = new File( core.getPortrataitPath(), getResource().getIdentifier() );
        if( !p.exists() ) {
            try {
                FileUtils.forceMkdir( p );
            } catch( IOException e ) {
                logger.warn( "Unable to create path", e );
                return null;
            }
        }

        return p;
    }

    public abstract String getUrl();

    public static abstract class AbstractPortraitDescriptor extends ExtensionDescriptor<AbstractPortrait> {

        protected AbstractPortraitDescriptor( Core core ) {
            super();
        }

        @Override
        public Class<AbstractPortrait> getExtensionClass() {
            return AbstractPortrait.class;
        }

        @Override
        public String getExtensionName() {
            return "portrait";
        }

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( AbstractPortrait.class, "Portrait" );
        }
    }
}
