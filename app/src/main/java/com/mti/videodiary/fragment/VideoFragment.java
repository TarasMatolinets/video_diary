package com.mti.videodiary.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.adapter.VideoAdapter;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.DataBaseManager;
import com.mti.videodiary.data.dao.Video;

import org.lucasr.twowayview.widget.SpannableGridLayoutManager;

import java.io.File;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class VideoFragment extends BaseFragment implements View.OnClickListener, TextWatcher {
    private static final int VIDEO_CAPTURE = 101;
    private static final String FILE_FORMAT = ".mp4";

    private String VIDEO_FILE_NAME = BaseActivity.DIVIDER + "video-daily" + FILE_FORMAT;

    private View mView;
    private ScrollView mCardView;
    private ImageView mIvThumbnail;
    private ImageView mIvDone;
    private ImageView ivClose;
    private EditText mEtTitle;
    private EditText mEtDescription;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private ButtonFloat mButtonFloat;
    private ImageView mIvCameraOff;
    private TextView mTvNoRecords;
    private String mVideoFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        DataBaseManager.init(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video, container, false);

        initViews();
        initListeners();
        setupRecycleView();
        showEmptyView();

        checkCamera();

        return mView;
    }

    private void showEmptyView() {
        final List<Video> listVideos = DataBaseManager.getInstance().getAllVideosList();

        if (listVideos.isEmpty()) {
            mIvCameraOff.setVisibility(View.VISIBLE);
            mTvNoRecords.setVisibility(View.VISIBLE);
        } else {
            mIvCameraOff.setVisibility(View.GONE);
            mTvNoRecords.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        mIvCameraOff = (ImageView) mView.findViewById(R.id.ivCameraOff);
        mTvNoRecords = (TextView) mView.findViewById(R.id.tvNoRecords);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.videoRecycleView);
        mCardView = (ScrollView) mView.findViewById(R.id.scrollCard);
        mEtTitle = (EditText) mCardView.findViewById(R.id.etTitle);
        mEtDescription = (EditText) mCardView.findViewById(R.id.etDescription);
        mIvThumbnail = (ImageView) mCardView.findViewById(R.id.ivVideoThumbnail);
        mIvDone = (ImageView) mCardView.findViewById(R.id.ivDone);
        ivClose = (ImageView) mCardView.findViewById(R.id.ivClose);
        mButtonFloat = (ButtonFloat) mView.findViewById(R.id.buttonFloat);
    }

    private void initListeners() {
        ivClose.setOnClickListener(this);
        mIvDone.setOnClickListener(this);
        mEtTitle.addTextChangedListener(this);
        mButtonFloat.setOnClickListener(this);
    }

    private void setupRecycleView() {
        final List<Video> listVideos = DataBaseManager.getInstance().getAllVideosList();

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new VideoAdapter(getActivity(), listVideos);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void checkCamera() {
        if (!hasCamera())
            mButtonFloat.setEnabled(false);
        else
            Crouton.makeText(getActivity(), R.string.fragment_broken_camera_warning, Style.ALERT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager.setSpanCount(1);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_video, menu);
    }

    private boolean hasCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFloat:

                final File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + BaseActivity.DIVIDER + BaseActivity.VIDEO_DAILY_DIRECTORY + BaseActivity.DIVIDER + BaseActivity.VIDEO_DIR + VIDEO_FILE_NAME + FILE_FORMAT);

                Uri fileUri = Uri.fromFile(mediaFile);

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult(intent, VIDEO_CAPTURE);

                break;

            case R.id.ivClose:
                animateZoomOutCard();
                break;
            case R.id.ivDone:
                animateZoomOutCard();

                VIDEO_FILE_NAME = mEtTitle.getText().toString();

                File oldFileName = new File(mVideoFilePath);
                File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + BaseActivity.VIDEO_DAILY_DIRECTORY + BaseActivity.VIDEO_DIR + BaseActivity.DIVIDER + VIDEO_FILE_NAME + FILE_FORMAT);

                boolean success = oldFileName.renameTo(newFileName);

                if (success) {
                    Video video = new Video();

                    video.setVideoUrl(newFileName.getAbsolutePath());
                    video.setTitle(mEtTitle.getText().toString());
                    video.setDescription(mEtDescription.getText().toString());

                    DataBaseManager.getInstance().createVideo(video);

                    final List<Video> listVideos = DataBaseManager.getInstance().getAllVideosList();
                    mAdapter.setListVideos(listVideos);

                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void animateZoomOutCard() {
        mCardView.setVisibility(View.GONE);

        YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomOut);
        composer.duration(1500);
        composer.playOn(mCardView);

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                mCardView.setVisibility(View.VISIBLE);

                mEtTitle.setText("");
                mEtDescription.setText("");

                getActivity().invalidateOptionsMenu();

                YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomIn);
                composer.duration(1500);
                composer.playOn(mCardView);

                final Uri videoUri = data.getData();
                mVideoFilePath = videoUri.getPath();

                final File file = new File(mVideoFilePath);

                mIvThumbnail.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = mIvThumbnail.getWidth();
                        int height = mIvThumbnail.getHeight();

                        if (width > 0 && height > 0) {

                            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            Bitmap newImage = Bitmap.createScaledBitmap(bMap, width, height, false);

                            mIvThumbnail.setImageBitmap(newImage);
                        }
                    }
                });

                Log.i(VideoDiaryApplication.TAG, "Video has been saved to: " + data.getData());
            } else if (resultCode == getActivity().RESULT_CANCELED) {

                Log.w(VideoDiaryApplication.TAG, "Video recording cancelled.");
            } else {

                Log.e(VideoDiaryApplication.TAG, "Failed to record video");
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCardView.getVisibility() == View.VISIBLE)
            ((MenuActivity) getActivity()).getSupportActionBar().hide();
        else
            ((MenuActivity) getActivity()).getSupportActionBar().show();
    }

    public void onBackPress() {

        if (mCardView.getVisibility() == View.VISIBLE) {
            mCardView.setVisibility(View.GONE);

            getActivity().invalidateOptionsMenu();

            YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomOut);
            composer.duration(1500);
            composer.playOn(mCardView);
        } else
            getActivity().moveTaskToBack(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEtTitle.getText().length() > 0)
            mIvDone.setVisibility(View.VISIBLE);
        else
            mIvDone.setVisibility(View.GONE);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
