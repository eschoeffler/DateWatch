package com.eschoeffler.datewatch;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.eschoeffler.datewatch.shared.Event;
import com.eschoeffler.datewatch.shared.WearableSerializer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * Created by eschoeffler on 10/19/14.
 */
public class WearableService {
  private static final String TAG = "WearableService";

  public static PendingResult<DataApi.DataItemResult> sendEvents(
      GoogleApiClient googleApiClient, List<Event> eventList) {
    Log.d(TAG, "Sending " + eventList.size() + " events to wearable.");
    return send(googleApiClient, new Event.ListSerializer(), eventList);
  }

  public static void sendEvents(Context context, final List<Event> eventList) {
    Log.d(TAG, "Sending " + eventList.size() + " events to wearable.");
    send(context, new Event.ListSerializer(), eventList);
  }

  public static <T> void send(
      Context context, final WearableSerializer<T> serializer, final T data) {
    final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
        .addApi(Wearable.API)
        .build();
    googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
      @Override public void onConnected(Bundle connectionHint) {
        send(googleApiClient, serializer, data);
        googleApiClient.disconnect();
      }
      @Override public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
      }
    });
    googleApiClient.connect();
  }

  public static <T> PendingResult<DataApi.DataItemResult> send(
      GoogleApiClient googleApiClient, WearableSerializer<T> serializer, T data) {
    if (!googleApiClient.isConnected()) {
      throw new IllegalArgumentException("GoogleApiClient must be connected to send events");
    }
    return Wearable.DataApi.putDataItem(googleApiClient, serializer.serialize(data));
  }
}
