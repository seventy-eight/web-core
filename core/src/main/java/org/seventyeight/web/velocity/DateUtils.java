package org.seventyeight.web.velocity;

import org.seventyeight.utils.TimeUtils;

import java.util.Date;

/**
 * @author cwolfgang
 */
public class DateUtils {
    public String getDateString( Date date ) {
        return TimeUtils.getSmallTimeString( System.currentTimeMillis() - date.getTime() );
    }
}
