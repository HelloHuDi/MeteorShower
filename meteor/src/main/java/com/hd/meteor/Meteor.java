package com.hd.meteor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.Random;

/**
 * Created by hd on 2018/1/8 .
 * draw meteor
 */
public class Meteor {

    private Paint mainPaint;

    private Rect meteorRect, iconRect;

    private float x, y;

    private final float centerCircleRadius = 40f, circleDistance = 40f;

    private int width, height;

    private MeteorConfig config;

    private MeteorDrawCallback callback;

    private Random random;

    private Drawable iconDrawable;

    private boolean createIconComplete,moveIconComplete,createMeteorComplete;

    private MeteorAnimator meteorAnimator;

    private float animatorValue;

    private Paint initSmoothPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        return paint;
    }

    public Meteor(Rect meteorRect, float x, float y, int width, int height, MeteorConfig config, MeteorDrawCallback callback) {
        this.meteorRect = meteorRect;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.config = config;
        this.callback = callback;
        List<Drawable> iconList = config.getBean().getIconList();
        random = new Random();
        iconDrawable = iconList.get(random.nextInt(iconList.size()));
        iconRect = new Rect((int) (x - centerCircleRadius), (int) (y - centerCircleRadius), //
                            (int) (x + centerCircleRadius), (int) (y + centerCircleRadius));
        meteorAnimator = new MeteorAnimator();
        createIconAnimator();
        mainPaint = initSmoothPaint();
        mainPaint.setColor(Color.LTGRAY);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(2);
    }

    private void createIconAnimator() {
        createIconComplete = false;
        meteorAnimator.createAnimator(500, 1, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float) animation.getAnimatedValue();
            }
        }, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                createIconComplete = true;
                moveIconAnimator();
            }
        });
    }

    private void moveIconAnimator() {
        moveIconComplete=false;
        final float end=random.nextInt(meteorRect.bottom-meteorRect.top);
        meteorAnimator.createAnimator(2000, 1, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float) animation.getAnimatedValue();
                float changeValue=(1-animatorValue/2.0f)/animatorValue/6.0f;
                float pointY=y+animatorValue*(end-y);
                iconRect = new Rect((int) (x - centerCircleRadius*changeValue), (int) (pointY - centerCircleRadius*changeValue), //
                                    (int) (x + centerCircleRadius*changeValue), (int) (pointY + centerCircleRadius*changeValue));
            }
        }, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                moveIconComplete = true;
                createMeteorAnimator();
            }
        });
    }

    private void createMeteorAnimator(){

    }

    public void draw(Canvas canvas) {
        if (!createIconComplete) {
            drawIcon(canvas, (int) (255 * animatorValue), iconRect);
            drawWaveCircle(canvas);
        } else if(!moveIconComplete){
            drawIcon(canvas, 255, iconRect);
        }else{
            drawIcon(canvas, 255, iconRect);
        }
    }

    private void drawWaveCircle(Canvas canvas) {
        mainPaint.setAlpha((int) (255 * (1 - animatorValue)));
        float increase = centerCircleRadius / 2.0f * animatorValue;
        canvas.drawCircle(x, y, centerCircleRadius + increase / 3.0f, mainPaint);
        canvas.drawCircle(x, y, centerCircleRadius + circleDistance + increase / 3.0f * 2, mainPaint);
        canvas.drawCircle(x, y, centerCircleRadius + 2 * circleDistance + increase, mainPaint);
    }

    private void drawIcon(Canvas canvas, int alpha, Rect iconRect) {
        iconDrawable.setAlpha(alpha);
        iconDrawable.setBounds(iconRect);
        iconDrawable.draw(canvas);
    }
}
