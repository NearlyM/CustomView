package com.ningerlei.timeline.callback;

/**
 * Description : 滚动监听事件
 * CreateTime : 2017/7/17 14:51
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/17 14:51
 * @ModifyDescription :
 */

public interface OnScrollListener {
    /**
     * The view is not scrolling. Note navigating the list using the
     * trackball counts as being in the idle state since these transitions
     * are not animated.
     */
    public static int SCROLL_STATE_IDLE = 0;

    /**
     * The user is scrolling using touch, and their finger is still on the
     * screen
     */
    public static int SCROLL_STATE_TOUCH_SCROLL = 1;

    /**
     * The user had previously been scrolling using touch and had performed
     * a fling. The animation is now coasting to a stop
     */
    public static int SCROLL_STATE_FLING = 2;

    /**
     * 滑到顶部
     */
    void onTopArrived();

    /**
     * 滑动到底部回调
     */
    void onBottomArrived();

    /**
     * 滑动状态回调
     *
     * @param view 当前的scrollView
     * @param scrollState 当前的状态
     */
    void onScrollStateChanged(IScrollView view, int scrollState);

    /**
     * 滑动位置回调
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    void onScrollChanged(int l, int t, int oldl, int oldt);
}
