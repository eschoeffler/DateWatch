package com.eschoeffler.datewatch;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by eschoeffler on 11/9/14.
 */
public class ClockMath {

  public static final long MILLIS_PER_HALF_DAY = 12 * 60 * 60 * 1000;
  public static final long MILLIS_PER_HOUR = 60 * 60 * 1000;
  public static final long MILLIS_PER_MINUTE =  60 * 1000;

  public static float getHalfDayDegrees(long time) {
    Calendar midnight = new GregorianCalendar();
    midnight.set(Calendar.HOUR_OF_DAY, 0);
    midnight.set(Calendar.MINUTE, 0);
    midnight.set(Calendar.SECOND, 0);
    long millisSinceMidnight = time - midnight.getTimeInMillis();
    return 360 * ((float) millisSinceMidnight / MILLIS_PER_HALF_DAY);
  }

  public static float getHourDegrees(long time) {
    Calendar lastHour = new GregorianCalendar();
    lastHour.set(Calendar.MINUTE, 0);
    lastHour.set(Calendar.SECOND, 0);
    long millisSinceLastHour = time - lastHour.getTimeInMillis();
    return 360 * ((float) millisSinceLastHour / MILLIS_PER_HOUR);
  }

  public static float getSecondDegrees(long time) {
    Calendar lastMinute = new GregorianCalendar();
    lastMinute.set(Calendar.SECOND, 0);
    long millisSinceLastMinute = time - lastMinute.getTimeInMillis();
    return 360 * ((float) millisSinceLastMinute / MILLIS_PER_MINUTE);
  }
}
