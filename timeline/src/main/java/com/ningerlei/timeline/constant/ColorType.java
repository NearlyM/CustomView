package com.ningerlei.timeline.constant;

import com.ningerlei.timeline.R;

/**
 * Description :
 * CreateTime : 2017/7/6 18:06
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/6 18:06
 * @ModifyDescription :
 */

public enum ColorType {

    SOUND(R.color.colorBlue), MOTION(R.color.colorGreen), NORMAL(R.color.colorWhite);

    int color;

    ColorType(int color){
        this.color = color;
    }

    public int getColorRes() {
        return color;
    }
}
