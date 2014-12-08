package com.eschoeffler.datewatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.eschoeffler.datewatch.shared.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by eschoeffler on 11/8/14.
 */
public class ClockView extends View {
  private static final String TAG = "ClockView";

  public enum Resolution {
    NONE(0),
    THREE_HOURS(4),
    HOUR(12),
    MINUTE(60);

    private final int mNumTicks;
    Resolution(int numTicks) {
      mNumTicks = numTicks;
    }

    public int getNumTicks() {
      return mNumTicks;
    }
  }

  private int mWidth;
  private int mHeight;
  private Iterable<Hand> mHands = new ArrayList<Hand>();
  private Resolution mTickResolution = Resolution.NONE;
  private boolean mShowCenter;

  public ClockView(Context context) {
    super(context);
  }

  public ClockView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }


  public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setTickResolution(Resolution resolution) {
    mTickResolution = resolution;
    invalidate();
  }

  public void setShowCenter(boolean showCenter) {
    mShowCenter = showCenter;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    this.mWidth = w;
    this.mHeight = h;
    super.onSizeChanged(w, h, oldw, oldh);
  }

  public PointF getCenter() {
    return new PointF(mWidth / 2f, mHeight / 2f);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (mShowCenter) {
      Paint centerPaint = new Paint();
      centerPaint.setStyle(Paint.Style.FILL);
      centerPaint.setColor(Color.WHITE);
      centerPaint.setAntiAlias(true);
      canvas.drawCircle(mWidth / 2f, mHeight / 2f, 5, centerPaint);
    }
    if (mTickResolution.getNumTicks() > 0) {
      int increment = 360 / mTickResolution.getNumTicks();
      for (int i = 0; i < 360; i+= increment) {
        int length = 10;
        int width = 3;
        if (i % 30 == 0) {
          length = 20;
          if (i % 90 == 0) {
            length = 40;
            width = 5;
          }
        }
        drawHand(canvas, new Hand(i, Color.GRAY, length, false, width));
      }
    }
    for (Hand hand : mHands) {
      drawHand(canvas, hand);
    }
  }

  public void setHands(Iterable<Hand> hands) {
    if (!mHands.equals(hands)) {
      mHands = hands;
      invalidate();
    }
  }

  private void drawHand(Canvas canvas, Hand hand) {
    PointF start;
    PointF end;
    if (hand.mStartInCenter) {
      start = pointFromCenter(10, hand.mDegrees);
      end = pointFromCenter(10 + hand.mLength, hand.mDegrees);
    } else {
      start = pointFromEdge(hand.mLength, hand.mDegrees);
      end = pointFromEdge(0, hand.mDegrees);
    }
    Paint handPaint = new Paint();
    handPaint.setAntiAlias(true);
    handPaint.setColor(hand.mColor);
    handPaint.setStrokeWidth(hand.mWidth);
    canvas.drawLine(start.x, start.y, end.x, end.y, handPaint);
  }

  protected PointF pointFromEdge(float distFromEdge, float degrees) {
    return pointFromCenter(Math.min(mHeight / 2, mWidth / 2) - distFromEdge, degrees);
  }

  protected PointF pointFromCenter(float distFromCenter, float degrees) {
    PointF center = getCenter();
    float x = center.x + distFromCenter * (float) Math.sin(Math.toRadians(degrees));
    // Subtract from centerY because 0,0 is top left of canvas instead of bottom left.
    float y= center.y - distFromCenter * (float) Math.cos(Math.toRadians(degrees));
    return new PointF(x, y);
  }

  public static class Hand {
    private float mDegrees;
    private int mColor;
    private int mLength;
    private boolean mStartInCenter;
    private float mWidth;
    public Hand(float degrees, int color) {
      this(degrees, color, 40, false);
    }
    public Hand(float degrees, int color, int length, boolean startInCenter) {
      this(degrees, color, length, startInCenter, 5);
    }
    public Hand(float degrees, int color, int length, boolean startInCenter, float width) {
      mDegrees = degrees;
      mColor = color;
      mLength = length;
      mStartInCenter = startInCenter;
      mWidth = width;
    }
    public boolean equals(Object obj) {
      if (!(obj instanceof Hand)) {
        return false;
      }
      Hand other = (Hand) obj;
      return this.mColor == other.mColor &&
          this.mDegrees == other.mDegrees &&
          this.mLength == other.mLength &&
          mStartInCenter == other.mStartInCenter &&
          this.mWidth == other.mWidth;
    }
  }
}
