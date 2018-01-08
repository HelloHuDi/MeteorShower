package com.hd.meteor.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ViewSwitcher;


/**
 * Created by hd on 2018/1/8 .
 */
public class TestActivity extends AppCompatActivity {
    private ViewSwitcher mSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSwitcher = (ViewSwitcher) findViewById(R.id.switcher);

        mSwitcher.setOnClickListener(onRotate);
    }

    private View.OnClickListener onRotate = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TransitionUtil.flipTransition(mSwitcher, TransitionUtil.FlipDirection.LEFT_RIGHT, 0.0f);

            // Set the views here when outanimation is started.
            mSwitcher.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    findViewById(R.id.screen).setVisibility(View.VISIBLE);
                    findViewById(R.id.img_a).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }
            });
            // Set the views here when inanimation is ended.
            mSwitcher.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.screen).setVisibility(View.GONE);
                    findViewById(R.id.img_a).setVisibility(View.VISIBLE);
                }
            });
        }
    };

}
