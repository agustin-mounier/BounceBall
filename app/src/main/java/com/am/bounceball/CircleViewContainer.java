package com.am.bounceball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amounier on 10/8/16.
 */

public class CircleViewContainer extends RelativeLayout {

    List<Circle> circles = new ArrayList<>();

    public CircleViewContainer(Context context) {
        this(context, null);
    }

    public CircleViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        addCircle();
        addCircle();
        addCircle();
        addCircle();
        addCircle();
    }


    public int collision(ImageView ball) {
        int score = 0;
        for (int i = 0; i < this.getChildCount(); i++) {
            View circleView = this.getChildAt(i);
            if(circleView.getAnimation() != null && !circleView.getAnimation().hasEnded())
                return 0;
            if (contains(ball, circleView)) {
                score = (int) (1000 / (circleView.getWidth()/2));
                fadeOutAndExpand(circleView);
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
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View circleView = vi.inflate(R.layout.circle_layout, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newCricle.width, newCricle.width);
        circleView.setLayoutParams(params);
        circleView.setX(newCricle.x - newCricle.radius);
        circleView.setY(newCricle.y - newCricle.radius);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(1000);
        circleView.startAnimation(fadeIn);

        this.addView(circleView);
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    private int getDisplayWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }

    private double dist(float startX, float startY, float endX, float endY) {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    public boolean contains(ImageView ball, View circle) {
        float error = ball.getWidth()/2;
        float centerBallX = ball.getX() + ball.getWidth()/2;
        float centerBallY = ball.getY() + ball.getWidth()/2;
        float centerCircleX = circle.getX() + circle.getWidth()/2;
        float centerCircleY = circle.getY() + circle.getWidth()/2;
        return dist(centerBallX, centerBallY, centerCircleX, centerCircleY) <=  ball.getWidth()/2 + circle.getWidth()/2 + error;
    }

    private void fadeOutAndExpand(final View circleView) {
        float pivotX = circleView.getX() + circleView.getWidth()/2;
        float pivotY = circleView.getY() + circleView.getWidth()/2;
        ScaleAnimation expandAnimation = new ScaleAnimation(1, 1.5f, 1, 1.5f, Animation.ABSOLUTE, pivotX, Animation.ABSOLUTE, pivotY);
        expandAnimation.setFillAfter(true);
        expandAnimation.setFillEnabled(true);
        expandAnimation.setDuration(800);
        Animation fadeOutAnimation = new AlphaAnimation(1, 0);
        fadeOutAnimation.setInterpolator(new LinearInterpolator());
        fadeOutAnimation.setDuration(800);

        AnimationSet fadeOutAndExpandAnimation = new AnimationSet(false);
        fadeOutAndExpandAnimation.addAnimation(expandAnimation);
        fadeOutAndExpandAnimation.addAnimation(fadeOutAnimation);
        fadeOutAndExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                Handler h = new Handler();
                h.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    removeView(circleView);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        circleView.startAnimation(fadeOutAndExpandAnimation);
    }

    private class Circle {
        float x;
        float y;
        float radius;
        int width;

        public Circle() {
            Random rand = new Random();
            x = rand.nextInt(getDisplayWidth());
            y = rand.nextInt(getDisplayHeight() / 2);
            radius = rand.nextInt((150 - 60) + 1) + 60;
            width = (int)(radius * 2);
        }

        private double dist(float endX, float endY) {
            return Math.sqrt(Math.pow(endX - x, 2) + Math.pow(endY - y, 2));
        }
    }

}
