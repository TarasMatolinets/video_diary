package com.mti.videodiary.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import mti.com.videodiary.R;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static com.mti.videodiary.data.Constants.RESULT_LOAD_IMAGE;

/**
 * Created by Taras Matolinets on 24.03.15.
 */
public class DialogTakePictureFragment extends DialogFragment {

    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choice_menu, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mUnbinder.unbind();
        dismiss();
    }

    @OnClick(R.id.bt_okay)
    public void okClick() {
        Intent i = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @OnClick(R.id.bt_cancel)
    public void cancelClick() {
        dismiss();
    }
}