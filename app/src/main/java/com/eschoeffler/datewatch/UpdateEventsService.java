package com.eschoeffler.datewatch;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by eschoeffler on 10/19/14.
 */
public class UpdateEventsService extends IntentService {
  private static final String TAG = "UpdateEventsService";
  private static final long POLL_INTERVAL = 1000 * 60 * 60;

  public UpdateEventsService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "Updating Events");
    WearableService.sendEvents(this, CalendarService.getUpcomingEvents(this));
  }

  public static void setServiceAlarm(Context context, boolean isOn) {
    Intent i = new Intent(context, UpdateEventsService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    if (isOn) {
      alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
    } else {
      alarmManager.cancel(pi);
      pi.cancel();
    }
  }

  public static boolean isServiceAlarmOn(Context context) {
    Intent i = new Intent(context, UpdateEventsService.class);
    PendingIntent pi = PendingIntent.getService(
        context, 0, i, PendingIntent.FLAG_NO_CREATE);
    return pi != null;
  }
}