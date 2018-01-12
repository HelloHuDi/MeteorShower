package com.hd.meteor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by hd on 2018/1/8 .
 * meteor bean
 */
public class MeteorBean implements Serializable {

    private LinkedList<Drawable> iconList;

    private LinkedList<Integer> colorList;

    public MeteorBean(Context context) {
        iconList = new LinkedList<>();
        colorList = new LinkedList<>();
        int a = 28;
        while (a > 0) {
            try {
                int resId = context.getApplicationContext().getResources().getIdentifier("meteor_music_" + a, "drawable", context.getPackageName());
                iconList.add(ContextCompat.getDrawable(context.getApplicationContext(), resId));
                a--;
            } catch (Exception e) {
                Log.d("MeteorBean", e.toString());
            }
        }
        generateMeteorColor();
    }

    private int index = 0;

    private void generateMeteorColor() {
        if (index > iconList.size() - 1) return;
        Drawable drawable = iconList.get(index);
        final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                colorList.add(palette.getVibrantColor(Color.WHITE));
                index += 1;
                generateMeteorColor();
            }
        });
    }

    public List<Drawable> getIconList() {
        return iconList;
    }

    public void setIconList(LinkedList<Drawable> iconList) {
        this.iconList = iconList;
    }

    public void setIcon(Drawable icon) {
        iconList.clear();
        colorList.clear();
        iconList.add(icon);
        generateMeteorColor();
    }

    public List<Integer> getColorList() {
        return colorList;
    }
}
