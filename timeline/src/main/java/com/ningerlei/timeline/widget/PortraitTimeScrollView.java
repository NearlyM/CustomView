package com.ningerlei.timeline.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ningerlei.timeline.R;
import com.ningerlei.timeline.callback.IScrollView;
import com.ningerlei.timeline.callback.OnAddImageCallback;
import com.ningerlei.timeline.callback.OnControlListener;
import com.ningerlei.timeline.callback.OnScrollListener;
import com.ningerlei.timeline.callback.OnTimeChange;
import com.ningerlei.timeline.entity.TimeData;
import com.ningerlei.timeline.util.ContextUtil;
import com.ningerlei.timeline.util.DateTimeUtil;
import com.ningerlei.timeline.util.DensityUtil;

import java.util.LinkedList;
import java.util.List;

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

class PortraitTimeScrollView extends ScrollView implements OnAddImageCallback, IScrollView {

    private static final String TAG = PortraitTimeScrollView.class.getSimpleName();
    // 检查ScrollView的最终状态
    private static final int CHECK_STATE = 0;

    private final int Default_imageX = 120, Default_logoX = 30;
    // 外部设置的监听方法
    private OnScrollListener onScrollListener;
    // 是否在触摸状态
    private boolean inTouch = false;
    // 上次滑动的最后位置
    private int lastT = 0;

    LinearLayout linearLayout;

    SlideRelativeLayout relativeLayout;

    TimelineAbs timelineAbs;
    private boolean isAutoTimer;
    private boolean isJumpToTop;

    public PortraitTimeScrollView(Context context) {
        this(context, null);
    }

    public PortraitTimeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initCacheView();
    }

    private void initView(Context context) {

        relativeLayout = new SlideRelativeLayout(context);
        ViewGroup.LayoutParams param0 = new ViewGroup.LayoutParams(
                DensityUtil.dp2px(getContext(), 300),
                ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(param0);
//        relativeLayout.setBackgroundColor(Color.RED);

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(param);
        linearLayout.setGravity(Gravity.RIGHT);

        AttributeSet attributes = ContextUtil.getAttributeSet(getResources(), R.layout.timeline);

        ViewGroup.LayoutParams param1 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        timelineAbs = new TimelineAbs(context, attributes);
        param1.height = DensityUtil.dp2px(context, getResources().getConfiguration().screenHeightDp);
        timelineAbs.setLayoutParams(param1);

        linearLayout.addView(relativeLayout);

        linearLayout.addView(timelineAbs);

        addView(linearLayout);

        timelineAbs.setOnAddImageCallback(this);

        imageX = DensityUtil.dp2px(context, Default_imageX);
        logoX = DensityUtil.dp2px(context, Default_logoX);
    }

    @Override
    public void setDataList(List<TimeData> dataList){
        if (timelineAbs != null){
            timelineAbs.setAllTimeDataList(dataList);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "ScrollView onMeasure");

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

        offsetDp = (int) ((24 * 60 * 60 * 1000 - ((timestamp - timeInMillis))) * timelineAbs.getMarkTotalLength() / (24 * 60 * 60 * 1000)) ;
//        Log.d(TAG, "offsetDp = " + offsetDp + "; height = " + timelineAbs.getMarkTotalLength() + "; timestamp = " + timestamp + "; timeinMillis = " + timeInMillis);
    }

    long offset = 24 * 60 * 60 ;
    int offsetDp;

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
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
                lastT = getScrollY();
                checkStateHandler.removeMessages(CHECK_STATE);// 确保只在最后一次做这个check
                checkStateHandler.sendEmptyMessageDelayed(CHECK_STATE, 5);// 5毫秒检查一下

                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    long currentTime;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        identifyInRecord(t, oldt);


        if (onScrollListener == null || isAutoTimer) {
            return;
        }

        int timelineLength = (int) timelineAbs.getMarkTotalLength();

        long secondTotal = offset  * t / timelineLength;

        currentTime = offset - secondTotal;

        if (onTimeChage != null){
            onTimeChage.timeChange(DateTimeUtil.getTime(currentTime));
        }

//        showContent(t, oldt);
        
        if (inTouch) {
            if (t != oldt) {
                // 有手指触摸，并且位置有滚动
                Log.i(TAG, "SCROLL_STATE_TOUCH_SCROLL");
                scrollerState = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
                onScrollListener.onScrollStateChanged(this,
                        OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
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

    /**处理从告警到正常录像或者空录像的边界
     * @param t
     * @param oldt
     */
    private void identifyInRecord(int t, int oldt) {
        long timeByLocation = timelineAbs.getTimeByLocation(t);
        TimeData timeData;

        if (t > oldt){
            if (timelineAbs.isInRecord()){
                timeData = timelineAbs.getTimeData(timelineAbs.getPosition());
            }else {
                timeData = timelineAbs.getTimeData(timelineAbs.getPosition() + 1);
            }

            if (timeData != null){
                if (timeData.getStartTime() + timeData.getOffset() > timeByLocation && !timelineAbs.isInRecord()){
                    timelineAbs.setPosition(timelineAbs.getPosition() + 1);
                    timelineAbs.setInRecord(true);
                }else if (timeData.getStartTime() > timeByLocation && timelineAbs.isInRecord()){
                    timelineAbs.setInRecord(false);
                }
            }
        }else {
            if (timelineAbs.isInRecord()){
                timeData = timelineAbs.getTimeData(timelineAbs.getPosition());
            }else {
                timeData = timelineAbs.getTimeData(timelineAbs.getPosition() - 1);
            }
            if (timeData != null){
                if (timeData.getStartTime() < timeByLocation && !timelineAbs.isInRecord()){
                    timelineAbs.setPosition(timelineAbs.getPosition() - 1);
                    timelineAbs.setInRecord(true);
                }else if (timeData.getStartTime() + timeData.getOffset() < timeByLocation && timelineAbs.isInRecord()) {
                    timelineAbs.setInRecord(false);
                    Log.d("Timelinerecord", "should jump");
                    if (!inTouch && isAutoTimer && !isJumpToTop){
                        Log.d("Timelinerecord", "jump");
                        autoJump2NextRecord();
                    }
                    Log.d("Timelinerecord", "jump end");
                }
            }
        }
    }

    private Handler checkStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (lastT == getScrollY()) {
                // 如果上次的位置和当前的位置相同，可认为是在空闲状态
                Log.e(TAG, "SCROLL_STATE_IDLE");
                scrollerState = OnScrollListener.SCROLL_STATE_IDLE;
                onScrollListener.onScrollStateChanged(PortraitTimeScrollView.this,
                        OnScrollListener.SCROLL_STATE_IDLE);

                if (onControlListener != null) {
                    onControlListener.onPlayVideo(timelineAbs.getTimeByLocation(lastT));
                }

                if (lastT == 0) {
                    onScrollListener.onTopArrived();
                    setAutoScrollerFlag(false, true);
                    isJumpToTop = false;
//                    initTime();
                } else if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
                    onScrollListener.onBottomArrived();
//                    addTimeline();
                    Log.d(TAG, "到最下方");
                    showLoading();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timelineAbs.addOneDay();
                            offset += 24 * 60 * 60;
                            dismissLoading();
                        }
                    }, 2000);
                    setAutoScrollerFlag(true, false);
                } else {
                    Log.d(TAG, "没有到最下方");
                    setAutoScrollerFlag(true, false);
                }
            }
        }
    };

    private void dismissLoading() {
        if (onRefreshChange != null){
            onRefreshChange.dismissLoad();
        }
    }

    private void showLoading() {
        if (onRefreshChange != null){
            onRefreshChange.onLoadMore();
        }
    }


    int scrollerState;
    float LastTop;

    LinkedList<ImageView> visibleImageViews;
    LinkedList<ImageView> inVisibleImageViews;
    LinkedList<ImageView> visibleLogoViews;
    LinkedList<ImageView> inVisibleLogoViews;

    private static final double SCROLL_SLOP = 30.75 * 10;

    int lastImageLocation;
    int imageX = 310, logoX = 100;

    private void showContent(int t, int oldt) {
        if (Math.abs(t - lastImageLocation) >= SCROLL_SLOP){
            float top = t - t % (10 * timelineAbs.getStep()) + 30 * timelineAbs.getStep() + timelineAbs.getPaddingTop();
            multiplexView(top, t > oldt);
            lastImageLocation = t;
        }
    }

    private void multiplexView(float top, boolean isUpScroll){

        if (visibleImageViews.size() <= 9){
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(layoutParams);
            imageView.setX(imageX);
            imageView.setY(top);
            imageView.setImageResource(R.drawable.ss);

            ImageView logo = new ImageView(getContext());
            logo.setLayoutParams(layoutParams);
            logo.setX(logoX);
            logo.setY(top);
            logo.setImageResource(R.drawable.messages_move);


            relativeLayout.addView(imageView);
            relativeLayout.addView(logo);

            visibleImageViews.addLast(imageView);
            visibleLogoViews.addLast(logo);

            if (visibleImageViews.size() > 5){
                inVisibleImageViews.addLast(visibleImageViews.removeFirst());
                inVisibleLogoViews.addLast(visibleLogoViews.removeFirst());
            }
        }else {
            if (isUpScroll) {
                //向上滑
                inVisibleImageViews.addLast(visibleImageViews.removeFirst());
                ImageView last = inVisibleImageViews.getFirst();
                last.setY(top);
                visibleImageViews.addLast(last);

                inVisibleLogoViews.addLast(visibleLogoViews.removeFirst());
                ImageView lastLogo = inVisibleLogoViews.getFirst();
                lastLogo.setY(top);
                visibleLogoViews.addLast(lastLogo);
            } else {
                //向下滑
                inVisibleImageViews.addFirst(visibleImageViews.removeLast());
                ImageView first = inVisibleImageViews.removeFirst();
                first.setY(top - 30 * timelineAbs.getStep());
                visibleImageViews.addFirst(first);

                inVisibleLogoViews.addFirst(visibleLogoViews.removeLast());
                ImageView firstLogo = inVisibleLogoViews.removeFirst();
                firstLogo.setY(top - 30 * timelineAbs.getStep());
                visibleLogoViews.addFirst(firstLogo);
            }
        }
    }


    @Override
    public void onAddImage(float left, float top, ImageLoadType imageLoadType) {

        Log.d("Timeline", "should add pic" + "; top = " + top + "; lastTop = " + LastTop);

        if (imageLoadType == ImageLoadType.LOADED || top > timelineAbs.getMarkTotalLength()){
            return;
        }

        Log.d("Timeline", "addImage");

//        if (lastT == 0){
//            if (LastTop < top && LastTop > 0){
//                Log.d("Timeline", "return 1");
//                return;
//            }
//            LastTop = top;
//        }else {
//            if (timelineAbs.getHeight() - 240 * timelineAbs.getStep() > top){
//                Log.d("Timeline", "return 2");
//                return;
//            }
//        }


        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final ImageView imageView = new ImageView(getContext());
        layoutParams.height = (int) (timelineAbs.getStep() * 7);
        layoutParams.width = layoutParams.height * 16 / 9;
        imageView.setLayoutParams(layoutParams);
        imageView.setX(imageX);
        imageView.setY(top);
        Log.d(TAG, "x = " + left + "; y = " + top);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.ss);

        OnClickListener l = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAutoScrollerFlag(false, false);
                PortraitTimeScrollView.this.smoothScrollBy(0, (int) (imageView.getY() - lastT - timelineAbs.getPaddingTop()));
            }
        };
        imageView.setOnClickListener(l);

        ViewGroup.LayoutParams layoutParamsLogo = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView logo = new ImageView(getContext());
        layoutParamsLogo.width = DensityUtil.dp2px(getContext(), 30);
        layoutParamsLogo.height = DensityUtil.dp2px(getContext(), 30);
        logo.setLayoutParams(layoutParamsLogo);
        logo.setX(logoX);
        logo.setY(top + timelineAbs.getStep() * 2 - DensityUtil.dp2px(getContext(), 30) / 2);
        logo.setBackgroundColor(Color.BLACK);

        logo.setOnClickListener(l);

        ViewGroup.LayoutParams layoutParamsLine = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(getContext(), 3));
        View line = new View(getContext());
        line.setLayoutParams(layoutParamsLine);
        line.setX(logoX + DensityUtil.dp2px(getContext(), 30));
        line.setY(top + timelineAbs.getStep() * 2);
        line.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        line.setBackgroundResource(R.drawable.dotted_line);


        relativeLayout.addView(logo);
        relativeLayout.addView(line);
        relativeLayout.addView(imageView);
    }

    @Override
    public void onMoveImage(float delta) {
        if (relativeLayout != null){
            relativeLayout.slideYBy(delta);
        }
//        initTime();
    }

    private void initCacheView() {
        if (visibleImageViews == null){
            visibleImageViews = new LinkedList<>();
            visibleLogoViews = new LinkedList<>();

            inVisibleImageViews = new LinkedList<>();
            inVisibleLogoViews = new LinkedList<>();
        }
    }

    OnTimeChange onTimeChage;

    @Override
    public void setOnTimeChage(OnTimeChange onTimeChange) {
        this.onTimeChage = onTimeChange;
    }

    OnRefreshStateListener onRefreshChange;

    public void setOnRefreshChange(OnRefreshStateListener onRefreshChange) {
        this.onRefreshChange = onRefreshChange;
    }

    public interface OnRefreshStateListener {
        void onLoadMore();
        void dismissLoad();
    }

    @Override
    public void startScroller(){
        setAutoScrollerFlag(false, true);
    }

    @Override
    public void setTime(final long timestamp) {
        timelineAbs.setTime(timestamp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initTime();
                setAutoScrollerFlag(false, false);
                scrollTo(0, (int) timelineAbs.getLocationByTime(timestamp) - timelineAbs.getPaddingTop());
                setAutoScrollerFlag(true, false);
            }
        }, 200);
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
                        offsetDp -= 1;
                        if (offsetDp > 0){
                            smoothScrollBy(0, -1);
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
                    if (onTimeChage != null){
                        onTimeChage.timeChange(DateTimeUtil.getTime(currentTime));
                    }
                    timeHandler.sendEmptyMessageDelayed(TIMELINE_POINTER_AUTO_TIMER, 1000);
                    break;
                case TIMELINE_NOT_DRAWED:
                    setAutoScrollerFlag(true, true);
                    break;
            }
        }
    };

    public void scrollerToTop(boolean smooth){
        if (smooth) {
            smoothScrollTo(0, 0);
        } else {
            scrollTo(0, 0);
        }
        isJumpToTop = true;
        initTime();
        setAutoScrollerFlag(false, true);
    }

    private void autoJump2NextRecord(){
        TimeData timeData = timelineAbs.getTimeData(timelineAbs.getPosition() - 1);
        if (timeData != null){
            TimeData oldData = timelineAbs.getTimeData(timelineAbs.getPosition());
            if (oldData != null){

                long offsetTime = timeData.getStartTime() - (oldData.getStartTime() + oldData.getOffset());

                float offsetDistance = timelineAbs.transformTime2Px(offsetTime);
                setAutoScrollerFlag(false, false);
                smoothScrollBy(0, (int) -offsetDistance);
            }
        }

    }

    OnControlListener onControlListener;

    @Override
    public void setOnControlListener(OnControlListener onControlListener) {
        this.onControlListener = onControlListener;
    }
}
