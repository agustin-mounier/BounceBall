package com.am.bounceball;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amounier on 10/7/16.
 */

public class AnimationStarter extends Activity {

    private static final float vel = (float) 1500 / 2400; // milis / pixel
    private static final List<PointF> previous = new ArrayList<>();
    private static final List<ImageView> balls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_layout);

        balls.add((ImageView) findViewById(R.id.ball2));
        balls.add((ImageView) findViewById(R.id.ball3));
        balls.add((ImageView) findViewById(R.id.ball4));
        balls.add((ImageView) findViewById(R.id.ball5));

        spawnBall();

        startTimer();
    }

    private void spawnBall() {
        TextView timeView = (TextView) findViewById(R.id.time_view);
        int currTime = Integer.valueOf(timeView.getText().toString().split(" ")[1]);
        if (currTime <= 0)
            return;
        Random rand = new Random();
        int min = getDisplayWidth() / 5;
        int max = getDisplayWidth() - min;
        Float startX = (float) rand.nextInt((max - min) + 1) + min;
        ImageView ball = (ImageView) findViewById(R.id.ball);
        ball.setX(startX);
        ball.setY(0);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ballDropAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ball.startAnimation(fadeIn);
    }


    private void ballDropAnimation() {
        ImageView ball = (ImageView) findViewById(R.id.ball);
        ObjectAnimator tw_One = ObjectAnimator.ofFloat(ball,
                "translationY", 0, getDisplayHeight());
        tw_One.setDuration((long) (vel * getDisplayHeight()));
        tw_One.setTarget(ball);
        tw_One.setInterpolator(new AccelerateInterpolator());
        tw_One.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                spawnBall();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                ImageView ball = (ImageView) findViewById(R.id.ball);
                LineView line = (LineView) findViewById(R.id.lineView);

                handleBallTrail(ball);

                if (line.hasCollisioned(ball)) {
                    line.calculateCollision(ball, animation, vel);
                    previous.clear();
                }

                CircleView circles = (CircleView) findViewById(R.id.CircleView);
                int score = circles.collision(ball);
                if (score != 0) {
                    updateScore(score);
                    circles.addCircle();
                }
            }
        };
        tw_One.addUpdateListener(updateListener);
        tw_One.start();
    }

    private void updateScore(int score) {
        TextView scoreView = (TextView) findViewById(R.id.score_view);
        int currentScore = Integer.valueOf(scoreView.getText().toString().split(" ")[1]);
        currentScore += score;
        scoreView.setText("Score: " + currentScore);
    }

    private void startTimer() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView timeView = (TextView) findViewById(R.id.time_view);
                                int currTime = Integer.valueOf(timeView.getText().toString().split(" ")[1]);
                                currTime -= 1;
                                timeView.setText("Time-Left: " + currTime);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    private int getDisplayWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }

    private static void handleBallTrail(ImageView ball){
        PointF p = new PointF(ball.getX(), ball.getY());
        if (previous.size() == 4) {
            previous.remove(0);
            if (!previous.contains(p))
                previous.add(p);
        } else {
            if (!previous.contains(p))
                previous.add(p);
        }
        if (previous.size() != 1) {
            for (int i = 0; i < previous.size(); i++) {
                balls.get(previous.size() - i - 1).setX(previous.get(i).x);
                balls.get(previous.size() - i - 1).setY(previous.get(i).y);
            }
        }
    }

}