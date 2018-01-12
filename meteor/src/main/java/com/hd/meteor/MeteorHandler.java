package com.hd.meteor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Vector;

/**
 * Created by hd on 2018/1/8 .
 * meteor handler
 */
public class MeteorHandler implements MeteorDrawCallback {

    enum MeteorState {

        /**
         * when the touch is completed, the symbol begins to create the symbol
         */
        CREATE_SYMBOL,

        /**
         * moving symbols to the starry sky
         */
        MOVE_SYMBOL,

        /**
         * the symbol is converted into a star
         */
        CREATE_STARS,

        /**
         * the star is converted into a meteor
         */
        CREATE_METEOR,

        /**
         * a meteor slid through
         */
        MOVE_METEOR,

        /**
         * disappearing in the specified range or
         * currently without the need to create a meteor
         */
        METEOR_LOST

    }

    private Context context;

    private MeteorConfig config;

    private final Vector<Meteor> meteorList;

    private Rect meteorRect, canvasRect;

    public MeteorHandler(Context context) {
        meteorList = new Vector<>();
        this.context = context;
    }

    public void setConfig(MeteorConfig config) {
        this.config = config;
    }

    public void setMeteorRect(Rect rect) {
        meteorRect = rect;
    }

    public void setMaxDrawRange(Rect rect) {
        this.canvasRect = rect;
    }

    public void touchComplete(float x, float y) {
        meteorList.addElement(new Meteor(meteorRect, canvasRect, x, y, config, this));
    }

    public void draw(Canvas canvas) {
        synchronized (meteorList) {
            for (Meteor meteor : meteorList) {
                meteor.draw(canvas);
            }
        }
    }

    @Override
    public void drawComplete(Meteor meteor) {
        meteorList.removeElement(meteor);
    }
}
