package com.mti.videodiary.mvp.view.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.di.component.FragmentComponent;
import com.mti.videodiary.di.module.FragmentModule;
import com.mti.videodiary.mvp.presenter.VideoFragmentPresenter;
import com.mti.videodiary.mvp.view.BaseActivity;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;
import com.mti.videodiary.adapter.VideoAdapter;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.storage.dao.Video;
import com.mti.videodiary.data.storage.manager.DataBaseManager;
import com.mti.videodiary.utils.Constants;
import com.mti.videodiary.data.helper.UserHelper;
import com.software.shell.fab.ActionButton;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import model.VideoDomain;
import mti.com.videodiary.R;

import static android.provider.MediaStore.ACTION_VIDEO_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import static android.provider.MediaStore.EXTRA_VIDEO_QUALITY;
import static android.view.View.OnClickListener;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class VideoFragment extends BaseFragment implements OnClickListener, SearchView.OnQueryTextListener {
    private static final int REQUEST_VIDEO_CAPTURE = 101;
    public static final String CONTENT_MEDIA = "/external/video";
    private static final long DURATION = 1500;

    @Inject VideoFragmentPresenter mPresenter;

    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private ImageView mIvCameraOff;
    private TextView mTvNoRecords;
    private AdView mAdView;

    private Unbinder mBinder;

    @BindView(R.id.coor_layout_create_video_note) CoordinatorLayout mCoordinateLayout;
    @BindView(R.id.video_note_recycle_view) RecyclerView mRecyclerView;
    @BindView(R.id.ivCameraOff) ImageView mIvNote;
    @BindView(R.id.tvNoRecords) TextView mTvNoNotes;
    @BindView(R.id.adViewNote) AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        ActivityComponent component = getComponent(ActivityComponent.class);
        FragmentComponent fragmentComponent = component.plus(new FragmentModule(getActivity()));
        fragmentComponent.inject(this);

        mBinder = ButterKnife.bind(this, view);
        mPresenter.setView(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setupRecycleView();
        mPresenter.loadVideoNoteList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        mBinder.unbind();
        mPresenter.destroy();
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }


    public void setupRecycleView() {

        Display display = ((WindowManager) getActivity().getSystemService(MenuActivity.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_90) {
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new VideoAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager.setSpanCount(1);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_video, menu);

        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setOnQueryTextListener(this);
    }

    @OnClick(R.id.buttonFloat)
    public void createVideoClick() {
        Uri fileUri = Uri.fromFile(saveFileInStorage());

        Intent intent = new Intent(ACTION_VIDEO_CAPTURE);

        intent.putExtra(EXTRA_OUTPUT, fileUri);
        intent.putExtra(EXTRA_VIDEO_QUALITY, 1);

        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    private File saveFileInStorage() {
        String state = Environment.getExternalStorageState();
        File mediaFile = null;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BaseActivity.APPLICATION_DIRECTORY + File.separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mediaFile = new File(getActivity().getFilesDir() + File.separator + BaseActivity.APPLICATION_DIRECTORY + File.separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
        }
        return mediaFile;
    }


    private File createFileFromUri(Uri uri) {
        String state = Environment.getExternalStorageState();
        File mediaFile = null;
        String path = UserHelper.getRealPathFromURI(getActivity(), uri);
        File videoFile = new File(path);

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BaseActivity.APPLICATION_DIRECTORY + File.separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mediaFile = new File(getActivity().getFilesDir() + File.separator + BaseActivity.APPLICATION_DIRECTORY + File.separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
        }
        try {
            UserHelper.copyFileUsingFileStreams(videoFile, mediaFile);
        } catch (IOException e) {
            Log.e(VideoDiaryApplication.TAG, "exception " + e.toString());
        }

        if (videoFile.exists())
            videoFile.delete();

        return mediaFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    startVideoActivity(data);
                } else if (resultCode == Activity.RESULT_OK)
                    showSnackView();

                break;

            case Constants.UPDATE_VIDEO_ADAPTER:
//                VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//
//                final List<Video> listVideos = videoDataManager.getAllVideosList();
//
//                mAdapter.setListVideos(listVideos);
//                mAdapter.notifyDataSetChanged();
//
//                loadNoteList();
                break;
        }
    }

    private void showSnackView() {
//        Crouton.makeText(getActivity(), getResources().getString(R.string.fragment_video_not_recorded_warning), Style.ALERT).show();
    }

    private void startVideoActivity(Intent data) {
        final Uri videoUri = data.getData();
        String videoFilePath = videoUri.getPath();

        if (videoFilePath.contains(CONTENT_MEDIA)) {
            File file = createFileFromUri(videoUri);
            videoFilePath = file.getAbsolutePath();
        }

        Intent intent = new Intent(getActivity(), CreateVideoNoteActivity.class);
        intent.putExtra(Constants.KEY_VIDEO_PATH, videoFilePath);

        startActivityForResult(intent, Constants.UPDATE_VIDEO_ADAPTER);

        Log.i(VideoDiaryApplication.TAG, "Video has been saved to: " + videoFilePath);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//
//        final List<Video> listVideos = videoDataManager.getAllVideosList();
//
//        ArrayList<Video> searchVideoList = new ArrayList<Video>();
//        for (Video v : listVideos) {
//            if (v.getTitle().contains(s))
//                searchVideoList.add(v);
//        }
//
//        if (!searchVideoList.isEmpty())
//            mAdapter.setListVideos(searchVideoList);
//
//        mAdapter.notifyDataSetChanged();

        return true;
    }

    public void showEmptyView(boolean showView) {
        if (showView) {
            mIvCameraOff.setVisibility(View.VISIBLE);
            mTvNoRecords.setVisibility(View.VISIBLE);

            YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.ZoomIn);
            personalAnim.duration(DURATION);
            personalAnim.playOn(mIvCameraOff);
            personalAnim.playOn(mTvNoRecords);
        } else {
            mIvCameraOff.setVisibility(View.GONE);
            mTvNoRecords.setVisibility(View.GONE);
        }
    }

    public void loadQueryNotes(List<VideoDomain> list) {

    }
}
