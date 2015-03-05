package com.mti.videodialy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.mti.videodialy.activity.CreateVideoNoteActivity;
import com.mti.videodialy.activity.MenuActivity;
import com.mti.videodialy.data.DataBaseManager;
import com.mti.videodialy.data.dao.Video;
import com.mti.videodialy.fragment.VideoFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.02.15.
 */
public class VideoAdapter extends RecyclerSwipeAdapter<VideoAdapter.ViewHolder> implements View.OnClickListener {

    private static final String FILE = "file:///";
    public static final String KEY_POSITION = "com.mti.position.key";
    private Context mContext;
    private List<Video> mListVideos;
    private View view;

    public VideoAdapter(Context context, List<Video> listVideos) {
        mContext = context;
        mListVideos = listVideos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_view_video, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Video video = mListVideos.get(position);

        holder.delete.setTag(position);
        holder.flMain.setTag(position);

        holder.tvDescription.clearFocus();
        holder.tvTitle.clearFocus();

        holder.tvDescription.setText(video.getDescription());
        holder.tvTitle.setText(video.getTitle());

        ImageLoader.getInstance().displayImage(FILE + video.getImageUrl(), holder.imIcon, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.imIcon.setImageBitmap(loadedImage);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListVideos.size();
    }

    public void setListVideos(List<Video> list) {
        mListVideos = list;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

        switch (v.getId()) {
            case R.id.trash:
                Video video = mListVideos.get(position);

                File videoFile = new File(video.getVideoName());
                File fileImage = new File(video.getImageUrl());

                deleteFile(videoFile);
                deleteFile(fileImage);
                DataBaseManager.getInstance().deleteVideoById(video.getId());

                mListVideos.remove(video);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mListVideos.size());

                Intent intent = new Intent(VideoFragment.UPDATE_ADAPTER);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                break;
            case R.id.flMain:
                Intent activityIntent = new Intent(mContext, CreateVideoNoteActivity.class);
                activityIntent.putExtra(KEY_POSITION, position);
                ((MenuActivity) mContext).startActivityForResult(activityIntent, MenuActivity.UPDATE_VIDEO_ADAPTER);
                break;
        }
    }

    private void deleteFile(File file) {
        if (file.exists())
            file.delete();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText tvTitle;
        public EditText tvDescription;
        public ImageView imIcon;
        public ImageView delete;
        public CardView cardView;
        public FrameLayout flMain;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvDescription = (EditText) itemLayoutView.findViewById(R.id.etDescription);
            tvTitle = (EditText) itemLayoutView.findViewById(R.id.etTitle);
            imIcon = (ImageView) itemLayoutView.findViewById(R.id.ivVideoThumbnail);
            delete = (ImageView) itemLayoutView.findViewById(R.id.trash);
            cardView = (CardView) itemLayoutView.findViewById(R.id.cardViewCreateVideo);
            flMain = (FrameLayout) itemLayoutView.findViewById(R.id.flMain);

            flMain.setOnClickListener(VideoAdapter.this);
            delete.setOnClickListener(VideoAdapter.this);
        }
    }
}
