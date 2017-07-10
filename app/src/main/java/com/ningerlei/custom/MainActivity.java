package com.ningerlei.custom;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ScrollView;

import com.ningerlei.custom.widget.TimePointer;
import com.ningerlei.custom.widget.TimeScrollView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    TimeScrollView timeScrollView;
    TimePointer timeHand;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main_scrollerview);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        timeScrollView = (TimeScrollView) findViewById(R.id.scrollview);
        timeHand = (TimePointer) findViewById(R.id.timehandle);
        timeScrollView.setOnScrollListener(new TimeScrollView.OnScrollListener() {
            @Override
            public void onTopArrived() {
                Log.d(TAG, "onTopArrived");

            }

            @Override
            public void onBottomArrived() {

            }

            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

            }
        });

        timeScrollView.setOnTimeChage(new TimeScrollView.OnTimeChange() {
            @Override
            public void timeChange(String time) {
                timeHand.setText(time);
            }
        });

        timeScrollView.setOnRefreshChange(new TimeScrollView.OnRefreshStateListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onRefreshStop() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        timeScrollView.startScroller();
    }
}
