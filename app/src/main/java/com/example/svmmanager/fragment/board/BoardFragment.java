package com.example.svmmanager.fragment.board;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.svmmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.example.svmmanager.R;

public class BoardFragment extends Fragment {

    private RecyclerView recyclerView;
    private BoardAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<BoardData> mSearchData = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;


    private static String IP_ADDRESS = "211.211.158.42/yongrun/svm";
    private static String TAG = "phptest";
    private String mJsonString; // JSON 파싱 값을 받아서 임시로 담는 공간.

    private FloatingActionButton addbtn; //게시물 작성 버튼

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_board, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(0);
        adapter = new BoardAdapter(mSearchData);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //커스텀 리스터 객체 생성 및 전달.
        adapter.setOnItemClickListener(new BoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // TODO : 아이템 클릭 이벤트를 플레그먼트에서 처뤼
                Bundle arguments = new Bundle();        // 프래그먼트 간 데이터 전달을 위한 Bundle 바구니 생성.

                arguments.putString("userid",mSearchData.get(position).getUserid());      // 글 작성자 ID
                arguments.putString("post_code",mSearchData.get(position).getCode());      // 게시글 코드

                arguments.putString("nickname", mSearchData.get(position).getNickname());  //작성자 닉네임
                arguments.putString("contents", mSearchData.get(position).getContents());  //작성자 글 내용
                arguments.putString("date", mSearchData.get(position).getDate());          //작성자 글 작성 날짜
                arguments.putString("title", mSearchData.get(position).getTitle());        //작성자 글 제목

                arguments.putString("answer", mSearchData.get(position).getAnswercontents()); // 관리자 답변
                arguments.putString("answerdate", mSearchData.get(position).getAnswerdate()); // 관리자 답변 작성 날짜

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BoardMainFragment boardMainFragment = new BoardMainFragment();
                boardMainFragment.setArguments(arguments);          // boardMainFragment ( 게시글 열람 ) 화면으로 데이터 바구니 전달.

                transaction.replace(R.id.nav_host_fragment, boardMainFragment).addToBackStack(null).commit();       // boardMainFragment로 전환.
            }
        });

        swipeRefreshLayout = rootview.findViewById(R.id.refresh_layout);        // 스와이프 리프레시

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PostUpdate();       // 게시글 목록 새로고침
                swipeRefreshLayout.setRefreshing(false); //새로고침표시 없애기
            }
        });
/*
        //게시글 작성 플로팅 버튼
        addbtn = (FloatingActionButton) rootview.findViewById(R.id.addBtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("버튼 작동확인", "게시물 추가 버튼 눌렀다.");
                Intent intent = new Intent(getActivity(), BoardWriteActivity.class ); // 게시글 작성 Activity로 이동.
                startActivity(intent);
            }
        });
*/
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        PostUpdate();
    }


    public void PostUpdate() {      //게시글 새로고침 메소드

        mSearchData.clear();        //게시글 목록 clear
        adapter.notifyDataSetChanged();  // 게시글 목록 갱신
        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/POST.php", "");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // 값 가져오는 클래스
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
                showResult();   //  showResult 메소드 실행
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


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

        String TAG_JSON = "POST_DATA";
        String TAG_CODE = "POST_CODE";
        String TAG_TITLE = "POST_TITLE";
        String TAG_NICKNAME = "POST_NICKNAME";
        String TAG_DATE = "POST_DATE";
        String TAG_MANAGER_COMMENT = "POST_MANAGER_COMMENT";
        String TAG_CONTENTS = "POST_CONTENTS";
        String TAG_ANSWER_CONTENTS = "POST_ANSWER_CONTENTS";
        String TAG_ANSWER_DATE = "POST_ANSWER_DATE";
        String TAG_POST_ID = "POST_ID";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = jsonArray.length()-1; i>=0; i--) {

                JSONObject item = jsonArray.getJSONObject(i);

                String POST_ID = item.getString(TAG_POST_ID);

                String POST_CODE = item.getString(TAG_CODE);
                String POST_TITLE = item.getString(TAG_TITLE);
                String POST_NICKNAME = item.getString(TAG_NICKNAME);
                String POST_DATE = item.getString(TAG_DATE);
                String POST_MANAGER_COMMENT = item.getString(TAG_MANAGER_COMMENT);
                String POST_CONTENTS = item.getString(TAG_CONTENTS);
                String POST_ANSWER_CONTENTS = item.getString(TAG_ANSWER_CONTENTS);
                String POST_ANSWER_DATE = item.getString(TAG_ANSWER_DATE);

                BoardData boardData = new BoardData();

                if(POST_MANAGER_COMMENT.equals("0")){
                    POST_MANAGER_COMMENT = "확인안함";
                }
                else {
                    POST_MANAGER_COMMENT = "확인됨";
                }

                boardData.setCode(POST_CODE); // 게시글 코드
                boardData.setTitle(POST_TITLE); // 게시글 제목
                boardData.setNickname(POST_NICKNAME); // 게시글 작성자
                boardData.setDate(POST_DATE); // 게시글 작성 날짜
                boardData.setContents(POST_CONTENTS); // 게시글 내용
                boardData.setUserid(POST_ID);

                boardData.setManagercomment(POST_MANAGER_COMMENT); // 건의 확인 여부
                boardData.setAnswercontents(POST_ANSWER_CONTENTS); // 답변 내용
                boardData.setAnswerdate(POST_ANSWER_DATE); // 답변 작성 날짜


                mSearchData.add(boardData);
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}