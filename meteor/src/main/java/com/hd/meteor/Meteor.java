package com.hd.meteor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.List;
import java.util.Random;


/**
 * Created by hd on 2018/1/8 .
 * draw meteor
 */
public class Meteor implements ValueAnimator.AnimatorUpdateListener {

    private Paint mainPaint;

    private Rect meteorRect, canvasRect, iconRect;

    private float touchPointX, touchPointY;

    private final float centerCircleRadius = 40f, circleDistance = 40f;

    private MeteorConfig config;

    private MeteorDrawCallback callback;

    private Random random;

    private Drawable iconDrawable;

    private int meteorColor;

    private MeteorAnimator meteorAnimator;

    private float animatorValue;

    private MeteorHandler.MeteorState meteorState;

    private float sinWeight;

    private Paint initSmoothPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        return paint;
    }

    public Meteor(Rect meteorRect, Rect canvasRect, float touchPointX, float touchPointY, MeteorConfig config, MeteorDrawCallback callback) {
        this.meteorRect = meteorRect;
        this.canvasRect = canvasRect;
        this.touchPointX = touchPointX;
        this.touchPointY = touchPointY;
        this.config = config;
        this.callback = callback;
        List<Drawable> drawableList = config.getBean().getIconList();
        random = new Random();
        int position = random.nextInt(drawableList.size());
        iconDrawable = drawableList.get(position);
        if (config.getBean().getColorList().size() > 0) {
            meteorColor = config.getBean().getColorList().get(position);
        } else {
            meteorColor = Color.WHITE;
        }
        iconRect = new Rect((int) (touchPointX - centerCircleRadius), (int) (touchPointY - centerCircleRadius), //
                            (int) (touchPointX + centerCircleRadius), (int) (touchPointY + centerCircleRadius));
        sinWeight = (float) Math.sin(Math.PI * config.getMeteorSlideAngle() / 180);
        meteorAnimator = new MeteorAnimator();
        createIconAnimator();
        mainPaint = initSmoothPaint();
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(2);
    }

    /**
     * create symbol
     */
    private void createIconAnimator() {
        meteorState = MeteorHandler.MeteorState.CREATE_SYMBOL;
        meteorAnimator.createAnimator(500, 1, this, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                moveIconAnimator();
            }
        });
    }

    private float highLinesY;

    /**
     * move symbol
     */
    private void moveIconAnimator() {
        meteorState = MeteorHandler.MeteorState.MOVE_SYMBOL;
        highLinesY = random.nextInt(meteorRect.bottom - meteorRect.top);
        meteorAnimator.createAnimator(2000, 1, this, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                createStars();
            }
        });
    }

    /**
     * create stars
     */
    private void createStars() {
        meteorState = MeteorHandler.MeteorState.CREATE_STARS;
        highLinesY = random.nextInt(meteorRect.bottom - meteorRect.top);
        meteorAnimator.createAnimator(random.nextInt(1500) + 500, 1, this, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                createMeteorAnimator();
            }
        });
    }

    private Path meteorPath;

    private float meteorStartX, meteorStartY;

    private float create_meteor_time;

    /**
     * create the shape of a meteor
     */
    private void createMeteorAnimator() {
        meteorState = MeteorHandler.MeteorState.CREATE_METEOR;
        meteorPath = new Path();
        meteorSlideRect = new Rect();
        meteorStartX = iconRect.centerX();
        meteorStartY = iconRect.centerY();
        meteorPath.moveTo(meteorStartX, meteorStartY);
        float moveWidth = canvasRect.right - meteorStartX;
        float moveDistance = (float) (Math.sqrt((moveWidth * moveWidth) / (1 - sinWeight * sinWeight)));
        create_meteor_time = moveDistance / 3.0f;
        meteorAnimator.createAnimator(0, moveDistance, 2000, 1, this, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawCompleted();
            }
        });
    }

    private void drawCompleted() {
        meteorState = MeteorHandler.MeteorState.METEOR_LOST;
        callback.drawComplete(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        switch (meteorState) {
            case CREATE_SYMBOL:
                animatorValue = (float) animation.getAnimatedValue();
                break;
            case MOVE_SYMBOL:
                animatorValue = (float) animation.getAnimatedValue();
                float changeValue = (1 - animatorValue * 0.2f) / animatorValue / 6f;
                float pointY = touchPointY + animatorValue * (highLinesY - touchPointY);
                iconRect.set((int) (touchPointX - centerCircleRadius * changeValue), (int) (pointY - centerCircleRadius * changeValue), //
                             (int) (touchPointX + centerCircleRadius * changeValue), (int) (pointY + centerCircleRadius * changeValue));
                break;
            case CREATE_METEOR:
            case MOVE_METEOR:
                if(!canvasRect.contains((int) meteorStartX,(int) meteorStartY)){
                    meteorAnimator.cancelAnimator();
                    drawCompleted();
                }else {
                    animatorValue = (float) animation.getAnimatedValue();
                    if (animatorValue > create_meteor_time && meteorState == MeteorHandler.MeteorState.CREATE_METEOR) {
                        meteorState = MeteorHandler.MeteorState.MOVE_METEOR;
                    }
                    createMeteor();
                }
                break;
            case METEOR_LOST:
                animatorValue = (float) animation.getAnimatedValue();
                break;
        }
    }

    private Rect meteorSlideRect;

    private float startXDistance, startYDistance;

    private void createMeteor() {
        float offsetDistanceX = (float) (circleDistance * 0.001 * animatorValue);
        float offsetDistanceY = (float) ((offsetDistanceX) / Math.sqrt((1 - sinWeight * sinWeight) / (sinWeight * sinWeight)));
        if (meteorState == MeteorHandler.MeteorState.MOVE_METEOR) {
            meteorSlideRect.offset((int) (offsetDistanceX), (int) offsetDistanceY);
            meteorStartX = meteorSlideRect.right - startXDistance;
            meteorStartY = meteorSlideRect.bottom - startYDistance;
        }
        iconRect.offset((int) (offsetDistanceX), (int) offsetDistanceY);
        meteorPath.reset();
        meteorPath.moveTo(meteorStartX, meteorStartY);
        float circleMoveDistanceX = iconRect.centerX() - meteorStartX;
        float circleMoveDistanceY = iconRect.centerY() - meteorStartY;
        float moveDistance = (float) Math.sqrt(circleMoveDistanceX * circleMoveDistanceX + circleMoveDistanceY * circleMoveDistanceY);
        float radius = (iconRect.bottom - iconRect.top) / 2.0f;
        float length = (float) Math.sqrt(moveDistance * moveDistance - radius * radius);
        float angle = (float) (Math.asin(radius / moveDistance * 1.0f) / 2 / Math.PI * 360);
        float Y = (float) (length * Math.sin(Math.PI * (angle + config.getMeteorSlideAngle()) / 180));
        float X = (float) Math.sqrt(length * length - Y * Y);
        meteorPath.quadTo(meteorStartX, meteorStartY, meteorStartX + X, meteorStartY + Y);
        meteorPath.quadTo(meteorStartX + X, meteorStartY + Y, meteorStartX + Y, meteorStartY + X);
        meteorPath.quadTo(meteorStartX + Y, meteorStartY + X, meteorStartX, meteorStartY);
        if (meteorState == MeteorHandler.MeteorState.CREATE_METEOR) {
            meteorSlideRect.set((int) meteorStartX, (int) meteorStartY, iconRect.right, iconRect.bottom);
            startXDistance = meteorSlideRect.right - meteorStartX;
            startYDistance = meteorSlideRect.bottom - meteorStartY;
        }
    }

    public void draw(Canvas canvas) {
        try {
            switch (meteorState) {
                case CREATE_SYMBOL:
                    drawWaveCircle(canvas);
                    break;
                case MOVE_SYMBOL:
                    drawIcon(iconDrawable, canvas, 255, iconRect);
                    break;
                case CREATE_STARS:
                    drawIcon(iconDrawable, canvas, 255, iconRect);
                    mainPaint.setAlpha(255);
                    mainPaint.setColor(meteorColor);
                    canvas.drawCircle(iconRect.centerX(), iconRect.centerY(), (iconRect.bottom - iconRect.top) / 2, mainPaint);
                    break;
                case CREATE_METEOR:
                case MOVE_METEOR:
                    mainPaint.setStyle(Paint.Style.FILL);
                    LinearGradient linearGradient = new LinearGradient(meteorStartX, meteorStartY, iconRect.centerX(), iconRect.centerY(),//
                                                                       new int[]{Color.TRANSPARENT, Color.BLACK, Color.WHITE, meteorColor}, //
                                                                       new float[]{0, 0.2f, 0.4f, 1}, Shader.TileMode.CLAMP);
                    mainPaint.setShader(linearGradient);
                    canvas.drawPath(meteorPath, mainPaint);
                    canvas.drawCircle(iconRect.centerX(), iconRect.centerY(), (iconRect.bottom - iconRect.top) / 2, mainPaint);
                    int alpha = (int) (255 * (1 - animatorValue));
                    if (alpha > 100) {
                        drawIcon(iconDrawable, canvas, alpha, iconRect);
                    }
                    break;
            }
        }catch (Exception e){
            Log.e("tag", "meteor draw error :"+e);
        }
    }

    private void drawWaveCircle(Canvas canvas) {
        mainPaint.setColor(Color.LTGRAY);
        float increase = centerCircleRadius / 2.0f * animatorValue;
        mainPaint.setAlpha((int) (255 * (1 - animatorValue*0.6f)));
        canvas.drawCircle(touchPointX, touchPointY, centerCircleRadius + increase / 3.0f, mainPaint);
        mainPaint.setAlpha((int) (255 * (1 - animatorValue*0.8f)));
        canvas.drawCircle(touchPointX, touchPointY, centerCircleRadius + circleDistance + increase / 3.0f * 2, mainPaint);
        mainPaint.setAlpha((int) (255 * (1 - animatorValue*1f)));
        canvas.drawCircle(touchPointX, touchPointY, centerCircleRadius + 2 * circleDistance + increase, mainPaint);
    }

    private void drawIcon(Drawable drawable, Canvas canvas, int alpha, Rect iconRect) {
        drawable.setAlpha(alpha);
        drawable.setBounds(iconRect);
        drawable.draw(canvas);
    }
}
