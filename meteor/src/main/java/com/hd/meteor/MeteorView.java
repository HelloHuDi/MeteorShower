package com.hd.meteor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by hd on 2018/1/8 .
 * meteor
 */
public class MeteorView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private String TAG = MeteorView.class.getSimpleName();

    private boolean isDraw;

    private SurfaceHolder holder;

    private Canvas canvas;

    private Drawable backgroundDrawable;

    private Rect backgroundRect;

    private MeteorHandler meteorHandler;

    public MeteorView(Context context) {
        super(context);
        init();
    }

    public MeteorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeteorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void addConfig(){

    }

    private void init() {
        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        holder = getHolder();
        holder.addCallback(this);
        meteorHandler = new MeteorHandler(getContext());
        backgroundDrawable = ContextCompat.getDrawable(getContext(), R.drawable.meteor_background);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG,"onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG,"onDetachedFromWindow");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        backgroundRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        meteorHandler.setMeteorRect(new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight() / 3));
        meteorHandler.setMaxDrawRange(getMeasuredWidth(),getMeasuredHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDraw = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                meteorHandler.touchComplete(event.getX(), event.getY());
                break;
        }
        return true;
    }

    @Override
    public void run() {
        while (isDraw) {
            draw();
        }
    }

    private void draw() {
        try {
            canvas = holder.lockCanvas();
            if (canvas == null)
                return;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            backgroundDrawable.setBounds(backgroundRect);
            backgroundDrawable.draw(canvas);
            meteorHandler.draw(canvas);
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }
}
