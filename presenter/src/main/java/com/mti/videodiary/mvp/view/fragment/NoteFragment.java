package com.mti.videodiary.mvp.view.fragment;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.mvp.view.activity.CreateNoteActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;
import com.mti.videodiary.adapter.NoteAdapter;
import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.storage.manager.DataBaseManager;
import com.mti.videodiary.data.storage.manager.NoteDataManager;
import com.mti.videodiary.utils.Constants;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 23.02.15.
 * Screen for present saved notes
 */
public class NoteFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final long DURATION = 1500;
    @BindView(R.id.note_recycle_view) RecyclerView mRecyclerView;
    @BindView(R.id.buttonFloat) ActionButton mButtonFloat;
    @BindView(R.id.ivCameraOff) ImageView mIvNote;
    @BindView(R.id.tvNoRecords) TextView mTvNoNotes;
    @BindView(R.id.adViewNote) AdView mAdView;

    private NoteAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private Unbinder mBinder;

    @Inject DataBaseManager mDataBaseManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getComponent(ActivityComponent.class).inject(this);

        mBinder = ButterKnife.bind(this, getActivity());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter(Constants.UPDATE_ADAPTER_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setupRecycleView();
        showEmptyView();

        return view;
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

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        mBinder.unbind();
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showEmptyView();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.UPDATE_NOTE_ADAPTER:
                NoteDataManager noteDataManager = (NoteDataManager) mDataBaseManager.getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);

                final List<Note> notesList = noteDataManager.getAllNotesList();

                mAdapter.setListNotes(notesList);
                mAdapter.notifyDataSetChanged();

                showEmptyView();
                break;
        }
    }

    private void showEmptyView() {
        NoteDataManager noteManager = (NoteDataManager) mDataBaseManager.getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
        final List<Note> listNotes = noteManager.getAllNotesList();

        if (listNotes.isEmpty()) {
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


    private void setupRecycleView() {
        NoteDataManager noteDataManager = (NoteDataManager) mDataBaseManager.getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
        final List<Note> listNotes = noteDataManager.getAllNotesList();

        mRecyclerView.setHasFixedSize(true);

        Display display = ((WindowManager) getActivity().getSystemService(MenuActivity.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getOrientation() == Surface.ROTATION_90)
            mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        else
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mAdapter = new NoteAdapter(getActivity(), listNotes);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager.setSpanCount(2);
        }
    }

    @OnClick(R.id.buttonFloat)
    public void addNoteClick() {
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        startActivityForResult(intent, Constants.UPDATE_NOTE_ADAPTER);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_note, menu);
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

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
        NoteDataManager noteDataManager = (NoteDataManager) mDataBaseManager.getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);

        final List<Note> listNotes = noteDataManager.getAllNotesList();

        ArrayList<Note> searchNoteList = new ArrayList<Note>();
        for (Note note : listNotes) {
            if (note.getTitle().contains(s))
                searchNoteList.add(note);
        }

        if (!searchNoteList.isEmpty())
            mAdapter.setListNotes(searchNoteList);

        mAdapter.notifyDataSetChanged();
        return false;
    }
}
