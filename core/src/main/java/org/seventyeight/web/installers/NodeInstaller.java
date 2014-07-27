package org.seventyeight.web.installers;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Descriptor;

/**
 * @author cwolfgang
 */
public abstract class NodeInstaller<T extends AbstractNode<T>> implements DBInstallable<T> {

    private static Logger logger = LogManager.getLogger( NodeInstaller.class );

    protected String title;

    protected T node;

    protected abstract void setJson( JsonObject json );

    protected abstract Descriptor<T> getDescriptor();

    protected abstract T getNodeFromDB();

    protected Core core;

    protected NodeInstaller( Core core, String title ) {
        this.title = title;
        this.core = core;
    }

    @Override
    public void install() throws DatabaseException {
        node = getNodeFromDB();
        if( node == null ) {
            logger.info( "Installing {}", title );

            JsonObject json = new JsonObject();
            setJson( json );
            try {
                T instance = getDescriptor().newInstance( core, json, core.getRoot() );
                instance.updateConfiguration( json );
                instance.save();
                node = instance;
            } catch( CoreException e ) {
                throw new DatabaseException( "Unable to install " + title, e );
            } catch( ClassNotFoundException e ) {
                throw new DatabaseException( "Unable to install " + title, e );
            }
        }
    }

    public void after() {
        /* Default implementation is no op */
    }

    @Override
    public T getValue() {
        return node;
    }
}
