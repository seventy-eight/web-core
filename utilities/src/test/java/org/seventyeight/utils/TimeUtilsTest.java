package org.seventyeight.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class TimeUtilsTest {

    @Test
    public void testOneSecond() {
        TimeUtils.Time t = TimeUtils.getTime( 1000 );

        assertThat( t.seconds, is( 1 ) );
    }

    @Test
    public void testOneHour() {
        TimeUtils.Time t = TimeUtils.getTime( TimeUtils.HOURS );

        assertThat( t.seconds, is( 0 ) );
        assertThat( t.hours, is( 1 ) );
    }

    @Test
    public void testMulti() {
        TimeUtils.Time t = TimeUtils.getTime( TimeUtils.HOURS * 2 + TimeUtils.MINUTES * 33 + TimeUtils.SECONDS * 13 );

        assertThat( t.millis, is( 0 ) );
        assertThat( t.seconds, is( 13 ) );
        assertThat( t.minutes, is( 33 ) );
        assertThat( t.hours, is( 2 ) );
        assertThat( t.days, is( 0 ) );
    }
}
