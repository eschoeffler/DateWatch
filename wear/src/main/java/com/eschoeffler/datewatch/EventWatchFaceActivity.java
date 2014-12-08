package com.eschoeffler.datewatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

import com.eschoeffler.datewatch.shared.Event;
import com.eschoeffler.datewatch.shared.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eschoeffler on 11/9/14.
 */
public abstract class EventWatchFaceActivity extends Activity implements
    DataApi.DataListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    WatchViewStub.OnLayoutInflatedListener {

  private static final String TAG = "EventWatchFaceActivity";
  private static final String TIME_FORMAT = "h:mm";
  private static final String TIME_FORMAT_24_HOUR = "H:mm";

  private BroadcastReceiver mTimeInfoReceiver;
  private GoogleApiClient mGoogleApiClient;
  private Event.ListSerializer eventsSerializer = new Event.ListSerializer();
  private Preferences.Serializer prefsSerializer = new Preferences.Serializer();
  private List<Event> mEvents = new ArrayList<Event>();
  private Preferences mPrefs = new Preferences();

  protected abstract int getLayout();
  protected abstract void updateWatchFace();
  protected List<Event> getEvents() {
    return mEvents;
  }
  protected Preferences getPrefs() {
    return mPrefs;
  }
  protected String getTimeUntilStr(Event event, long now) {
    long timeUntil = event.begin - now;
    int minutesUntil = (int) Math.ceil(timeUntil / 1000.0 / 60.0);
    if (minutesUntil <= 90) {
      switch (minutesUntil) {
        case 0:
          return getString(R.string.now);
        case 1:
          return getString(R.string.one_min);
        default:
          return getString(R.string.minutes, minutesUntil);
      }
    } else {
      return new SimpleDateFormat(getTimeFormat()).format(event.begin);
    }
  }

  protected String getTimeFormat() {
    return getPrefs().getBoolean(Preferences.USE_24_HOUR_CLOCK) ? TIME_FORMAT_24_HOUR : TIME_FORMAT;
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayout());
    mTimeInfoReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        updateWatchFace();
      }
    };
    final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
    stub.setOnLayoutInflatedListener(this);
  }

  @Override
  public void onLayoutInflated(WatchViewStub stub) {
    Log.d("EventView", "On layout inflated");
    IntentFilter timeChangedFilter = new IntentFilter();
    timeChangedFilter.addAction(Intent.ACTION_TIME_TICK);
    timeChangedFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    timeChangedFilter.addAction(Intent.ACTION_TIME_CHANGED);
    registerReceiver(mTimeInfoReceiver, timeChangedFilter);
    updateWatchFace();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(Wearable.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    if (mGoogleApiClient != null) {
      mGoogleApiClient.disconnect();
    }
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(mTimeInfoReceiver);
    super.onDestroy();
  }

  @Override
  public void onConnected(Bundle connectionHint) {
    Log.d(TAG, "Connected to Google API Service");
    Wearable.DataApi.addListener(mGoogleApiClient, this);
    getData();
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.d(TAG, "Connection to Google API Service suspended");
    Wearable.DataApi.removeListener(mGoogleApiClient, this);
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.d(TAG, "Failed to connect to Google API Service");
  }

  @Override
  public void onDataChanged(DataEventBuffer dataEvents) {
    Log.d(TAG, "Data Changed");
    for (DataEvent event : dataEvents) {
      Log.d(TAG, "  Type: " + event.getType() + " Uri: " + event.getDataItem().getUri().toString());
      if (event.getType() == DataEvent.TYPE_CHANGED) {
        handleDataItem(event.getDataItem());
      }
    }
  }

  private void getData() {
    Log.d(TAG, "Loading data items");
    PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(mGoogleApiClient);
    results.setResultCallback(new ResultCallback<DataItemBuffer>() {
      @Override
      public void onResult(DataItemBuffer dataItems) {
        for (DataItem dataItem : dataItems) {
          Log.d(TAG, "Data Item with uri: " + dataItem.getUri().toString());
          handleDataItem(dataItem);
        }
        dataItems.release();
      }
    });
  }

  private void handleDataItem(DataItem dataItem) {
    if (eventsSerializer.matchesType(dataItem)) {
      mEvents = eventsSerializer.deserialize(dataItem);
      Log.d(TAG, "Updating events. " + mEvents.toString());
      updateWatchFaceInUiThread();
    } else if(prefsSerializer.matchesType(dataItem)) {
      mPrefs = prefsSerializer.deserialize(dataItem);
      Log.d(TAG, "Updating prefs.\n" + mPrefs.toString());
      updateWatchFaceInUiThread();
    }
  }

  private void updateWatchFaceInUiThread() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        updateWatchFace();
      }
    });
  }
}