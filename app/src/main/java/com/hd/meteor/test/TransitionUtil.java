package com.hd.meteor.test;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.widget.ViewAnimator;

/**
 * Created by hd on 2018/1/8 .
 */

public class TransitionUtil {

    /**
     * The {@code FlipDirection} enumeration defines the most typical flip view
     * transitions: left-to-right and right-to-left. {@code FlipDirection} is
     * used during the creation of {@link FlipAnimation} animations.
     *
     * @author Ephraim A. Tekle
     *
     */

    private static final long DURATION_DEFAULT = 1000;

    public enum FlipDirection {
        LEFT_RIGHT, RIGHT_LEFT;

        public float getStartDegreeForFirstView() {
            return 0;
        }

        public float getEndDegreeForFirstView() {
            switch (this) {
                case LEFT_RIGHT:
                    return 90;
                case RIGHT_LEFT:
                    return -90;
                default:
                    return 0;
            }
        }

        public float getStartDegreeForSecondView() {
            switch (this) {
                case LEFT_RIGHT:
                    return -90;
                case RIGHT_LEFT:
                    return 90;
                default:
                    return 0;
            }
        }

        public float getEndDegreeForSecondView() {
            return 0;
        }

        public FlipDirection theOtherDirection() {
            switch (this) {
                case LEFT_RIGHT:
                    return RIGHT_LEFT;
                case RIGHT_LEFT:
                    return LEFT_RIGHT;
                default:
                    return null;
            }
        }
    };

    /**
     * Find two views(from, to) and set animation to flip.
     *
     * @param viewAnimator
     *            group of the views
     * @param dir
     *            direction of the animation
     * @param depthZ
     *            depth of the view when the animation starts
     */
    public static void flipTransition(final ViewAnimator viewAnimator, FlipDirection dir, float depthZ) {

        final View fromView = viewAnimator.getCurrentView();
        final int currentIndex = viewAnimator.getDisplayedChild();
        final int nextIndex = (currentIndex + 1) % viewAnimator.getChildCount();
        final View toView = viewAnimator.getChildAt(nextIndex);

        Animation[] animc = flipAnimation(fromView, toView, (nextIndex < currentIndex ? dir.theOtherDirection() : dir), DURATION_DEFAULT, null, depthZ);

        viewAnimator.setOutAnimation(animc[0]);
        viewAnimator.setInAnimation(animc[1]);

        viewAnimator.showNext();
    }

    /**
     * Create a pair of {@link FlipAnimation} that can be used to flip 3D
     * transition from {@code fromView} to {@code toView}. A typical use case is
     * with {@link ViewAnimator} as an out and in transition.
     *
     * NOTE: Avoid using this method. Instead, use {@link #flipTransition}.
     *
     * @param fromView
     *            the view transition away from
     * @param toView
     *            the view transition to
     * @param dir
     *            the flip direction
     * @param duration
     *            the transition duration in milliseconds
     * @param interpolator
     *            the interpolator to use (pass {@code null} to use the
     *            {@link AccelerateInterpolator} interpolator)
     * @param depthZ
     *            depth of the view when the animation starts
     * @return array of animation
     */
    public static Animation[] flipAnimation(final View fromView, final View toView, FlipDirection dir, long duration, Interpolator interpolator, float depthZ) {
        Animation[] result = new Animation[2];
        float centerX;
        float centerY;

        centerX = fromView.getWidth() / 2.0f;
        centerY = fromView.getHeight() / 2.0f;

        Animation outFlip = new Rotate3dAnimation(dir.getStartDegreeForFirstView(), dir.getEndDegreeForFirstView(), centerX, centerY, depthZ, false);
        outFlip.setDuration(duration);
        outFlip.setFillAfter(true);
        outFlip.setInterpolator(interpolator == null ? new AccelerateInterpolator() : interpolator);

        AnimationSet outAnimation = new AnimationSet(true);
        outAnimation.addAnimation(outFlip);
        result[0] = outAnimation;

        Animation inFlip = new Rotate3dAnimation(dir.getStartDegreeForSecondView(), dir.getEndDegreeForSecondView(), centerX, centerY, depthZ, false);
        inFlip.setDuration(duration);
        inFlip.setFillAfter(true);
        inFlip.setInterpolator(interpolator == null ? new AccelerateInterpolator() : interpolator);
        inFlip.setStartOffset(duration);

        AnimationSet inAnimation = new AnimationSet(true);
        inAnimation.addAnimation(inFlip);
        result[1] = inAnimation;

        return result;
    }
}
