package com.mti.videodiary.mvp.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.mvp.view.BaseActivity;
import com.mti.videodiary.mvp.view.fragment.NoteFragment;
import com.mti.videodiary.mvp.view.fragment.SupportFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment;
import com.mti.videodiary.navigator.Navigator;
import com.mti.videodiary.utils.Constants;
import com.mti.videodiary.utils.UserHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import mti.com.videodiary.R;

import static android.view.View.GONE;

/**
 * Created by Taras Matolinets on 23.02.15.
 * Main activity
 */
public class MenuActivity extends BaseActivity implements IHasComponent<ActivityComponent>, OnNavigationItemSelectedListener {

    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.fl_main_header) FrameLayout mFrameLayoutMain;
    @BindView(R.id.tv_choice_image) TextView mChoiceImage;
    @BindView(R.id.toolbar) Toolbar mToolBar;

    @Inject VideoDairySharePreferences mPreferences;
    @Inject Navigator mNavigator;

    private ActivityComponent mActivityComponent;
    private boolean isImageAlreadySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        mNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.video_notes, R.string.video_notes) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RESULT_LOAD_IMAGE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    if (picturePath != null) {
                        setImageInBackground(picturePath);
                        mPreferences.setDataToSharePreferences(Constants.IMAGE_HEADER_MENU, picturePath, VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING);
                    } else {
                        showSnackView();
                    }
                    break;
                case Constants.UPDATE_VIDEO_ADAPTER:
//                    Fragment fragment = (Fragment) getCurrentSection().getTargetFragment();
//
//                    // update current note card data
//                    if (fragment instanceof VideoFragment)
//                        fragment.onActivityResult(requestCode, resultCode, data);
                    break;

                case Constants.UPDATE_NOTE_ADAPTER:
//                    Fragment noteFragment = (Fragment) getCurrentSection().getTargetFragment();
//
//                    // update current note card data
//                    if (noteFragment instanceof NoteFragment)
//                        noteFragment.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    private void showSnackView() {
        Crouton.makeText(this, getResources().getString(R.string.error_picture), Style.ALERT).show();
    }

    private void setImageInBackground(final String picturePath) {
        mChoiceImage.setVisibility(GONE);

        mFrameLayoutMain.post(new Runnable() {
            @Override
            public void run() {
                isImageAlreadySet = true;

                int width = mFrameLayoutMain.getMeasuredWidth();
                int height = mFrameLayoutMain.getMeasuredHeight();

                if (width > 0 && height > 0) {

                    Bitmap bitmap = UserHelper.decodeSampledBitmapFromResource(picturePath);

                    Bitmap newImage = UserHelper.cropImage(bitmap, width, height);
                    Drawable drawable = new BitmapDrawable(getResources(), newImage);

                    mFrameLayoutMain.setBackground(drawable);
                }
            }
        });
    }

//    @OnClick(R.id.tv_choice_image)
//    public void clickChoiceImage() {
//        DialogTakePictureFragment dialog = new DialogTakePictureFragment();
//        dialog.setDialogClickListener(this);
//        dialog.show(getSupportFragmentManager(), null);
//    }

    public void onDrawerStateChanged(int newState) {
        //bug image height == 0. That's why we set image when drawerOpen
        if (!isImageAlreadySet) {
            String picturePath = mPreferences.getSharedPreferences().getString(Constants.IMAGE_HEADER_MENU, null);
            if (picturePath != null)
                setImageInBackground(picturePath);
        }
    }


//    @Override
//    public void dialogClick() {
//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, Constants.RESULT_LOAD_IMAGE);
//    }

    @Override
    public void setComponent() {
        mActivityComponent = getActivityComponent();
        mActivityComponent.inject(this);
    }

    @Override
    public ActivityComponent getComponent() {
        return mActivityComponent;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawers();

        switch (item.getItemId()) {
            case R.id.action_record:
                mNavigator.replace(this, VideoFragment.class, R.id.main_container, null, false);
                break;
            case R.id.action_notes:
                mNavigator.replace(this, NoteFragment.class, R.id.main_container, null, false);
                break;
            case R.id.action_contact:
                mNavigator.replace(this, SupportFragment.class, R.id.main_container, null, false);
                break;
        }
        return false;
    }
}
