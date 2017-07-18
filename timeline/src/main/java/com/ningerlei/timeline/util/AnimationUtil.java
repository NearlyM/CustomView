package com.ningerlei.timeline.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ningerlei.timeline.R;

/**
 * Description :
 * CreateTime : 2016/10/29 15:55
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2016/10/29 15:55
 * @ModifyDescription :
 */

public class AnimationUtil {

    protected static final int SCREEN_TOP = 0;
    public static final int SCREEN_BOTTOM = 1;
    protected static final int SCREEN_RIGHT = 2;


    /**
     * 设备list动画显示与隐藏
     * @param context
     * @param view
     * @param menuState
     */
    public static void switchVideoMenuByAnimation(Context context, View view, boolean menuState, int location){
        switchVideoMenuByAnimation(context, view, menuState, location, null);
    }

    /**
     * 设备list动画显示与隐藏
     * @param context
     * @param view
     * @param menuState
     * @param listener
     */
    public static void switchVideoMenuByAnimation(Context context, final View view, boolean menuState, int location, final Animation.AnimationListener listener) {
        if (view == null || context ==null){
            return;
        }
        if (menuState) {
            Animation inAnim;
            if (location == SCREEN_RIGHT){
                inAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            }else if (location == SCREEN_BOTTOM){
                inAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
            }else {
                inAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_top);
            }
            view.setVisibility(View.VISIBLE);
            inAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (listener != null){
                        listener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (listener != null){
                        listener.onAnimationEnd(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (listener != null){
                        listener.onAnimationRepeat(animation);
                    }
                }
            });
            view.startAnimation(inAnim);

        } else {
            Animation outAnim;
            if (location == SCREEN_RIGHT){
                outAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
            }else if (location == SCREEN_BOTTOM){
                outAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom);
            }else {
                outAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_top);
            }

            view.setVisibility(View.GONE);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (listener != null){
                        listener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (listener != null){
                        listener.onAnimationEnd(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (listener != null){
                        listener.onAnimationRepeat(animation);
                    }
                }
            });
            view.startAnimation(outAnim);
        }
    }
}
