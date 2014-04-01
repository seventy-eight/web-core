package org.seventyeight.web.extensions;

import org.seventyeight.web.model.ContributingView;
import org.seventyeight.web.servlet.Request;

import java.util.List;
import java.util.Locale;

/**
 * For {@link org.seventyeight.web.model.Node}'s that partitions ....
 *
 * @author cwolfgang
 */
public interface Partitioned {
    public List<ContributingView> getContributingViews( Locale locale );
    public List<ContributingView> getAdministrativePartitions( Request request );
    public ContributingView getActiveView( Request request );
}
