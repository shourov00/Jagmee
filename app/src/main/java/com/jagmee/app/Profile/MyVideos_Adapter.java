package com.jagmee.app.Profile;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jagmee.app.Home.Home_Get_Set;
import com.jagmee.app.R;
import com.jagmee.app.SimpleClasses.Functions;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class MyVideos_Adapter extends RecyclerView.Adapter<MyVideos_Adapter.CustomViewHolder > {

    public Context context;
    private MyVideos_Adapter.OnItemClickListener listener;
    private MyVideos_Adapter.OnItemLongClickListener longClickListener;
    private ArrayList<Home_Get_Set> dataList;


      public interface OnItemClickListener {
        void onItemClick(int postion, Home_Get_Set item, View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int postion, Home_Get_Set item, View view);
    }

    public MyVideos_Adapter(Context context, ArrayList<Home_Get_Set> dataList,
                            MyVideos_Adapter.OnItemClickListener listener,MyVideos_Adapter.OnItemLongClickListener onItemLongListener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
        this.longClickListener = onItemLongListener;

    }

    @Override
    public MyVideos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myvideo_layout,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        MyVideos_Adapter.CustomViewHolder viewHolder = new MyVideos_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {


        ImageView thumb_image;

        TextView view_txt;

        public CustomViewHolder(View view) {
            super(view);

            thumb_image=view.findViewById(R.id.thumb_image);
            view_txt=view.findViewById(R.id.view_txt);

        }

        public void bind(final int position,final Home_Get_Set item, final MyVideos_Adapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,item,v);
                }
            }
            );

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onItemLongClick(position,item,v);

                    return true;
                }
            });



        }

    }




    @Override
    public void onBindViewHolder(final MyVideos_Adapter.CustomViewHolder holder, final int i) {
        final Home_Get_Set item= dataList.get(i);
        holder.setIsRecyclable(false);



        try {
            Glide.with(context)
                    .asGif()
                    .load(item.gif)
                    .skipMemoryCache(true)
                     .thumbnail(new RequestBuilder[]{Glide
                             .with(context)
                             .load(item.thum)})
                    .apply(RequestOptions.diskCacheStrategyOf( DiskCacheStrategy.RESOURCE)
                            .placeholder(new ColorDrawable(context.getResources().getColor(R.color.profile_video_back))).centerCrop())

                    .into(holder.thumb_image);

        }catch (Exception e){

        }



        holder.view_txt.setText(item.views);
        holder.view_txt.setText(Functions.GetSuffix(item.views));



        holder.bind(i,item,listener);

   }

}