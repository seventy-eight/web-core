package org.seventyeight.web.music;

import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;

/**
 * @author cwolfgang
 */
public interface Event extends Node {
    public void setAsPartOf(Resource<?> resource);
}
