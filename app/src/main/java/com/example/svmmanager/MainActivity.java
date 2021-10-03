package com.example.svmmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.svmmanager.Home.HomeFragment;
import com.example.svmmanager.fragment.board.BoardFragment;
import com.example.svmmanager.fragment.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;

    //프래그먼트 변수와 바텀네비게이션바 변수를 선정
    private BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment ;
    BoardFragment boardFragment ;
    CalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);

        // 바텀 내비게이션 뷰 초기 선택 값 (홈)
        bottomNavigationView.setSelectedItemId(R.id.frag_navigation_home);

        // 처음에 띄울화면 홈 화면으로
        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment,"home").commit();

        //바텀바를 클릭시 이동될 프래그먼트를 선정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.frag_navigation_home: {
                        fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        homeFragment = new HomeFragment();
                        transaction.replace(R.id.nav_host_fragment, homeFragment,"home");
                        transaction.addToBackStack("home");
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();
                        transaction.isAddToBackStackAllowed();
                        return true;
                    }
                    case R.id.frag_navigation_board: {
                        fm.popBackStack("board",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        boardFragment = new BoardFragment();
                        transaction.replace(R.id.nav_host_fragment, boardFragment,"board");
                        transaction.addToBackStack("board");
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();
                        transaction.isAddToBackStackAllowed();
                        return true;
                    }
                    case R.id.frag_navigation_calendar: {
                        fm.popBackStack("calendar",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        calendarFragment = new CalendarFragment();
                        transaction.replace(R.id.nav_host_fragment, calendarFragment,"calendar");
                        transaction.addToBackStack("calendar");
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();
                        transaction.isAddToBackStackAllowed();
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });
    }

    // 화면 전환 시 바텀 내비게이션 바 메뉴 선택 정보 갱신
    public void updateBottomMenu (BottomNavigationView navigation)
    {
        if(getSupportFragmentManager().findFragmentByTag("home") != null && getSupportFragmentManager().findFragmentByTag("home").isVisible() ) {
            bottomNavigationView.getMenu().findItem(R.id.frag_navigation_home).setChecked(true);
        }
        else if( getSupportFragmentManager().findFragmentByTag("board") != null && getSupportFragmentManager().findFragmentByTag("board").isVisible() ) {
            bottomNavigationView.getMenu().findItem(R.id.frag_navigation_board).setChecked(true);
        }
        else if(getSupportFragmentManager().findFragmentByTag("calendar") != null && getSupportFragmentManager().findFragmentByTag("calendar").isVisible() ) {
            bottomNavigationView.getMenu().findItem(R.id.frag_navigation_calendar).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        super.onBackPressed();
        BottomNavigationView bnv = findViewById(R.id.nav_view);
        updateBottomMenu(bnv);
    }

    @Override
    public void supportFinishAfterTransition() {
        ActivityCompat.finishAfterTransition( this );
    }
}