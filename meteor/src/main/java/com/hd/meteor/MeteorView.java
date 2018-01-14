package com.hd.meteor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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

    private MeteorConfig config;

    public MeteorView(Context context) {
        super(context);
    }

    public MeteorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeteorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addConfig(MeteorConfig config) {
        this.config = config;
        init();
    }

    private void init() {
        if (holder == null) {
            getHolder().setFormat(PixelFormat.TRANSLUCENT);
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            holder = getHolder();
            holder.addCallback(this);
        }
        meteorHandler = new MeteorHandler(getContext());
        meteorHandler.setConfig(config);
        backgroundDrawable = config.getNightSkyBackgroundDrawable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        backgroundRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        meteorHandler.setMeteorRect(new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight() / 4));
        meteorHandler.setMaxDrawRange(new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight()));
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
                if (config.getCreateCallback()!= null)
                    config.getCreateCallback().create();
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
