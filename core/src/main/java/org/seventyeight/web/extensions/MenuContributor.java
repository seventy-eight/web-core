package org.seventyeight.web.extensions;

import org.seventyeight.web.model.Menu;
import org.seventyeight.web.model.Node;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface MenuContributor<T extends Node> {
    public void addContributingMenu( T node, Menu menu );
}
