package com.ningerlei.timeline.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ningerlei.timeline.R;
import com.ningerlei.timeline.callback.OnAddImageCallback;
import com.ningerlei.timeline.constant.OrientationMode;
import com.ningerlei.timeline.entity.TimeData;
import com.ningerlei.timeline.util.DateTimeUtil;
import com.ningerlei.timeline.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * CreateTime : 2017/7/5 9:57
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/5 9:57
 * @ModifyDescription :
 */

class TimelineAbs extends View {

    private final String TAG = TimelineAbs.class.getSimpleName();

    private final int WIDTH_DEFAULT_PORTRAIT= 60;
    private final int HEIGHT_DEFAULT_LANDSCAPE = 60;
    private final int UNIT = 6; // min 时间刻度间隔

    private int lMarkH = 5; //刻度的高（非物理上的高）
    private int lMarkW = 2;  //刻度的宽

    private int sMarkH = 3;  //刻度的高（非物理上的高）
    private int sMarkW = 1;  //刻度的宽

    private int colorBackground = Color.GRAY;
    private int markColor = Color.BLACK; //刻度颜色
    private int background = Color.WHITE;   //背景颜色

    private int textSize = 12;  //时间轴上刻度标记的文字大小

    private int mWidth, mHeight;

    private OrientationMode orientationMode;    //时间轴的方向

    private ScrollMode scrollMode;  //时间刻度的滚动方式

    /**
     * sMPaint  刻度尺的的短刻度画笔(short mark)
     * lMPaint  刻度尺的的长刻度画笔(long mark)
     * tMPaint   刻度尺的的文字画笔(text mark)
     * cPaint   颜色轴的画笔(color)
     */
    private Paint sMPaint, lMPaint, tMPaint, cPaint;

    private int colorPadding = 0;   //颜色轴上外围颜色和轴心颜色的padding

    private int colorWidth = 10; //颜色轴宽度

    private float markTotalLength; //刻度尺上刻度的总长度 像素级

    float step; //刻度尺上的最小间隔

    float page = 4.0f;  //时间轴占的屏幕页数

    private int dayCount;

    List<TimeData> allTimeDataList;
    List<TimeData> oldTimeDataList;

    /**
     * 当前时间轴指针指示的录像位置
     */
    private int position = -1;

    /**
     * 时间轴指针是否正处于录像区域中
     */
    private boolean isInRecord;

    public void setAllTimeDataList(List<TimeData> timeDataList) {
        if (allTimeDataList == null){
            allTimeDataList = new ArrayList<>();
        }
        if (oldTimeDataList == null){
            oldTimeDataList = new ArrayList<>();
        }

        oldTimeDataList.clear();
        oldTimeDataList.addAll(this.allTimeDataList);

        allTimeDataList.clear();
        allTimeDataList.addAll(timeDataList);

        invalidate();
    }

    public float getStep() {
        return step;
    }

    public float getPage() {
        return page;
    }

    public float getMarkTotalLength() {
        return markTotalLength;
    }

    int visualLength;
    public void setVisualLength(int visualLength) {
        visualLength = DensityUtil.dp2px(getContext(), getResources().getConfiguration().screenHeightDp);
        this.visualLength = visualLength;
        markTotalLength = getHeight() - (DensityUtil.dp2px(getContext(), getResources().getConfiguration().screenHeightDp) - visualLength) - getPaddingTop();
        step = markTotalLength / (60 * page);
        Log.d(TAG, "markTotalLength = " + markTotalLength + "; height = " + getHeight() + "; step = " + step);
    }

    public void updateTimeline() {
        if (orientationMode == OrientationMode.HORIZONTAL) {
            markTotalLength = getWidth() - getPaddingRight();
            step = markTotalLength / (60 * page);
        } else {
            markTotalLength = getHeight() - getPaddingTop();
            step = markTotalLength / (60 * page);
//        Log.d(TAG, "markTotalLength = " + markTotalLength + "; height = " + getHeight() + "; step = " + step + "; page = " + page);
        }
    }

    boolean firstMeasure = true;
    public void initTime(){
        if (firstMeasure){
            firstMeasure = false;

            long timeInMillis = DateTimeUtil.getTodayStart();

            long totalTime = (System.currentTimeMillis() - timeInMillis) / 1000;

            page = (float) totalTime * 4 / (24 * 60 * 60);

            markTotalLength = totalTime * markTotalLength / (3600 * 24);
        }
    }

    /**
     * 时间轴的滑动类型
     */
    public enum ScrollMode{
        In_Day, //  只能滑动一天
        Out_Day //  可以跨天滑动
    }

    public TimelineAbs(Context context) {
        this(context, null);
    }

    public TimelineAbs(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Timeline, 0, 0);

        orientationMode = typedArray.getInt(R.styleable.Timeline_orientation, 0) == 0 ? OrientationMode.HORIZONTAL : OrientationMode.VERTICAL;
        scrollMode = typedArray.getInt(R.styleable.Timeline_scrollMode, 0) == 0 ? ScrollMode.In_Day : ScrollMode.Out_Day;

        if (orientationMode == OrientationMode.HORIZONTAL) {
            mHeight = DensityUtil.dp2px(getContext(), HEIGHT_DEFAULT_LANDSCAPE);
        } else {
            mWidth = DensityUtil.dp2px(getContext(), WIDTH_DEFAULT_PORTRAIT);
        }

        colorBackground = getResources().getColor(R.color.colorWhite);

        typedArray.recycle();

        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        sMPaint = new Paint();
        sMPaint.setColor(markColor);
        sMPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), sMarkW));

        lMPaint = new Paint();
        lMPaint.setColor(markColor);
        lMPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), lMarkW));

        tMPaint = new Paint();
        tMPaint.setTextSize(DensityUtil.dp2px(getContext(), textSize));
        tMPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), lMarkW));
        tMPaint.setColor(markColor);
        tMPaint.setAntiAlias(true);
        tMPaint.setTextAlign(Paint.Align.CENTER);

        cPaint = new Paint();
    }

    public void setTime(long timestamp) {
        long baseTime = DateTimeUtil.getTodayStart();
        dayCount = 0;
        page = page % 4;
        while (timestamp - baseTime < 0){
            dayCount += 1;
            page += 4;
            baseTime -= 24 * 60 * 60 * 1000;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        setLayoutParams(layoutParams);
    }

    public void addOneDay(){
//        Log.d(TAG, "old height = " + getHeight());
        dayCount += 1;
        page += 4;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        setLayoutParams(layoutParams);
    }

    long lastTimestamp;

    public void updateCurrentTime(){
        long timestamp = System.currentTimeMillis();

        long d_time;
        if (lastTimestamp == 0) {
            d_time = (long) (360000 / step);
        } else {
            d_time = timestamp - lastTimestamp;
        }

        if ((step == 0 || d_time < 360000 / step) && lastTimestamp != 0){
            return;
        }

        lastTimestamp = timestamp;

        long timeInMillis = DateTimeUtil.getTodayStart();

        if (timestamp - timeInMillis > 24 * 3600 * 1000){
            dayCount += 1;
        }

        long totalTime = (timestamp - timeInMillis) / 1000;

        page = (float) totalTime * 4 / (24 * 60 * 60);

        page = page + dayCount * 4;

        float todayLength = totalTime * (markTotalLength - dayCount * 24 * 10 * step) / (3600 * 24);

        markTotalLength = todayLength + dayCount * 24 * 10 * step;

        if (onAddImageCallback != null){
            onAddImageCallback.onMoveImage(d_time / (360000 / step));
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        setLayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("Timeline", "Timeline onMeasure");

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
//            Log.d(TAG, "onMeasure 0");
            setMeasuredDimension(mWidth, (int) (mHeight * (page/* + 1*/) + getPaddingTop()));
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
//            Log.d(TAG, "onMeasure 1");
            if (orientationMode == OrientationMode.HORIZONTAL) {
                int measuredWidth = (int) (widthSpecSize * (page/* + 1*/));
                setMeasuredDimension(measuredWidth, mHeight);
            } else {
                int measuredHeight = (int) (heightSpecSize * (page/* + 1*/) + getPaddingTop());
                setMeasuredDimension(mWidth, measuredHeight);
            }
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
//            Log.d(TAG, "onMeasure 2");

            if (orientationMode == OrientationMode.HORIZONTAL) {
                int measuredWidth = (int) (widthSpecSize * (page/* + 1*/));
                setMeasuredDimension(measuredWidth, mHeight);
            } else {
                setMeasuredDimension(widthSpecSize, (int) (mHeight * (page/* + 1*/) + getPaddingTop()));
            }

        } else {
//            Log.d(TAG, "onMeasure 3");

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw" + "; getWidth = " + getWidth());
        updateTimeline();
        setBackgroundColor(background);
        if (orientationMode == OrientationMode.HORIZONTAL) {
            drawLandscapeMark(canvas);
        } else {
            drawPortraitMark(canvas);
        }
        drawColor(canvas);
    }

    /**
     * 画颜色轴
     * @param canvas
     */
    private void drawColor(Canvas canvas) {
        if (allTimeDataList != null){
//            Log.d("drawCapsule", "drawColor" + "; orientationMode = " + orientationMode);
            Rect rect = new Rect();
            for (TimeData timeData : allTimeDataList) {
                if (orientationMode == OrientationMode.HORIZONTAL) {
                    drawLandCapsule(canvas, rect, timeData);
                } else {
                    drawCapsule(canvas, rect, timeData);
                }
            }
//            Log.d(TAG, "oldTimeDataList == null : " + (oldTimeDataList == null));



            if (oldTimeDataList == null || oldTimeDataList.size() == 0){
                calculateWarnLocation(allTimeDataList.size() - 1, 0);
            }else {

                calculateWarnLocation(allTimeDataList.size() - 1, 0);
//                calculateWarnLocation(allTimeDataList.size() - 1, oldTimeDataList.size());
            }
        }
    }

    /**
     * 画一个录像对应的彩色胶囊
     * @param canvas
     * @param rect
     * @param timeData
     */
    private void drawLandCapsule(Canvas canvas, Rect rect, TimeData timeData) {

        int colorPaddingDp = DensityUtil.dp2px(getContext(), colorPadding);
        int colorHeightDp = DensityUtil.dp2px(getContext(), colorWidth);

        int colorLeft = getWidth() - DensityUtil.dp2px(getContext(), lMarkH * 6f) - colorPaddingDp;

        float start = getLocationByTime(timeData.getStartTime());
        float offset = transformTime2Px(timeData.getOffset());

        rect.set((int) (start + colorHeightDp / 2), getHeight() - colorHeightDp * 1, (int) (start + offset - colorHeightDp / 2), getHeight() - colorHeightDp * 0);

        cPaint.setColor(getResources().getColor(timeData.getColorType().getColorRes()));
        cPaint.setAntiAlias(true);
        canvas.drawRect(rect, cPaint);

        canvas.drawCircle(start + colorHeightDp / 2, getHeight() - colorHeightDp * 0 - colorHeightDp/2, colorHeightDp/2, cPaint);
        canvas.drawCircle(start + offset - colorHeightDp / 2, getHeight() - colorHeightDp * 0 - colorHeightDp/2, colorHeightDp/2, cPaint);

    }

    /**
     * 画一个录像对应的彩色胶囊
     * @param canvas
     * @param rect
     * @param timeData
     */
    private void drawCapsule(Canvas canvas, Rect rect, TimeData timeData) {

        int colorPaddingDp = DensityUtil.dp2px(getContext(), colorPadding);
        int colorWidthDp = DensityUtil.dp2px(getContext(), colorWidth);

        int colorLeft = getWidth() - DensityUtil.dp2px(getContext(), lMarkH * 6f) - colorPaddingDp;

        colorLeft = 0;

        float start = getLocationByTime(timeData.getStartTime());
        float offset = transformTime2Px(timeData.getOffset());

//                rect.set(colorLeft + colorPaddingDp, (int) (start - offset), colorLeft + colorWidthDp - colorPaddingDp, (int) start);
        rect.set(colorLeft + colorPaddingDp, (int) (start - offset + colorWidthDp / 2), colorLeft + colorWidthDp - colorPaddingDp, (int) start - colorWidthDp / 2);

        cPaint.setColor(getResources().getColor(timeData.getColorType().getColorRes()));
        cPaint.setAntiAlias(true);
        canvas.drawRect(rect, cPaint);

        canvas.drawCircle(colorLeft + colorWidthDp / 2, start - offset + colorWidthDp / 2, colorWidthDp / 2, cPaint);
        canvas.drawCircle(colorLeft + colorWidthDp / 2, start - colorWidthDp / 2, colorWidthDp / 2, cPaint);

    }

    /**
     * 计算告警图片的位置
     * @param index
     */
    private void calculateWarnLocation(int index, int lastIndex) {
        Log.d(TAG, "index = " + index + "; lastIndex = " + lastIndex);
        if (index >= lastIndex) {
            TimeData timeData = allTimeDataList.get(index);
            float locationByTime = getLocationByTime(timeData.getStartTime());
            notifyToAddImage(locationByTime, timeData);
            function(index, locationByTime, lastIndex);
        }
    }

    private void function(int index, float locationByTime, int lastIndex) {
        long timeByLocation;
        if (orientationMode == OrientationMode.HORIZONTAL) {
            timeByLocation = getTimeByLocation((long) (locationByTime + getStep() * 10));
        } else {
            timeByLocation = getTimeByLocation((long) (locationByTime - getStep() * 10));
        }
        function2(index, timeByLocation, locationByTime, lastIndex);
    }

    private void function2(int index, float timeByLocation, float locationByTime, int lastIndex){
        if (index >= lastIndex) {
            TimeData timeData = allTimeDataList.get(index);
            if (timeData.getStartTime() + timeData.getOffset() < timeByLocation) {
                function2(index - 1, timeByLocation, locationByTime, lastIndex);
            } else if (timeData.getStartTime() > timeByLocation) {
                locationByTime = getLocationByTime(timeData.getStartTime());
                notifyToAddImage(locationByTime, timeData);

                function(index - 1, locationByTime, lastIndex);
            } else if (timeData.getStartTime() < timeByLocation && timeData.getStartTime() + timeData.getOffset() > timeByLocation) {
                notifyToAddImage(locationByTime, timeData);

                function(index - 1, locationByTime, lastIndex);
            }
        }
    }

    private void notifyToAddImage(float locationByTime, TimeData timeData) {
        if (onAddImageCallback != null) {
            float location;
            if (orientationMode == OrientationMode.HORIZONTAL) {
                location = locationByTime - getStep() * 5;
                onAddImageCallback.onAddImage(location, 0, judgeLoadType(timeData));
            } else {
                location = locationByTime - getStep() * 2;
                onAddImageCallback.onAddImage(0, location, judgeLoadType(timeData));
            }
            if (location > 0 && location < getMarkTotalLength()){
                timeData.setImageLoadType(OnAddImageCallback.ImageLoadType.LOADED);
            }
        }
    }

    private OnAddImageCallback.ImageLoadType judgeLoadType(TimeData timeData) {
        if (timeData.getImageLoadType() == null){
            if (oldTimeDataList != null && oldTimeDataList.size() != 0) {
                if (timeData.getStartTime() > oldTimeDataList.get(0).getStartTime()) {
                    return OnAddImageCallback.ImageLoadType.REFRESH;
                } else if (timeData.getStartTime() < oldTimeDataList.get(oldTimeDataList.size() - 1).getStartTime()){
                    return OnAddImageCallback.ImageLoadType.LOADING;
                } else {
                    return OnAddImageCallback.ImageLoadType.LOADED;
                }
            }else {
                return OnAddImageCallback.ImageLoadType.LOADING;
            }
        }else {
            return timeData.getImageLoadType();
        }
    }

    /**
     * 根据时间获取时间轴上的时刻
     * @param timestamp
     * @return
     */
    public float getLocationByTime(long timestamp){
        long timeInMillis = DateTimeUtil.getTodayStart();
        if (orientationMode == OrientationMode.HORIZONTAL) {
            return (timestamp - timeInMillis + dayCount * 24 * 60 * 60 * 1000) * getMarkTotalLength() / getTotalTime() + getPaddingRight();
        } else {
            return  (getTotalTime() - (timestamp - timeInMillis + dayCount * 24 * 60 * 60 * 1000)) * getMarkTotalLength() / getTotalTime() + getPaddingTop();
        }
    }

    /**
     *  将时间长度转换成对应的物理长度
     * @param timestamp
     * @return
     */
    public float transformTime2Px(long timestamp){
        return timestamp * getMarkTotalLength() / getTotalTime();
    }

    /**
     * 根据时间轴上的刻度获取对应的时刻
     * @param location
     * @return
     */
    public long getTimeByLocation(long location){
        if (orientationMode == OrientationMode.HORIZONTAL) {
            return (long) (location * getTotalTime() / getMarkTotalLength()  - dayCount * 24 * 60 * 60 * 1000 + DateTimeUtil.getTodayStart());
        } else {
            return (long) (getTotalTime() - location * getTotalTime() / getMarkTotalLength()  - dayCount * 24 * 60 * 60 * 1000 + DateTimeUtil.getTodayStart());
        }
    }

    /**
     * 将物理长度转换成对应的时间长度
     * @param px
     * @return
     */
    public long transformPx2Time(long px){
        return (long) (px * getTotalTime() / getMarkTotalLength());
    }

    private long getTotalTime(){
        return (long) (24 * 60 * 60 * 1000 * page / 4);
    }

    /**
     * 画水平方向的刻度尺
     * @param canvas
     */
    private void drawLandscapeMark(Canvas canvas) {
        int totalCount = (int) (markTotalLength / step);

        for (int count = 0; count < totalCount; count++){
            float startX = getLeft() + count * step;
            if (count % 10 == 0) {
                canvas.drawLine(startX, getHeight(), startX, getHeight() - DensityUtil.dp2px(getContext(), lMarkH), lMPaint);
                canvas.drawText("" + ((/*24 - */count / 10 % 24) % 24), startX, getHeight() - (lMarkH + DensityUtil.dp2px(getContext(), textSize)), tMPaint);
            }else {
                canvas.drawLine(startX, getHeight(), startX, getHeight() - DensityUtil.dp2px(getContext(), sMarkH), sMPaint);
            }
        }
    }

    /**
     * 画竖直方向刻度尺
     * @param canvas
     */
    private void drawPortraitMark(Canvas canvas) {

        int totalCount = (int) (markTotalLength / step);

        int textLeft = getWidth() - DensityUtil.dp2px(getContext(), lMarkH) - DensityUtil.dp2px(getContext(), textSize);

        int colorPaddingDp = DensityUtil.dp2px(getContext(), colorPadding);
        int colorWidthDp = DensityUtil.dp2px(getContext(), colorWidth);
        int colorLeft = getWidth() - DensityUtil.dp2px(getContext(), lMarkH * 6f) - colorPaddingDp;

        colorLeft = 0;

        Rect rect = new Rect();

        rect.set(colorLeft, getPaddingTop(), colorLeft + colorWidthDp, (int) markTotalLength + getPaddingTop());
        cPaint.setColor(colorBackground);
        canvas.drawRect(rect, cPaint);

        for (int count = 0; count <= totalCount; count++) {
//            float startY = getTop() + getPaddingTop() + count * step; //自上而下画

            float startY = markTotalLength - count * step + getPaddingTop();  //自下而上画

            if (count % 10 == 0) {
                canvas.drawLine(getWidth(), startY, getWidth() - DensityUtil.dp2px(getContext(), lMarkH), startY,  lMPaint);
                canvas.drawText("" + ((/*24 - */count / 10 % 24) % 24), textLeft, startY + DensityUtil.dp2px(getContext(), textSize) / 3, tMPaint);
//                if (onAddImageCallback != null){
//                    onAddImageCallback.onAddImage(getWidth() - DensityUtil.dp2px(getContext(), lMarkH * 13), (int) startY);
//                }
            } else {
                canvas.drawLine(getWidth(), startY, getWidth() - DensityUtil.dp2px(getContext(), sMarkH), startY,  sMPaint);
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationMode = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? OrientationMode.HORIZONTAL : OrientationMode.VERTICAL;

        if (orientationMode == OrientationMode.HORIZONTAL) {
            mHeight = DensityUtil.dp2px(getContext(), HEIGHT_DEFAULT_LANDSCAPE);
        } else {
            mWidth = DensityUtil.dp2px(getContext(), WIDTH_DEFAULT_PORTRAIT);
        }
    }

    public void setOrientationMode(OrientationMode orientationMode) {
        this.orientationMode = orientationMode;
    }

    public OrientationMode getOrientationMode() {
        return orientationMode;
    }

    OnAddImageCallback onAddImageCallback;

    public void setOnAddImageCallback(OnAddImageCallback onAddImageCallback) {
        this.onAddImageCallback = onAddImageCallback;
    }

    public boolean isNeedDisplayWarn(float location) {

        return false;
    }

    public TimeData getTimeData(int position) {
        if (allTimeDataList != null && position >= 0 && position < allTimeDataList.size()) {
            return allTimeDataList.get(position);
        } else {
            return null;
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        Log.d(TAG + "record", "position = " + position);
    }


    public void setInRecord(boolean inRecord) {
        isInRecord = inRecord;
        Log.d(TAG + "record", "inRecord = " + inRecord);
    }

    public boolean isInRecord() {
        return isInRecord;
    }
}
