package com.jagmee.app.Video_Recording.DraftVideos;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jagmee.app.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class DraftVideos_Adapter extends RecyclerView.Adapter<DraftVideos_Adapter.CustomViewHolder > {

    public Context context;
    private DraftVideos_Adapter.OnItemClickListener listener;
    private ArrayList<DraftVideo_Get_Set> dataList;


      public interface OnItemClickListener {
        void onItemClick(int postion, DraftVideo_Get_Set item, View view);
    }

    public DraftVideos_Adapter(Context context, ArrayList<DraftVideo_Get_Set> dataList, DraftVideos_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public DraftVideos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_galleryvideo_layout,null);
        //view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        DraftVideos_Adapter.CustomViewHolder viewHolder = new DraftVideos_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {


        ImageView thumb_image;
        TextView view_txt;
        ImageButton cross_btn;

        public CustomViewHolder(View view) {
            super(view);

            thumb_image=view.findViewById(R.id.thumb_image);
            view_txt=view.findViewById(R.id.view_txt);
            cross_btn=view.findViewById(R.id.cross_btn);

        }

        public void bind(final int position, final DraftVideo_Get_Set item, final DraftVideos_Adapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,item,v);
                }
            });

            cross_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,item,v);
                }
            });

        }

    }




    @Override
    public void onBindViewHolder(final DraftVideos_Adapter.CustomViewHolder holder, final int i) {
        final DraftVideo_Get_Set item= dataList.get(i);

        holder.view_txt.setText(item.video_time);

        Glide.with( context )
                .load(Uri.fromFile(new File(item.video_path)) )
                .into(holder.thumb_image);

        holder.bind(i,item,listener);

   }

}