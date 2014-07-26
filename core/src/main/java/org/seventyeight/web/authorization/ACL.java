package org.seventyeight.web.authorization;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;

import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class ACL<T extends ACL<T>> extends PersistedNode implements Describable<T> {

    private static Logger logger = LogManager.getLogger( ACL.class );

    public enum Permission {
        NONE("none"),
        READ("read"),
        WRITE("write"),
        ADMIN("admin");

        private String dbname;

        private Permission( String dbname ) {
            this.dbname = dbname;
        }

        public String getDbname() {
            return dbname;
        }
    }

    protected Node parent;
    protected Resource<?> resourceParent;

    public ACL( Core core, Node parent, MongoDocument document ) {
        super(core, document);
        this.parent = parent;
        if(parent instanceof Resource) {
            resourceParent = (Resource<?>) parent;
        }
    }

    public Node getParent() {
        return parent;
    }

    //public abstract boolean hasPermission( User user, Permission permission );

    public boolean canRead( User user ) {
        return getPermission( user ).ordinal() >= Permission.READ.ordinal();
    }

    public boolean canWrite( User user ) {
        return getPermission( user ).ordinal() >= Permission.WRITE.ordinal();
    }

    public boolean isAdmin( User user ) {
        return getPermission( user ).ordinal() >= Permission.ADMIN.ordinal();
    }

    public abstract List<Authorizable> getAuthorized( Permission permission );

    public abstract Permission getPermission( User user );

    public static final AllAccess ALL_ACCESS = new AllAccess( null, null, null );

    @Override
    public MongoDocument getDocument() {
        return document;
    }

    @Override
    public void save() {
        // Should not be needed!?
    }

    @Override
    public Descriptor<T> getDescriptor() {
        return core.getDescriptor( getClass() );
    }

    public static abstract class ACLDescriptor<T extends ACL<T>> extends Descriptor<T> {

        protected ACLDescriptor( Core core ) {
            super( core );
        }

        @Override
        public Describable<T> getDescribable( Node parent, MongoDocument document ) throws ItemInstantiationException {
            MongoDocument d = document.get( "ACL" );
            logger.debug( "ACL DOC: {}", d );
            if(d != null && !d.isNull()) {
                return (Describable<T>) core.getNode( parent, d );
            } else {
                return null;
            }
        }
    }

    private static class AllAccess extends ACL {

        public AllAccess( Core core, Node parent, MongoDocument document ) {
            super( core, parent, document );
        }

        @Override
        public List<Authorizable> getAuthorized( Permission permission ) {
            return Collections.emptyList();
        }

        /*
        @Override
        public List<Authorizable> getAuthorized( Permission permission ) {
            return Collections.emptyList();
        }
        */

        @Override
        public Permission getPermission( User user ) {
            return Permission.ADMIN;
        }

        @Override
        public void updateNode( JsonObject jsonData ) {
            /* Implementation is a no op */
        }

        @Override
        public String getDisplayName() {
            return "All access";
        }

        @Override
        public String getMainTemplate() {
            return null;
        }
    }
}
