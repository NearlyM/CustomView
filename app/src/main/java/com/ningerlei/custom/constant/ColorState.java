package com.ningerlei.custom.constant;

import com.ningerlei.custom.R;

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

public enum ColorState {

    SOUND(R.color.colorBlue), MOTION(R.color.colorGreen), NORMAL(R.color.colorWhite);

    int color;

    ColorState(int color){
        this.color = color;
    }

    public int getColorRes() {
        return color;
    }
}
