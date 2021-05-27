package com.example.svmmanager.piechart;


import android.content.Context;
import android.widget.TextView;

import com.example.svmmanager.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //마커뷰에 보여질 텍스트 값을 지정해준다.
        tvContent.setText("" + e.getData()); // set the entry-value as the display text
    }


    public int getXOffset(float xpos) {
        // 마커 뷰가 보여질 x위치값을 리턴
        return -(getWidth() / 2);
    }


    public int getYOffset(float ypos) {
        // 마커 뷰가 보여질 y위치값을 리턴
        return -getHeight();
    }
}