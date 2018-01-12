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

    private void setMeteorSlideAngle(Double meteorSlideAngle) {
        if (meteorSlideAngle > 90 && meteorSlideAngle <= 180) {
            meteorSlideAngle = 180 - meteorSlideAngle;
        } else if (meteorSlideAngle > 180 && meteorSlideAngle <= 270) {
            meteorSlideAngle = meteorSlideAngle - 180;
        } else if (meteorSlideAngle > 270) {
            meteorSlideAngle = 360 - meteorSlideAngle;
        }
        this.meteorSlideAngle = meteorSlideAngle;
    }
}
