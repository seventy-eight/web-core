package org.seventyeight.web.extensions;

import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;

/**
 * @author cwolfgang
 */
public class DefaultAddMenuContributor implements MenuContributor {
    @Override
    public void addContributingMenu( Node node, Menu menu ) {
        menu.addItem( "Add stuff", new Menu.MenuItem( "Upload", "/upload", ACL.Permission.READ ) );
    }

	@Override
	public boolean isApplicable(Node node) {
		return true;
	}
}
