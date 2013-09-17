package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public abstract class ACL {

    public enum Permission {
        NONE,
        READ,
        WRITE,
        OWNER
    }

    public static class Permissions {

    }
}
