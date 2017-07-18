package com.ningerlei.timeline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ningerlei.timeline.R;
import com.ningerlei.timeline.util.DensityUtil;

/**
 * Description :
 * CreateTime : 2017/7/6 19:30
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/6 19:30
 * @ModifyDescription :
 */

class TimePointer extends View {

    private Paint textPaint;
    private Paint linePaint;
    String text = "";
    private int mWidth = 0;
    private int mHeight = 0;

    public TimePointer(Context context) {
        this(context, null);
    }

    public TimePointer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.colorBlue));
        linePaint.setStrokeWidth(DensityUtil.dp2px(getContext(), 1));

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DensityUtil.dp2px(getContext(), 14));
        textPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), 2));
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
            setMeasuredDimension(mWidth, heightSpecSize + getPaddingTop());
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(0, getHeight() / 2, getWidth() - DensityUtil.dp2px(getContext(), 90), getHeight() / 2, linePaint);

        drawText(canvas);

        float width = textPaint.measureText(text);
        canvas.drawLine(getWidth() - DensityUtil.dp2px(getContext(), 90) + width, getHeight() / 2 , getWidth(), getHeight() / 2, linePaint);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(text, getWidth() - DensityUtil.dp2px(getContext(), 90), getHeight() / 2 + DensityUtil.dp2px(getContext(), 14) / 3, textPaint);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}
