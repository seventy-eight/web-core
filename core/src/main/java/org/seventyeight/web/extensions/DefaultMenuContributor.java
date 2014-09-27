package org.seventyeight.web.extensions;

import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;

/**
 * @author cwolfgang
 */
public class DefaultMenuContributor implements MenuContributor {
    @Override
    public void addContributingMenu( Node node, Menu menu ) {
        //menu.addItem( "Main", new Menu.MenuItem( "View", node.getUrl(), ACL.Permission.READ ) );
        menu.addItem( "Main", new Menu.MenuItem( "View", "view", ACL.Permission.READ ) );
        //menu.addItem( "Main", new Menu.MenuItem( "Edit", node.getConfigUrl(), ACL.Permission.ADMIN ) );
        menu.addItem( "Main", new Menu.MenuItem( "Edit", "configure", ACL.Permission.ADMIN ) );
    }

	@Override
	public boolean isApplicable(Node node) {
		return node instanceof Resource;
	}
}
