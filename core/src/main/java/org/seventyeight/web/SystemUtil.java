package org.seventyeight.web;

/**
 * @author cwolfgang
 *
 * .... Not to be used yet...
 */
public class SystemUtil {

    private static final ThreadLocal<Core> store = new ThreadLocal<Core>();

    public static void setCore(Core core) {
        if(store.get() == null) {
            store.set( core );
        }
    }

    public static Core getCore() {
        return store.get();
    }
}
