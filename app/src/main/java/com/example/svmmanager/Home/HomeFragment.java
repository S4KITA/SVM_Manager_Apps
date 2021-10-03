package com.example.svmmanager.Home;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //수량 TextView
    private TextView mTextViewCocacolaStock, mTextViewSpriteStock, mTextViewPepsiStock, mTextViewFantaStock;

    //가격 TextView
    private TextView mTextViewCocacolaPrice, mTextViewSpritePrice, mTextViewPepsiPrice, mTextViewFantaPrice;

    private static String IP_ADDRESS = "211.211.158.42/yongrun/svm";
    private static String TAG = "phptest";


    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<HomeData> mArrayList;
    private HomeAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mJsonString;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_home, container, false);


        // 수량
        mTextViewCocacolaStock = (TextView) rootview.findViewById(R.id.mTextViewCocacolaStock);
        mTextViewSpriteStock = (TextView) rootview.findViewById(R.id.mTextViewSpriteStock);
        mTextViewPepsiStock = (TextView) rootview.findViewById(R.id.mTextViewPepsiStock);
        mTextViewFantaStock = (TextView) rootview.findViewById(R.id.mTextViewFantaStock);

        // 가격
        mTextViewCocacolaPrice = (TextView) rootview.findViewById(R.id.mTextViewCocacolaPrice);
        mTextViewSpritePrice = (TextView) rootview.findViewById(R.id.mTextViewSpritePrice);
        mTextViewPepsiPrice = (TextView) rootview.findViewById(R.id.mTextViewPepsiPrice);
        mTextViewFantaPrice = (TextView) rootview.findViewById(R.id.mTextViewFantaPrice);


        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mArrayList = new ArrayList<>();

        mAdapter = new HomeAdapter(getActivity(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mArrayList.clear();
        mAdapter.notifyDataSetChanged();

        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/drink.php", "");

        swipeRefreshLayout = rootview.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/drink.php", "");
                swipeRefreshLayout.setRefreshing(false); //새로고침표시 없애기
            }
        });


        return rootview;
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

        String TAG_JSON = "drink_data";
        String TAG_DRID = "DRID";
        String TAG_DRCode = "DRCode";
        String TAG_DRStock = "DRStock";
        String TAG_DRPrice = "DRPrice";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                //태그 값에 맞는 값 출력
                String DRID = item.getString(TAG_DRID);
                String DRCode = item.getString(TAG_DRCode);
                String DRStock = item.getString(TAG_DRStock);
                String DRPrice = item.getString(TAG_DRPrice);

                HomeData personalData = new HomeData();

                personalData.setDRID(DRID);
                personalData.setDRCode(DRCode);
                personalData.setDRStock(DRStock);
                personalData.setDRPrice(DRPrice);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();


                //출력 switch 문 ( 음료 Code에 따라서 무슨 음료인지 식별. 식별 후 음료의 가격과 개수를 표시한다. )
                switch (mArrayList.get(i).getDRCode()) {
                    case "CocaCola":
                        mTextViewCocacolaPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewCocacolaStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Sprite":
                        mTextViewSpritePrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewSpriteStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Pepsi":
                        mTextViewPepsiPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewPepsiStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    case "Fanta Orange":
                        mTextViewFantaPrice.setText("가격 : " + mArrayList.get(i).getDRPrice() + " 원");
                        mTextViewFantaStock.setText("수량 : " + mArrayList.get(i).getDRStock() + " 개");
                        break;

                    default:
                        break;
                }


            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}