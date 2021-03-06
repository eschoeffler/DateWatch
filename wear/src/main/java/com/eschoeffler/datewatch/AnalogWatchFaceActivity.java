package com.eschoeffler.datewatch;

import android.graphics.Color;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.eschoeffler.datewatch.shared.Event;
import com.eschoeffler.datewatch.shared.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eschoeffler on 11/9/14.
 */
public class AnalogWatchFaceActivity extends EventWatchFaceActivity {
  private static final String TAG = "AnalogWatchFaceActivity";
  private static final int MILLIS_PER_HALF_DAY = 12 * 60 * 60 * 1000;
  private static final String DAY_FORMAT_DISPLAYED = "EEE d";

  private TextView mDayView;
  private TextView mNextEvent;
  private TextView mTimeTill;
  private ClockView mClockView;
  private View mTextLayout;
  private boolean mNoColors;

  @Override
  protected int getLayout() {
    return R.layout.activity_analog_watch_face;
  }

  @Override
  public void onLayoutInflated(WatchViewStub stub) {
    mDayView = (TextView) stub.findViewById(R.id.day);
    mNextEvent = (TextView) stub.findViewById(R.id.nextEvent);
    mTimeTill = (TextView) stub.findViewById(R.id.timeTill);
    mTextLayout = (View) stub.findViewById(R.id.text);
    mClockView = (ClockView) stub.findViewById(R.id.clock);
    mClockView.setShowCenter(true);
    mClockView.setTickResolution(ClockView.Resolution.HOUR);
    super.onLayoutInflated(stub);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mNoColors = true;
    updateClockView(Calendar.getInstance().getTime().getTime());
  }

  @Override
  protected void onResume() {
    mNoColors = false;
    updateClockView(Calendar.getInstance().getTime().getTime());
    super.onResume();
  }

  @Override
  protected void updateWatchFace() {
    Date now = Calendar.getInstance().getTime();
    mDayView.setText(new SimpleDateFormat(DAY_FORMAT_DISPLAYED).format(now));
    updateEventView(now.getTime());
    updateClockView(now.getTime());
    if (getPrefs().getBoolean(Preferences.SHOW_TEXT_ABOVE)) {
      mTextLayout.bringToFront();
      mTextLayout.invalidate();
    } else {
      mClockView.bringToFront();
      mClockView.invalidate();
    }
  }


  private void updateEventView(long now) {
    Event nextEvent = Event.getNextEvent(getEvents());
    mNextEvent.setVisibility(nextEvent == null ? View.GONE : View.VISIBLE);
    mTimeTill.setVisibility(nextEvent == null ? View.GONE : View.VISIBLE);
    if (nextEvent == null || !getPrefs().getBoolean(Preferences.SHOW_EVENTS)) {
      mNextEvent.setText("");
      mTimeTill.setText("");
    } else {
      mNextEvent.setText(nextEvent.title);
      mTimeTill.setText(getTimeUntilStr(nextEvent, now));
    }
  }

  private void updateClockView(long now) {
    if (mClockView == null) {
      return;
    }
    List<ClockView.Hand> hands = new ArrayList<ClockView.Hand>();
    hands.add(new ClockView.Hand(ClockMath.getHalfDayDegrees(now), Color.WHITE, 80, true));
    hands.add(new ClockView.Hand(ClockMath.getHourDegrees(now), Color.WHITE, 120, true));
    for (Event e : getEvents()) {
      long timeTill = e.begin - now;
      if (timeTill > 0 && timeTill < MILLIS_PER_HALF_DAY) {
        hands.add(new ClockView.Hand(ClockMath.getHalfDayDegrees(e.begin),
            mNoColors ? Color.WHITE : Color.CYAN));
      }
    }
    mClockView.setHands(hands);
  }
}
