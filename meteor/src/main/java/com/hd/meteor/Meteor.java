package com.hd.meteor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by hd on 2018/1/8 .
 *
 */
public class Meteor {

    private Paint initSmoothPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        return paint;
    }

    public Meteor(Rect meteorRect, float x, float y, int measuredWidth, int measuredHeight,MeteorDrawCallback callback) {

    }

    public void draw(Canvas canvas) {

    }
}
