package com.mti.videodiary.mvp.view.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.di.component.FragmentComponent;
import com.mti.videodiary.di.module.FragmentModule;
import com.mti.videodiary.mvp.presenter.VideoFragmentPresenter;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;
import com.mti.videodiary.mvp.view.adapter.VideoAdapter;
import com.mti.videodiary.navigator.Navigator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import model.VideoDomain;
import mti.com.videodiary.R;

import static android.app.Activity.RESULT_OK;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.MEDIA_MOUNTED_READ_ONLY;
import static android.provider.MediaStore.ACTION_VIDEO_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import static android.provider.MediaStore.EXTRA_VIDEO_QUALITY;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.support.v7.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;
import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.data.Constants.KEY_VIDEO_PATH;
import static com.mti.videodiary.data.Constants.VIDEO_DIR;
import static com.mti.videodiary.data.Constants.VIDEO_FILE_NAME;

/**
 * Created by Taras Matolinets on 23.02.15.
 * View for present to user video notes
 */
public class VideoFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private static final String CONTENT_MEDIA = "/external/video";
    private static final long DURATION = 1500;

    @BindView(R.id.coor_layout_create_video_note) CoordinatorLayout mCoordinateLayout;
    @BindView(R.id.video_note_recycle_view) RecyclerView mRecyclerView;
    @BindView(R.id.iv_camera_off) ImageView mIvCameraOff;
    @BindView(R.id.tv_no_records) TextView mTvNoRecords;
    @BindView(R.id.ll_video_image) LinearLayout mLLImageVideo;
    @BindView(R.id.progress_load) ProgressBar mProgressLoadVideo;
    @BindView(R.id.ad_view_note) AdView mAdView;

    @Inject VideoFragmentPresenter mPresenter;
    @Inject Navigator mNavigator;

    private VideoAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private Unbinder mBinder;

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
        mPresenter.setView(this);

        mBinder = ButterKnife.bind(this, view);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setupRecycleView();
        mProgressLoadVideo.setVisibility(VISIBLE);
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

        createLayoutManager(display);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new VideoAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void createLayoutManager(Display display) {
        if (display.getRotation() == Surface.ROTATION_90) {
            mLayoutManager = new StaggeredGridLayoutManager(2, VERTICAL);
        } else {
            mLayoutManager = new StaggeredGridLayoutManager(1, VERTICAL);
        }
        mLayoutManager.setGapStrategy(GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(2);
        } else if (newConfig.orientation == ORIENTATION_PORTRAIT) {
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

    @OnClick(R.id.button_float)
    public void createVideoClick() {
        Intent intent = new Intent(ACTION_VIDEO_CAPTURE);
        intent.putExtra(EXTRA_VIDEO_QUALITY, 1);

        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == RESULT_OK && data != null) {
                    startVideoActivity(data);
                } else if (resultCode == RESULT_OK) {
                    Snackbar snackbar = Snackbar.make(mCoordinateLayout, getString(R.string.fragment_video_not_recorded_warning), LENGTH_SHORT);
                    snackbar.show();
                }
                break;
        }
    }

    @Subscribe
    public void showNoteAction(VideoNoteText videoNoteText) {
        Snackbar snackbar = Snackbar.make(mCoordinateLayout, videoNoteText.getText(), LENGTH_SHORT);
        snackbar.show();

        mPresenter.loadVideoNoteList();
    }

    private void startVideoActivity(Intent data) {
        final Uri videoUri = data.getData();
        String videoFilePath = UserHelper.getRealPathFromURI(getActivity(), videoUri);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_VIDEO_PATH, videoFilePath);

        mNavigator.replaceActivity(getActivity(), CreateVideoNoteActivity.class, bundle);
    }

    @Subscribe
    public void editVideoNote(EditVideoNote videoNote) {
        startActivity(videoNote.getIntentRequest());
        getActivity().overridePendingTransition(0, 0);
    }

    @Subscribe
    public void shareVideoNote(ShareVideoNote videoNote) {
        startActivity(Intent.createChooser(videoNote.getShareIntent(), getString(R.string.share_text_video)));
    }

    @Subscribe
    public void deleteItem(final DeleteVideoNote videoNote) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_item)
                .setMessage(R.string.delete_note_description)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteVideoNoteItem(videoNote.getId(), videoNote.getNotePosition());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!TextUtils.isEmpty(s)) {
            mPresenter.loadSearchedVideoNotes(s);
        } else {
            mPresenter.loadVideoNoteList();
        }

        return true;
    }

    public void showEmptyView(boolean showView) {
        mProgressLoadVideo.setVisibility(GONE);

        if (showView) {
            mLLImageVideo.setVisibility(VISIBLE);
            YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.ZoomIn);
            personalAnim.duration(DURATION);
            personalAnim.playOn(mIvCameraOff);
            personalAnim.playOn(mTvNoRecords);
        } else {
            mLLImageVideo.setVisibility(GONE);
        }
    }

    public void loadQueryNotes(List<VideoDomain> list) {
        mAdapter.updateList(list);
    }

    public void updateRecycleView(List<VideoDomain> list) {
        mAdapter.updateList(list);
    }

    public void removeNoteFromList(int id) {
        mAdapter.removeNote(id);
    }

    //region INNER CLASS
    public static class VideoNoteText {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class EditVideoNote {
        private Intent intentRequest;

        public Intent getIntentRequest() {
            return intentRequest;
        }

        public void setIntentRequest(Intent intentRequest) {
            this.intentRequest = intentRequest;
        }
    }

    public static class ShareVideoNote {
        private Intent shareIntent;

        public Intent getShareIntent() {
            return shareIntent;
        }

        public void setShareIntent(Intent shareIntent) {
            this.shareIntent = shareIntent;
        }
    }

    public static class DeleteVideoNote {

        private int id;
        private int notePosition;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getNotePosition() {
            return notePosition;
        }

        public void setNotePosition(int notePosition) {
            this.notePosition = notePosition;
        }
    }

    public static class UpdateVideoNoteList {
    }
    // endregion
}
