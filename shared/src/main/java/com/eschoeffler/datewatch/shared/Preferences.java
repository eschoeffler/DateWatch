package com.eschoeffler.datewatch.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eschoeffler on 11/9/14.
 */
public class Preferences implements WearableSerializable {
  public static final String SHOW_TEXT_ABOVE = "showTextAbove";
  public static final String SHOW_EVENTS = "showEvents";
  public static final String USE_24_HOUR_CLOCK = "use24HourClock";
  public static final String USE_EDGE_TICKS = "useEdgeTicks";
  public static final String[] SERIALIZED_KEYS = new String[]{
      SHOW_TEXT_ABOVE, SHOW_EVENTS, USE_24_HOUR_CLOCK, USE_EDGE_TICKS};

  public Map<String, Boolean> booleanPrefs;

  public Preferences() {
    booleanPrefs = new HashMap<String, Boolean>();
    for (String key : SERIALIZED_KEYS) {
      booleanPrefs.put(key, false);
    }
  }

  public void setBoolean(String key, boolean value) {
    booleanPrefs.put(key, value);
  }

  public boolean getBoolean(String key) {
    return booleanPrefs.containsKey(key) ? booleanPrefs.get(key) : false;
  }

  public DataMap serialize() {
    DataMap map = new DataMap();
    for (String key : SERIALIZED_KEYS) {
      map.putBoolean(key, booleanPrefs.get(key));
    }
    return map;
  }

  @Override
  public String toString() {
    ArrayList<String> prefs = new ArrayList<String>();
    for (String key : booleanPrefs.keySet()) {
      prefs.add(key + "=" + booleanPrefs.get(key));
    }
    return "(" + prefs.toString() + ")";
  }

  public static class Serializer extends WearableMapSerializer<Preferences> {
    public Serializer() {
      super("prefs");
    }

    @Override
    protected Preferences fromDataMap(DataMap dataMap) {
      Preferences prefs = new Preferences();
      for (String key : SERIALIZED_KEYS) {
        prefs.setBoolean(key, dataMap.getBoolean(key));
      }
      return prefs;
    }
  }

  public void load(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    for (String key : SERIALIZED_KEYS) {
      setBoolean(key, prefs.getBoolean(key, false));
    }
  }

  public void save(Context context) {
    SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context)
        .edit();
    for (String key : SERIALIZED_KEYS) {
      e.putBoolean(key, getBoolean(key));
    }
    e.commit();
  }
}
