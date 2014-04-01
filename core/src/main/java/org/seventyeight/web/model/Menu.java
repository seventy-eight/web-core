package org.seventyeight.web.model;

import org.seventyeight.web.authorization.ACL;

import java.util.*;

/**
 * @author cwolfgang
 */
public class Menu {
    public static class MenuItem {
        private String title;
        private String url;
        private ACL.Permission permission;

        public MenuItem( String title, String url, ACL.Permission permission ) {
            this.title = title;
            this.url = url;
            this.permission = permission;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public ACL.Permission getPermission() {
            return permission;
        }
    }

    private Map<String, List<MenuItem>> menu = new HashMap<String, List<MenuItem>>();

    public void addItem(String title, MenuItem item) {
        if(!menu.containsKey( title )) {
            menu.put( title, new ArrayList<MenuItem>() );
        }

        menu.get( title ).add( item );
    }

    public Set<String> getHeaderKeys() {
        return menu.keySet();
    }

    public List<MenuItem> getMenuItems(String key) {
        return menu.get( key );
    }
}
