package com.example.svmmanager.fragment.board;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.svmmanager.R;
public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {


    final private String TAG1 = "테스트중이에요옹";

    private ArrayList<BoardData> iData;
    //리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    //OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메소드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView profile;
        public TextView nickname;
        public TextView title;
        public TextView contents;
        public TextView date;
        public TextView managercomment;
        // public TextView time; 안씀






        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰 객체에 대한 참조.(Hold Strong reference)
            //profile = itemView.findViewById(R.id.imgProfile);
            nickname = itemView.findViewById(R.id.txtNick);
            title = itemView.findViewById(R.id.txtTitle);
            contents = itemView.findViewById(R.id.contents);
            date = itemView.findViewById(R.id.txtDate);
            managercomment = itemView.findViewById(R.id.txtManager);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 참조.
                        if (mListener != null) {
                            mListener.onItemClick(view, pos);
                            Log.d(TAG1, "onClick: "+pos+"번째");
                        }
                    }
                }
            });

        }
    }

    //생성자에서 데이터 리스트 객체를 전달받음.
    public BoardAdapter(ArrayList<BoardData> mSearchData) {
        this.iData = mSearchData;
    }

    @NonNull
    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_notice_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(iData.get(position).getTitle());
        holder.nickname.setText(iData.get(position).getNickname());
        holder.contents.setText(iData.get(position).getContents());
        holder.date.setText(iData.get(position).getDate());
        holder.managercomment.setText(iData.get(position).getManagercomment());

        // .child( iData.get(position).getImageSource ) .getdownloadurl().~~~~~
        // 위에서 가져온 이미지가 uri에 담기고 Glide.Glide.with(getApplicationContext()).load(Uri).into(viewImage);

    }

    //전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return iData.size();
    }
}
