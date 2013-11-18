package org.seventyeight.web.authorization;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class ACL {

    private static Logger logger = Logger.getLogger( ACL.class );

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
    protected MongoDocument document;

    public ACL( Node parent, MongoDocument document ) {
        this.parent = parent;
        this.document = document;
    }

    public Node getParent() {
        return parent;
    }

    public abstract boolean hasPermission( User user, Permission permission );

    public boolean canModerate( User user ) {
        return getPermission( user ).ordinal() >= Permission.WRITE.ordinal();
    }

    public abstract List<Authorizable> getAuthorized( Permission permission );

    public abstract Permission getPermission( User user );
}
