package com.mti.videodiary.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
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
import com.gc.materialdesign.views.ButtonFloat;
import com.mti.videodiary.activity.CreateNoteActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.adapter.NoteAdapter;
import com.mti.videodiary.data.dao.Note;
import com.mti.videodiary.data.dao.Video;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.manager.NoteDataManager;
import com.mti.videodiary.utils.Constants;

import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class NoteFragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private ButtonFloat mButtonFloat;
    private ImageView mIvNote;
    private TextView mTvNoNotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(Constants.UPDATE_ADAPTER_NOTE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note, container, false);

        initViews();
        setupRecycleView();
        initListeners();
        showEmptyView();

        return mView;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showEmptyView();
        }
    };


    private void initViews() {
        mIvNote = (ImageView) mView.findViewById(R.id.ivCameraOff);
        mTvNoNotes = (TextView) mView.findViewById(R.id.tvNoRecords);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.noteRecycleView);

        mButtonFloat = (ButtonFloat) mView.findViewById(R.id.buttonFloat);
    }

    private void initListeners() {
        mButtonFloat.setOnClickListener(this);
    }

    private void showEmptyView() {
        NoteDataManager noteManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
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
        NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
        final List<Note> listNotes = noteDataManager.getAllNotesList();

        mRecyclerView.setHasFixedSize(true);

        Display display = ((WindowManager) getActivity().getSystemService(MenuActivity.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getOrientation() == Surface.ROTATION_90)
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        else
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

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
            mLayoutManager.setSpanCount(2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager.setSpanCount(1);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFloat:
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_note, menu);
    }

}
