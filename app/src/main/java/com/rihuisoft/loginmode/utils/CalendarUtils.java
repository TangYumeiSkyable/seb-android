package com.rihuisoft.loginmode.utils;

import java.util.Calendar;

/**
 * Created by emma on 2017/5/19.
 */

public final class CalendarUtils {
    /**
     * To convert a timestamp to zero point in the day time stamp
     *
     * @param milliseconds
     * @return
     */
    public static Calendar zeroFromHour(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(completMilliseconds(milliseconds));
        zeroFromHour(calendar);
        return calendar;
    }
    /**
     * Will, minutes, seconds, and milliseconds value is set to 0
     *
     * @param calendar
     */
    public static void zeroFromHour(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    /**
     * Because the server returns is 10, mobile terminal used to fill all three
     *
     * @param milliseconds
     * @return
     */
    private static long completMilliseconds(long milliseconds) {
        String milStr = Long.toString(milliseconds);
        if (milStr.length() == 10) {
            milliseconds = milliseconds * 1000;
        }
        return milliseconds;
    }
}
