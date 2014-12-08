package com.eschoeffler.datewatch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.eschoeffler.datewatch.shared.Event;
import com.eschoeffler.datewatch.shared.Preferences;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;


public class DateWatchActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {

  private static final String TAG = "DateWatchActivity";

  private Preferences mPrefs = new Preferences();
  private UpdateEventsService mUpdateEventsAlarm;
  private GoogleApiClient mGoogleApiClient;
  private List<Event> mEventList = new ArrayList<Event>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_date_watch);
    mPrefs.load(this);

    final CheckBox showCalendarEvents = (CheckBox) findViewById(R.id.showCalendarEvents);
    showCalendarEvents.setChecked(UpdateEventsService.isServiceAlarmOn(DateWatchActivity.this));
    showCalendarEvents.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        UpdateEventsService.setServiceAlarm(DateWatchActivity.this, showCalendarEvents.isChecked());
        mPrefs.setBoolean(Preferences.SHOW_EVENTS, showCalendarEvents.isChecked());
        mPrefs.save(DateWatchActivity.this);
        sendData();
      }
    });

    setupPreferenceCheckbox(R.id.showTextAboveClock, Preferences.SHOW_TEXT_ABOVE);
    setupPreferenceCheckbox(R.id.use24HourClock, Preferences.USE_24_HOUR_CLOCK);
    setupPreferenceCheckbox(R.id.useEdgeTicks, Preferences.USE_EDGE_TICKS);
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addApi(Wearable.API)
        .build();
  }

  private void setupPreferenceCheckbox(int checkBoxId, final String preference) {
    final CheckBox checkBox = (CheckBox) findViewById(checkBoxId);
    checkBox.setChecked(mPrefs.getBoolean(preference));
    checkBox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPrefs.setBoolean(preference, checkBox.isChecked());
        mPrefs.save(DateWatchActivity.this);
        sendData();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mGoogleApiClient.disconnect();
  }

  private void sendData() {
    if (!mGoogleApiClient.isConnected()) {
      return;
    }
    WearableService.sendEvents(
        mGoogleApiClient, CalendarService.getUpcomingEvents(DateWatchActivity.this));
    WearableService.send(mGoogleApiClient, new Preferences.Serializer(), mPrefs);
  }

  @Override
  public void onConnected(Bundle bundle) {
    sendData();
  }

  @Override
  public void onConnectionSuspended(int cause) {
    Log.d(TAG, "onConnectionSuspended: " + cause);
  }
}
