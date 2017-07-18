package com.ningerlei.timeline.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ningerlei.timeline.R;
import com.ningerlei.timeline.callback.IScrollView;
import com.ningerlei.timeline.callback.OnControlListener;
import com.ningerlei.timeline.callback.OnScrollListener;
import com.ningerlei.timeline.callback.OnTimeChange;
import com.ningerlei.timeline.constant.OrientationMode;
import com.ningerlei.timeline.entity.TimeData;
import com.ningerlei.timeline.util.AnimationUtil;
import com.ningerlei.timeline.util.DensityUtil;
import com.ningerlei.timeline.util.ViewUtil;

import java.util.List;

/**
 * Description :
 * CreateTime : 2017/7/17 19:45
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/17 19:45
 * @ModifyDescription :
 */

public class TimelineScrollView extends RelativeLayout implements OnScrollListener, OnTimeChange {

//    HorizonTimeScrollView horizonTimeScrollView;
//    PortraitTimeScrollView portraitTimeScrollView;

    IScrollView iScrollView;
    OrientationMode orientationMode;

    TextView timeView;
    TimePointer timePointer;

    SwipeRefreshLayout swipeRefreshLayout;

    public TimelineScrollView(Context context) {
        this(context, null);
    }

    public TimelineScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimelineScrollView);
        orientationMode = typedArray.getInt(R.styleable.TimelineScrollView_orientate, 0) == 0 ? OrientationMode.HORIZONTAL : OrientationMode.VERTICAL;
        typedArray.recycle();

        initView();
    }

    private void initView(){
        if (orientationMode == OrientationMode.HORIZONTAL) {
            initHorizontalView();
        } else {
            initVerticalView();
        }

        iScrollView.setOnScrollListener(this);
        iScrollView.setOnTimeChage(this);
        iScrollView.startScroller();
    }

    private void initVerticalView() {
        /****************************************上拉加载View**********************************************************/
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(getContext(), 50));
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams2);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setVisibility(GONE);
        ViewUtil.setId(linearLayout);

        ViewGroup.LayoutParams layoutParams3 = new ViewGroup.LayoutParams(DensityUtil.dp2px(getContext(), 30), DensityUtil.dp2px(getContext(), 30));
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setLayoutParams(layoutParams3);

        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams4.rightMargin = DensityUtil.dp2px(getContext(), 10);
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(layoutParams4);

        linearLayout.addView(progressBar);
        linearLayout.addView(textView);
        /***********************************************************************************************************/


        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        iScrollView = new PortraitTimeScrollView(getContext());
        PortraitTimeScrollView portraitTimeScrollView = (PortraitTimeScrollView) this.iScrollView;
        portraitTimeScrollView.setLayoutParams(layoutParams);
        portraitTimeScrollView.setFillViewport(true);
        portraitTimeScrollView.setVerticalScrollBarEnabled(false);
        portraitTimeScrollView.setBackgroundColor(Color.WHITE);


        LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        swipeRefreshLayout = new SwipeRefreshLayout(getContext());
        layoutParams1.addRule(RelativeLayout.ABOVE, linearLayout.getId());
        swipeRefreshLayout.setLayoutParams(layoutParams1);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));

        swipeRefreshLayout.addView(portraitTimeScrollView);

        ViewGroup.LayoutParams layoutParams5 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(getContext(), 40));
        timePointer = new TimePointer(getContext());
        timePointer.setLayoutParams(layoutParams5);

        addView(swipeRefreshLayout);
        addView(linearLayout);
        addView(timePointer);

        portraitTimeScrollView.setOnRefreshChange(new PortraitTimeScrollView.OnRefreshStateListener() {

            @Override
            public void onLoadMore() {
                AnimationUtil.switchVideoMenuByAnimation(getContext(), linearLayout, true, AnimationUtil.SCREEN_BOTTOM);
            }

            @Override
            public void dismissLoad() {
                AnimationUtil.switchVideoMenuByAnimation(getContext(), linearLayout, false, AnimationUtil.SCREEN_BOTTOM);
            }
        });
    }

    private void initHorizontalView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        iScrollView = new HorizonTimeScrollView(getContext());
        HorizonTimeScrollView horizonTimeScrollView = (HorizonTimeScrollView) this.iScrollView;

        horizonTimeScrollView.setLayoutParams(layoutParams);
        horizonTimeScrollView.setFillViewport(true);
        horizonTimeScrollView.setHorizontalScrollBarEnabled(false);

        LayoutParams layoutParams1 = new LayoutParams(DensityUtil.dp2px(getContext(), 1), DensityUtil.dp2px(getContext(), 60));
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        View view = new View(getContext());

        ViewUtil.setId(view);

        view.setLayoutParams(layoutParams1);
        view.setBackgroundColor(getResources().getColor(R.color.colorBlue));

        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(getContext(), 20));
        layoutParams2.addRule(ABOVE, view.getId());
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        timeView = new TextView(getContext());
        timeView.setLayoutParams(layoutParams2);
        timeView.setTextColor(Color.WHITE);

        addView(horizonTimeScrollView);
        addView(view);
        addView(timeView);
    }

    public void setDataList(List<TimeData> timeDataList) {
        iScrollView.setDataList(timeDataList);
    }

    public void setOnControlListener(OnControlListener controllListener) {
        iScrollView.setOnControlListener(controllListener);
    }

    public void setTime(long timestamp){
        iScrollView.setTime(timestamp);
    }

    @Override
    public void onTopArrived() {

    }

    @Override
    public void onBottomArrived() {

    }

    @Override
    public void onScrollStateChanged(IScrollView view, int scrollState) {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }

    @Override
    public void timeChange(String time) {
        if (orientationMode == OrientationMode.HORIZONTAL) {
            timeView.setText(time);
        } else {
            timePointer.setText(time);
        }
    }
}
