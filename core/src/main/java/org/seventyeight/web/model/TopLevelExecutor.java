package org.seventyeight.web.model;

import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

/**
 * @author cwolfgang
 *         Date: 02-12-12
 *         Time: 16:00
 */
public interface TopLevelExecutor extends TopLevelGizmo {

    public void execute( Request request, Response response );
}
