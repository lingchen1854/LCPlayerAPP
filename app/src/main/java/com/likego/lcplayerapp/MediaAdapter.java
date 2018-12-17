package com.likego.lcplayerapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private static final String TAG = "MediaAdapter xmlc";
    private List<MediaInfo> mMediaInfos;
    private Context mContext;

    public MediaAdapter(List<MediaInfo> fruitList, Context context) {
        mContext = context;
        mMediaInfos = fruitList;
        Log.d(TAG, "MediaAdapter: "+mMediaInfos.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mediaView;
        ImageView mediaImage;
        TextView mediaName;

        public ViewHolder(View view) {
            super(view);
            mediaView = view;
            mediaImage = (ImageView) view.findViewById(R.id.media_image);
            mediaName = (TextView) view.findViewById(R.id.media_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_item,
                viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MediaInfo mediaInfo = mMediaInfos.get(position);
                Log.d(TAG, "onClick:--->>name "+mediaInfo.getMediaName());
                HashMap<String,String> mediaHashMap = ListActivity.getMediaHash();
                String path = mediaHashMap.get(mediaInfo.getMediaName());
                Intent intent = new Intent(mContext, MediaPlayerActivity.class);
                intent.putExtra("path",path);
                mContext.startActivity(intent);
            }
        });
        holder.mediaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MediaInfo mediaInfo = mMediaInfos.get(position);
                Log.d(TAG, "onClick:--->>image  "+mediaInfo.getMediaName());
                HashMap<String,String> mediaHashMap = ListActivity.getMediaHash();
                String path = mediaHashMap.get(mediaInfo.getMediaName());
                Intent intent = new Intent(mContext, MediaPlayerActivity.class);
                intent.putExtra("path",path);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MediaInfo mediaInfo = mMediaInfos.get(i);
        viewHolder.mediaImage.setImageBitmap(mediaInfo.getMediaImage());
        viewHolder.mediaName.setText(mediaInfo.getMediaName());
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: "+mMediaInfos.size());
        return mMediaInfos.size();
    }
}
