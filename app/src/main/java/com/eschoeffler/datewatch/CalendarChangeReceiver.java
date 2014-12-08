package com.eschoeffler.datewatch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by eschoeffler on 10/19/14.
 */
public class CalendarChangeReceiver extends BroadcastReceiver {
  private static final String TAG = "CalendarChangeReceiver";
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Updating Events");
    WearableService.sendEvents(context, CalendarService.getUpcomingEvents(context));
  }
}