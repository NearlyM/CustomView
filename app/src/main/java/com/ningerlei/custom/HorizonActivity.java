package com.ningerlei.custom;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ningerlei.timeline.constant.ColorType;
import com.ningerlei.timeline.entity.TimeData;
import com.ningerlei.timeline.widget.TimelineScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * CreateTime : 2017/7/17 11:19
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/17 11:19
 * @ModifyDescription :
 */

public class HorizonActivity extends Activity {

    TimelineScrollView timelineScrollView;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizeon_scroll);

        startTime = 1500279402000l - 24 * 60 * 60 * 1000 * 1;
        initData();

        timelineScrollView = (TimelineScrollView) findViewById(R.id.action0);
        timelineScrollView.setDataList(timeDatas);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = startTime - 24 * 60 * 60 * 1000 * 1;
                initData();
                timelineScrollView.setTime(1500279402000l - 24 * 60 * 60 * 1000 * 1);
            }
        });
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
