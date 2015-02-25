package com.mti.videodiary.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.utils.UserHelper;

import java.io.File;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 23.02.15.
 */
public class VideoFragment extends BaseFragment implements View.OnClickListener {
    private static final int VIDEO_CAPTURE = 101;
    private static final int THUMB_SIZE = 200;

    private String VIDEO_NAME = "/video-daily.mp4";


    private View mView;
    private CardView mCardView;

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

        if (!hasCamera())
            buttonFloat.setEnabled(false);

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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                mCardView.setVisibility(View.VISIBLE);

                YoYo.AnimationComposer composer = YoYo.with(Techniques.ZoomIn);
                composer.duration(1500);
                composer.playOn(mCardView);

                final Uri videoUri = data.getData();

                final File file = new File(videoUri.getPath());

                final ImageView image = (ImageView) mCardView.findViewById(R.id.ivVideoThumbnail);

                image.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = image.getWidth();
                        int height = image.getHeight();

                        if (width > 0 && height > 0) {

                            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            Bitmap newImage = Bitmap.createScaledBitmap(bMap,width,height,false);

                            image.setImageBitmap(newImage);
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


}
