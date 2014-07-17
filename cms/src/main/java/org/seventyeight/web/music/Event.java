package org.seventyeight.web.music;

import org.seventyeight.web.model.Documented;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;

/**
 * @author cwolfgang
 */
public interface Event extends Node, Documented {
    public void setAsPartOf(Resource<?> resource);
    public String getIdentifier();
    public void save();
}
