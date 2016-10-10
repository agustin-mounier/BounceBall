package com.am.bounceball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amounier on 10/8/16.
 */

public class CircleView extends View {

    List<Circle> circles = new ArrayList<>();

    private static final int FADE_MILLISECONDS = 1000;
    private static final int FADE_STEP = 30;

    private static final int ALPHA_STEP = 255 / (FADE_MILLISECONDS / FADE_STEP);

    private int circleColor1 = getResources().getColor(R.color.circleColor1);
    private int circleColor2 = getResources().getColor(R.color.circleColor2);

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addCircle();
        addCircle();
        addCircle();
        addCircle();
        addCircle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean refresh = false;
        for (int i = 0; i < circles.size(); i++) {
            Circle c = circles.get(i);
            if (c.faddingIn) {
                fadeInCircle(c, canvas);
            } else if (c.faddingOut) {
                fadeOutAndExpandCircle(c, canvas);
            } else {
                float rad = c.radius;
                while (rad > 0) {
                    canvas.drawCircle(c.x, c.y, rad, c.mPaint);
                    rad -= 15;
                }
            }
            if(!refresh && (c.faddingIn || c.faddingOut)){
                refresh = true;
            }
        }
        if(refresh){
            postInvalidateDelayed(FADE_STEP);
        }
    }


    public int collision(ImageView ball) {
        int score = 0;
        for (int i = 0; i < circles.size(); i++) {
            Circle c = circles.get(i);
            if(c.faddingOut)
                return 0;
            if (c.contains(ball.getX(), ball.getY())) {
                score = (int) (1000 / c.radius);
                c.faddingOut = true;
                invalidate();
                return score;
            }
        }
        return score;
    }

    public void addCircle() {
        Circle newCricle = new Circle();
        boolean found = false;
        while(!found) {
            found = true;
            for (Circle c : circles) {
                if (newCricle.dist(c.x, c.y) < newCricle.radius + c.radius) {
                    newCricle = new Circle();
                    found = false;
                    break;
                }
            }
        }
        circles.add(newCricle);
        invalidate();
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    private int getDisplayWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }

    private void fadeInCircle(Circle c, Canvas canvas) {
        if (c.mPaint.getAlpha() < 255) {
            int newAlpha = ALPHA_STEP + c.mPaint.getAlpha() > 255 ? 255 : ALPHA_STEP + c.mPaint.getAlpha();
            c.mPaint.setAlpha(newAlpha);
            float rad = c.radius;
            while (rad > 0) {
                canvas.drawCircle(c.x, c.y, rad, c.mPaint);
                rad -= 15;
            }
        } else {
            c.faddingIn = false;
        }
    }

    private void fadeOutAndExpandCircle(Circle c, Canvas canvas) {
        if (c.mPaint.getAlpha() > 0) {
            int newAlpha = c.mPaint.getAlpha() - ALPHA_STEP < 0 ? 0 : c.mPaint.getAlpha() - ALPHA_STEP;
            c.mPaint.setAlpha(newAlpha);
            c.radius += c.radiusIncreaseStep;
            float rad = c.radius;
            while (rad > 0) {
                canvas.drawCircle(c.x, c.y, rad, c.mPaint);
                rad -= 15;
            }
        } else {
            circles.remove(c);
            invalidate();
        }
    }

    private class Circle {
        Paint mPaint;
        float x;
        float y;
        float radius;
        float radiusIncreaseStep;
        boolean faddingIn = true;
        boolean faddingOut = false;

        public Circle() {
            Random rand = new Random();
            x = rand.nextInt(getDisplayWidth());
            y = rand.nextInt(getDisplayHeight() / 2);
            radius = rand.nextInt((150 - 60) + 1) + 60;
            radiusIncreaseStep = (radius * 0.5f)/ FADE_STEP;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.STROKE);
            int color = rand.nextInt(10) < 5 ? circleColor1 : circleColor2;
            mPaint.setColor(color);
            mPaint.setStrokeWidth(10);
            mPaint.setAlpha(0);
        }

        public boolean contains(float x1, float y1) {
            return dist(x1, y1) <= radius + 40;
        }

        private double dist(float endX, float endY) {
            return Math.sqrt(Math.pow(endX - x, 2) + Math.pow(endY - y, 2));
        }
    }
}
