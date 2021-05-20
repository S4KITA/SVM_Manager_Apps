package com.example.svmmanager.pay;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.svmmanager.R;

import java.util.ArrayList;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.CustomViewHolder> {

    private ArrayList<PayData> mList = null;
    private Activity context = null;


    public PayAdapter(Activity context, ArrayList<PayData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTextViewTRCode;
        protected TextView mTextViewTRDate;
        protected TextView mTextViewVMCode;
        protected TextView mTextViewDRCode;


        public CustomViewHolder(View view) {
            super(view);
            this.mTextViewTRCode = (TextView) view.findViewById(R.id.mTextViewTRCode);
            this.mTextViewTRDate = (TextView) view.findViewById(R.id.mTextViewTRDate);
            this.mTextViewVMCode = (TextView) view.findViewById(R.id.mTextViewVMCode);
            this.mTextViewDRCode = (TextView) view.findViewById(R.id.mTextViewDRCode);
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

        viewholder.mTextViewTRCode.setText(mList.get(position).getTD_TRCODE());
        viewholder.mTextViewTRDate.setText(mList.get(position).getTD_TRDATE());
        viewholder.mTextViewVMCode.setText(mList.get(position).getTD_VMCODE());
        viewholder.mTextViewDRCode.setText(mList.get(position).getTD_DRCODE());

    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}