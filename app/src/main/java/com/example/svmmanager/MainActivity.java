package com.example.svmmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.svmmanager.fragment.BottomNavigationFragment1;
import com.example.svmmanager.fragment.BottomNavigationFragment2;
import com.example.svmmanager.fragment.BottomNavigationFragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class MainActivity extends AppCompatActivity {

    //프래그먼트 변수와 바텀네비게이션바 변수를 선정
    public BottomNavigationView bottomNavigationView;
    BottomNavigationFragment1 fragment1;
    BottomNavigationFragment2 fragment2;
    BottomNavigationFragment3 fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);

        //각 바텀 네비게이션 바에 프래그먼트를 보여주기 위해 지정
        fragment1 = new BottomNavigationFragment1();
        fragment2 = new BottomNavigationFragment2();
        fragment3 = new BottomNavigationFragment3();

        //첫번째홈화면에 프래그먼트1을 보여줌
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment1).commitAllowingStateLoss();

        //바텀바를 클릭시 이동될 프래그먼트를 선정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.frag_navigation_home: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment1).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.frag_navigation_board: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment2).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.frag_navigation_transaction: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment3).commitAllowingStateLoss();
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });
    }
}