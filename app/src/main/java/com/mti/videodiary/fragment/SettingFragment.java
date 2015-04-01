package com.mti.videodiary.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gc.materialdesign.widgets.SnackBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.dialog.DialogAvatarFragment;
import com.mti.videodiary.interfaces.OnDialogClickListener;
import com.mti.videodiary.utils.Constants;
import com.mti.videodiary.utils.UserHelper;
import com.mti.videodiary.utils.VideoDairySharePreferences;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.02.15.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener, TextWatcher, OnDialogClickListener {

    private EditText mName;
    private RoundedImageView mAvatar;
    private ActionBar mActionBar;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);

        initViews();
        initActionBar();
        initListeners();
        fillData();

        return mView;
    }

    private void fillData() {
        String name = VideoDairySharePreferences.getSharedPreferences().getString(Constants.KEY_PERSON_NAME, null);
        String avatarPath = VideoDairySharePreferences.getSharedPreferences().getString(Constants.IMAGE_AVATAR, null);

        mName.setText(name);
        setAvatar(avatarPath);
    }

    private void initListeners() {
        mAvatar.setOnClickListener(this);
        mName.addTextChangedListener(this);
    }

    private void initActionBar() {
        mActionBar = ((MenuActivity) getActivity()).getSupportActionBar();
        mActionBar.setTitle(R.string.frag_setting_title);
    }

    private void initViews() {
        mName = (EditText) mView.findViewById(R.id.etPersonalName);
        mAvatar = (RoundedImageView) mView.findViewById(R.id.ivAvatar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_setting, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAvatar:
                DialogAvatarFragment dialog = new DialogAvatarFragment();
                dialog.setDialogClickListener(this);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void dialogWithDataClick(Object object) {
        String photoPath = (String) object;

        setAvatar(photoPath);
    }

    private void setAvatar(String photoPath) {
        if (photoPath != null) {
            Bitmap bitmap = UserHelper.decodeSampledBitmapFromResource(photoPath);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);

            mAvatar.setBackground(drawable);
        } else
            showSnackView();
    }

    private void showSnackView() {
        SnackBar snackbar = new SnackBar(getActivity(), getResources().getString(R.string.error_picture), null, null);
        snackbar.setBackgroundSnackBar(getResources().getColor(R.color.blue));
        snackbar.show();
    }

    @Override
    public void dialogClick() {

    }
}
