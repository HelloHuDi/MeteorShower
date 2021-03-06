package com.hd.meteor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by hd on 2018/1/8 .
 *
 */
public class MeteorConfig {

    private MeteorBean bean;

    private Drawable nightSkyBackgroundDrawable;

    private Double meteorSlideAngle =45d;

    private MeteorCreateCallback createCallback;

    public MeteorConfig(Context context) {
        nightSkyBackgroundDrawable = ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.night_sky_background);
    }

    public MeteorConfig setMeteorBean(MeteorBean bean) {
        this.bean = bean;
        return this;
    }

    public MeteorBean getBean() {
        return bean;
    }

    public Drawable getNightSkyBackgroundDrawable() {
        return nightSkyBackgroundDrawable;
    }

    public void setNightSkyBackgroundDrawable(Drawable nightSkyBackgroundDrawable) {
        this.nightSkyBackgroundDrawable = nightSkyBackgroundDrawable;
    }

    public Double getMeteorSlideAngle() {
        return meteorSlideAngle;
    }

    public MeteorCreateCallback getCreateCallback() {
        return createCallback;
    }

    public void setCreateCallback(MeteorCreateCallback createCallback) {
        this.createCallback = createCallback;
    }
}
