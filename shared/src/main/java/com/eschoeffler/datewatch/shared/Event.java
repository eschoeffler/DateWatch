package com.eschoeffler.datewatch.shared;

import com.google.android.gms.wearable.DataMap;

import java.util.Calendar;

/**
 * A simple calendar event.
 */
public class Event implements WearableSerializable {
  private static final String TITLE_KEY = "title";
  private static final String BEGIN_KEY = "begin";

  public long begin;
  public String title;

  public Event(String title, long begin) {
    this.begin = begin;
    this.title = title;
  }

  @Override
  public DataMap serialize() {
    DataMap map = new DataMap();
    map.putLong(BEGIN_KEY, begin);
    map.putString(TITLE_KEY, title);
    return map;
  }

  @Override
  public String toString() {
    return "(begin=" + begin + ", title=" + title + ")";
  }

  public static class ListSerializer extends WearableListSerializer<Event> {
    public ListSerializer() {
      super("events");
    }

    @Override
    protected Event fromDataMap(DataMap dataMap) {
      return new Event(dataMap.getString(TITLE_KEY), dataMap.getLong(BEGIN_KEY));
    }
  }

  public static Event getNextEvent(Iterable<Event> eventList) {
    long now = Calendar.getInstance().getTimeInMillis();
    Event nextEvent = null;
    for (Event event : eventList) {
      if (event.begin > now) {
        if (nextEvent == null || nextEvent.begin > event.begin) {
          nextEvent = event;
        }
      }
    }
    return nextEvent;
  }
}
