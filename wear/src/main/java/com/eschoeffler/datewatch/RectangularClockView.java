package com.eschoeffler.datewatch;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.eschoeffler.datewatch.shared.Preferences;

/**
 * Created by eschoeffler on 12/5/14.
 */
public class RectangularClockView extends ClockView {
  public RectangularClockView(Context context) {
    super(context);
  }

  public RectangularClockView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RectangularClockView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected PointF pointFromEdge(float distFromEdge, float degrees) {
    if (!((EventWatchFaceActivity) getContext()).getPrefs().getBoolean(Preferences.USE_EDGE_TICKS)) {
      return super.pointFromEdge(distFromEdge, degrees);
    } else {
      PointF center = getCenter();
      if (degrees == 0) {
        return new PointF(center.x, distFromEdge);
      } else if (degrees == 90) {
        return new PointF(getWidth() - distFromEdge, center.y);
      } else if (degrees == 180) {
        return new PointF(center.x, getHeight() - distFromEdge);
      } else if (degrees == 270) {
        return new PointF(distFromEdge, center.y);
      } else {
        float distToEdge = (float) Math.min(
            Math.abs(getWidth() / (2 * Math.sin(Math.toRadians(degrees)))),
            Math.abs(getHeight() / (2 * Math.cos(Math.toRadians(degrees)))));
        return pointFromCenter(distToEdge - distFromEdge, degrees);
      }
    }
  }
}