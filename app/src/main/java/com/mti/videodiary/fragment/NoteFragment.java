package com.mti.videodiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import mti.com.videodiary.R;

/**
 * Created by taras on 23.02.15.
 */
public class NoteFragment extends BaseFragment {

    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note, container, false);

        initViews();

        return mView;
    }

    private void initViews() {

        ImageView note = (ImageView) mView.findViewById(R.id.ivNote);
        TextView noteTitle = (TextView) mView.findViewById(R.id.tvNoNotes);

        YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.ZoomIn);
        personalAnim.duration(DURATION);
        personalAnim.playOn(note);
        personalAnim.playOn(noteTitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_note, menu);
    }

}
