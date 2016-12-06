package com.mti.videodiary.mvp.view.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mti.videodiary.adapter.NoteAdapter;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.di.component.FragmentComponent;
import com.mti.videodiary.di.module.FragmentModule;
import com.mti.videodiary.dialog.DeleteItemDialogFragment.DeleteItem;
import com.mti.videodiary.mvp.presenter.NoteFragmentPresenter;
import com.mti.videodiary.mvp.view.activity.CreateNoteActivity;
import com.mti.videodiary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import model.NoteDomain;
import mti.com.videodiary.R;

import static android.content.Context.SEARCH_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.support.v7.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;
import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static android.view.Surface.ROTATION_90;
import static com.mti.videodiary.utils.Constants.UPDATE_NOTE_ADAPTER;

/**
 * Created by Taras Matolinets on 23.02.15.
 * Screen for present saved notes
 */
public class NoteFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final long DURATION = 1500;

    @Inject NoteFragmentPresenter mPresenter;

    @BindView(R.id.note_recycle_view) RecyclerView mRecyclerView;
    @BindView(R.id.ivCameraOff) ImageView mIvNote;
    @BindView(R.id.tvNoRecords) TextView mTvNoNotes;
    @BindView(R.id.adViewNote) AdView mAdView;

    private NoteAdapter mAdapter;
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
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        ActivityComponent component = getComponent(ActivityComponent.class);
        FragmentComponent fragmentComponent = component.plus(new FragmentModule(getActivity()));
        fragmentComponent.inject(this);

        mBinder = ButterKnife.bind(this, view);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        configureRecycleView();

        mPresenter.setView(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.loadNoteList();
    }

    private void configureRecycleView() {
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getOrientation() == ROTATION_90) {
            mLayoutManager = new StaggeredGridLayoutManager(3, VERTICAL);
        } else {
            mLayoutManager = new StaggeredGridLayoutManager(2, VERTICAL);
        }
        mLayoutManager.setGapStrategy(GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new NoteAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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
        EventBus.getDefault().unregister(this);
        mPresenter.destroy();
        mBinder.unbind();
        super.onDestroy();
    }

    @Subscribe
    public void deleteItem(DeleteItem item) {
        mPresenter.deleteNoteItem(item.getId(), item.getNotePosition());
    }

    public void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            mIvNote.setVisibility(View.VISIBLE);
            mTvNoNotes.setVisibility(View.VISIBLE);

            YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.ZoomIn);
            personalAnim.duration(DURATION);
            personalAnim.playOn(mIvNote);
            personalAnim.playOn(mTvNoNotes);
        } else {
            mIvNote.setVisibility(View.GONE);
            mTvNoNotes.setVisibility(View.GONE);
        }
    }

    public void setupRecycleView(List<NoteDomain> list) {
        mAdapter.setListNotes(list);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(3);
        } else if (newConfig.orientation == ORIENTATION_PORTRAIT) {
            mLayoutManager.setSpanCount(2);
        }
    }

    @OnClick(R.id.buttonFloat)
    public void addNoteClick() {
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        startActivityForResult(intent, UPDATE_NOTE_ADAPTER);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_note, menu);
        SearchManager manager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!TextUtils.isEmpty(s)) {
            mPresenter.loadSearchedNotes(s);
        }
        return false;
    }

    public void loadQueryNotes(List<NoteDomain> list) {
        mAdapter.setListNotes(list);
    }

    public void removeNoteFromList(int id) {
        mAdapter.removeNote(id);
    }
}
