package com.mti.videodialy.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.mti.videodialy.adapter.VideoAdapter;
import com.mti.videodialy.data.DataBaseManager;
import com.mti.videodialy.data.dao.Video;
import com.mti.videodialy.fragment.VideoFragment;
import com.mti.videodialy.utils.UserHelper;

import java.io.File;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 01.03.15.
 */
public class CreateVideoNoteActivity extends BaseActivity implements TextWatcher {

    private ImageView mIvThumbnail;
    private EditText mEtTitle;
    private EditText mEtDescription;
    private boolean isShowSave;
    private ActionBar mActionBar;
    private boolean isEditVideoDaily;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBaseManager.init(this);

        setContentView(R.layout.fragment_create_video_note);

        initViews();
        initListeners();

        setDataToView();
        initActionBar();
    }


    private void initActionBar() {
        mActionBar = getSupportActionBar();

        mActionBar.setTitle(R.string.create_video_note);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.show();
    }

    private void setDataToView() {
        int position = getIntent().getIntExtra(VideoFragment.KEY_POSITION, -1);

        String videoFilePath;

        if (position != -1) {
            isEditVideoDaily = true;
            Video video = DataBaseManager.getInstance().getVideoByPosition(position);
            videoFilePath = video.getVideoName();

            mEtTitle.setText(video.getTitle());
            mEtDescription.setText(video.getDescription());
        } else
            videoFilePath = getIntent().getStringExtra(VideoFragment.KEY_VIDEO_PATH);

        final File file = new File(videoFilePath);

        mIvThumbnail.post(new Runnable() {
            @Override
            public void run() {
                int width = mIvThumbnail.getWidth();
                int height = mIvThumbnail.getHeight();

                if (width > 0 && height > 0) {

                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);

                    Bitmap newImage = UserHelper.cropImage(bMap, width, height);

                    mIvThumbnail.setImageBitmap(newImage);
                }
            }
        });
    }

    private void initViews() {
        mEtTitle = (EditText) findViewById(R.id.etTitle);
        mEtDescription = (EditText) findViewById(R.id.etDescription);
        mIvThumbnail = (ImageView) findViewById(R.id.ivVideoThumbnail);
    }

    private void initListeners() {
        mEtTitle.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_video_note, menu);

        MenuItem item = menu.getItem(0);
        item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.getItem(0);
        if (isShowSave)
            item.setVisible(true);
        else
            item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                saveVideoNote();
                break;
            case android.R.id.home:
                deleteVideoFile();

                finish();
                break;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteVideoFile();
    }

    private void deleteVideoFile() {
        String videoFilePath = getIntent().getStringExtra(VideoFragment.KEY_VIDEO_PATH);

        if (videoFilePath != null) {
            File file = new File(videoFilePath);
            if (file.exists())
                file.delete();
        }
    }

    private void saveVideoNote() {
        if (!isEditVideoDaily) {
            createNewVideoDaily();
        } else
            updateVideoDaily();

        setResult(RESULT_OK);

        finish();
    }

    private void updateVideoDaily() {
        int position = getIntent().getIntExtra(VideoFragment.KEY_POSITION, -1);

        Video video = DataBaseManager.getInstance().getVideoByPosition(position);
        video.setDescription(mEtDescription.getText().toString());
        video.setTitle(mEtTitle.getText().toString());

        DataBaseManager.getInstance().updateVideoList(video);
    }

    private void createNewVideoDaily() {
        String videoFilePath = getIntent().getExtras().getString(VideoFragment.KEY_VIDEO_PATH);

        VideoFragment.VIDEO_FILE_NAME = mEtTitle.getText().toString();

        Bitmap bitmap = ((BitmapDrawable) mIvThumbnail.getDrawable()).getBitmap();

        File oldFileName = new File(videoFilePath);
        File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + BaseActivity.APPLICATION_DIRECTORY + BaseActivity.VIDEO_DIR + File.separator + VideoFragment.VIDEO_FILE_NAME + VideoFragment.FILE_FORMAT);

        boolean success = oldFileName.renameTo(newFileName);

        if (success) {
            Video video = new Video();

            video.setVideoUrl(newFileName.getAbsolutePath());
            video.setTitle(mEtTitle.getText().toString());
            video.setDescription(mEtDescription.getText().toString());

            String tempBitmapPath = UserHelper.saveBitmapToSD(bitmap);
            //we can use decodeSampledBitmapFromResource when we have stored bitmap in sd otherwise bitmap  will be null
            Bitmap finalBitmap = UserHelper.decodeSampledBitmapFromResource(tempBitmapPath);

            String finalPathBitmap = UserHelper.saveBitmapToSD(finalBitmap);

            File file = new File(tempBitmapPath);

            if (file.exists())
                file.delete();

            video.setImageUrl(finalPathBitmap);

            DataBaseManager.getInstance().createVideo(video);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEtTitle.getText().length() > 0)
            isShowSave = true;
        else
            isShowSave = false;

        invalidateOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}