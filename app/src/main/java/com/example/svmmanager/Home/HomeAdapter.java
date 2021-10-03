package com.example.svmmanager.Home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.svmmanager.R;

import java.util.ArrayList;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder> {

    private ArrayList<HomeData> mList = null;
    private Activity context = null;


    public HomeAdapter(Activity context, ArrayList<HomeData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTextViewDRID;
        protected TextView mTextViewDRCode;
        protected TextView mTextViewDRStock;
        protected TextView mTextViewDRPrice;


        public CustomViewHolder(View view) {
            super(view);
            this.mTextViewDRID = (TextView) view.findViewById(R.id.mTextViewDRID);
            this.mTextViewDRCode = (TextView) view.findViewById(R.id.mTextViewDRCode);
            this.mTextViewDRStock = (TextView) view.findViewById(R.id.mTextViewDRStock);
            this.mTextViewDRPrice = (TextView) view.findViewById(R.id.mTextViewDRPrice);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.mTextViewDRID.setText(mList.get(position).getDRID());
        viewholder.mTextViewDRCode.setText(mList.get(position).getDRCode());
        viewholder.mTextViewDRStock.setText(mList.get(position).getDRStock());
        viewholder.mTextViewDRPrice.setText(mList.get(position).getDRPrice());

    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}