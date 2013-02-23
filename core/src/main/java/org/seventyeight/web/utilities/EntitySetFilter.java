package org.seventyeight.web.utilities;

/**
 * @author cwolfgang
 *         Date: 31-01-13
 *         Time: 23:32
 */
public interface EntitySetFilter {
    public void filter( EntitySet set );
    public String getName();
}
