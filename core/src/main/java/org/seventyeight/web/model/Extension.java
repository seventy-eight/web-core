package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface Extension<T extends Node> {
    public boolean isApplicable(T node);
}
