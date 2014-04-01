package org.seventyeight.web.extensions;

import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Menu;

/**
 * @author cwolfgang
 */
public class DefaultMenuContributor implements MenuContributor<AbstractNode<?>> {
    @Override
    public void addContributingMenu( AbstractNode<?> node, Menu menu ) {
        menu.addItem( "Main", new Menu.MenuItem( "View", node.getUrl(), ACL.Permission.READ ) );
        menu.addItem( "Main", new Menu.MenuItem( "Edit", node.getConfigUrl(), ACL.Permission.ADMIN ) );
    }
}
