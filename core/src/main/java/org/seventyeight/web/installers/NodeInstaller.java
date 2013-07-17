package org.seventyeight.web.installers;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public abstract class NodeInstaller<T extends AbstractNode<T>> implements DBInstallable<T> {

    protected String title;

    protected T node;

    protected abstract void setParameters( Parameters parameters );

    protected abstract Descriptor<T> getDescriptor();

    protected abstract T getNodeFromDB();

    protected NodeInstaller( String title ) {
        this.title = title;
    }

    @Override
    public void install() throws DatabaseException {
        node = getNodeFromDB();
        if( node == null ) {
            Parameters p = new Parameters();
            setParameters( p );
            try {
                T instance = getDescriptor().newInstance( title );
                instance.save( p, null );
                node = instance;
            } catch( CoreException e ) {
                throw new DatabaseException( "Unable to install " + title, e );
            } catch( ClassNotFoundException e ) {
                throw new DatabaseException( e );
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
