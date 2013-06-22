package org.seventyeight.web.project.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class AddNode implements Node, Parent {

    private Map<String, AddAction> actions = new HashMap<String, AddAction>(  );

    public void addAction( String name, AddAction a ) {
        actions.put( name, a );
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        if( actions.containsKey( name ) ) {
            return actions.get( name );
        } else {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Search";
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }
}
