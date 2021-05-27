package com.example.svmmanager.calender;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.example.svmmanager.R;
import com.example.svmmanager.fragment.BottomNavigationFragment3;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final Drawable drawable;
    //private TextView totalmoney;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates, BottomNavigationFragment3 context) {
        drawable = context.getResources().getDrawable(R.drawable.toptextview);
        //totalmoney = context.getResources().getText()
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable); //지정된 날자 뒤에 배경색을 지정
        view.addSpan(new DotSpan(5, color)); // 날자밑에 점
    }
}