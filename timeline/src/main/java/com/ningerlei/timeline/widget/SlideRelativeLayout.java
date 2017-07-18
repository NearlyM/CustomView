package com.ningerlei.timeline.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Description :
 * CreateTime : 2017/7/12 16:23
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/12 16:23
 * @ModifyDescription :
 */

class SlideRelativeLayout extends RelativeLayout {

    public SlideRelativeLayout(Context context) {
        this(context, null);
    }

    public SlideRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void slideYBy(float distance){
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.setY(child.getY() + distance);
            }
        }
    }

    public void slideXBy(float distance){
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof ImageView && child.getVisibility() != GONE) {
                child.setX(child.getX() + distance);
            }
        }
    }
}
