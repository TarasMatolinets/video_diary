package com.mti.videodiary.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.activity.CreateVideoNoteActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.adapter.VideoAdapter;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.dao.Video;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.manager.VideoDataManager;
import com.mti.videodiary.utils.Constants;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import mti.com.videodiary.R;

import static android.view.View.OnClickListener;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class VideoFragment extends BaseFragment implements OnClickListener, SearchView.OnQueryTextListener {
    private static final int REQUEST_VIDEO_CAPTURE = 101;

    private View mView;
    private RecyclerView mRecyclerView;
    private static VideoAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private ActionButton mButtonFloat;
    private ImageView mIvCameraOff;
    private TextView mTvNoRecords;
    private AdView mAdView;

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
        if (mAdView != null)
            mAdView.destroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video, container, false);

        mAdView = (AdView) mView.findViewById(R.id.adViewVideo);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        initViews();
        setupRecycleView();
        initListeners();
        showEmptyView();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null)
            mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null)
            mAdView.pause();
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

        mButtonFloat = (ActionButton) mView.findViewById(R.id.buttonFloat);
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
                Uri fileUri = Uri.fromFile(saveFileInStorage());

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
                break;
        }
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
                VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);

                final List<Video> listVideos = videoDataManager.getAllVideosList();

                mAdapter.setListVideos(listVideos);
                mAdapter.notifyDataSetChanged();

                showEmptyView();
                break;
        }
    }

    private void showSnackView() {
        Crouton.makeText(getActivity(), getResources().getString(R.string.fragment_video_not_recorded_warning), Style.ALERT).show();
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
