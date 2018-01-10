package com.hd.meteor;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by hd on 2018/1/9 .
 *
 */
public class MeteorAnimator {

    private ValueAnimator valueAnimator;

    public void cancelAnimator() {
        Log.d("tag", "cancel animator ");
        if (valueAnimator != null)
            valueAnimator.cancel();
        valueAnimator = null;
    }

    public void createAnimator(long duration, int repeatCount, @NonNull ValueAnimator.AnimatorUpdateListener updateListener,//
                               @Nullable AnimatorListenerAdapter listenerAdapter) {
        createAnimator(0,1,duration,repeatCount,updateListener,listenerAdapter);
    }

    public void createAnimator(float start,float end,long duration, int repeatCount, @NonNull ValueAnimator.AnimatorUpdateListener updateListener,//
                               @Nullable AnimatorListenerAdapter listenerAdapter) {
        valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.addUpdateListener(updateListener);
        if (listenerAdapter != null)
            valueAnimator.addListener(listenerAdapter);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (Math.abs(repeatCount) != 1)
            valueAnimator.setRepeatCount(repeatCount);
        valueAnimator.start();
    }


}
