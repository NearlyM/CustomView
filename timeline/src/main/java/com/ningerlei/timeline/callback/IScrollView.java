package com.ningerlei.timeline.callback;

import com.ningerlei.timeline.entity.TimeData;

import java.util.List;

/**
 * Description :
 * CreateTime : 2017/7/17 14:53
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/17 14:53
 * @ModifyDescription :
 */

public interface IScrollView {
    void setDataList(List<TimeData> timeDataList);
    void setOnScrollListener(OnScrollListener onScrollListener);
    void setOnTimeChage(OnTimeChange onTimeChange);
    void setOnControlListener(OnControlListener onControlListener);
    void startScroller();
    void setTime(long timestamp);
}
