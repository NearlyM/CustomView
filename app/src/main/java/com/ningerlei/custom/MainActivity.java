package com.ningerlei.custom;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ningerlei.timeline.constant.ColorType;
import com.ningerlei.timeline.entity.TimeData;
import com.ningerlei.timeline.widget.TimelineScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    TimelineScrollView timelineScrollView, timelineScrollViewLand;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scrollerview);
        timelineScrollView = (TimelineScrollView) findViewById(R.id.scrollview);
        timelineScrollViewLand = (TimelineScrollView) findViewById(R.id.action0);

        startTime = 1500279402000l;
        initData();
        timelineScrollView.setDataList(timeDatas);

        timelineScrollViewLand.setDataList(timeDatas);

        button = (Button) findViewById(R.id.top);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime -=  24 * 60 * 60 * 1000 * 1;
                initData();
                timelineScrollView.setDataList(timeDatas);
//                timelineScrollView.setTime(1500279402000l - 24 * 60 * 60 * 1000 * 1);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            timelineScrollViewLand.setVisibility(View.VISIBLE);
            timelineScrollView.setVisibility(View.GONE);
        }else {
            timelineScrollViewLand.setVisibility(View.GONE);
            timelineScrollView.setVisibility(View.VISIBLE);
        }
    }

    List<TimeData> timeDatas = new ArrayList<TimeData>();
    long startTime;
    private void initData() {

        for (int i = 0; i < 10; i++){
            TimeData timeData = new TimeData();
            if (i % 2 == 0){
                timeData.setColorType(ColorType.MOTION);
            }else if (i % 2 == 1){
                timeData.setColorType(ColorType.SOUND);
            }
            timeData.setStartTime(startTime);
            timeData.setOffset(1000 * 60 * 6 * 3);
            startTime = startTime - 2000000;
//            timeData.setStartTime(1499920654960l - 1000 * 60 * 60 * i);
//            timeData.setOffset(1000 * 60 * 60);
            timeDatas.add(timeData);
        }

    }
}
