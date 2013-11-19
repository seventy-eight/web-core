package org.seventyeight.web.velocity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.utils.TimeUtils;

import java.util.Date;

/**
 * @author cwolfgang
 */
public class DateUtils {

    private static Logger logger = LogManager.getLogger( DateUtils.class );

    public String getDateString( Date date ) {
        return TimeUtils.getSmallTimeString( System.currentTimeMillis() - date.getTime() );
    }
}
