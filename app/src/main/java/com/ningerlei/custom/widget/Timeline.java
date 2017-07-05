package com.ningerlei.custom.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import com.ningerlei.custom.R;
import com.ningerlei.custom.util.DensityUtil;

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

public class Timeline extends View{

    private final String TAG = Timeline.class.getSimpleName();

    private final int WIDTH_DEFAULT_PORTRAIT= 50;
    private final int HEIGHT_DEFAULT_LANDSCAPE = 50;
    private final int UNIT = 6; // min 时间刻度间隔

    private int lMarkH = 20; //刻度的高（非物理上的高）
    private int lMarkW = 4;  //刻度的宽

    private int sMarkH = 14;  //刻度的高（非物理上的高）
    private int sMarkW = 2;  //刻度的宽

    private int markColor = Color.BLACK; //刻度颜色
    private int background = Color.WHITE;   //背景颜色

    private int textSize = 12;  //时间轴上刻度标记

    private int mWidth, mHeight;

    private OrientationMode orientationMode;

    private ScrollMode scrollMode;

    private Scroller scroller;

    private VelocityTracker velocityTracker;

    private Paint sMPaint, lMPaint, tPaint;

    /**
     * 时间轴的滑动类型
     */
    public enum ScrollMode{
        In_Day, //  只能滑动一天
        Out_Day //  可以跨天滑动
    }

    /**
     * 时间轴的方向
     */
    public enum OrientationMode{
        Landscape,
        Portrait
    }

    public Timeline(Context context) {
        this(context, null);
    }

    public Timeline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Timeline, 0, 0);

        orientationMode = typedArray.getInt(0, R.styleable.Timeline_orientation) == 0 ? OrientationMode.Landscape : OrientationMode.Portrait;
        scrollMode = typedArray.getInt(0, R.styleable.Timeline_scrollMode) == 0 ? ScrollMode.In_Day : ScrollMode.Out_Day;

        if (orientationMode == OrientationMode.Landscape) {
            mHeight = DensityUtil.dp2px(getContext(), HEIGHT_DEFAULT_LANDSCAPE);
        } else {
            mWidth = DensityUtil.dp2px(getContext(), WIDTH_DEFAULT_PORTRAIT);
        }

        typedArray.recycle();

        scroller = new Scroller(getContext());
//        scroller = new Scroller(getContext(), new LinearInterpolator());

        sMPaint = new Paint();
        sMPaint.setColor(markColor);
        sMPaint.setStrokeWidth(sMarkW);

        lMPaint = new Paint();
        lMPaint.setColor(markColor);
        lMPaint.setStrokeWidth(lMarkW);

        tPaint = new Paint();
        tPaint.setTextSize(DensityUtil.dp2px(getContext(), textSize));
        tPaint.setStrokeWidth(lMarkW);
        tPaint.setColor(markColor);
        tPaint.setAntiAlias(true);
        tPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(background);
        if (orientationMode == OrientationMode.Landscape) {
            drawLandscapeMark(canvas);
        } else {
            drawPortraitMark(canvas);
        }
    }

    private void drawLandscapeMark(Canvas canvas) {
        int width = getWidth();
        float step = width / 30;
        int visible = (int) (width / step);

        for (int count = 0; count < visible; count++){
            float startX = getLeft() + count * step;
            if (count % 10 == 0) {
                canvas.drawLine(startX, getHeight(), startX, getHeight() - DensityUtil.dp2px(getContext(), lMarkH), lMPaint);
            }else {
                canvas.drawLine(startX, getHeight(), startX, getHeight() - DensityUtil.dp2px(getContext(), sMarkH), sMPaint);
            }
        }
    }

    private void drawPortraitMark(Canvas canvas) {
        int height = getHeight() * 4;
        float step = (float) height / (60 * 4);
        int totalCount = (int) (height / step);

        for (int count = 0; count <= totalCount; count++){
            float startY = getTop() + count * step;
            if (count % 10 == 0) {
                canvas.drawLine(getWidth(), startY, getWidth() - DensityUtil.dp2px(getContext(), lMarkH), startY,  lMPaint);
                canvas.drawText("" + count / 10, getWidth() - DensityUtil.dp2px(getContext(), lMarkH) - DensityUtil.dp2px(getContext(), textSize), startY + DensityUtil.dp2px(getContext(), textSize) / 3, tPaint);
            }else {
                canvas.drawLine(getWidth(), startY, getWidth() - DensityUtil.dp2px(getContext(), sMarkH), startY,  sMPaint);
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationMode = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? OrientationMode.Landscape : OrientationMode.Portrait;

        if (orientationMode == OrientationMode.Landscape) {
            mHeight = DensityUtil.dp2px(getContext(), HEIGHT_DEFAULT_LANDSCAPE);
        } else {
            mWidth = DensityUtil.dp2px(getContext(), WIDTH_DEFAULT_PORTRAIT);
        }
    }

    float startX , startY;
    int base;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (event.getRawX() - startX);
                int deltaY = (int) (event.getRawY() - startY);

                if (base <= 0){
                    smoothScrollToInY(deltaY, 5);
                }
                Log.d(TAG, "ACTION_MOVE deltaY = " + deltaY + "; base = " + base);
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();
                float yVelocity = velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) >= 100){
                    if (base <= 0){
                        Log.d(TAG, "ACTION_UP deltaY = " + (int) (yVelocity / 4) + "; base = " + base);
                        smoothScrollToInY((int) (yVelocity / 4), 500);
                    }
                }
                velocityTracker.clear();
                velocityTracker.recycle();
                velocityTracker = null;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    private void smoothScrollToInY(int deltaY, int duration){
        base += deltaY;
        if (base > 0){
            base = 0;
            return;
        }
        int scrollY = getScrollY();
        scroller.startScroll(0, scrollY, 0, -deltaY, duration);
        invalidate();
    }

    private void smoothScrollToInX(int destX, int destY){
        int scrollX = getScrollX();
        int deltaX = destX - scrollX;
        scroller.startScroll(scrollX, 0, deltaX, 0, 1000);
        invalidate();
    }

    public void setOrientationMode(OrientationMode orientationMode) {
        this.orientationMode = orientationMode;
    }

    public OrientationMode getOrientationMode() {
        return orientationMode;
    }
}
