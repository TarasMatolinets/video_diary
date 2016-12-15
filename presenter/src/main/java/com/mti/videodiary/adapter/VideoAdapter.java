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

import com.mti.videodiary.mvp.view.BaseActivity;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;
import com.mti.videodiary.data.storage.dao.Video;
import com.mti.videodiary.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.VideoDomain;
import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.02.15.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private static final String FILE_UNIVERSAL_LOADER = "file:///";
    private static final String FILE_PLAY_VIDEO = "file://";
    private Context mContext;
    private List<VideoDomain> mListVideos = new ArrayList<>();
    private View view;

    public VideoAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_view_video, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        VideoDomain video = mListVideos.get(position);

        holder.tvDescription.clearFocus();
        holder.tvTitle.clearFocus();
        holder.tvTitle.setText(video.getTitle());

        if (!TextUtils.isEmpty(video.getDescription())) {
            holder.tvDescription.setText(video.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.viewDivider.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
            holder.viewDivider.setVisibility(View.VISIBLE);
        }

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

    public void setListVideos(List<VideoDomain> list) {
        mListVideos = list;
    }

    private void deleteFile(File file) {
        if (file.exists())
            file.delete();
    }

    public void updateList(List<VideoDomain> list) {
        mListVideos = list;
        notifyDataSetChanged();
    }
//
//    @Override
//    public void dialogWithDataClick(Object object) {
//        int position = (Integer) object;
//
//        Video video = mListVideos.get(position);
//
//        File videoFile = new File(video.getVideoName());
//        File fileImage = new File(video.getImageUrl());
//
//        deleteFile(videoFile);
//        deleteFile(fileImage);
//        VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//        videoDataManager.deleteVideoById(video.getId());
//
//        mListVideos.remove(video);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mListVideos.size());
//
//        Intent intent = new Intent(Constants.UPDATE_ADAPTER_INTENT);
//        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.et_title) TextView tvTitle;
        @BindView(R.id.etDescription) TextView tvDescription;
        @BindView(R.id.ivVideoThumbnail) ImageView imIcon;
        @BindView(R.id.cardViewCreateVideo) CardView cardView;
        @BindView(R.id.viewDivider) View viewDivider;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ButterKnife.bind(this, itemLayoutView);
        }

        @OnClick(R.id.ivPlay)
        public void playClick() {
//            try {
//                VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//                Video video = videoDataManager.getVideoById(position);
//                String videoFilePath = FILE_PLAY_VIDEO + video.getVideoName();
//
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoFilePath));
//                intent.setDataAndType(Uri.parse(videoFilePath), "video/mp4");
//                mContext.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                Crouton.makeText((Activity) mContext, mContext.getString(R.string.error_play_video), Style.ALERT).show();
//            }
        }

        @OnClick(R.id.ivEdit)
        public void editClick() {
//            int[] screenLocation = new int[2];
//            v.getLocationOnScreen(screenLocation);
//            int orientation = mContext.getResources().getConfiguration().orientation;
//
//            Intent activityIntent = new Intent(mContext, CreateVideoNoteActivity.class);
//
//            activityIntent.putExtra(Constants.KEY_POSITION, position).
//                    putExtra(BaseActivity.PACKAGE + Constants.ORIENTATION, orientation).
//                    putExtra(BaseActivity.PACKAGE + ".left", screenLocation[0]).
//                    putExtra(BaseActivity.PACKAGE + ".top", screenLocation[1]).
//                    putExtra(BaseActivity.PACKAGE + ".width", v.getWidth()).
//                    putExtra(BaseActivity.PACKAGE + ".height", v.getHeight());
//
//            ((MenuActivity) mContext).startActivityForResult(activityIntent, Constants.UPDATE_VIDEO_ADAPTER);
//
//            ((Activity) mContext).overridePendingTransition(0, 0);
        }

        @OnClick(R.id.trash)
        public void deleteClick() {
//            Bundle bundle = new Bundle();
//            bundle.putInt(Constants.KEY_POSITION_NOTE_ADAPTER, position);
//
//            DeleteItemDialogFragment fragment = new DeleteItemDialogFragment();
//            fragment.setDialogClickListener(this);
//            fragment.setArguments(bundle);
//            fragment.show(((MenuActivity) mContext).getSupportFragmentManager(), null);
        }

        @OnClick(R.id.ivShare)
        public void shareClick() {
//            Video videoForShare = mListVideos.get(position);
//
//            ContentValues content = new ContentValues(4);
//            content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
//                    System.currentTimeMillis() / 1000);
//            content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
//            content.put(MediaStore.Video.Media.DATA, videoForShare.getVideoName());
//            ContentResolver resolver = mContext.getContentResolver();
//            Uri uri = resolver.insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, content);
//
//            if (uri == null)
//                uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);
//
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("video/*");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Title");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
//
//            mContext.startActivity(Intent.createChooser(sharingIntent, mContext.getResources().getString(R.string.share_text_video)));
        }
    }
}
