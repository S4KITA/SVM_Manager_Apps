package com.example.svmmanager.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.example.svmmanager.R;
import com.example.svmmanager.calender.EventDecorator;
import com.example.svmmanager.calender.OneDayDecorator;
import com.example.svmmanager.calender.SaturdayDecorator;
import com.example.svmmanager.calender.SundayDecorator;
import com.example.svmmanager.pay.PayAdapter;
import com.example.svmmanager.pay.PayData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import static android.graphics.Typeface.*;

//dd
public class CalendarFragment extends Fragment {

    //자판기 음료 원형서킷으로 보여주기 위한 변수
    PieChart pieChart;
//
    //날짜 비교하기 위한 변수
    String firstcaldata, lastcaldata;

    //데이트 피커에서 날짜를 담기 위한 변수
    int y1,m1,d1,y2,m2,d2;

    //달력에 총금액 출력하기 위해 필요한 메소드
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

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
    TextView pepsinum, pepsimoney;

    //스피너로 인한 자판기 출력 변수
    Spinner vendingSpinner;


    //달력 버튼 클릭시 달력 보이게 하기 위한 변수
    CalendarView calendershow2;
    MaterialCalendarView calendershow;

    //날짜를 가져와서 초기화함
    Calendar cal = Calendar.getInstance();


    //날짜를 비교하기 위해 필요한 변수
    Calendar calendar1, calendar2;
    Date date1, date2;

    //날짜데이터를 담기위해 날짜데이터형식을 지정
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat moneyFormat = new DecimalFormat("###,###");

    //php데이터 출력 변수
    int count = 0; // 건수 출력
    int SVMtotalmoney=0; // 자판기 총 금액 변수
    int SVMtotalcount=0; // 자판기 총 건수 변수
    int cocatotalmoney = 0; // 코카콜라 총 금액 변수
    int cidartotalmoney = 0; // 스프라이트 총 금액 변수
    int fantatotalmoney = 0; // 환타 총 금액 변수
    int pepsitotalmoney = 0; // 펩시 총 금액 변수
    int cocacount=0; // 코카콜라 총 건수 변수
    int cidarcount=0; // 스프라이트 총 건수 변수
    int fantacount=0; // 환타 총 건수 변수
    int pepsicount=0; // 펩시 총 건수 변수

    //php데이터
    private static String IP_ADDRESS = "59.14.35.61/yongrun/svm"; //php파일 주소
    private static String TAG = "phptest"; //log test

    //php파일의 데이터를 담을 변수들들
    private ArrayList<PayData> mArrayList;
    private PayAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mJsonString;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //viewGroup변수를 통해서 프래그먼트에 레이아웃 배치
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        //오늘 날짜를 보여주게하는 변수
        datenow = (TextView) rootView.findViewById(R.id.datenow);

        //datelayout에 배치되는 변수들 오늘,월별,기간별,달력 textview
        today = (TextView) rootView.findViewById(R.id.today);
        month = (TextView) rootView.findViewById(R.id.month);
        year = (TextView) rootView.findViewById(R.id.year);
        calender = (TextView) rootView.findViewById(R.id.calender);

        //기간별 출력을 하기 위해 필요한 변수 누르면 데이트피커를 통해 보여주게함
        firstcal = (TextView) rootView.findViewById(R.id.firstcal);
        lastcal = (TextView) rootView.findViewById(R.id.lastcal);

        //총금액과 총건수를 보여주는 변수
        totalmoneyshow = (TextView) rootView.findViewById(R.id.totalmoneyshow);
        totalnumbershow = (TextView) rootView.findViewById(R.id.totalnumbershow);

        //카테고리별 금액과 건수 변수
        cocacolanum = (TextView) rootView.findViewById(R.id.cocacolanum);
        cocacolamoney = (TextView) rootView.findViewById(R.id.cocacolamoney);
        cidarmoney = (TextView) rootView.findViewById(R.id.cidarmoney);
        cidarnum = (TextView) rootView.findViewById(R.id.cidarnum);
        fantanum = (TextView) rootView.findViewById(R.id.fantanum);
        fantamoney = (TextView) rootView.findViewById(R.id.fantamoney);
        pepsinum = (TextView) rootView.findViewById(R.id.pepsinum);
        pepsimoney = (TextView) rootView.findViewById(R.id.pepsimoney);


//-----------PHP부분-----------------------------------------------------------------------------------------------------------------

        //데이터를 받아오기 위해 리사이클러뷰 선언
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mRecyclerView);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //php데이터들을 배열을 통해 받아오기위해 ArrayList를 선언
        mArrayList = new ArrayList<>();

        //PayAdapter를 불러와서 php데이터가 담겨있는 ArrayList를 정의한다.
        mAdapter = new PayAdapter(getActivity(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mArrayList.clear();
        mAdapter.notifyDataSetChanged();

        //GetData클래스를 생성하여 php파일의 주소로 연동을 한다.
        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.detach(this).attach(this).commit();


        //calendershow2 = (CalendarView) rootView.findViewById(R.id.calenershow2);


//-----------달력부분-----------------------------------------------------------------------------------------------------------------
        calendershow = (MaterialCalendarView) rootView.findViewById(R.id.calendershow);

        //캘린더뷰를 디자인하는 부분
        calendershow.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(1970, 0, 1))
                .setMaximumDate(CalendarDay.from(2099, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        //캘린더 뷰를 이벤트를 주는 부분 SundayDecorator(), SaturdayDecorator(), oneDayDecorator 메소드를 불러와서 토요일 일요일 해당요일 해당하는 이벤트를 정의한다.
        calendershow.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        //오늘 날짜 불러오기 위한 변수
        String[] result = {String.valueOf(cal.get(Calendar.YEAR))+","+String.valueOf((cal.get(Calendar.MONTH)+1))+","+String.valueOf(cal.get(Calendar.DATE)) };

        //ApiSimulator클래스를 불러와 result변수에 담겨있는 요일에 해당하는 이벤트를 발생시킨다.
        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        //달력의 날짜를 클릭하면 나오는 이벤트
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

                //해당날짜를 클릭하면 클릭한 날짜의 년,월,일의 메시지를 토스트 메시지로 출력을 한다.
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

        //스피너를 선택을하게 되면 발생하는 이벤트 리스너
        vendingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),data[i]+"의 매출을 보여줍니다.",Toast.LENGTH_SHORT).show();
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                //php파일을 불러옴
                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");

                System.out.println(count);
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

        //dateFormat형식으로 오늘날짜를 지정
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

//-----------데이트 피커를 활용한 날짜 설정-----------------------------------------------------------------------------------------------------------------

        //firstcal을 클릭시 발생하는 이벤트 리스너 클릭하면 데이트피커를 보여줌
        firstcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "시작 날짜를 입력하세요", Toast.LENGTH_SHORT).show();

                //lastcal에 설정될 datepicker리스너 두번째 기간별 선택하기 위한 리스너
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


                                //dateFormat형식으로 지정하기 위해 날짜를 dateFormat에 담아놈
                                try {
                                    date1 = dateFormat.parse(y1+"-" + m1+"-"+d1);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //지정한 날짜를 calendar1에 담아놓음
                                calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.YEAR, y1);
                                calendar1.set(Calendar.MONTH, m1);
                                calendar1.set(Calendar.DAY_OF_MONTH, d1);

                                //지정한 날짜를 lastcal의 텍스트로 변경을함
                                lastcal.setText(dateFormat.format(date1));
                                //firstcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                year.setBackgroundResource(R.drawable.toptextview);
                                year.setTextColor(Color.parseColor("#9E9E9E"));
                                year.setTypeface(null, NORMAL);

                                today.setBackgroundResource(R.drawable.todaytextview);
                                today.setTextColor(Color.parseColor("#9E9E9E"));
                                today.setTypeface(null, NORMAL);

                                month.setBackgroundResource(R.drawable.toptextview);
                                month.setTextColor(Color.parseColor("#9E9E9E"));
                                month.setTypeface(null, NORMAL);

                                calender.setBackgroundResource(R.drawable.caltextview);
                                calender.setTextColor(Color.parseColor("#9E9E9E"));
                                calender.setTypeface(null, NORMAL);


                                Log.i("Year test",  calendar1+ " ### " +calendar2);

                                //만약 달력의 기간이 오류가 나거나 앞뒤 기간을 다시 확인하기 위해 필요한 로직들
                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);
                                    //compareTo()메소드를 통해 날짜 비교를 구현
                                    if (FirstDate.compareTo(SecondDate)>0){ //fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하기 위한 변수
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

                //firstcal에 설정될 datepicker리스너 첫번째 기간별 선택하기 위한 리스너
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

                                //dateFormat형식으로 지정하기 위해 날짜를 dateFormat에 담아놈
                                try {
                                    date2 = dateFormat.parse(y2+"-" + m2 +"-"+d2);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.YEAR, y2);
                                calendar2.set(Calendar.MONTH, m2);
                                calendar2.set(Calendar.DAY_OF_MONTH, d2);

                                Toast.makeText(getContext(), "끝 날짜를 입력하세요", Toast.LENGTH_SHORT).show();

                                //지정한 날짜를 firstcal의 텍스트로 변경을함
                                firstcal.setText(dateFormat.format(date2));
                                //lastcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                year.setBackgroundResource(R.drawable.toptextview);
                                year.setTextColor(Color.parseColor("#9E9E9E"));
                                year.setTypeface(null, NORMAL);

                                today.setBackgroundResource(R.drawable.todaytextview);
                                today.setTextColor(Color.parseColor("#9E9E9E"));
                                today.setTypeface(null, NORMAL);

                                month.setBackgroundResource(R.drawable.toptextview);
                                month.setTextColor(Color.parseColor("#9E9E9E"));
                                month.setTypeface(null, NORMAL);

                                calender.setBackgroundResource(R.drawable.caltextview);
                                calender.setTextColor(Color.parseColor("#9E9E9E"));
                                calender.setTypeface(null, NORMAL);

                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );

                cal=Calendar.getInstance();
                //cal = cal.get(Calendar.YEAR)+" - "+ (cal.get(Calendar.MONTH)+1) + " - " + cal.get(Calendar.DATE);
                //cal.set(cal.get(Calendar.YEAR)+" - "+ (cal.get(Calendar.MONTH)+1) + " - " + cal.get(Calendar.DATE));

                //오늘날짜보다 뒤의 날짜는 비활성화함
                lastdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                firstdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                //각 정의한 데이트다이얼로그를 보여줌
                lastdatePickerDialog.show();
                firstdatePickerDialog.show();

                firstdatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),
                        (dialog, which) -> {
                    if ( which == DialogInterface.BUTTON_NEGATIVE) {
                        Toast.makeText(
                                getContext(),
                                "끝 날짜를 입력하여 주십시오",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                        });

                lastdatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),
                        (dialog, which) -> {
                            if ( which == DialogInterface.BUTTON_NEGATIVE) {
                                Toast.makeText(
                                        getContext(),
                                        "날짜를 취소하였습니다. 기존의 검색되어있던 날짜로 출력합니다.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                //만약 달력의 기간이 오류가 나거나 앞뒤 기간을 다시 확인하기 위해 필요한 로직들
                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);
                                    //compareTo()메소드를 통해 날짜 비교를 구현
                                    if (FirstDate.compareTo(SecondDate)>0){ //fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하기 위한 변수
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
                        });

            }
        });


        //lastcal을 클릭시 보여주는 이벤트 리스너 클릭하면 데이트 피커를 보여줌
        lastcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "끝 날짜를 입력하세요", Toast.LENGTH_SHORT).show();

                //lastcal을 변경하기 위한, 날짜를 지정하기 위한 데이트피커를 정의함
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

                                //dateFormat형식으로 지정하기 위해 날짜를 dateFormat에 담아놈
                                try {
                                    date2 = dateFormat.parse(y2+"-" + m2 +"-"+d2);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                                calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.YEAR, y2);
                                calendar2.set(Calendar.MONTH, m2);
                                calendar2.set(Calendar.DAY_OF_MONTH, d2);

                                //지정한 날짜를 lastcal 텍스트로 변경을함
                                lastcal.setText(dateFormat.format(date2));
                                //lastcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                year.setBackgroundResource(R.drawable.toptextview);
                                year.setTextColor(Color.parseColor("#9E9E9E"));
                                year.setTypeface(null, NORMAL);

                                today.setBackgroundResource(R.drawable.todaytextview);
                                today.setTextColor(Color.parseColor("#9E9E9E"));
                                today.setTypeface(null, NORMAL);

                                month.setBackgroundResource(R.drawable.toptextview);
                                month.setTextColor(Color.parseColor("#9E9E9E"));
                                month.setTypeface(null, NORMAL);

                                calender.setBackgroundResource(R.drawable.caltextview);
                                calender.setTextColor(Color.parseColor("#9E9E9E"));
                                calender.setTypeface(null, NORMAL);


                                Log.i("Year test",  calendar1+ " ### " +calendar2);

                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);

                                    //compareTo()메소드를 통해 날짜를 비교함
                                    if (FirstDate.compareTo(SecondDate)>0){//fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        lastcal.setText(firstcal.getText());
                                        firstcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }

                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    //날짜를 선택 완료하면 다시 php파일을 불러옴
                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하는 변수
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
                ///오늘날짜 뒤의 날짜는 비활성화함
                afterdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                afterdatePickerDialog.show();// 다이얼로그를 불러옴

                afterdatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),
                        (dialog, which) -> {
                            if ( which == DialogInterface.BUTTON_NEGATIVE) {
                                Toast.makeText(
                                        getContext(),
                                        "날짜를 취소하였습니다. 기존의 검색되어있던 날짜로 출력합니다.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                //만약 달력의 기간이 오류가 나거나 앞뒤 기간을 다시 확인하기 위해 필요한 로직들
                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);
                                    //compareTo()메소드를 통해 날짜 비교를 구현
                                    if (FirstDate.compareTo(SecondDate)>0){ //fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하기 위한 변수
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
                        });

            }
        });

        firstcal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(getContext(), "시작 날짜를 입력하세요", Toast.LENGTH_SHORT).show();

                //lastcal에 설정될 datepicker리스너 두번째 기간별 선택하기 위한 리스너
                DatePickerDialog lastdatePickerDialog = new DatePickerDialog(
                        getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                                // Date Picker에서 선택한 날짜를 TextView에 설정
                                firstcaldata = yy+"-"+(mm+1)+"-"+dd;
                                y1=yy;
                                m1=mm+1;
                                d1=dd;

                                Log.i("last datepicker",  "last datepicker");


                                //dateFormat형식으로 지정하기 위해 날짜를 dateFormat에 담아놈
                                try {
                                    date1 = dateFormat.parse(y1+"-" + m1+"-"+d1);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //지정한 날짜를 calendar1에 담아놓음
                                calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.YEAR, y1);
                                calendar1.set(Calendar.MONTH, m1);
                                calendar1.set(Calendar.DAY_OF_MONTH, d1);

                                //지정한 날짜를 lastcal의 텍스트로 변경을함
                                lastcal.setText(dateFormat.format(date1));
                                //firstcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                year.setBackgroundResource(R.drawable.toptextview);
                                year.setTextColor(Color.parseColor("#9E9E9E"));
                                year.setTypeface(null, NORMAL);

                                today.setBackgroundResource(R.drawable.todaytextview);
                                today.setTextColor(Color.parseColor("#9E9E9E"));
                                today.setTypeface(null, NORMAL);

                                month.setBackgroundResource(R.drawable.toptextview);
                                month.setTextColor(Color.parseColor("#9E9E9E"));
                                month.setTypeface(null, NORMAL);

                                calender.setBackgroundResource(R.drawable.caltextview);
                                calender.setTextColor(Color.parseColor("#9E9E9E"));
                                calender.setTypeface(null, NORMAL);


                                Log.i("Year test",  calendar1+ " ### " +calendar2);

                                //만약 달력의 기간이 오류가 나거나 앞뒤 기간을 다시 확인하기 위해 필요한 로직들
                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);
                                    //compareTo()메소드를 통해 날짜 비교를 구현
                                    if (FirstDate.compareTo(SecondDate)>0){ //fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하기 위한 변수
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

                //firstcal에 설정될 datepicker리스너 첫번째 기간별 선택하기 위한 리스너
                DatePickerDialog firstdatePickerDialog = new DatePickerDialog(
                        getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                                // Date Picker에서 선택한 날짜를 TextView에 설정
                                lastcaldata = yy+"-"+(mm+1)+"-"+dd;
                                y2=yy;
                                m2=mm+1;
                                d2=dd;

                                Log.i("first datepicker",  "first datepicker");

                                //dateFormat형식으로 지정하기 위해 날짜를 dateFormat에 담아놈
                                try {
                                    date2 = dateFormat.parse(y2+"-" + m2 +"-"+d2);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.YEAR, y2);
                                calendar2.set(Calendar.MONTH, m2);
                                calendar2.set(Calendar.DAY_OF_MONTH, d2);

                                Toast.makeText(getContext(), "끝 날짜를 입력하세요", Toast.LENGTH_SHORT).show();

                                //지정한 날짜를 firstcal의 텍스트로 변경을함
                                firstcal.setText(dateFormat.format(date2));
                                //lastcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                year.setBackgroundResource(R.drawable.toptextview);
                                year.setTextColor(Color.parseColor("#9E9E9E"));
                                year.setTypeface(null, NORMAL);

                                today.setBackgroundResource(R.drawable.todaytextview);
                                today.setTextColor(Color.parseColor("#9E9E9E"));
                                today.setTypeface(null, NORMAL);

                                month.setBackgroundResource(R.drawable.toptextview);
                                month.setTextColor(Color.parseColor("#9E9E9E"));
                                month.setTypeface(null, NORMAL);

                                calender.setBackgroundResource(R.drawable.caltextview);
                                calender.setTextColor(Color.parseColor("#9E9E9E"));
                                calender.setTypeface(null, NORMAL);

                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );

                cal=Calendar.getInstance();
                //cal = cal.get(Calendar.YEAR)+" - "+ (cal.get(Calendar.MONTH)+1) + " - " + cal.get(Calendar.DATE);
                //cal.set(cal.get(Calendar.YEAR)+" - "+ (cal.get(Calendar.MONTH)+1) + " - " + cal.get(Calendar.DATE));

                //오늘날짜보다 뒤의 날짜는 비활성화함
                lastdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                firstdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                //각 정의한 데이트다이얼로그를 보여줌

                lastdatePickerDialog.getDatePicker().setCalendarViewShown(false);
                lastdatePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                firstdatePickerDialog.getDatePicker().setCalendarViewShown(false);
                firstdatePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                firstdatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),
                        (dialog, which) -> {
                            if ( which == DialogInterface.BUTTON_NEGATIVE) {
                                Toast.makeText(
                                        getContext(),
                                        "끝 날짜를 입력하여 주십시오",
                                        Toast.LENGTH_SHORT
                                ).show();

                            }
                        });

                lastdatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),
                        (dialog, which) -> {
                            if ( which == DialogInterface.BUTTON_NEGATIVE) {
                                Toast.makeText(
                                        getContext(),
                                        "두번째 취소 버튼 클릭",
                                        Toast.LENGTH_SHORT
                                ).show();

                                //만약 달력의 기간이 오류가 나거나 앞뒤 기간을 다시 확인하기 위해 필요한 로직들
                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);
                                    //compareTo()메소드를 통해 날짜 비교를 구현
                                    if (FirstDate.compareTo(SecondDate)>0){ //fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하기 위한 변수
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
                        });



                lastdatePickerDialog.show();
                firstdatePickerDialog.show();




                return true;
            }
        });

        lastcal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(getContext(), "끝 날짜를 입력하세요", Toast.LENGTH_SHORT).show();

                //lastcal을 변경하기 위한, 날짜를 지정하기 위한 데이트피커를 정의함
                DatePickerDialog afterdatePickerDialog = new DatePickerDialog(
                        getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                                // Date Picker에서 선택한 날짜를 TextView에 설정
                                lastcaldata = yy+"-"+(mm+1)+"-"+dd;
                                y2=yy;
                                m2=mm+1;
                                d2=dd;


                                Log.i("first datepicker",  "first datepicker");

                                //dateFormat형식으로 지정하기 위해 날짜를 dateFormat에 담아놈
                                try {
                                    date2 = dateFormat.parse(y2+"-" + m2 +"-"+d2);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                                calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.YEAR, y2);
                                calendar2.set(Calendar.MONTH, m2);
                                calendar2.set(Calendar.DAY_OF_MONTH, d2);


                                //지정한 날짜를 lastcal 텍스트로 변경을함
                                lastcal.setText(dateFormat.format(date2));
                                //lastcal.setText(String.format("%d - %d - %d", yy,mm+1,dd));

                                year.setBackgroundResource(R.drawable.toptextview);
                                year.setTextColor(Color.parseColor("#9E9E9E"));
                                year.setTypeface(null, NORMAL);

                                today.setBackgroundResource(R.drawable.todaytextview);
                                today.setTextColor(Color.parseColor("#9E9E9E"));
                                today.setTypeface(null, NORMAL);

                                month.setBackgroundResource(R.drawable.toptextview);
                                month.setTextColor(Color.parseColor("#9E9E9E"));
                                month.setTypeface(null, NORMAL);

                                calender.setBackgroundResource(R.drawable.caltextview);
                                calender.setTextColor(Color.parseColor("#9E9E9E"));
                                calender.setTypeface(null, NORMAL);


                                Log.i("Year test",  calendar1+ " ### " +calendar2);

                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);

                                    //compareTo()메소드를 통해 날짜를 비교함
                                    if (FirstDate.compareTo(SecondDate)>0){//fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        lastcal.setText(firstcal.getText());
                                        firstcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }

                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    //날짜를 선택 완료하면 다시 php파일을 불러옴
                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하는 변수
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
                //오늘날짜 뒤의 날짜는 비활성화함
                afterdatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                afterdatePickerDialog.getDatePicker().setCalendarViewShown(false);
                afterdatePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                afterdatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),
                        (dialog, which) -> {
                            if ( which == DialogInterface.BUTTON_NEGATIVE) {
                                Toast.makeText(
                                        getContext(),
                                        "두번째 취소 버튼 클릭",
                                        Toast.LENGTH_SHORT
                                ).show();

                                //만약 달력의 기간이 오류가 나거나 앞뒤 기간을 다시 확인하기 위해 필요한 로직들
                                String exdate1 = String.valueOf(firstcal.getText());
                                String exdate2 = String.valueOf(lastcal.getText());
                                Log.i("Year test",  exdate1+ " ### " +exdate2);
                                //int result1 = calendar1.compareTo(calendar2);
                                try {
                                    Date FirstDate = dateFormat.parse(exdate1);
                                    Date SecondDate = dateFormat.parse(exdate2);
                                    Log.i("test",  FirstDate+ " ### " +SecondDate);
                                    //compareTo()메소드를 통해 날짜 비교를 구현
                                    if (FirstDate.compareTo(SecondDate)>0){ //fisetDate가 SecondDate보다 값이 크면 1을 반환하게됨
                                        //첫번째 날짜 값이 두번째 날짜값보다 크기에, 오류 토스트메시지를 출력하며 두개의 날짜 값을 다시 변경하여 조정한다.
                                        firstcal.setText(dateFormat.format(date1));
                                        lastcal.setText(dateFormat.format(date2));
                                        Toast.makeText(getActivity(), "달력의 앞뒤 기간을 다시 확인해주십시오.\n"
                                                +firstcal.getText()+"~"+lastcal.getText()+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if (FirstDate.compareTo(SecondDate)<0){ //fisetDate가 SecondDate보다 값이 작으면 -1을 반환하게됨 정상적으로 출력
                                        Toast.makeText(getActivity(), dateFormat.format(FirstDate)+"~"+dateFormat.format(SecondDate)+"의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }else if(FirstDate.compareTo(SecondDate)==0){ //fisetDate가 SecondDate보다 값이 같으면 오늘 날짜를 출력하는것
                                        Toast.makeText(getActivity(), "오늘"+dateFormat.format(FirstDate)+"일의 매출을 확인하십시오." , Toast.LENGTH_SHORT).show();
                                    }
                                    mArrayList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    GetData task = new GetData();
                                    task.execute("http://" + IP_ADDRESS + "/TransactionDetails.php", "");


                                    System.out.println(count);

                                    //두 날짜간의 날짜 차이를 출력하기 위한 변수
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
                        });

                afterdatePickerDialog.show();// 다이얼로그를 불러옴

                return true;
            }
        });

        //원형서킷 출력
        pieChart = (PieChart) rootView.findViewById(R.id.piechart);


        //일별, 월별, 년별, 기간별, 달력 버튼 클릭시 나오게하는 이벤트
        today.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                //php파일 불러옴
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

                //php파일 불러옴
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

                //php파일 불러옴
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




    //달력을 보이게 하고 안보이게 하는 애니메이션 메소드
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
            //addDecorator를 활용하여 EventDecorator에서 정의한 도트를 해당 날짜에 정의한다.
            calendershow.addDecorator(new EventDecorator(Color.RED, calendarDays, CalendarFragment.this));
        }



    }
//--------PHP파일 불러오기 위한 클래스--------------------------------------------------------------------------------------------------------------
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
            showResult(); //showResult메소드를 불러온다.


//--------------------------------pieChart를 보여주기 위한 부분--------------------------------------------------
            pieChart.setUsePercentValues(true); //true로 퍼센트 설정, 개수만 보여줄라면 false하면 됨
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(5,10,5,5);



            pieChart.setDragDecelerationFrictionCoef(0.95f);

            pieChart.setDrawHoleEnabled(false);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(61f);

            pieChart.setTransparentCircleColor(Color.BLACK);

            pieChart.setNoDataText("해당되는 매출이 없습니다.");
            //pieChart.setNoDataTextColor(Color.parseColor("#465088"));

            pieChart.setEntryLabelColor(Color.BLACK); //엔트리 이름 컬러
            pieChart.setEntryLabelTypeface(defaultFromStyle(BOLD));

            //pieChart.invalidate(); //차드 새로고침 메소드
            //Legend i = pieChart.getLegend();
            ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();


            Log.i("countnum", String.valueOf(cocacount));

            //원형서킷안에 데이터들을 추가한다. 여기서 php파일에서 받아온 데이터를 저장한 변수들을 넣어논다.
            yValues.add(new PieEntry(Math.round(cocacount),"코카콜라"));
            yValues.add(new PieEntry(Math.round(cidarcount),"스프라이트"));
            yValues.add(new PieEntry(Math.round(fantacount),"환타"));
            yValues.add(new PieEntry(Math.round(pepsicount),"펩시"));

            Description description = new Description();
            description.setText("카테고리 별 분석"); //라벨
            description.setTextSize(18);
            description.setTypeface(defaultFromStyle(BOLD));
            description.setTextColor(Color.parseColor("#465088"));
            description.setPosition(500,90);
            pieChart.setDescription(description);


            pieChart.animateY(1000, Easing.EaseInOutCubic);
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
            piedata.setValueTextColor(Color.parseColor("#465088")); //숫자 퍼센트 데이터 컬러

            pieChart.setData(piedata);

            //piedata.setValueFormatter(new MyValueFormatter());

            //piedata.setValueFormatter(new DefaultValueFormatter(0)); //소수점 없에는거
            piedata.setValueFormatter(new PercentFormatter()); //소수점 첫째자리
            //piedata.setValueFormatter(new IndexAxisValueFormatter()); //숫자 없에는거
            //piedata.setValueFormatter(new DefaultAxisValueFormatter(1));

            //CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout); //마커 나오게 하는 코드
            //pieChart.setMarkerView(mv);


            //불러올때마다 다시 초기화를 시켜주는 부분
            count = 0; // 건수 출력
            SVMtotalmoney=0; // 자판기 총 금액 변수
            SVMtotalcount=0; // 자판기 총 건수 변수
            cocatotalmoney = 0; // 코카콜라 총 금액 변수
            cidartotalmoney = 0; // 스프라이트 총 금액 변수
            fantatotalmoney = 0; // 환타 총 금액 변수
            pepsitotalmoney = 0; // 펩시 총 금액 변수
            cocacount=0; // 코카콜라 총 건수 변수
            cidarcount=0; // 스프라이트 총 건수 변수
            fantacount=0; // 환타 총 건수 변수
            pepsicount=0; // 펩시 총 건수 변수

        }
    }


    //서버와 연동하는 메소드
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

//php파일 데이터를 처리하고 정의하는 메소드
    private void showResult() {

        //받아올 php파일 데이터와 레코드들
        String TAG_JSON = "TransactionDetails_DATA";
        String TAG_TD_TRCODE = "TD_TRCODE";
        String TAG_TD_TRDATE = "TD_TRDATE";
        String TAG_TD_VMCODE = "TD_VMCODE";
        String TAG_TD_DRCODE = "TD_DRCODE";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            //jsonArray를 통해 받아온 데이터의 배열 크기만큼 for문을 돌려 반복한다.
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String TD_TRCODE = item.getString(TAG_TD_TRCODE);
                String TD_TRDATE = item.getString(TAG_TD_TRDATE);
                String TD_VMCODE = item.getString(TAG_TD_VMCODE);
                String TD_DRCODE = item.getString(TAG_TD_DRCODE);

                PayData personalData = new PayData();

                personalData.setTD_TRCODE(TD_TRCODE);
                personalData.setTD_TRDATE(TD_TRDATE);
                personalData.setTD_VMCODE(TD_VMCODE);
                personalData.setTD_DRCODE(TD_DRCODE);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();


                //기간별 금액과 건수를 출력하기 위해 기간별 날짜값을 불러온다.
                String date11 = String.valueOf(firstcal.getText());
                String date22 = String.valueOf(lastcal.getText());

                try {
                    //데이트피커 firstcal과 lastcal에서 선택된 값을 시간값으로 바꾸어 처리하기위해 그 시간값을 getTime으로 값으로 바꾸어 비교한다.
                    Date FirDate = dateFormat.parse(date11);
                    Date SecDate = dateFormat.parse(date22);

                    Date tagetDate=dateFormat.parse(TD_TRDATE);
                    String vendingitem =  vendingSpinner.getSelectedItem().toString();

                    Log.i("vmspinner", vendingitem);
                    Log.i("vmspinner", TD_VMCODE);
                    Log.i("vmspinner", TD_TRDATE);

                    //for문을 통하여 배열의 크기만큼 하나씩 돌때 스피너 선택된 값을 불러와서 스위치문을 통하여 조건문 실행
                    switch (vendingitem){
                        case "성결관 자판기": //스위치 조건문중 성결관 자판기일때
                            //만약 php파일에서 VMCODE값이 해당값과 같을때를 비교하고
                            //그다음 TRDATE값이 첫번째 데이트타임에서 설정된 시간보다 크거나 같을때와 두번재 데이트타임에서 설정된 시간보다 작거나 같을때를 비교하여 조건문을 실행한다.
                            //조건문에서는 각 음료 카테고리의 건수와 금액을 누적한다.
                            if (TD_VMCODE.equals("SKU001") && (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime())){
                                if(TD_DRCODE.equals("CocaCola")){
                                    cocatotalmoney = cocatotalmoney+1200;
                                    cocacount = cocacount +1;
                                }else if (TD_DRCODE.equals("Sprite")){
                                    cidartotalmoney = cidartotalmoney+1100;
                                    cidarcount = cidarcount +1;
                                }else if (TD_DRCODE.equals("Fanta Orange")){
                                    fantatotalmoney = fantatotalmoney+1000;
                                    fantacount = fantacount +1;
                                }else if (TD_DRCODE.equals("Pepsi")){
                                    pepsitotalmoney = pepsitotalmoney+1500;
                                    pepsicount = pepsicount +1;
                                }
                                //총금액과 총건수를 각 음료의 금액과 건수로 합친다.
                                SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + pepsitotalmoney;
                                SVMtotalcount= fantacount + cocacount + cidarcount + pepsicount;

                            }
                            break;

                        case "재림관 자판기":
                            if (TD_VMCODE.equals("SKU002") && (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime())){
                                if(TD_DRCODE.equals("CocaCola")){
                                    cocatotalmoney = cocatotalmoney+1200;
                                    cocacount = cocacount +1;
                                }else if (TD_DRCODE.equals("Sprite")){
                                    cidartotalmoney = cidartotalmoney+1100;
                                    cidarcount = cidarcount +1;
                                }else if (TD_DRCODE.equals("Fanta Orange")){
                                    fantatotalmoney = fantatotalmoney+1000;
                                    fantacount = fantacount +1;
                                }else if (TD_DRCODE.equals("Pepsi")){
                                    pepsitotalmoney = pepsitotalmoney+1500;
                                    pepsicount = pepsicount +1;
                                }
                                SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + pepsitotalmoney;
                                SVMtotalcount= fantacount + cocacount + cidarcount + pepsicount;

                            }
                            break;

                        case "중생관 자판기":
                            if (TD_VMCODE.equals("SKU003") && (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime())){
                                if(TD_DRCODE.equals("CocaCola")){
                                    cocatotalmoney = cocatotalmoney+1200;
                                    cocacount = cocacount +1;
                                }else if (TD_DRCODE.equals("Sprite")){
                                    cidartotalmoney = cidartotalmoney+1100;
                                    cidarcount = cidarcount +1;
                                }else if (TD_DRCODE.equals("Fanta Orange")){
                                    fantatotalmoney = fantatotalmoney+1000;
                                    fantacount = fantacount +1;
                                }else if (TD_DRCODE.equals("Pepsi")){
                                    pepsitotalmoney = pepsitotalmoney+1500;
                                    pepsicount = pepsicount +1;
                                }
                                SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + pepsitotalmoney;
                                SVMtotalcount= fantacount + cocacount + cidarcount + pepsicount;

                            }
                            break;

                        case "영암관 자판기":
                            if (TD_VMCODE.equals("SKU004") && (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime())){
                                if(TD_DRCODE.equals("CocaCola")){
                                    cocatotalmoney = cocatotalmoney+1200;
                                    cocacount = cocacount +1;
                                }else if (TD_DRCODE.equals("Sprite")){
                                    cidartotalmoney = cidartotalmoney+1100;
                                    cidarcount = cidarcount +1;
                                }else if (TD_DRCODE.equals("Fanta Orange")){
                                    fantatotalmoney = fantatotalmoney+1000;
                                    fantacount = fantacount +1;
                                }else if (TD_DRCODE.equals("Pepsi")){
                                    pepsitotalmoney = pepsitotalmoney+1500;
                                    pepsicount = pepsicount +1;
                                }
                                SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + pepsitotalmoney;
                                SVMtotalcount= fantacount + cocacount + cidarcount + pepsicount;

                            }
                            break;

                        case "학생회관 자판기":
                            if (TD_VMCODE.equals("SKU005") && (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime())){
                                if(TD_DRCODE.equals("CocaCola")){
                                    cocatotalmoney = cocatotalmoney+1200;
                                    cocacount = cocacount +1;
                                }else if (TD_DRCODE.equals("Sprite")){
                                    cidartotalmoney = cidartotalmoney+1100;
                                    cidarcount = cidarcount +1;
                                }else if (TD_DRCODE.equals("Fanta Orange")){
                                    fantatotalmoney = fantatotalmoney+1000;
                                    fantacount = fantacount +1;
                                }else if (TD_DRCODE.equals("Pepsi")){
                                    pepsitotalmoney = pepsitotalmoney+1500;
                                    pepsicount = pepsicount +1;
                                }
                                SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + pepsitotalmoney;
                                SVMtotalcount= fantacount + cocacount + cidarcount + pepsicount;

                            }
                            break;

                        case "학술정보관 자판기":
                            if (TD_VMCODE.equals("SKU006") && (FirDate.getTime()<= tagetDate.getTime() && tagetDate.getTime() <= SecDate.getTime())){
                                if(TD_DRCODE.equals("CocaCola")){
                                    cocatotalmoney = cocatotalmoney+1200;
                                    cocacount = cocacount +1;
                                }else if (TD_DRCODE.equals("Sprite")){
                                    cidartotalmoney = cidartotalmoney+1100;
                                    cidarcount = cidarcount +1;
                                }else if (TD_DRCODE.equals("Fanta Orange")){
                                    fantatotalmoney = fantatotalmoney+1000;
                                    fantacount = fantacount +1;
                                }else if (TD_DRCODE.equals("Pepsi")){
                                    pepsitotalmoney = pepsitotalmoney+1500;
                                    pepsicount = pepsicount +1;
                                }
                                SVMtotalmoney= fantatotalmoney + cocatotalmoney + cidartotalmoney + pepsitotalmoney;
                                SVMtotalcount= fantacount + cocacount + cidarcount + pepsicount;

                            }
                            break;

                        default:
                            break;
                    }

                    //Layout 보여주는 부분 변경사항
                    if( SVMtotalmoney == 0 ){
                        totalmoneyshow.setText(0+"원");
                        totalnumbershow.setText(0+"건");

                        cocacolamoney.setText(0 + "원");
                        cidarmoney.setText(0 + "원");
                        fantamoney.setText(0 + "원");
                        pepsimoney.setText(0 + "원");

                        cocacolanum.setText(0+"건");
                        cidarnum.setText(0+"건");
                        fantanum.setText(0+"건");
                        pepsinum.setText(0+"건");
                    }else if( SVMtotalmoney != 0 ){
                        totalmoneyshow.setText(moneyFormat.format(SVMtotalmoney)+"원");
                        totalnumbershow.setText(SVMtotalcount+"건");
                        cocacolamoney.setText(moneyFormat.format(cocatotalmoney) + "원");
                        cidarmoney.setText(moneyFormat.format(cidartotalmoney) + "원");
                        fantamoney.setText(moneyFormat.format(fantatotalmoney) + "원");
                        pepsimoney.setText(moneyFormat.format(pepsitotalmoney) + "원");

                        cocacolanum.setText(cocacount+"건");
                        cidarnum.setText(cidarcount+"건");
                        fantanum.setText(fantacount+"건");
                        pepsinum.setText(pepsicount+"건");
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Log.i("testtest",   TD_TRCODE +" / " +   TD_TRDATE +" / " + TD_VMCODE +" / " + TD_DRCODE);


            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


//----------------------------------------------------------------------------------------------------------------------------


}