package com.ningerlei.timeline.callback;

/**
 * Description :
 * CreateTime : 2017/7/18 17:56
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/18 17:56
 * @ModifyDescription :
 */

public interface OnAddImageCallback{
    enum ImageLoadType{
        REFRESH, LOADED, LOADING
    }

    void onAddImage(float pointer, float top, ImageLoadType imageLoadType);
    void onMoveImage(float delta);
}
