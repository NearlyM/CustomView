package com.ningerlei.timeline.entity;

import com.ningerlei.timeline.callback.OnAddImageCallback;
import com.ningerlei.timeline.constant.ColorType;

/**
 * Description :
 * CreateTime : 2017/7/10 16:20
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/10 16:20
 * @ModifyDescription :
 */

public class TimeData {
    long startTime;
    int offset;
    ColorType colorType = ColorType.MOTION;

    OnAddImageCallback.ImageLoadType imageLoadType;

    public void setImageLoadType(OnAddImageCallback.ImageLoadType imageLoadType) {
        this.imageLoadType = imageLoadType;
    }

    public OnAddImageCallback.ImageLoadType getImageLoadType() {
        return imageLoadType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public void setColorType(ColorType colorType) {
        this.colorType = colorType;
    }
}
