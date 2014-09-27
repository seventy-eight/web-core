package org.seventyeight.web.extensions;

import org.seventyeight.web.model.Menu;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public interface MenuContributor {
	public boolean isApplicable(Node node);
    public void addContributingMenu( Node node, Menu menu );
}
