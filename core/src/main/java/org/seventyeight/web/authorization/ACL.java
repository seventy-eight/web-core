package org.seventyeight.web.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class ACL {

    private static Logger logger = LogManager.getLogger( ACL.class );

    public enum Permission {
        NONE("none"),
        ALL("all"),
        ANONYMOUS("anonymous"),
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
    protected MongoDocument document;

    public ACL( Node parent, MongoDocument document ) {
        this.parent = parent;
        this.document = document;
    }

    public Node getParent() {
        return parent;
    }

    public abstract boolean hasPermission( User user, Permission permission );

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

    public static final AllAccess ALL_ACCESS = new AllAccess( null, null );

    private static class AllAccess extends ACL {

        public AllAccess( Node parent, MongoDocument document ) {
            super( parent, document );
        }

        @Override
        public boolean hasPermission( User user, Permission permission ) {
            return true;
        }

        @Override
        public List<Authorizable> getAuthorized( Permission permission ) {
            return Collections.emptyList();
        }

        @Override
        public Permission getPermission( User user ) {
            return Permission.ADMIN;
        }
    }
}
