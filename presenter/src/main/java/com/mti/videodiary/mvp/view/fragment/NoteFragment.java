package com.mti.videodiary.mvp.view.fragment;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
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
import com.mti.videodiary.mvp.view.adapter.NoteAdapter;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.di.component.FragmentComponent;
import com.mti.videodiary.di.module.FragmentModule;
import com.mti.videodiary.mvp.presenter.CreateNotePresenter.NoteText;
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
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.support.v7.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;
import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static android.view.Surface.ROTATION_90;
import static com.mti.videodiary.utils.Constants.UPDATE_NOTE_ADAPTER;

/**
 * Created by Taras Matolinets on 23.02.15.
 * Screen for present saved notes
 */
public class NoteFragment extends BaseFragment implements OnQueryTextListener, OnCloseListener {

    private static final long DURATION = 1500;

    @Inject NoteFragmentPresenter mPresenter;

    @BindView(R.id.coor_layout_create_note) CoordinatorLayout mCoordinateLayout;
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
        mPresenter.loadNoteList();

        return view;
    }

    private void configureRecycleView() {
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == ROTATION_90) {
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
    public void deleteItem(final DeleteItem item) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_item)
                .setMessage(R.string.delete_note_description)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteNoteItem(item.getId(), item.getNotePosition());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Subscribe
    public void uodateNote(UpdateNote item) {
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        intent.putExtra(Constants.KEY_POSITION, item.getNoteId());
        startActivity(intent);
    }

    @Subscribe
    public void showNoteAction(NoteText noteText) {
        Snackbar snackbar = Snackbar.make(mCoordinateLayout, noteText.getText(), LENGTH_SHORT);
        snackbar.show();

        mPresenter.loadNoteList();
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
        search.setOnCloseListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!TextUtils.isEmpty(s)) {
            mPresenter.loadSearchedNotes(s);
        } else {
            mPresenter.loadNoteList();
        }
        return false;
    }

    public void loadQueryNotes(List<NoteDomain> list) {
        mAdapter.setListNotes(list);
    }

    public void removeNoteFromList(int id) {
        mAdapter.removeNote(id);
    }

    @Override
    public boolean onClose() {
        mPresenter.loadNoteList();
        return false;
    }

    // region INNER CLASS
    public static class UpdateNote {
        private int noteId;

        public int getNoteId() {
            return noteId;
        }

        public void setNoteId(int noteId) {
            this.noteId = noteId;
        }
    }

    public static class DeleteItem {

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
    //endregion
}
