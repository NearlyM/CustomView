package com.ningerlei.custom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ningerlei.custom.R;
import com.ningerlei.custom.util.ContextUtil;
import com.ningerlei.custom.util.DateTimeUtil;
import com.ningerlei.custom.util.DensityUtil;

import java.util.Calendar;

/**
 * Description :
 * CreateTime : 2017/7/6 12:03
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/6 12:03
 * @ModifyDescription :
 */

public class TimeScrollView extends ScrollView implements TimelineAbs.OnAddImageCallback{

    private static final String TAG = TimeScrollView.class.getSimpleName();
    // 检查ScrollView的最终状态
    private static final int CHECK_STATE = 0;
    // 外部设置的监听方法
    private OnScrollListener onScrollListener;
    // 是否在触摸状态
    private boolean inTouch = false;
    // 上次滑动的最后位置
    private int lastT = 0;

    LinearLayout linearLayout;

    RelativeLayout relativeLayout;

    TimelineAbs timelineAbs;

    public TimeScrollView(Context context) {
        super(context, null);
    }

    public TimeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {

        relativeLayout = new RelativeLayout(context);
        ViewGroup.LayoutParams param0 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(param0);
        relativeLayout.setBackgroundColor(Color.WHITE);


        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(param);
        linearLayout.setGravity(Gravity.RIGHT);

        AttributeSet attributes = ContextUtil.getAttributeSet(getResources(), R.layout.timeline);

        ViewGroup.LayoutParams param1 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        timelineAbs = new TimelineAbs(context, attributes);
        param1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        param1.height = DensityUtil.dp2px(context, getResources().getConfiguration().screenHeightDp);
        timelineAbs.setLayoutParams(param1);

        linearLayout.addView(relativeLayout);
        linearLayout.addView(timelineAbs);
        addView(linearLayout);


        timelineAbs.setOnAddImageCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int location[] = new int[2];
        getLocationOnScreen(location);
        timelineAbs.setVisualLength(location[1] - getStatusBarHeight());
        Log.d(TAG, "top = " + location[1]);
//        setTime(System.currentTimeMillis());

    }

    public int getStatusBarHeight(){
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void setTime(long timestamp){
//        timelineAbs.setTime(timestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long timeInMillis = calendar.getTimeInMillis();

        int offset = (int) ((timestamp - timeInMillis) / 1000);

        offsetDp = (int) ((24 * 60 * 60 * 1000 - ((timestamp - timeInMillis))) * timelineAbs.getMarkTotalLength() / (24 * 60 * 60 * 1000));
        Log.d(TAG, "offsetDp = " + offsetDp + "; height = " + timelineAbs.getHeight());
        if (offsetDp > 0 && !hasScroll){
            hasScroll = true;
            scrollBy(0, offsetDp);
        }
    }

    long offset = 24 * 60 * 60 * 7;
    int offsetDp;
    boolean hasScroll;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isAutoScroller = false;
                setScrollerFlag(false);
                inTouch = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setScrollerFlag(true);
                inTouch = false;
                lastT = getScrollY();
                checkStateHandler.removeMessages(CHECK_STATE);// 确保只在最后一次做这个check
                checkStateHandler.sendEmptyMessageDelayed(CHECK_STATE, 5);// 5毫秒检查一下

                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener == null) {
            return;
        }

        if (!isAutoScroller && t < offsetDp) {
            scrollBy(0, offsetDp - t);
            return;
        }

        int timelineLength = timelineAbs.getMarkTotalLength();

        long secondTotal = offset  * t / timelineLength;

        long current = offset - secondTotal;

        if (onTimeChage != null){
            onTimeChage.timeChange(DateTimeUtil.getTime(current));
        }

        if (inTouch) {
            if (t != oldt) {
                // 有手指触摸，并且位置有滚动
                Log.i(TAG, "SCROLL_STATE_TOUCH_SCROLL");
                scrollerState = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
                onScrollListener.onScrollStateChanged(this,
                        OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                if (onRefreshChange != null){
                    onRefreshChange.onRefreshStop();
                }
            }
        } else {
            if (t != oldt) {
                // 没有手指触摸，并且位置有滚动，就可以简单的认为是在fling
                Log.w(TAG, "SCROLL_STATE_FLING");
                scrollerState = OnScrollListener.SCROLL_STATE_FLING;
                onScrollListener.onScrollStateChanged(this,
                        OnScrollListener.SCROLL_STATE_FLING);
                // 记住上次滑动的最后位置
                lastT = t;
                checkStateHandler.removeMessages(CHECK_STATE);// 确保只在最后一次做这个check
                checkStateHandler.sendEmptyMessageDelayed(CHECK_STATE, 5);// 5毫秒检查一下
            }
        }
        onScrollListener.onScrollChanged(l, t, oldl, oldt);
    }

    private Handler checkStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (lastT == getScrollY()) {
                // 如果上次的位置和当前的位置相同，可认为是在空闲状态
                Log.e(TAG, "SCROLL_STATE_IDLE");
                scrollerState = OnScrollListener.SCROLL_STATE_IDLE;
                onScrollListener.onScrollStateChanged(TimeScrollView.this,
                        OnScrollListener.SCROLL_STATE_IDLE);
                if (lastT == 0) {
                    onScrollListener.onTopArrived();
                } else if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
                    onScrollListener.onBottomArrived();
                } else {
                    Log.d(TAG, "没有到最下方");
                }
            }
        }
    };

    int scrollerState;

    @Override
    public void onAddImage(float left, float top) {
        left = 700;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(layoutParams);
        imageView.setX(left);
        imageView.setY(top);
        Log.d(TAG, "x = " + left + "; y = " + top);
        imageView.setImageResource(R.drawable.ss);
//        linearLayout.addView(imageView);

        ImageView logo = new ImageView(getContext());


        relativeLayout.addView(imageView);
    }

    /**
     * 滚动监听事件
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
        void onScrollStateChanged(ScrollView view, int scrollState);

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

    OnTimeChange onTimeChage;

    public void setOnTimeChage(OnTimeChange onTimeChange) {
        this.onTimeChage = onTimeChange;
    }

    public interface OnTimeChange {
        void timeChange(String time);
    }

    OnRefreshStateListener onRefreshChange;

    public void setOnRefreshChange(OnRefreshStateListener onRefreshChange) {
        this.onRefreshChange = onRefreshChange;
    }

    public interface OnRefreshStateListener {
        void onRefresh();
        void onRefreshStop();
    }

    boolean isAutoScroller;

    public void startScroller(){
        setScrollerFlag(true);
        timeHandle.sendEmptyMessageDelayed(1, 1000);
    }

    private void setScrollerFlag(boolean isStart) {
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = isStart ? 1 : 0;
        timeHandle.sendMessage(msg);
    }

    Handler timeHandle = new Handler(){

        boolean isStart;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (isStart){
                        isAutoScroller = true;
                        offsetDp = (int) (offsetDp - timelineAbs.getStep() / 33);
                        if (offsetDp > 0){
                            smoothScrollBy(0, - (int) (timelineAbs.getStep() / 33));
                            timeHandle.sendEmptyMessageDelayed(1, 10667);
                        }
                    }
                    break;
                case 2:
                    isStart = msg.arg1 == 1;
                    break;
            }
        }
    };


}
