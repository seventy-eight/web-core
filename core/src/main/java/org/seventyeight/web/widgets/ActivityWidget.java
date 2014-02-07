package org.seventyeight.web.widgets;

import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Widget;

/**
 * @author cwolfgang
 */
public class ActivityWidget extends Widget {

    @Override
    public Node getParent() {
        return null;  /* Implementation is a no op */
    }

    @Override
    public String getDisplayName() {
        return "Activity";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public String getName() {
        return "Activity widget";
    }
}
