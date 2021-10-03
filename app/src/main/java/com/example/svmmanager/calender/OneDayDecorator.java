package com.example.svmmanager.calender;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class OneDayDecorator implements DayViewDecorator {
    private CalendarDay date;

    public OneDayDecorator() {
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD)); //해당 날짜의 글씨체를 진하게
        view.addSpan(new RelativeSizeSpan(1.4f)); //해당 날짜의 글씨체 크기 사이즈
        view.addSpan(new ForegroundColorSpan(Color.GREEN)); // 해당 날짜의 글씨 색 설정
    }

    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
