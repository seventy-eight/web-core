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

    public enum Month {
        JANUARY( 1, "January" ),
        FEBRUARY( 2, "February" ),
        MARCH( 3, "March" ),
        APRIL( 4, "April" ),
        MAY( 5, "May" ),
        JUNE( 6, "June" ),
        JULY( 7, "July" ),
        AUGUST( 8, "August" ),
        SEPTEMBER( 9, "September" ),
        OCTOBER( 10, "October" ),
        NOVEMBER( 11, "November" ),
        DECEMBER( 12, "December" );

        private String monthTitle;
        private int monthNumber;

        Month( int number, String title ) {
            this.monthNumber = number;
            this.monthTitle = title;
        }

        public static Month getMonth( int monthNumber ) {
            switch( monthNumber ) {
                case 1:
                    return JANUARY;
                case 2:
                    return FEBRUARY;
                case 3:
                    return MARCH;
                case 4:
                    return APRIL;
                case 5:
                    return MAY;
                case 6:
                    return JUNE;
                case 7:
                    return JULY;
                case 8:
                    return AUGUST;
                case 9:
                    return SEPTEMBER;
                case 10:
                    return OCTOBER;
                case 11:
                    return NOVEMBER;
                case 12:
                    return DECEMBER;
                default:
                    return null;
            }
        }
    }

    public String getMonthString( int monthNumber ) {
        Month month = Month.getMonth( monthNumber );
        if( month != null ) {
            return month.monthTitle;
        } else {
            return null;
        }
    }
}
