package com.mti.videodiary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.mti.videodiary.dialog.TakePictureDialog;
import com.mti.videodiary.fragment.AboutMeFragment;
import com.mti.videodiary.fragment.NoteFragment;
import com.mti.videodiary.fragment.VideoFragment;
import com.mti.videodiary.utils.Constants;
import com.mti.videodiary.utils.UserHelper;
import com.mti.videodiary.utils.VideoDairySharePreferences;

import java.io.File;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import mti.com.videodiary.R;


public class MenuActivity extends MaterialNavigationDrawer implements View.OnClickListener {

    public static final int UPDATE_VIDEO_ADAPTER = 22;
    public static final int RESULT_LOAD_IMAGE = 133;
    private FrameLayout mFrameLayoutMain;
    private TextView mChoiceImage;

    @Override
    public void init(Bundle bundle) {

        // create and set the header
        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer_header, null);
        mFrameLayoutMain = (FrameLayout) view.findViewById(R.id.flMain);
        mChoiceImage = (TextView) view.findViewById(R.id.tvChoiceImage);
        setDrawerHeaderCustom(view);

        int selectedColor = getResources().getColor(R.color.blue);

        addSection(newSection(getString(R.string.menu_records), R.drawable.ic_videocam_black, new VideoFragment()).setSectionColor(selectedColor));
        addSection(newSection(getString(R.string.menu_notes), R.drawable.ic_note_add_black, new NoteFragment()).setSectionColor(selectedColor));
        addSection(newSection(getString(R.string.menu_about_me), R.drawable.ic_person_black, new AboutMeFragment()).setSectionColor(selectedColor));

        addBottomSection(newSection(getString(R.string.menu_settings), R.drawable.ic_settings_black, new SettingFragment()).setSectionColor(selectedColor));

        String videoFolder = BaseActivity.APPLICATION_DIRECTORY + BaseActivity.VIDEO_DIR;
        String noteFolder = BaseActivity.APPLICATION_DIRECTORY + BaseActivity.NOTE_DIR;
        String imageDir = BaseActivity.APPLICATION_DIRECTORY + BaseActivity.IMAGE_DIR;

        createFolder(videoFolder);
        createFolder(noteFolder);
        createFolder(imageDir);

        String picturePath = VideoDairySharePreferences.getSharedPreferences().getString(Constants.IMAGE_HEADER, null);

        if (picturePath != null)
            setImageInBackground(picturePath);

        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_CUSTOM);

        getHeaderView().setOnClickListener(this);
    }

    /**
     * create folders for feature files
     *
     * @param nameFolder folder name
     */
    private void createFolder(String nameFolder) {
        File f = new File(Environment.getExternalStorageDirectory(), nameFolder);

        if (!f.exists()) {
            f.mkdirs();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //close drawer on start
        if (isDrawerOpen())
            closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath != null) {
                setImageInBackground(picturePath);
                VideoDairySharePreferences.setDataToSharePreferences(Constants.IMAGE_HEADER, picturePath, VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING);
            } else {
                showSnackView();
            }
        } else if (requestCode == MenuActivity.UPDATE_VIDEO_ADAPTER ) {
            Fragment fragment = (Fragment) getCurrentSection().getTargetFragment();

            // update current video card data
            if (fragment instanceof VideoFragment)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            showSnackView();
        }
    }

    private void showSnackView() {
        SnackBar snackbar = new SnackBar(this, getResources().getString(R.string.error_picture), null, null);
        snackbar.show();
    }

    private void setImageInBackground(final String picturePath) {
        mChoiceImage.setVisibility(View.GONE);

        mFrameLayoutMain.post(new Runnable() {
            @Override
            public void run() {
                int width = mFrameLayoutMain.getWidth();
                int height = mFrameLayoutMain.getHeight();

                if (width > 0 && height > 0) {

                    Bitmap bitmap = UserHelper.decodeSampledBitmapFromResource(picturePath);

                    Bitmap newImage = UserHelper.cropImage(bitmap, width, height);
                    Drawable drawable = new BitmapDrawable(getResources(), newImage);

                    mFrameLayoutMain.setBackground(drawable);
                }
            }
        });
    }

    @Override
    protected MaterialSection backToSection(MaterialSection currentSection) {
        MaterialSection section;
        int sectionPos = currentSection.getPosition();

        switch (sectionPos) {
            case 0:
                section = getSectionAtCurrentPosition(sectionPos);
                break;
            case 1:
                section = getSectionAtCurrentPosition(sectionPos);

                break;
            case 2:
                section = getSectionAtCurrentPosition(sectionPos);

                break;
            default:
                section = super.backToSection(currentSection);
                break;
        }

        return section;
    }

    @Override
    public void onClick(View v) {

        final CharSequence[] choice = {"Choose from Gallery", "Capture a photo"};

        TakePictureDialog dialog = new TakePictureDialog();
        dialog.show(getSupportFragmentManager(), null);

    }
}
