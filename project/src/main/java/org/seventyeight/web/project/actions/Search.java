package org.seventyeight.web.project.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.SearchAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class Search implements Action {

    private Map<String, SearchAction> actions = new HashMap<String, SearchAction>(  );

    public void addAction( SearchAction a ) {
        actions.put( a.getUrlName(), a );
    }

    @Override
    public String getUrlName() {
        return "search";
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
