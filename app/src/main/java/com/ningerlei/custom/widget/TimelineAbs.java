package com.ningerlei.custom.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ningerlei.custom.R;
import com.ningerlei.custom.constant.ColorState;
import com.ningerlei.custom.util.DensityUtil;

import java.util.Calendar;

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

public class TimelineAbs extends View{

    private final String TAG = Timeline.class.getSimpleName();

    private final int WIDTH_DEFAULT_PORTRAIT= 500;
    private final int HEIGHT_DEFAULT_LANDSCAPE = 50;
    private final int UNIT = 6; // min 时间刻度间隔

    private int lMarkH = 20; //刻度的高（非物理上的高）
    private int lMarkW = 4;  //刻度的宽

    private int sMarkH = 14;  //刻度的高（非物理上的高）
    private int sMarkW = 2;  //刻度的宽

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

    private Bitmap bitmap;  //示例图片

    private int colorPadding = 0;   //颜色轴上外围颜色和轴心颜色的padding

    private int colorWidth = 8; //颜色轴宽度

    private int markTotalLength; //刻度尺上刻度的总长度 像素级

    float step; //刻度尺上的最小间隔

    float page = 4.0f;  //时间轴占的屏幕页数

    float totalTime = 24 * 60 * 60; //时间轴上显示的总时间 s

    public float getStep() {
        return step;
    }

    public int getMarkTotalLength() {
        return markTotalLength;
    }

    public void setVisualLength(int visualLength) {
        markTotalLength = getHeight() - (DensityUtil.dp2px(getContext(), getResources().getConfiguration().screenHeightDp) - visualLength);
        step = (float) markTotalLength / (60 * page);
//        step = 33.75f;
    }

    public void setTime(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long timeInMillis = calendar.getTimeInMillis();

        totalTime = (timestamp - timeInMillis) / 1000;

        page = (float) (totalTime * 4) / (24 * 60 * 60);
        Log.d(TAG, "page = " + page);

        markTotalLength = (int) (totalTime * markTotalLength / (3600 * 24));
    }

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

    public TimelineAbs(Context context) {
        this(context, null);
    }

    public TimelineAbs(Context context, @Nullable AttributeSet attrs) {
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

        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        sMPaint = new Paint();
        sMPaint.setColor(markColor);
        sMPaint.setStrokeWidth(sMarkW);

        lMPaint = new Paint();
        lMPaint.setColor(markColor);
        lMPaint.setStrokeWidth(lMarkW);

        tMPaint = new Paint();
        tMPaint.setTextSize(DensityUtil.dp2px(getContext(), textSize));
        tMPaint.setStrokeWidth(lMarkW);
        tMPaint.setColor(markColor);
        tMPaint.setAntiAlias(true);
        tMPaint.setTextAlign(Paint.Align.CENTER);

        cPaint = new Paint();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ss);
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
            setMeasuredDimension(mWidth, (int) (heightSpecSize * (page + 1) + getPaddingTop()));
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

    /**
     * 画颜色轴
     * @param canvas
     * @param rect
     * @param state
     */
    private void drawColor(Canvas canvas, Rect rect, ColorState state) {
        switch (state){
            case MOTION:
                cPaint.setColor(Color.GREEN);
                break;
            case NORMAL:
                cPaint.setColor(getResources().getColor(R.color.colorWhite));
                break;
            case SOUND:
                cPaint.setColor(getResources().getColor(R.color.colorBlue));
                break;
        }
        canvas.drawRect(rect, cPaint);
    }

    /**
     * 画水平方向的刻度尺
     * @param canvas
     */
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

    /**
     * 画竖直方向刻度尺
     * @param canvas
     */
    private void drawPortraitMark(Canvas canvas) {

        int totalCount = (int) (markTotalLength / step);

        int textLeft = getWidth() - DensityUtil.dp2px(getContext(), lMarkH) - DensityUtil.dp2px(getContext(), textSize);

        int colorPaddingDp = DensityUtil.dp2px(getContext(), colorPadding);
        int colorWidthDp = DensityUtil.dp2px(getContext(), colorWidth);
        int colorLeft = getWidth() - DensityUtil.dp2px(getContext(), lMarkH * 3.2f) - colorPaddingDp;

        Rect rect = new Rect();

        for (int count = 0; count <= totalCount; count++) {
//            float startY = getTop() + getPaddingTop() + count * step; //自上而下画

            float startY = markTotalLength - count * step + getPaddingTop();  //自下而上画

            rect.set(colorLeft, (int) startY, colorLeft + colorWidthDp, (int) (startY + step));
            cPaint.setColor(colorBackground);
            canvas.drawRect(rect, cPaint);

            rect.set(colorLeft + colorPaddingDp, (int) startY, colorLeft + colorWidthDp - colorPaddingDp, (int) (startY + step));
            if (count % 10 == 0) {
                canvas.drawLine(getWidth(), startY, getWidth() - DensityUtil.dp2px(getContext(), lMarkH), startY,  lMPaint);
                canvas.drawText("" + (/*24 - */count / 10), textLeft, startY + DensityUtil.dp2px(getContext(), textSize) / 3, tMPaint);
                drawColor(canvas, rect, ColorState.NORMAL);
                if (count % 20 == 0) {
                    canvas.drawBitmap(bitmap, getWidth() - DensityUtil.dp2px(getContext(), lMarkH * 13), (int) startY, cPaint);
                }
            } else {
                canvas.drawLine(getWidth(), startY, getWidth() - DensityUtil.dp2px(getContext(), sMarkH), startY,  sMPaint);
                if (count % 9 > 5){
                    drawColor(canvas, rect, ColorState.MOTION);
                    canvas.drawCircle(colorLeft + colorWidthDp / 2, startY, colorWidthDp / 2, cPaint);
                    canvas.drawCircle(colorLeft + colorWidthDp / 2, startY + step, colorWidthDp / 2, cPaint);
                } else if (count % 9 < 4 && count % 9 > 1){
                    drawColor(canvas, rect, ColorState.SOUND);
                    canvas.drawCircle(colorLeft + colorWidthDp / 2, startY, colorWidthDp / 2, cPaint);
                    canvas.drawCircle(colorLeft + colorWidthDp / 2, startY + step, colorWidthDp / 2, cPaint);
                } else {
                    drawColor(canvas, rect, ColorState.NORMAL);
                }
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

    public void setOrientationMode(OrientationMode orientationMode) {
        this.orientationMode = orientationMode;
    }

    public OrientationMode getOrientationMode() {
        return orientationMode;
    }
}
