package org.seventyeight.web.model;

import java.util.ArrayList;

/**
 * @author cwolfgang
 */
public class Menu extends ArrayList<Menu.MenuItem> {
    public static class MenuItem {
        private String name;
        private String url;

        public MenuItem( String name, String url ) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return name + ", " + url;
        }
    }
}
