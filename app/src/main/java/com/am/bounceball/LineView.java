package com.am.bounceball;

/**
 * Created by amounier on 10/7/16.
 */

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class LineView extends View {
    private final Paint mPaint;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private double distance;
    private boolean fading = false;
    private int color = getResources().getColor(R.color.lineColor);
    private int backgroundColor = getResources().getColor(R.color.background);

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(12);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                endX = event.getX();
                endY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                distance = dist(startX, startY, endX, endY);
                invalidate();
                break;
        }
        return true;
    }

    public void reset() {
        fadeLine();
    }

    private boolean contains(float x, float y) {
        if (fading)
            return false;
        if (startX == 0 && startY == 0 && endX == 0 && endY == 0)
            return false;
        if (Math.abs(dist(startX, startY, x, y) + dist(x, y, endX, endY) - distance) < 20) {
            return true;
        }
        return false;
    }

    private double dist(float startX, float startY, float endX, float endY) {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    private void fadeLine() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new LinearInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                mPaint.setColor(backgroundColor);
                invalidate();
                startX = 0;
                startY = 0;
                endX = 0;
                endY = 0;
                fading = false;
                mPaint.setColor(color);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
        fading = true;
        this.startAnimation(fadeOut);
    }

    private double getAngle() {

        double y1 = Math.abs(endY - startY);
        double x1 = Math.abs(endX - startX);

        double angle1 = Math.toDegrees(Math.atan2(y1, x1));

        if ((startX < endX && startY > endY)
                || (startX > endX && startY < endY)) {
            return (90 - 2 * angle1);
        }
        if (startX < endX && startY < endY
                || startX > endX && startY > endY) {
            return (90 + 2 * angle1);
        }
        return (angle1 - 90);
    }

    public void calculateCollision(ImageView ball, ValueAnimator animation, float vel) {
        Log.i("collision at", ball.getX() + " " + ball.getY());

        double angle = getAngle();
        Log.i("angles", " " + angle);
        PropertyValuesHolder pvhX;
        PropertyValuesHolder pvhy;
        float dist = 0;
        if (angle < 45) {
            double rads = Math.toRadians(angle);
            float y;
            if (ball.getX() < ball.getY()) {
                y = ball.getY() - (ball.getY() * (float) Math.tan(rads));
            } else {
                y = ball.getY() - (ball.getX() * (float) Math.tan(rads));
            }
            float x = 0;
            if (y > getDisplayHeight()) {
                y = getDisplayHeight() - 100;
                rads = Math.toRadians(90 - angle);
                x = ball.getX() + ((getDisplayHeight() - ball.getY()) * (float) Math.tan(rads));
            }
            pvhX = PropertyValuesHolder.ofFloat("x", x);
            pvhy = PropertyValuesHolder.ofFloat("y", y);
            dist = (float) dist(ball.getX(), ball.getY(), x, y);
        } else if (angle == 45) {
            pvhX = PropertyValuesHolder.ofFloat("x", 0);
            pvhy = PropertyValuesHolder.ofFloat("y", 0);
        } else if (angle > 45 && angle < 90) {
            double rads = Math.toRadians(90 - angle);
            float x;
            if (ball.getX() < ball.getY())
                x = ball.getX() - (ball.getX() * (float) Math.tan(rads));
            else
                x = ball.getX() - (ball.getY() * (float) Math.tan(rads));
            pvhX = PropertyValuesHolder.ofFloat("x", x);
            pvhy = PropertyValuesHolder.ofFloat("y", 0);
            dist = (float) dist(ball.getX(), ball.getY(), x, 0);

        } else if (angle == 90) {
            pvhX = PropertyValuesHolder.ofFloat("x", getDisplayWidth() / 2);
            pvhy = PropertyValuesHolder.ofFloat("y", 0);
            dist = (float) dist(ball.getX(), ball.getY(), getDisplayWidth() / 2, 0);
        } else if (angle > 90 && angle < 135) {
            double rads = Math.toRadians(angle - 90);
            float x;
            if (ball.getX() < ball.getY())
                x = ball.getX() + (ball.getX() * (float) Math.tan(rads));
            else
                x = ball.getX() + (ball.getY() * (float) Math.tan(rads));
            pvhX = PropertyValuesHolder.ofFloat("x", x);
            pvhy = PropertyValuesHolder.ofFloat("y", 0);
            dist = (float) dist(ball.getX(), ball.getY(), x, 0);
        } else if (angle == 135) {
            pvhX = PropertyValuesHolder.ofFloat("x", getDisplayWidth() - 20);
            pvhy = PropertyValuesHolder.ofFloat("y", 0);
            dist = (float) dist(ball.getX(), ball.getY(), getDisplayWidth() - 20, 0);
        } else if (angle > 135 && angle < 180) {
            double rads = Math.toRadians(180 - angle);
            pvhX = PropertyValuesHolder.ofFloat("x", getDisplayWidth() - 20);
            float y = ball.getY() - ((getDisplayWidth() - ball.getX()) * (float) Math.tan(rads));
            pvhy = PropertyValuesHolder.ofFloat("y", y);
            dist = (float) dist(ball.getX(), ball.getY(), getDisplayWidth() - 20, y);
        } else if (angle == 180) {
            pvhX = PropertyValuesHolder.ofFloat("x", getDisplayWidth() - 20);
            pvhy = PropertyValuesHolder.ofFloat("y", ball.getY());
            dist = (float) dist(ball.getX(), ball.getY(), getDisplayWidth() - 20, ball.getY());
        } else {
            double rads = Math.toRadians(angle - 180);
            float x = getDisplayWidth() - 20;
            float y = ball.getY() + ((getDisplayWidth() - ball.getX()) * (float) Math.tan(rads));
            if (y > getDisplayHeight()) {
                y = getDisplayHeight() - 80;
                rads = Math.toRadians(90 - (angle - 180));
                x = ball.getX() + ((getDisplayHeight() - ball.getY()) * (float) Math.tan(rads));
            }

            pvhX = PropertyValuesHolder.ofFloat("x", x);
            pvhy = PropertyValuesHolder.ofFloat("y", y);
            dist = (float) dist(ball.getX(), ball.getY(), x, y);
        }

        animation.setValues(pvhX, pvhy);
        animation.setDuration((long) (vel * dist));
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        reset();
    }

    public boolean hasCollisioned(ImageView ball) {
        return contains(ball.getX(), ball.getY());
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    private int getDisplayWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }
}