package com.ningerlei.custom.entity;

import com.ningerlei.custom.constant.ColorState;

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
    ColorState colorState = ColorState.MOTION;

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

    public ColorState getColorState() {
        return colorState;
    }

    public void setColorState(ColorState colorState) {
        this.colorState = colorState;
    }
}
