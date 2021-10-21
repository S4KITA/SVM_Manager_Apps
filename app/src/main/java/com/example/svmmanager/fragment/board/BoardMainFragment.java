package com.example.svmmanager.fragment.board;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.svmmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BoardMainFragment extends Fragment {

    private ImageView backspace;

    // Bundle로 받은 값 담는 변수들.
    private String snickname;   //닉네임
    private String scontents;   //게시글 내용
    private String sdate;   //작성일자
    private String stitle;  //게시글 제목
    private String sanswer; //게시글 답변
    private String sanswerdate; //게시글 답변 일자
    private String spostcode;   //게시글 코드

    // 작성자 글
    private TextView nickname;
    private TextView contents;
    private TextView date;
    private TextView title;

    // 관리자 답변
    private ConstraintLayout managerlayout;
    private TextView answer;
    private TextView answerdate;
    ImageView imgManagerProfile;

    // 답변 작성
    private ConstraintLayout writelayout;
    private EditText mEditTextManager;
    private Button mButtonSubmit;

    // 게시글 삭제
    private TextView BtnDelete;

    // 답변 수정 삭제
    private EditText EdtManagerComments;
    private TextView BtnManagerEdit;
    private TextView BtnManagerDelete;
    private TextView Seperate;

    // 수정 완료 버튼
    private Button mButtonEditComplete;

    private static String TAG = "게시글 보기";

    private String mJsonString;

    private static String IP_ADDRESS = "59.14.35.61/yongrun/svm";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_boardmain, container, false);

        //작성자
        nickname = rootView.findViewById(R.id.txtCommentNick);
        contents = rootView.findViewById(R.id.txtCommentContents);
        date = rootView.findViewById(R.id.txtCommentDate);
        title = rootView.findViewById(R.id.txtCommentTitle);

        // 관리자
        answer = rootView.findViewById(R.id.txtManagerComments);
        answerdate = rootView.findViewById(R.id.txtManagerCommentDate);
        imgManagerProfile = rootView.findViewById(R.id.imgManagerProfile);
        managerlayout = rootView.findViewById(R.id.managerlayout);

        // 관리자 답변 작성 기능.
        writelayout = rootView.findViewById(R.id.writelayout);
        mEditTextManager = rootView.findViewById(R.id.mEditTextManager);
        mButtonSubmit = rootView.findViewById(R.id.mButtonSubmit);

        // 게시글 삭제
        BtnDelete = rootView.findViewById(R.id.BtnDelete);

        // 답변글 수정 , 삭제
        EdtManagerComments = rootView.findViewById(R.id.EdtManagerComments);
        BtnManagerEdit = rootView.findViewById(R.id.BtnManagerEdit);
        BtnManagerDelete = rootView.findViewById(R.id.BtnManagerDelete);

        // 경계선
        Seperate = rootView.findViewById(R.id.Seperate);

        // 수정 완료 버튼
        mButtonEditComplete = rootView.findViewById(R.id.mButtonEditComplete);

        if (getArguments() != null) {
            spostcode = getArguments().getString("post_code"); // 게시글 코드
            snickname = getArguments().getString("nickname"); // 작성자
            scontents = getArguments().getString("contents"); // 건의 내용
            sdate = getArguments().getString("date"); // 건의 작성 날짜
            stitle = getArguments().getString("title"); // 건의 글 제목

            sanswer = getArguments().getString("answer"); // 답변
            sanswerdate = getArguments().getString("answerdate"); // 답변 날짜

            nickname.setText(snickname);
            contents.setText(scontents);
            date.setText(sdate);
            title.setText(stitle);

            if (!(sanswer.equals("null") || sanswer.equals("")))    // 답변 내용이 있으면
            {
                answer.setVisibility(View.VISIBLE);                 // 보이게 하기
                answerdate.setVisibility(View.VISIBLE);         // 보이게 하기
                imgManagerProfile.setVisibility(View.VISIBLE);  // 보이게 하기

                managerlayout.setVisibility(View.VISIBLE);
                answer.setText(sanswer);
                answerdate.setText(sanswerdate);

                BtnManagerEdit.setVisibility(View.VISIBLE);
                BtnManagerDelete.setVisibility(View.VISIBLE);
                Seperate.setVisibility(View.VISIBLE);

                BtnManagerEdit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        EdtManagerComments.setVisibility(View.VISIBLE);
                        answer.setVisibility(View.INVISIBLE);
                        mButtonEditComplete.setVisibility(View.VISIBLE);

                        // 기존 답변 내용 가져오기.
                        EdtManagerComments.setText( answer.getText().toString() );

                        mButtonEditComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!(EdtManagerComments.getText().equals("") || EdtManagerComments.getText().equals(null)))
                                    AnswerEdit(EdtManagerComments.getText().toString());

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                BoardFragment boardFragment = new BoardFragment();

                                transaction.replace(R.id.nav_host_fragment, boardFragment).commit();

                                Toast.makeText(getActivity(), "답변이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                //답변글 삭제
                BtnManagerDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // App 사용자 ID와 글 작성자 ID가 일치
                        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                        dlg.setTitle("관리자 답변 삭제");
                        dlg.setMessage("답변을 삭제하시겠습니까 ? ");
                        dlg.setIcon(R.drawable.delete);

                        dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "답변이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                //답변 삭제 코드
                                AnswerDelete();
                                //////////////////

                                //게시글 삭제 후 게시글 열람 창에서 나가기.
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                BoardFragment boardFragment = new BoardFragment();

                                transaction.replace(R.id.nav_host_fragment, boardFragment).commit();
                            }

                        });

                        AlertDialog alertDialog = dlg.create();
                        dlg.show();


                    }
                });

            } else {
                writelayout.setVisibility(View.VISIBLE);

                mButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!(mEditTextManager.getText().equals("") || mEditTextManager.getText().equals(null)))
                            AnswerInsert(mEditTextManager.getText().toString());

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        BoardFragment boardFragment = new BoardFragment();

                        transaction.replace(R.id.nav_host_fragment, boardFragment).commit();

                        Toast.makeText(getActivity(), "답변이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

/*
        //게시글 수정
        BtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suserid.equals(userid)) {
                    // App 사용자 ID와 글 작성자 ID가 일치
                    Toast.makeText(getActivity().getApplicationContext(), "게시글을 수정합니다.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), BoardEditActivity.class);
                    intent.putExtra("post_code",spostcode); // 게시글 코드
                    intent.putExtra("title",stitle);    // 제목
                    intent.putExtra("contents",scontents); // 내용
                    startActivity(intent);
                }
                else {
                    // App 사용자 ID와 글 작성자 ID가 일치 X
                }


            }
        });
*/


        //게시글 삭제
        BtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // App 사용자 ID와 글 작성자 ID가 일치
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                dlg.setTitle("게시글 삭제");
                dlg.setMessage("게시글을 삭제하시겠습니까 ? ");
                dlg.setIcon(R.drawable.delete);

                dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        //게시글 삭제 코드
                        PostDelete();

                        //게시글 삭제 후 게시글 열람 창에서 나가기.
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        BoardFragment boardFragment = new BoardFragment();

                        transaction.replace(R.id.nav_host_fragment, boardFragment).commit();
                    }

                });

                AlertDialog alertDialog = dlg.create();
                dlg.show();


            }
        });




        /// 뒤로가기 버튼
        backspace = rootView.findViewById(R.id.backspace);

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BoardFragment boardFragment = new BoardFragment();

                transaction.replace(R.id.nav_host_fragment, boardFragment).commit();
            }
        });

        return rootView;
    }


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

            for (int i = jsonArray.length() - 1; i >= 0; i--) {

                JSONObject item = jsonArray.getJSONObject(i);

                String POST_ID = item.getString(TAG_POST_ID);

                String POST_CODE = item.getString(TAG_CODE);

                if (POST_CODE.equals(spostcode)) {
                    String POST_TITLE = item.getString(TAG_TITLE);
                    String POST_NICKNAME = item.getString(TAG_NICKNAME);
                    String POST_DATE = item.getString(TAG_DATE);
                    String POST_MANAGER_COMMENT = item.getString(TAG_MANAGER_COMMENT);
                    String POST_CONTENTS = item.getString(TAG_CONTENTS);
                    String POST_ANSWER_CONTENTS = item.getString(TAG_ANSWER_CONTENTS);
                    String POST_ANSWER_DATE = item.getString(TAG_ANSWER_DATE);

                    if (POST_MANAGER_COMMENT.equals("0")) {
                        POST_MANAGER_COMMENT = "확인안함";
                    } else {
                        POST_MANAGER_COMMENT = "확인됨";
                    }

                    title.setText(POST_TITLE);
                    contents.setText(POST_CONTENTS);
                    nickname.setText(POST_NICKNAME);
                    date.setText(POST_DATE);

                    if (!(POST_ANSWER_CONTENTS.equals("null") || POST_ANSWER_CONTENTS.equals(""))) {
                        answer.setVisibility(View.VISIBLE);
                        answerdate.setVisibility(View.VISIBLE);
                        imgManagerProfile.setVisibility(View.VISIBLE);

                        answer.setText(POST_ANSWER_CONTENTS);
                        answerdate.setText(POST_ANSWER_DATE);
                    } else {
                        answer.setVisibility(View.VISIBLE);
                        answer.setText("답변 내용이 없습니다. 빠른 시일내에 답변 드리겠습니다.");
                        answer.setTypeface(null, Typeface.BOLD_ITALIC);
                    }

                }

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    // 답변 입력
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String POST_ANSWER_CONTENTS = (String) params[1];
            String POST_CODE = (String) params[2];
            String serverURL = (String) params[0];

            String postParameters = "POST_ANSWER_CONTENTS=" + POST_ANSWER_CONTENTS + "&POST_CODE=" + POST_CODE;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String POST_CODE = (String) params[1];
            String serverURL = (String) params[0];

            String postParameters = "POST_CODE=" + POST_CODE;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    // 답변 입력
    public void AnswerInsert(String answer) {
        InsertData insertData = new InsertData();
        insertData.execute("http://" + IP_ADDRESS + "/POST_ANSWER_ANDRIOD.php", answer, spostcode);
    }

    // 답변 수정
    public void AnswerEdit(String answer) {
        InsertData insertData = new InsertData();
        insertData.execute("http://" + IP_ADDRESS + "/POST_ANSWER_MODIFY_ANDRIOD.php", answer, spostcode);
    }

    // 답변 삭제
    public void AnswerDelete() {
        DeleteData deleteAnswer = new DeleteData();
        deleteAnswer.execute("http://" + IP_ADDRESS + "/POST_ANSWER_DELETE_ANDRIOD.php", spostcode);
    }


    public void PostUpdate() {
        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/POST.php", "");
    }

    public void PostDelete() {
        DeleteData deleteBoard = new DeleteData();
        deleteBoard.execute("http://" + IP_ADDRESS + "/POST_DELETE_ANDROID.php", spostcode);
    }


    @Override
    public void onResume() {
        super.onResume();
        PostUpdate();
    }
}