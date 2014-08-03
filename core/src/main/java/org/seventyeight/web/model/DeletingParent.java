package org.seventyeight.web.model;

/**
 * Methods implementing this, will be able to delete the requested child {@link Node}
 *
 * @author cwolfgang
 */
public interface DeletingParent {
    public void deleteChild(Node node);
}
