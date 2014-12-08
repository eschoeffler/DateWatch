package com.eschoeffler.datewatch;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.eschoeffler.datewatch.shared.Event;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by eschoeffler on 10/19/14.
 */
public class CalendarService {

  private static final String TAG = "CalendarService";
  public static final int SECONDS_MILLIS = 1000; // 1 second is 1000 ms
  public static final int MINUTES_MILLIS = 60 * SECONDS_MILLIS; // 1 minute = 60 sec
  public static final int HOURS_MILLIS = 60 * MINUTES_MILLIS; // 1 hour = 60 min
  public static final String[] EVENT_PROJECTION = new String[] {
      CalendarContract.Instances.TITLE,
      CalendarContract.Instances.BEGIN
  };

  public static List<Event> getUpcomingEvents(Context context) {
    long now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    long tomorrow = now + 24 * HOURS_MILLIS;
    Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
    ContentUris.appendId(eventsUriBuilder, now);
    ContentUris.appendId(eventsUriBuilder, tomorrow);
    String selection = CalendarContract.Instances.ALL_DAY + " == 0"
        + " AND " + CalendarContract.Instances.SELF_ATTENDEE_STATUS
        + "!="	+ CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED
        + " AND " + CalendarContract.Instances.STATUS + "!="
        + CalendarContract.Instances.STATUS_CANCELED
        + " AND " + CalendarContract.Instances.VISIBLE + "!=0";
    Cursor cur = null;
    try {
      cur = context.getContentResolver().query(eventsUriBuilder.build(),
          EVENT_PROJECTION, selection, new String[0], CalendarContract.Instances.BEGIN);
    } catch (  SQLiteException e) {
      Log.d(TAG, "SQL Error: " + e.getMessage());
    }
    ArrayList<Event> eventList = new ArrayList<Event>();
    while (cur != null && cur.moveToNext()) {
      eventList.add(new Event(cur.getString(0), cur.getLong(1)));
    }
    if (cur != null) {
      cur.close();
    }
    return eventList;
  }
}
