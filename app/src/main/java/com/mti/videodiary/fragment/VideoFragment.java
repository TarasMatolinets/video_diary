package com.mti.videodiary.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.application.VideoDiaryApplication;

import java.io.File;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class VideoFragment extends BaseFragment implements View.OnClickListener, TextWatcher {
    private static final int VIDEO_CAPTURE = 101;
    private static final int THUMB_SIZE = 200;

    private String VIDEO_NAME = "/video-daily.mp4";


    private View mView;
    private CardView mCardView;
    private ImageView mIvThumbnail;
    private ImageView mIvDone;
    private ImageView ivClose;
    private EditText mEtTitle;
    private EditText mEtDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video, container, false);

        ButtonFloat buttonFloat = (ButtonFloat) mView.findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(this);

        mCardView = (CardView) mView.findViewById(R.id.cardViewCreateVideo);

        mEtTitle = (EditText) mCardView.findViewById(R.id.etTitle);
        mEtDescription = (EditText) mCardView.findViewById(R.id.etDescription);
        mIvThumbnail = (ImageView) mCardView.findViewById(R.id.ivVideoThumbnail);
        mIvDone = (ImageView) mCardView.findViewById(R.id.ivDone);
        ivClose = (ImageView) mCardView.findViewById(R.id.ivClose);

        ivClose.setOnClickListener(this);
        mIvDone.setOnClickListener(this);
        mEtTitle.addTextChangedListener(this);

        if (!hasCamera())
            buttonFloat.setEnabled(false);
        else
            Crouton.makeText(getActivity(), R.string.fragment_broken_camera_warning, Style.ALERT);

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar_video, menu);
    }

    private boolean hasCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
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

                final File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + BaseActivity.VIDEO_DAILY_DIRECTORY + BaseActivity.VIDEO_DIR + VIDEO_NAME);

                Uri fileUri = Uri.fromFile(mediaFile);

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult(intent, VIDEO_CAPTURE);

                break;

            case R.id.ivClose:
                animateZoomOutCard();
                break;
            case R.id.ivDone:
                animateZoomOutCard();
                break;
        }
    }

    private void animateZoomOutCard() {
        mCardView.setVisibility(View.GONE);

        YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomOut);
        composer.duration(1500);
        composer.playOn(mCardView);

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                mCardView.setVisibility(View.VISIBLE);

                mEtTitle.setText("");
                mEtDescription.setText("");

                getActivity().invalidateOptionsMenu();

                YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomIn);
                composer.duration(1500);
                composer.playOn(mCardView);

                final Uri videoUri = data.getData();

                final File file = new File(videoUri.getPath());

                mIvThumbnail.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = mIvThumbnail.getWidth();
                        int height = mIvThumbnail.getHeight();

                        if (width > 0 && height > 0) {

                            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            Bitmap newImage = Bitmap.createScaledBitmap(bMap, width, height, false);

                            mIvThumbnail.setImageBitmap(newImage);
                        }
                    }
                });

                Log.i(VideoDiaryApplication.TAG, "Video has been saved to: " + data.getData());
            } else if (resultCode == getActivity().RESULT_CANCELED) {

                Log.w(VideoDiaryApplication.TAG, "Video recording cancelled.");
            } else {

                Log.e(VideoDiaryApplication.TAG, "Failed to record video");
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCardView.getVisibility() == View.VISIBLE)
            ((MenuActivity) getActivity()).getSupportActionBar().hide();
        else
            ((MenuActivity) getActivity()).getSupportActionBar().show();
    }

    public void onBackPress() {

        if (mCardView.getVisibility() == View.VISIBLE) {
            mCardView.setVisibility(View.GONE);

            YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomOut);
            composer.duration(1500);
            composer.playOn(mCardView);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEtTitle.getText().length() > 0)
            mIvDone.setVisibility(View.VISIBLE);
        else
            mIvDone.setVisibility(View.GONE);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
