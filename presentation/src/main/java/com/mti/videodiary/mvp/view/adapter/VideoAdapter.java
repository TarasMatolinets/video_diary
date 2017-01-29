package com.mti.videodiary.mvp.view.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mti.videodiary.data.storage.dao.Video;
import com.mti.videodiary.data.transformer.DataToDomainTransformer;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.DeleteVideoNote;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.EditVideoNote;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.ShareVideoNote;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.VideoDomain;
import mti.com.videodiary.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;
import static android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.data.Constants.HEIGHT;
import static com.mti.videodiary.data.Constants.KEY_POSITION;
import static com.mti.videodiary.data.Constants.ORIENTATION;
import static com.mti.videodiary.data.Constants.WIDTH;
import static com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity.VIDEO_MP4;

/**
 * Created by Taras Matolinets on 24.02.15.
 * Adapter for Adapter for handle video note data model and present it to user
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoDomain> mListVideos = new ArrayList<>();

    public VideoAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_view_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VideoDomain video = mListVideos.get(position);

        holder.tvDescription.clearFocus();
        holder.tvTitle.clearFocus();
        holder.tvTitle.setText(video.getTitle());

        if (!TextUtils.isEmpty(video.getDescription())) {
            holder.tvDescription.setText(video.getDescription());
            holder.tvDescription.setVisibility(VISIBLE);
            holder.viewDivider.setVisibility(VISIBLE);
        } else {
            holder.tvDescription.setVisibility(GONE);
            holder.viewDivider.setVisibility(VISIBLE);
        }

        getBitmapObservable(video)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                if (bitmap != null) {
                    holder.imIcon.setImageBitmap(bitmap);
                    holder.tvNoVideoRecord.setVisibility(GONE);
                    holder.ivPlay.setVisibility(VISIBLE);
                    holder.cardView.setVisibility(VISIBLE);
                } else {
                    holder.imIcon.setVisibility(GONE);
                    holder.tvNoVideoRecord.setVisibility(VISIBLE);
                    holder.ivPlay.setVisibility(GONE);
                    holder.cardView.setVisibility(VISIBLE);
                }
            }
        });
    }

    @NonNull
    private Observable<Bitmap> getBitmapObservable(final VideoDomain video) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap imageBitmap = ThumbnailUtils.createVideoThumbnail(video.getImageUrl(), MINI_KIND);
                subscriber.onNext(imageBitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListVideos.size();
    }

    public void updateList(List<VideoDomain> list) {
        mListVideos = list;
        notifyDataSetChanged();
    }

    public void removeNote(int id) {
        mListVideos.remove(id);
        notifyItemRemoved(id);
        notifyItemRangeChanged(id, mListVideos.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String VIDEO = "video/*";
        private static final String TITLE = "Title";

        @BindView(R.id.et_title) TextView tvTitle;
        @BindView(R.id.et_description) TextView tvDescription;
        @BindView(R.id.tv_no_video_note) TextView tvNoVideoRecord;
        @BindView(R.id.iv_video_thumbnail) ImageView imIcon;
        @BindView(R.id.cv_create_video) CardView cardView;
        @BindView(R.id.viewDivider) View viewDivider;
        @BindView(R.id.iv_play) ImageView ivPlay;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ButterKnife.bind(this, itemLayoutView);
        }

        @OnClick(R.id.iv_play)
        public void playClick() {
            VideoDomain video = mListVideos.get(getAdapterPosition());

            Intent intent = new Intent(ACTION_VIEW, Uri.parse(video.getVideoName()));
            intent.setDataAndType(Uri.parse(video.getVideoName()), VIDEO_MP4);
            mContext.startActivity(intent);
        }

        @OnClick(R.id.ivEdit)
        public void editClick() {
            int[] screenLocation = new int[2];
            cardView.getLocationOnScreen(screenLocation);
            int orientation = mContext.getResources().getConfiguration().orientation;

            Intent intent = new Intent(mContext, CreateVideoNoteActivity.class);

            VideoDomain video = mListVideos.get(getAdapterPosition());
            intent.putExtra(KEY_POSITION, video.getId());
            intent.putExtra(ORIENTATION, orientation);
            intent.putExtra(WIDTH, cardView.getWidth());
            intent.putExtra(HEIGHT, cardView.getHeight());

            EditVideoNote videoNote = new EditVideoNote();
            videoNote.setIntentRequest(intent);

            EventBus.getDefault().post(videoNote);
        }

        @OnClick(R.id.trash)
        public void deleteClick() {
            VideoDomain videoDomain = mListVideos.get(getAdapterPosition());
            DeleteVideoNote deleteItem = new DeleteVideoNote();
            deleteItem.setNotePosition(getAdapterPosition());
            deleteItem.setId(videoDomain.getId());

            EventBus.getDefault().post(deleteItem);
        }

        @OnClick(R.id.iv_share)
        public void shareClick() {
            VideoDomain videoForShare = mListVideos.get(getAdapterPosition());

            ContentValues content = new ContentValues(4);
            content.put(DATE_ADDED, System.currentTimeMillis() / 1000);
            content.put(MIME_TYPE, VIDEO_MP4);
            content.put(DATA, videoForShare.getVideoName());
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = resolver.insert(EXTERNAL_CONTENT_URI, content);

            Intent sharingIntent = new Intent(ACTION_SEND);
            sharingIntent.setType(VIDEO);
            sharingIntent.putExtra(EXTRA_SUBJECT, TITLE);
            sharingIntent.putExtra(EXTRA_STREAM, uri);

            ShareVideoNote shareVideoNote = new ShareVideoNote();
            shareVideoNote.setShareIntent(sharingIntent);

            EventBus.getDefault().post(shareVideoNote);
        }
    }
}
