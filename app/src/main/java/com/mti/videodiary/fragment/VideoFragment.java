package com.mti.videodiary.fragment;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.activity.CreateVideoNoteActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.adapter.VideoAdapter;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.dao.Video;
import com.mti.videodiary.data.manager.VideoDataManager;
import com.mti.videodiary.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mti.com.videodiary.R;

import static android.view.View.OnClickListener;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class VideoFragment extends BaseFragment implements OnClickListener, SearchView.OnQueryTextListener {
    private static final int REQUEST_VIDEO_CAPTURE = 101;

    private View mView;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private ButtonFloat mButtonFloat;
    private ImageView mIvCameraOff;
    private TextView mTvNoRecords;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(Constants.UPDATE_ADAPTER_INTENT));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showEmptyView();
        }
    };


    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video, container, false);

        AdView mAdView = (AdView) mView.findViewById(R.id.adViewVideo);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        initViews();
        setupRecycleView();
        initListeners();
        showEmptyView();

        return mView;
    }

    private void showEmptyView() {

        VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);

        final List<Video> listVideos = videoDataManager.getAllVideosList();

        if (listVideos.isEmpty()) {
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

    private void initViews() {
        mIvCameraOff = (ImageView) mView.findViewById(R.id.ivCameraOff);
        mTvNoRecords = (TextView) mView.findViewById(R.id.tvNoRecords);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.videoRecycleView);

        mButtonFloat = (ButtonFloat) mView.findViewById(R.id.buttonFloat);
    }

    private void initListeners() {
        mButtonFloat.setOnClickListener(this);
    }

    private void setupRecycleView() {
        VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);

        final List<Video> listVideos = videoDataManager.getAllVideosList();

        mRecyclerView.setHasFixedSize(true);

        Display display = ((WindowManager) getActivity().getSystemService(MenuActivity.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getOrientation() == Surface.ROTATION_90)
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        else
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mAdapter = new VideoAdapter(getActivity(), listVideos);
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

    private boolean hasCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
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
                if (hasCamera()) {
                    Uri fileUri = Uri.fromFile(saveFileInStorage());

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                   // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                    startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
                } else {
                    SnackBar snackbar = new SnackBar(getActivity(), getResources().getString(R.string.fragment_broken_camera_warning), null, null);
                    snackbar.setBackgroundSnackBar(getResources().getColor(R.color.blue));
                    snackbar.show();
                }
                break;
        }
    }

    private File saveFileInStorage() {
        String state = Environment.getExternalStorageState();
        File mediaFile = null;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BaseActivity.APPLICATION_DIRECTORY + File.separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
          return  mediaFile = new File(getActivity().getFilesDir() + File.separator + BaseActivity.APPLICATION_DIRECTORY + File.separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    startVideoActivity(data);
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Log.w(VideoDiaryApplication.TAG, "Video recording cancelled.");
                } else {
                    Log.e(VideoDiaryApplication.TAG, "Failed to record video");
                }
                break;

            case Constants.UPDATE_VIDEO_ADAPTER:
                VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);

                final List<Video> listVideos = videoDataManager.getAllVideosList();

                mAdapter.setListVideos(listVideos);
                mAdapter.notifyDataSetChanged();

                showEmptyView();
                break;
        }
    }

    private void startVideoActivity(Intent data) {
        final Uri videoUri = data.getData();
        String videoFilePath = videoUri.getPath();

        Intent intent = new Intent(getActivity(), CreateVideoNoteActivity.class);
        intent.putExtra(Constants.KEY_VIDEO_PATH, videoFilePath);

        startActivityForResult(intent, Constants.UPDATE_VIDEO_ADAPTER);

        Log.i(VideoDiaryApplication.TAG, "Video has been saved to: " + data.getData());
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);

        final List<Video> listVideos = videoDataManager.getAllVideosList();

        ArrayList<Video> searchVideoList = new ArrayList<Video>();
        for (Video v : listVideos) {
            if (v.getTitle().contains(s))
                searchVideoList.add(v);
        }

        if (!searchVideoList.isEmpty())
            mAdapter.setListVideos(searchVideoList);

        mAdapter.notifyDataSetChanged();

        return true;
    }
}
