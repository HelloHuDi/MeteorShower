package com.hd.meteor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hd on 2018/1/8 .
 * meteor handler
 */
public class MeteorHandler implements MeteorDrawCallback {

    private Context context;

    private List<Meteor> meteorList;

    private Rect meteorRect;

    private int maxWidth, maxHeight;

    private final Object object = new Object();

    public MeteorHandler(Context context) {
        meteorList = new ArrayList<>();
        this.context=context;
    }

    public void setMeteorRect(Rect rect) {
        meteorRect = rect;
    }

    public void setMaxDrawRange(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public void touchComplete(float x, float y) {
        synchronized (object) {
            meteorList.add(new Meteor(meteorRect, x, y, maxWidth, maxHeight, this));
        }
    }

    public void draw(Canvas canvas) {
        synchronized (object) {
            for (Meteor meteor : meteorList) {
                meteor.draw(canvas);
            }
            object.notify();
        }
    }

    @Override
    public void drawComplete(Meteor meteor) {
        synchronized (object) {
            meteorList.remove(meteor);
            object.notify();
        }
    }
}
