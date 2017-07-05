package com.ningerlei.custom.util;

import android.content.Context;

public class DensityUtil {
    public static int dp2px(Context context,float dpvalue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpvalue * scale + 0.5f);

    }

    public static int px2dp(Context context,float pxvalue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxvalue / scale + 0.5f);

    }
}
