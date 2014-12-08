package com.eschoeffler.datewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eschoeffler.datewatch.shared.Preferences;

/**
 * Created by eschoeffler on 10/19/14.
 */
public class StartupReceiver extends BroadcastReceiver {
  private static final String TAG = "StartupReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Received broadcast intent: " + intent.getAction());
    Preferences prefs = new Preferences();
    prefs.load(context);
    UpdateEventsService.setServiceAlarm(context, prefs.getBoolean(Preferences.SHOW_EVENTS));
  }
}
