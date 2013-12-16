package org.seventyeight.web.project.model;

import org.joda.time.DateTime;

/**
 * @author cwolfgang
 */
public interface Experience {
    public DateTime getDate();
    public String getDisplayName();
    public String getType();
    public String getUrl();
}
