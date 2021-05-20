package com.example.svmmanager.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.svmmanager.R;
import com.example.svmmanager.calender.EventDecorator;
import com.example.svmmanager.calender.OneDayDecorator;
import com.example.svmmanager.calender.SaturdayDecorator;
import com.example.svmmanager.calender.SundayDecorator;
import com.example.svmmanager.pay.PayAdapter;
import com.example.svmmanager.pay.PayData;
import com.example.svmmanager.piechart.CustomMarkerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import static android.graphics.Typeface.*;

//dd
public class BottomNavigationFragment3 extends Fragment {

    String time,kcal,menu;

    PieChart pieChart;

    //날짜 비교하기 위한 변수
    String firstcaldata, lastcaldata;

    int y1,m1,d1,y2,m2,d2;

    //달력 총금액 출력하기 위해 필요한 메소드
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;

    //화면 맨 상단에 당일 날짜 출력 변수
    TextView datenow;

    //일별, 월별, 년별, 기간별 텍스트 변수
    TextView today, month, year, calender;

    //기간별 입력칸 텍스트 변수
    TextView firstcal, lastcal;

    //매출현황 출력 변수
    TextView totalmoneyshow,totalnumbershow;

    //카테고리별 구매건수와 매출 금액변수
    TextView cocacolanum, cocacolamoney;
    TextView cidarnum, cidarmoney;
    TextView fantanum, fantamoney;
    TextView mountinduenum, mountinduemoney;

    //스피너로 인한 자판기 출력 변수
    Spinner vendingSpinner;

    //달력 버튼 클릭시 달력 보이게 하기 위한 변수
    MaterialCalendarView calendershow;
    Calendar cal = Calendar.getInstance();

    Calendar calendar1, calendar2;

    Date date1, date2;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //php데이터 출력 변수
    int count = 0; // 건수 출력
    int SVMtotalmoney=0; // 자판기 총 금액 변수
    int SVMtotalcount=0; // 자판기 총 건수 변수
    int cocatotalmoney = 0; // 코카콜라 총 금액 변수
    int cidartotalmoney = 0; // 사이다 총 금액 변수
    int fantatotalmoney = 0; // 환타 총 금액 변수
    int mountintotalmoney = 0; // 마운틴듀 총 금액 변수
    int cocacount=0; // 코카콜라 총 건수 변수
    int cidarcount=0; // 사이다 총 건수 변수
    int fantacount=0; // 환타 총 건수 변수
    int mountincount=0; // 마운틴 듀 총 건수 변수

    //php데이터 테스트
    private static String IP_ADDRESS = "211.211.158.42/yongrun/svm";
    private static String TAG = "phptest";

    private ArrayList<PayData> mArrayList;
    private PayAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mJsonString;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bottomnavigation3, container, false);

        datenow = (TextView) rootView.findViewById(R.id.datenow);

        today = (TextView) rootView.findViewById(R.id.today);
        month = (TextView) rootView.findViewById(R.id.month);
        year = (TextView) rootView.findViewById(R.id.year);
        calender = (TextView) rootView.findViewById(R.id.calender);

        firstcal = (TextView) rootView.findViewById(R.id.firstcal);
        lastcal = (TextView) rootView.findViewById(R.id.lastcal);

        totalmoneyshow = (TextView) rootView.findViewById(R.id.totalmoneyshow);
        totalnumbershow = (TextView) rootView.findViewById(R.id.totalnumbershow);

        cocacolanum = (TextView) rootView.findViewById(R.id.cocacolanum);
        cocacolamoney = (TextView) rootView.findViewById(R.id.cocacolamoney);
        cidarmoney = (TextView) rootView.findViewById(R.id.cidarmoney);
        cidarnum = (TextView) rootView.findViewById(R.id.cidarnum);
        fantanum = (TextView) rootView.findViewById(R.id.fantanum);
        fantamoney = (TextView) rootView.findViewById(R.id.fantamoney);
        mountinduenum = (TextView) rootView.findViewById(R.id.mountinduenum);
        mountinduemoney = (TextView) rootView.findViewById(R.id.mountinduemoney);


//-----------PHP부분-----------------------------------------------------------------------------------------------------------------


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mArrayList = new ArrayList<>();

        mAdapter = new PayAdapter(getActivity(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mArrayList.clear();
        mAdapter.notifyDataSetChanged();

        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.detach(this).attach(this).commit();



//-----------달력부분-----------------------------------------------------------------------------------------------------------------
        calendershow = (MaterialCalendarView) rootView.findViewById(R.id.calendershow);

        calendershow.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(1970, 0, 1))
                .setMaximumDate(CalendarDay.from(2099, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendershow.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        //오늘 날짜 불러오기 위한 변수
        String[] result = {String.valueOf(cal.get(Calendar.YEAR))+","+String.valueOf((cal.get(Calendar.MONTH)+1))+","+String.valueOf(cal.get(Calendar.DATE)) };

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        calendershow.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year + "-" + Month + "-" + Day;

                Log.i("shot_Day test", shot_Day + "");
                calendershow.clearSelection();

                Toast.makeText(getActivity(), shot_Day , Toast.LENGTH_SHORT).show();
            }
        });
//----------------------------------------------------------------------------------------------------------------------------


        //스피너 리스너 토스트 메시지 출력
        vendingSpinner = (Spinner) rootView.findViewById(R.id.vendingSpinner);
        final String[] data = getResources().getStringArray(R.array.vending);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendingSpinner.setAdapter(adapter);
        //스피너 특접값으로 초기값 고정하고 싶다면
        vendingSpinner.setSelection(0);

        //vendingSpinner.getSelectedItem().toString(); //스피너 선택값 가져오는 방법

        vendingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),data[i],Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

//----------------------------------------------------------------------------------------------------------------------------

        //요일별 출력 하는 로직
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        String day = " ";
        switch (dayofweek){
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }

        Date firstnowdate = null;
        try {
            firstnowdate = dateFormat.parse(cal.get(Calendar.YEAR)+"-"+ (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //오늘 날짜 출력
        cal = Calendar.getInstance();
        datenow.setText(cal.get(Calendar.YEAR)+"년 "+ (cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DATE) + "일"+ "("+day+")");
        firstcal.setText(dateFormat.format(firstnowdate));
        lastcal.setText(dateFormat.format(firstnowdate));

//----------------------------------------------------------------------------------------------------------------------------

        //데이트 피커를 활용한 날짜 설정
        firstcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog lastdatePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                                // Date Picker에서 선택한 날짜를 TextView에 설정
                                firstcaldata = yy+"-"+(mm+1)+"-"+dd;
                                y1=yy;
                                m1=mm+1;
                                d1=dd;

                                Log.i("last datepicker",  "last datepicker");


                                try {
                                    date1 = dateFormat.parse(y1+"-" + m1+"-"+d1);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.YEAR, y1);
                                calendar1.set(Calendar.MONTH, m1);
                                calendar1.set(Calendar.DAY_OF_MONTH, d1);

                                lastcal.setText(dateFormat.format(date1));
                                //firstcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                Log.i("Year test",  calendar1+ " ### " +calendar2);

                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);

                                    if (FirstDate.compareTo(SecondDate)>0){
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    long calDate = FirstDate.getTime() - SecondDate.getTime();

                                    // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                                    // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                                    long calDateDays = calDate / ( 24*60*60*1000);

                                    calDateDays = Math.abs(calDateDays);

                                    System.out.println("두 날짜의 날짜 차이: "+calDateDays);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DATE)
                );

                DatePickerDialog firstdatePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                                // Date Picker에서 선택한 날짜를 TextView에 설정
                                lastcaldata = yy+"-"+(mm+1)+"-"+dd;
                                y2=yy;
                                m2=mm+1;
                                d2=dd;

                                Log.i("first datepicker",  "first datepicker");


                                try {
                                    date2 = dateFormat.parse(y2+"-" + m2 +"-"+d2);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.YEAR, y2);
                                calendar2.set(Calendar.MONTH, m2);
                                calendar2.set(Calendar.DAY_OF_MONTH, d2);

                                firstcal.setText(dateFormat.format(date2));
                                //lastcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));


                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );

                cal=Calendar.getInstance();
                //cal = cal.get(Calendar.YEAR)+" - "+ (cal.get(Calendar.MONTH)+1) + " - " + cal.get(Calendar.DATE);
                //cal.set(cal.get(Calendar.YEAR)+" - "+ (cal.get(Calendar.MONTH)+1) + " - " + cal.get(Calendar.DATE));
                lastdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                firstdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                lastdatePickerDialog.show();
                firstdatePickerDialog.show();




            }
        });


        lastcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog afterdatePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                                // Date Picker에서 선택한 날짜를 TextView에 설정
                                lastcaldata = yy+"-"+(mm+1)+"-"+dd;
                                y2=yy;
                                m2=mm+1;
                                d2=dd;


                                Log.i("first datepicker",  "first datepicker");


                                try {
                                    date2 = dateFormat.parse(y2+"-" + m2 +"-"+d2);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                                calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.YEAR, y2);
                                calendar2.set(Calendar.MONTH, m2);
                                calendar2.set(Calendar.DAY_OF_MONTH, d2);

                                lastcal.setText(dateFormat.format(date2));
                                //lastcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));



                                Log.i("Year test",  calendar1+ " ### " +calendar2);

                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);

                                    if (FirstDate.compareTo(SecondDate)>0){
                                        lastcal.setText(firstcal.getText());
                                        firstcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }

                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    long calDate = FirstDate.getTime() - SecondDate.getTime();

                                    // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                                    // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                                    long calDateDays = calDate / ( 24*60*60*1000);

                                    calDateDays = Math.abs(calDateDays);

                                    System.out.println("두 날짜의 날짜 차이: "+calDateDays);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );



                cal=Calendar.getInstance();
                afterdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                afterdatePickerDialog.show();


            }
        });


        //매출관리

        //원형서킷 출력

        pieChart = (PieChart) rootView.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        //pieChart.invalidate(); //차드 새로고침 메소드

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(25f,"코카콜라"));
        yValues.add(new PieEntry(25f,"사이다"));
        yValues.add(new PieEntry(25f,"환타"));
        yValues.add(new PieEntry(25f,"마운틴 듀"));

        Description description = new Description();
        description.setText("카테고리 별 분석"); //라벨
        description.setTextSize(18);
        description.setTypeface(Typeface.defaultFromStyle(BOLD));
        description.setTextColor(Color.parseColor("#465088"));
        description.setPosition(500,90);
        pieChart.setDescription(description);


        pieChart.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic);
        //pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션


        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueTextColor(Color.parseColor("#465088"));
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS); //고정색깔 나오게 하기

        //랜덤색 나오게 하는 로직
        Random random = new Random();
        int color[] = new int[40];
        for(int i=0;i<40;i++){
            color[i] = Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        }

        //dataSet.setColors(ColorTemplate.createColors(color)); //랜덤색깔 나오게 하기

        PieData piedata = new PieData((dataSet));
        piedata.setValueTextSize(15f);
        piedata.setValueTextColor(Color.parseColor("#465088"));

        pieChart.setData(piedata);

        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout); //마커 나오게 하는 코드
        pieChart.setMarkerView(mv);


        //카테고리별 음료수 출력

        //일별, 월별, 년별, 기간별, 달력 버튼 클릭시 나오게하는 이벤트
        today.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");

               //today.setBackgroundColor(getContext().getResources().getColor(R.drawable.todaytextview2));

                //클릭시 배경색 변경
               today.setBackgroundResource(R.drawable.todaytextview2);
               today.setTextColor(Color.parseColor("#465088"));
               today.setTypeface(null, BOLD);

               month.setBackgroundResource(R.drawable.toptextview);
               month.setTextColor(Color.parseColor("#9E9E9E"));
               month.setTypeface(null, NORMAL);

               year.setBackgroundResource(R.drawable.toptextview);
               year.setTextColor(Color.parseColor("#9E9E9E"));
               year.setTypeface(null, NORMAL);

               calender.setBackgroundResource(R.drawable.caltextview);
               calender.setTextColor(Color.parseColor("#9E9E9E"));
               calender.setTypeface(null, NORMAL);

               //날짜 변경
                try {
                    firstcal.setText(dateFormat.format(dateFormat.parse(cal.get(Calendar.YEAR)+"-" +(cal.get(Calendar.MONTH)+1)+ "-" + cal.get(Calendar.DATE))));
                    lastcal.setText(dateFormat.format(dateFormat.parse(cal.get(Calendar.YEAR)+"-" +(cal.get(Calendar.MONTH)+1)+ "-" + cal.get(Calendar.DATE))));
                    Toast.makeText(getActivity(), "오늘의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //firstcal.setText(String.format("%d - %d - %d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE)));

            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");

                //클릭시 배경색 변경
                month.setBackgroundResource(R.drawable.toptextview2);
                month.setTextColor(Color.parseColor("#465088"));
                month.setTypeface(null, BOLD);

                today.setBackgroundResource(R.drawable.todaytextview);
                today.setTextColor(Color.parseColor("#9E9E9E"));
                today.setTypeface(null, NORMAL);

                year.setBackgroundResource(R.drawable.toptextview);
                year.setTextColor(Color.parseColor("#9E9E9E"));
                year.setTypeface(null, NORMAL);

                calender.setBackgroundResource(R.drawable.caltextview);
                calender.setTextColor(Color.parseColor("#9E9E9E"));
                calender.setTypeface(null, NORMAL);

                //cal.get(Calendar.YEAR)+"년 "+ (cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DATE)
                //날짜 변경

                try {
                    firstcal.setText(dateFormat.format(dateFormat.parse(cal.get(Calendar.YEAR)+"-" +(cal.get(Calendar.MONTH)+1)+ "-" + "01")));
                    lastcal.setText(dateFormat.format(dateFormat.parse(cal.get(Calendar.YEAR)+"-" +(cal.get(Calendar.MONTH)+1)+ "-" + cal.get(Calendar.DATE))));
                    Toast.makeText(getActivity(), firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //firstcal.setText(String.format("%d - %d - %d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 01));

            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");

                //클릭시 배경색 변경
                year.setBackgroundResource(R.drawable.toptextview2);
                year.setTextColor(Color.parseColor("#465088"));
                year.setTypeface(null, BOLD);

                today.setBackgroundResource(R.drawable.todaytextview);
                today.setTextColor(Color.parseColor("#9E9E9E"));
                today.setTypeface(null, NORMAL);

                month.setBackgroundResource(R.drawable.toptextview);
                month.setTextColor(Color.parseColor("#9E9E9E"));
                month.setTypeface(null, NORMAL);

                calender.setBackgroundResource(R.drawable.caltextview);
                calender.setTextColor(Color.parseColor("#9E9E9E"));
                calender.setTypeface(null, NORMAL);


                try {
                    firstcal.setText(dateFormat.format(dateFormat.parse(cal.get(Calendar.YEAR)+"-" +"01"+ "-" + "01")));
                    lastcal.setText(dateFormat.format(dateFormat.parse(cal.get(Calendar.YEAR)+"-" +(cal.get(Calendar.MONTH)+1)+ "-" + cal.get(Calendar.DATE))));
                    Toast.makeText(getActivity(), firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //날짜 변경
                //firstcal.setText(String.format("%d - %d - %d", cal.get(Calendar.YEAR), 01, 01));

            }
        });


        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //캘린더뷰 출력하기 안보이는거 다시 보이게하기
                //calendershow.setVisibility(view.VISIBLE);
                if (calendershow.getVisibility() == view.GONE){
                    calendershow.setVisibility(view.VISIBLE);
                    //클릭시 배경색 변경
                    calender.setBackgroundResource(R.drawable.caltextview2);
                    calender.setTextColor(Color.parseColor("#465088"));
                    calender.setTypeface(null, BOLD);

                    year.setBackgroundResource(R.drawable.toptextview);
                    year.setTextColor(Color.parseColor("#9E9E9E"));
                    year.setTypeface(null, NORMAL);

                    today.setBackgroundResource(R.drawable.todaytextview);
                    today.setTextColor(Color.parseColor("#9E9E9E"));
                    today.setTypeface(null, NORMAL);

                    month.setBackgroundResource(R.drawable.toptextview);
                    month.setTextColor(Color.parseColor("#9E9E9E"));
                    month.setTypeface(null, NORMAL);
                    //transAnimation(true);
                }else{
                    calendershow.setVisibility(view.GONE);
                    //클릭시 배경색 변경
                    calender.setBackgroundResource(R.drawable.caltextview);
                    calender.setTextColor(Color.parseColor("#9E9E9E"));
                    calender.setTypeface(null, BOLD);

                    year.setBackgroundResource(R.drawable.toptextview);
                    year.setTextColor(Color.parseColor("#9E9E9E"));
                    year.setTypeface(null, NORMAL);

                    today.setBackgroundResource(R.drawable.todaytextview);
                    today.setTextColor(Color.parseColor("#9E9E9E"));
                    today.setTypeface(null, NORMAL);

                    month.setBackgroundResource(R.drawable.toptextview);
                    month.setTextColor(Color.parseColor("#9E9E9E"));
                    month.setTypeface(null, NORMAL);
                    //transAnimation(false);
                }



            }
        });



        return rootView;
    }




    private void transAnimation(boolean bool){
        AnimationSet aniInSet = new AnimationSet(true);
        AnimationSet aniOutSet = new AnimationSet(true);
        aniInSet.setInterpolator(new AccelerateInterpolator());
        Animation transInAni = new TranslateAnimation(0,0,100.0f,0);
        Animation transOutAni = new TranslateAnimation(0,0,0,100.0f);
        transInAni.setDuration(100);
        transOutAni.setDuration(100);
        aniInSet.addAnimation(transInAni);
        aniOutSet.addAnimation(transOutAni);
        if (bool) {
            calendershow.setAnimation(aniInSet);
            calendershow.setVisibility(View.VISIBLE);
        } else {
            calendershow.setAnimation(aniOutSet);
            calendershow.setVisibility(View.GONE);
        }
    }


//-------------------------------------달력 이벤트 글씨 점 찍는거---------------------------------------------------------------------------------------

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();


            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로 짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.length; i++) {


                //이부분에서 day를 선언하면 초기 값에 오늘 날짜 데이터 들어간다.
                //오늘 날짜 데이터를 첫 번째 인자로 넣기 때문에 데이터가 하나씩 밀려 마지막 데이터는 표시되지 않고, 오늘 날짜 데이터가 표시 됨.
                // day선언 주석처리

                //                CalendarDay day = CalendarDay.from(calendar);
                //                Log.e("데이터 확인","day"+day);
                String[] time = Time_Result[i].split(",");

                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                //선언문을 아래와 같은 위치에 선언
                //먼저 .set 으로 데이터를 설정한 다음 CalendarDay day = CalendarDay.from(calendar); 선언해주면 첫 번째 인자로 새로 정렬한 데이터를 넣어 줌.
                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);

            }


            return dates;
        }

        //----------------------------------------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
/*
            if (isFinishing()) {
                return;
            }
*/
            calendershow.addDecorator(new EventDecorator(Color.RED, calendarDays, BottomNavigationFragment3.this));
        }

    }
//----------------------------------------------------------------------------------------------------------------------------
private class GetData extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;
    String errorString = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        progressDialog.dismiss();

        Log.d(TAG, "response - " + result);

        if (result == null) {

        } else {

            mJsonString = result;
            showResult();

            count = 0; // 건수 출력
            SVMtotalmoney=0; // 자판기 총 금액 변수
            SVMtotalcount=0; // 자판기 총 건수 변수
            cocatotalmoney = 0; // 코카콜라 총 금액 변수
            cidartotalmoney = 0; // 사이다 총 금액 변수
            fantatotalmoney = 0; // 환타 총 금액 변수
            mountintotalmoney = 0; // 마운틴듀 총 금액 변수
            cocacount=0; // 코카콜라 총 건수 변수
            cidarcount=0; // 사이다 총 건수 변수
            fantacount=0; // 환타 총 건수 변수
            mountincount=0; // 마운틴 듀 총 건수 변수

        }
    }


    @Override
    protected String doInBackground(String... params) {

        String serverURL = params[0];
        String postParameters = params[1];
        Log.d(TAG, "response code - test");



        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString().trim();


        } catch (Exception e) {

            Log.d(TAG, "GetData : Error ", e);
            errorString = e.toString();

            return null;
        }

    }
}

    private void showResult() {

        String TAG_JSON = "TransactionDetails_DATA";
        String TAG_TD_TRCODE = "TD_TRCODE";
        String TAG_TD_TRDATE = "TD_TRDATE";
        String TAG_TD_VMCODE = "TD_VMCODE";
        String TAG_TD_DRCODE = "TD_DRCODE";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String TD_TRCODE = item.getString(TAG_TD_TRCODE);
                String TD_TRDATE = item.getString(TAG_TD_TRDATE);
                String TD_VMCODE = item.getString(TAG_TD_VMCODE);
                String TD_DRCODE = item.getString(TAG_TD_DRCODE);

                //TD_TRDATE.substring(0, 9);


                PayData personalData = new PayData();

                personalData.setTD_TRCODE(TD_TRCODE);
                personalData.setTD_TRDATE(TD_TRDATE);
                personalData.setTD_VMCODE(TD_VMCODE);
                personalData.setTD_DRCODE(TD_DRCODE);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();



                //오늘 매출
                /*
                    int SVMtotalmoney=0;
                    int SVMtotalcount=0;
                    int cocamoney = 0;
                    int cidarmoney = 0;
                    int fantamoney = 0;
                    int mountinmoney = 0;
                    int cocacount=0;
                    int cidarcount=0;
                    int fantacount=0;
                    int mountincount=0;
*/
                String date1 = String.valueOf(firstcal.getText());
                String date2 = String.valueOf(lastcal.getText());

                try {
                    Date FirDate = dateFormat.parse(date1);
                    Date SecDate = dateFormat.parse(date2);

                    Date tagetDate=dateFormat.parse(TD_TRDATE.substring(0,TD_TRDATE.indexOf(" ")));

                    if (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime()){
                        if(TD_DRCODE.equals("CocaCola")){
                            cocatotalmoney = cocatotalmoney+1200;
                            cocacount = cocacount +1;
                        }else if (TD_DRCODE.equals("Chilsung Cider")){
                            cidartotalmoney = cidartotalmoney+1100;
                            cidarcount = cidarcount +1;
                        }else if (TD_DRCODE.equals("Fanta Orange")){
                            fantatotalmoney = fantatotalmoney+1000;
                            fantacount = fantacount +1;
                        }else if (TD_DRCODE.equals("Mountain Dew")){
                            mountintotalmoney = mountintotalmoney+1500;
                            mountincount = mountincount +1;
                        }
                        SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + mountintotalmoney;
                        SVMtotalcount= fantacount + cocacount + cidarcount + mountincount;

                        totalmoneyshow.setText(SVMtotalmoney+"원");
                        totalnumbershow.setText(SVMtotalcount+"건");
                        cocacolamoney.setText(cocatotalmoney + "원");
                        cidarmoney.setText(cidartotalmoney + "원");
                        fantamoney.setText(fantatotalmoney + "원");
                        mountinduemoney.setText(mountintotalmoney + "원");

                        cocacolanum.setText(cocacount+"건");
                        cidarnum.setText(cidarcount+"건");
                        fantanum.setText(fantacount+"건");
                        mountinduenum.setText(mountincount+"건");
                    }else{
                        totalmoneyshow.setText(0+"원");
                        totalnumbershow.setText(0+"건");

                        cocacolamoney.setText(0 + "원");
                        cidarmoney.setText(0 + "원");
                        fantamoney.setText(0 + "원");
                        mountinduemoney.setText(0 + "원");

                        cocacolanum.setText(0+"건");
                        cidarnum.setText(0+"건");
                        fantanum.setText(0+"건");
                        mountinduenum.setText(0+"건");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }



/*
                    if(mArrayList.get(i).getTD_TRDATE().substring(0,TD_TRDATE.indexOf(" ")).equals(firstcal.getText()) && mArrayList.get(i).getTD_DRCODE().equals("CocaCola")){
                        cocatotalmoney = cocatotalmoney+1200;
                        cocacount = cocacount +1;
                    }else if (mArrayList.get(i).getTD_TRDATE().substring(0,TD_TRDATE.indexOf(" ")).equals(firstcal.getText()) && mArrayList.get(i).getTD_DRCODE().equals("Chilsung Cider")){
                        cidartotalmoney = cidartotalmoney+1100;
                        cidarcount = cidarcount +1;
                    }else if (mArrayList.get(i).getTD_TRDATE().substring(0,TD_TRDATE.indexOf(" ")).equals(firstcal.getText()) && mArrayList.get(i).getTD_DRCODE().equals("Chilsung Cider")){
                        fantatotalmoney = fantatotalmoney+1000;
                        fantacount = fantacount +1;
                    }else if (mArrayList.get(i).getTD_TRDATE().substring(0,TD_TRDATE.indexOf(" ")).equals(firstcal.getText()) && mArrayList.get(i).getTD_DRCODE().equals("Chilsung Cider")){
                        mountintotalmoney = mountintotalmoney+1500;
                        mountincount = mountincount +1;
                    }

                    SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + mountintotalmoney;
                    SVMtotalcount= fantacount + cocacount + cidarcount + mountincount;

*/
                    System.out.println();
                    System.out.println(i+" 번째 : "+SVMtotalmoney);
                    System.out.println(i+" 번째 : "+SVMtotalcount);



/*
                //출력 switch 문
                switch (mArrayList.get(i).getDRCode()) {
                    case "CocaCola":
                        mTextViewCocacolaPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewCocacolaStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Chilsung Cider":
                        mTextViewChilsungPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewChilsungStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Ssekssek":
                        mTextViewSsekssekPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewSsekssekStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Fanta Orange":
                        mTextViewFantaPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewFantaStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Mountain Dew":
                        mTextViewMountainPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewMountainStock.setText("수량: " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Galbae":
                        mTextViewGalbaePrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewGalbaeStock.setText("수량: " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    default:
                        break;


                }
*/

                Log.i("testtest",   TD_TRCODE +" / " +   TD_TRDATE.substring(0, TD_TRDATE.indexOf(" ")) +" / " + TD_VMCODE +" / " + TD_DRCODE);


            }
/*
            cocacolamoney.setText(cocatotalmoney);
            cidarmoney.setText(cidartotalmoney);
            fantamoney.setText(fantatotalmoney);
            mountinduemoney.setText(mountintotalmoney);
*/


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


//----------------------------------------------------------------------------------------------------------------------------


}