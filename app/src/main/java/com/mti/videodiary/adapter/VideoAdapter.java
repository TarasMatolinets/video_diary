package com.mti.videodiary.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.activity.CreateVideoNoteActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.dao.Video;
import com.mti.videodiary.data.manager.VideoDataManager;
import com.mti.videodiary.dialog.DeleteItemDialogFragment;
import com.mti.videodiary.interfaces.OnDialogClickListener;
import com.mti.videodiary.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.02.15.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> implements View.OnClickListener, OnDialogClickListener {

    private static final String FILE_UNIVERSAL_LOADER = "file:///";
    private static final String FILE_PLAY_VIDEO = "file://";
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
        holder.edit.setTag(position);
        holder.share.setTag(position);
        holder.play.setTag(position);

        holder.tvDescription.clearFocus();
        holder.tvTitle.clearFocus();

        if (!TextUtils.isEmpty(video.getDescription())) {
            holder.tvDescription.setText(video.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.viewDivider.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
            holder.viewDivider.setVisibility(View.VISIBLE);
        }
        holder.tvTitle.setText(video.getTitle());

        ImageLoader.getInstance().displayImage(FILE_UNIVERSAL_LOADER + video.getImageUrl(), holder.imIcon, new SimpleImageLoadingListener() {
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
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_POSITION_NOTE_ADAPTER, position);

                DeleteItemDialogFragment fragment = new DeleteItemDialogFragment();
                fragment.setDialogClickListener(this);
                fragment.setArguments(bundle);
                fragment.show(((MenuActivity) mContext).getSupportFragmentManager(), null);
                break;

            case R.id.ivPlay:
                try {
                    VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
                    Video video = videoDataManager.getVideoByPosition(position);
                    String videoFilePath = FILE_PLAY_VIDEO + video.getVideoName();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoFilePath));
                    intent.setDataAndType(Uri.parse(videoFilePath), "video/mp4");
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    SnackBar snackbar = new SnackBar((Activity) mContext, mContext.getString(R.string.error_play_video), null, null);
                    snackbar.setBackgroundSnackBar(mContext.getResources().getColor(R.color.blue));
                    snackbar.show();
                }

                break;
            case R.id.ivEdit:
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                int orientation = mContext.getResources().getConfiguration().orientation;

                Intent activityIntent = new Intent(mContext, CreateVideoNoteActivity.class);

                activityIntent.putExtra(Constants.KEY_POSITION, position).
                        putExtra(BaseActivity.PACKAGE + Constants.ORIENTATION, orientation).
                        putExtra(BaseActivity.PACKAGE + ".left", screenLocation[0]).
                        putExtra(BaseActivity.PACKAGE + ".top", screenLocation[1]).
                        putExtra(BaseActivity.PACKAGE + ".width", v.getWidth()).
                        putExtra(BaseActivity.PACKAGE + ".height", v.getHeight());

                ((MenuActivity) mContext).startActivityForResult(activityIntent, Constants.UPDATE_VIDEO_ADAPTER);

                ((Activity) mContext).overridePendingTransition(0, 0);
                break;
            case R.id.ivShare:
                Video videoForShare = mListVideos.get(position);

                ContentValues content = new ContentValues(4);
                content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000);
                content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                content.put(MediaStore.Video.Media.DATA, videoForShare.getVideoName());
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = resolver.insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, content);

                if (uri == null)
                    uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Title");
                sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);

                mContext.startActivity(Intent.createChooser(sharingIntent, mContext.getResources().getString(R.string.share_text_video)));
                break;
        }
    }


    private void deleteFile(File file) {
        if (file.exists())
            file.delete();
    }

    @Override
    public void dialogWithDataClick(Object object) {
        int position = (Integer) object;

        Video video = mListVideos.get(position);

        File videoFile = new File(video.getVideoName());
        File fileImage = new File(video.getImageUrl());

        deleteFile(videoFile);
        deleteFile(fileImage);
        VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
        videoDataManager.deleteVideoById(video.getId());

        mListVideos.remove(video);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mListVideos.size());

        Intent intent = new Intent(Constants.UPDATE_ADAPTER_INTENT);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void dialogClick() {
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView imIcon;
        public ImageView delete;
        public ImageView play;
        public ImageView share;
        public ImageView edit;
        public CardView cardView;
        public FrameLayout flMain;

        public View viewDivider;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvDescription = (TextView) itemLayoutView.findViewById(R.id.etDescription);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.etTitle);
            imIcon = (ImageView) itemLayoutView.findViewById(R.id.ivVideoThumbnail);
            share = (ImageView) itemLayoutView.findViewById(R.id.ivShare);
            delete = (ImageView) itemLayoutView.findViewById(R.id.trash);
            play = (ImageView) itemLayoutView.findViewById(R.id.ivPlay);
            edit = (ImageView) itemLayoutView.findViewById(R.id.ivEdit);
            cardView = (CardView) itemLayoutView.findViewById(R.id.cardViewCreateVideo);
            flMain = (FrameLayout) itemLayoutView.findViewById(R.id.flMain);
            viewDivider = (View) itemLayoutView.findViewById(R.id.viewDivider);

            play.setOnClickListener(VideoAdapter.this);
            edit.setOnClickListener(VideoAdapter.this);
            delete.setOnClickListener(VideoAdapter.this);
            share.setOnClickListener(VideoAdapter.this);
        }
    }
}
