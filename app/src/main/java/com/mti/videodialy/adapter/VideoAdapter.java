package com.mti.videodialy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mti.videodialy.data.dao.Video;

import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.02.15.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private List<Video> mListVideos;

    public VideoAdapter(Context context, List<Video> listVideos) {
        mContext = context;
        mListVideos = listVideos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_view_video, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = mListVideos.get(position);

        holder.tvDescription.clearFocus();
        holder.tvTitle.clearFocus();

        holder.tvDescription.setText(video.getDescription());
        holder.tvTitle.setText(video.getTitle());
    }

    @Override
    public int getItemCount() {
        return mListVideos.size();
    }

    public void setListVideos(List<Video> list) {
        mListVideos = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EditText tvTitle;
        public EditText tvDescription;
        public ImageView imIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvTitle = (EditText) itemLayoutView.findViewById(R.id.etDescription);
            tvDescription = (EditText) itemLayoutView.findViewById(R.id.etTitle);
            imIcon = (ImageView) itemLayoutView.findViewById(R.id.ivVideoThumbnail);
        }
    }
}
