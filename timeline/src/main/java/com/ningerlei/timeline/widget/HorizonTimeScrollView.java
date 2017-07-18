package com.ningerlei.timeline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ningerlei.timeline.R;
import com.ningerlei.timeline.callback.IScrollView;
import com.ningerlei.timeline.callback.OnAddImageCallback;
import com.ningerlei.timeline.callback.OnControlListener;
import com.ningerlei.timeline.callback.OnScrollListener;
import com.ningerlei.timeline.callback.OnTimeChange;
import com.ningerlei.timeline.constant.OrientationMode;
import com.ningerlei.timeline.entity.TimeData;
import com.ningerlei.timeline.util.ContextUtil;
import com.ningerlei.timeline.util.DateTimeUtil;
import com.ningerlei.timeline.util.DensityUtil;

import java.util.List;

/**
 * Description :
 * CreateTime : 2017/7/17 9:54
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/17 9:54
 * @ModifyDescription :
 */

class HorizonTimeScrollView extends HorizontalScrollView implements IScrollView, OnAddImageCallback {

    private final String TAG = HorizonTimeScrollView.class.getSimpleName();
    private OnScrollListener onScrollListener;
    // 检查ScrollView的最终状态
    private static final int CHECK_STATE = 0;

    TimelineAbs timelineAbs;
    SlideRelativeLayout relativeLayout;
    RelativeLayout blockView;
    private boolean inTouch;
    private int lastL;
    private boolean isOnDraw;
    private long offset;
    private int offsetDp;
    private long currentTime;
    private boolean isAutoTimer;

    public HorizonTimeScrollView(Context context) {
        this(context, null);
    }

    public HorizonTimeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    void initView(Context context) {
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        param.width = DensityUtil.dp2px(context, getResources().getConfiguration().screenWidthDp);
        AttributeSet attributes = ContextUtil.getAttributeSet(getResources(), R.layout.timeline_horizon);
        timelineAbs = new TimelineAbs(context, attributes);
        timelineAbs.setLayoutParams(param);

        timelineAbs.setOnAddImageCallback(this);

        ViewGroup.LayoutParams param1 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        relativeLayout = new SlideRelativeLayout(context);
        relativeLayout.setLayoutParams(param1);


        blockView = new RelativeLayout(context);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DensityUtil.dp2px(context, getResources().getConfiguration().screenWidthDp / 2), DensityUtil.dp2px(context, 60));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, timelineAbs.getId());
        blockView.setBackgroundColor(Color.WHITE);
        blockView.setLayoutParams(layoutParams);

        relativeLayout.addView(blockView);

        relativeLayout.addView(timelineAbs);

        addView(relativeLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initTime();
    }

    public void initTime(){
        long timestamp = System.currentTimeMillis();
        timelineAbs.initTime();

        long timeInMillis = DateTimeUtil.getTodayStart();

        offset = (int) ((timestamp - timeInMillis) / 1000);

        offset = offset + ((int)timelineAbs.getPage() / 4 * 24 * 3600);

        if (currentTime == 0){
            currentTime = offset;
        }

        offsetDp = (int) (((timestamp - timeInMillis)) * timelineAbs.getMarkTotalLength() / (24 * 60 * 60 * 1000)) ;
//        Log.d(TAG, "offsetDp = " + offsetDp + "; height = " + timelineAbs.getMarkTotalLength() + "; timestamp = " + timestamp + "; timeinMillis = " + timeInMillis);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setAutoScrollerFlag(false, false);
                inTouch = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                inTouch = false;
                lastL = getScrollX();
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
        if (onScrollListener == null || isAutoTimer) {
            return;
        }

        int width = timelineAbs.getWidth() - DensityUtil.dp2px(getContext(), getResources().getConfiguration().screenWidthDp) / 2;

        int timelineLength = (int) timelineAbs.getMarkTotalLength();

        if (timelineLength != 0){

            long secondTotal = offset  * (width - l) / timelineLength;

            currentTime = offset - secondTotal;

            if (onTimeChange != null){
                onTimeChange.timeChange(DateTimeUtil.getTime(currentTime));
            }
        }

        if (inTouch) {
            if (l != oldl) {
                // 有手指触摸，并且位置有滚动
                Log.i(TAG, "SCROLL_STATE_TOUCH_SCROLL");
                onScrollListener.onScrollStateChanged(this,
                        OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }
        } else {
            if (l != oldl) {
                // 没有手指触摸，并且位置有滚动，就可以简单的认为是在fling
                Log.w(TAG, "SCROLL_STATE_FLING");
                onScrollListener.onScrollStateChanged(this,
                        OnScrollListener.SCROLL_STATE_FLING);
                // 记住上次滑动的最后位置
                lastL = l;
                checkStateHandler.removeMessages(CHECK_STATE);// 确保只在最后一次做这个check
                checkStateHandler.sendEmptyMessageDelayed(CHECK_STATE, 5);// 5毫秒检查一下
            }
        }
        onScrollListener.onScrollChanged(l, t, oldl, oldt);
    }

    private Handler checkStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (lastL == getScrollX()) {
                // 如果上次的位置和当前的位置相同，可认为是在空闲状态
                Log.e(TAG, "SCROLL_STATE_IDLE");
                onScrollListener.onScrollStateChanged(HorizonTimeScrollView.this,
                        OnScrollListener.SCROLL_STATE_IDLE);
                if (lastL == 0) {
                    onScrollListener.onTopArrived();
                    Log.d(TAG, "到最左方");

                    load();
                    setAutoScrollerFlag(true, false);
//                    initTime();
                } else if (getScrollX() + getWidth() >= computeHorizontalScrollRange()) {
                    onScrollListener.onBottomArrived();
//                    addTimeline();
                    scrollerToTop();
                    Log.d(TAG, "到最右方");
                } else {
                    setAutoScrollerFlag(true, false);
                    Log.d(TAG, "没有到最右方");
                }
            }
        }
    };

    private void load() {
        timelineAbs.addOneDay();
        offset += 24 * 60 * 60;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollBy((int) (timelineAbs.getStep() * 10 * 24), 0);
                int day = (int) timelineAbs.getPage() / 4;
                relativeLayout.slideXBy(timelineAbs.getStep() * 10 * 24 * day);

//                relativeLayout.slideXBy(timelineAbs.getStep() * 10 * 24);
            }
        }, 200);
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isOnDraw){
            isOnDraw = true;
            scrollTo(timelineAbs.getWidth(), 0);
        }
    }

    public void scrollerToTop(){
//        scrollTo(timelineAbs.getWidth(), 0);
        setAutoScrollerFlag(true, true);
        scrollTo(timelineAbs.getWidth(), 0);
    }

    @Override
    public void onAddImage(float right, float top, ImageLoadType imageLoadType) {
        if (imageLoadType == ImageLoadType.LOADED || right < 0){
            return;
        }

        Log.d("TimelineAbs", "right = " + right);
        int day;
        if (imageLoadType == ImageLoadType.REFRESH){
            day = 0;
        } else {
            day = (int) (timelineAbs.getMarkTotalLength() / (timelineAbs.getStep() * 10 * 24));
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final ImageView imageView = new ImageView(getContext());
        layoutParams.width = (int) (timelineAbs.getStep() * 7);
        layoutParams.height = layoutParams.width * 9 / 16;
        imageView.setLayoutParams(layoutParams);
        right = right - timelineAbs.getStep() * 10 * 24 * day;
        imageView.setX(right);
        imageView.setY(0);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.ss);

        OnClickListener l = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAutoScrollerFlag(false, false);
                smoothScrollBy((int) (imageView.getX() - lastL - timelineAbs.getPaddingTop()), 0);
            }
        };
        imageView.setOnClickListener(l);

        relativeLayout.addView(imageView);
    }

    @Override
    public void onMoveImage(float delta) {
//        relativeLayout.slideXBy(delta);
    }

    @Override
    public void setDataList(List<TimeData> timeDatas) {
        timelineAbs.setAllTimeDataList(timeDatas);
    }

    private void setAutoScrollerFlag(boolean isStart, boolean isTop) {
        isAutoTimer = isStart;
        if (timelineAbs.getStep() == 0){
            timeHandler.sendEmptyMessageDelayed(4, 1000);
            return;
        }

        delayTime = (long) (360000 / timelineAbs.getStep());

        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = isStart ? 1 : 0;
        msg.arg2 = isTop ? 1 : 0;
        timeHandler.sendMessage(msg);
    }

    long delayTime;

    private OnTimeChange onTimeChange;

    @Override
    public void setOnTimeChage(OnTimeChange onTimeChange) {
        this.onTimeChange = onTimeChange;
    }

    Handler timeHandler = new Handler(){

        static final int TIMELINE_AUTO_REFRESH = 0;
        static final int TIMELINE_AUTO_SCROLL = 1;
        static final int TIMELINE_AUTO_SCROLL_STATE_CHANGE = 2;
        static final int TIMELINE_POINTER_AUTO_TIMER = 3;
        static final int TIMELINE_NOT_DRAWED = 4;

        boolean isStart;
        boolean isTop = true;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIMELINE_AUTO_REFRESH:
                    if (isTop){
                        timelineAbs.updateCurrentTime();
                        timeHandler.sendEmptyMessageDelayed(TIMELINE_AUTO_REFRESH, delayTime);
                    }
                    break;
                case TIMELINE_AUTO_SCROLL:
                    if (isStart){
                        offsetDp += 1;
                        if (offsetDp > 0){
                            if (timelineAbs.getOrientationMode() == OrientationMode.VERTICAL) {
                                smoothScrollBy(1, 0);
                            } else {
                                smoothScrollBy(0, 1);
                            }
                            timeHandler.sendEmptyMessageDelayed(TIMELINE_AUTO_SCROLL, delayTime);
                        }
                    }
                    break;
                case TIMELINE_AUTO_SCROLL_STATE_CHANGE:
                    isStart = msg.arg1 == 1;
                    isTop = msg.arg2 == 1;
                    timeHandler.removeMessages(TIMELINE_AUTO_SCROLL);
                    timeHandler.sendEmptyMessageDelayed(TIMELINE_AUTO_SCROLL, delayTime);

                    timeHandler.removeMessages(TIMELINE_AUTO_REFRESH);
                    timeHandler.sendEmptyMessage(TIMELINE_AUTO_REFRESH);

                    timeHandler.removeMessages(TIMELINE_POINTER_AUTO_TIMER);
                    timeHandler.sendEmptyMessage(TIMELINE_POINTER_AUTO_TIMER);
                    break;
                case TIMELINE_POINTER_AUTO_TIMER:
                    currentTime += 1;
                    if (onTimeChange != null){
                        onTimeChange.timeChange(DateTimeUtil.getTime(currentTime));
                    }
                    timeHandler.sendEmptyMessageDelayed(TIMELINE_POINTER_AUTO_TIMER, 1000);
                    break;
                case TIMELINE_NOT_DRAWED:
                    setAutoScrollerFlag(true, true);
                    break;
            }
        }
    };

    @Override
    public void startScroller(){
        setAutoScrollerFlag(true, true);
    }

    @Override
    public void setTime(final long timestamp) {
        if (timelineAbs.getLocationByTime(timestamp) < 0){
            timelineAbs.setTime(timestamp);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initTime();
                    setAutoScrollerFlag(false, false);
                    scrollTo((int) timelineAbs.getLocationByTime(timestamp) - DensityUtil.dp2px(getContext(), getResources().getConfiguration().screenWidthDp/2), 0);
                    int day = (int) timelineAbs.getPage() / 4;
                    relativeLayout.slideXBy(timelineAbs.getStep() * 10 * 24 * day);
                    setAutoScrollerFlag(true, false);
                }
            }, 200);
        } else {
            setAutoScrollerFlag(false, false);
            scrollTo((int) timelineAbs.getLocationByTime(timestamp) - DensityUtil.dp2px(getContext(), getResources().getConfiguration().screenWidthDp/2), 0);
            setAutoScrollerFlag(true, false);
        }
    }

    OnControlListener onControlListener;

    @Override
    public void setOnControlListener(OnControlListener onControlListener) {
        this.onControlListener = onControlListener;
    }
}
