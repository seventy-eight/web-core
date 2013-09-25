package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface TreeNodeVerb {
    public Class<? extends Resource<?>> consumes();
    public Class<? extends Resource<?>> produces();
}
