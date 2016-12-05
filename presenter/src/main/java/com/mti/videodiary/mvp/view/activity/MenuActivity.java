package com.mti.videodiary.mvp.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.dialog.DialogTakePictureFragment;
import com.mti.videodiary.mvp.presenter.CreateNotePresenter.SaveEditNoteText;
import com.mti.videodiary.mvp.presenter.MenuPresenter;
import com.mti.videodiary.mvp.view.BaseActivity;
import com.mti.videodiary.mvp.view.fragment.NoteFragment;
import com.mti.videodiary.mvp.view.fragment.SupportFragment;
import com.mti.videodiary.navigator.Navigator;
import com.mti.videodiary.data.helper.UserHelper;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import mti.com.videodiary.R;

import static android.provider.MediaStore.MediaColumns.DATA;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.view.View.GONE;
import static com.mti.videodiary.data.storage.VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING;
import static com.mti.videodiary.utils.Constants.IMAGE_HEADER_MENU;
import static com.mti.videodiary.utils.Constants.RESULT_LOAD_IMAGE;
import static com.mti.videodiary.utils.Constants.UPDATE_NOTE_ADAPTER;
import static com.mti.videodiary.utils.Constants.UPDATE_VIDEO_ADAPTER;

/**
 * Created by Taras Matolinets on 23.02.15.
 * Main activity
 */
public class MenuActivity extends BaseActivity implements IHasComponent<ActivityComponent>, OnNavigationItemSelectedListener, OnClickListener {

    public static final String GOOGLE_PHOTOS_CONTENT = "content://com.google.android.apps.photos.content";
    public static final int DEFAULT_VALUE = 0;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar mToolBar;

    @Inject VideoDairySharePreferences mPreferences;
    @Inject Navigator mNavigator;
    @Inject MenuPresenter mPresenter;

    private TextView mChoiceImage;
    private ActivityComponent mActivityComponent;
    private View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mPresenter.setView(this);

        mHeaderView = mNavigationView.getHeaderView(DEFAULT_VALUE);
        mChoiceImage = (TextView) mHeaderView.findViewById(R.id.tv_choice_image);
        mHeaderView.setOnClickListener(this);

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(DEFAULT_VALUE).setChecked(true);

        syncActionBarToggle();
        setImageToHeaderView();
    }

    private void syncActionBarToggle() {
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
                case RESULT_LOAD_IMAGE:
                    Uri selectedImage = data.getData();

                    if (selectedImage.toString().startsWith(GOOGLE_PHOTOS_CONTENT)) {
                        mPresenter.storeImage(selectedImage.toString());
                    } else {
                        getImageFromStorage(selectedImage);
                    }
                    break;
                case UPDATE_VIDEO_ADAPTER:
//                    Fragment fragment = (Fragment) getCurrentSection().getTargetFragment();
//
//                    // update current note card data
//                    if (fragment instanceof VideoFragment)
//                        fragment.onActivityResult(requestCode, resultCode, data);
                    break;

                case UPDATE_NOTE_ADAPTER:
//                    Fragment noteFragment = (Fragment) getCurrentSection().getTargetFragment();
//
//                    // update current note card data
//                    if (noteFragment instanceof NoteFragment)
//                        noteFragment.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }

    }

    private void getImageFromStorage(Uri selectedImage) {
        String[] filePathColumn = {DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (!TextUtils.isEmpty(picturePath)) {
                setImageInBackground(picturePath);
                mPreferences.setDataToSharePreferences(IMAGE_HEADER_MENU, picturePath, STRING);
            } else {
                showSnackView();
            }
        }
    }

    @Subscribe
    public void messageNoteAction(SaveEditNoteText saveEditNoteText) {
        Snackbar snackbar = Snackbar.make(mNavigationView, saveEditNoteText.getText(), LENGTH_SHORT);
        snackbar.show();
    }

    private void showSnackView() {
        Snackbar snackbar = Snackbar.make(mNavigationView, getString(R.string.error_picture), LENGTH_SHORT);
        snackbar.show();
    }

    public void setImageInBackground(final String picturePath) {
        Bitmap bitmap = UserHelper.decodeSampledBitmapFromResource(picturePath);

        int width = 300;
        int height = 200;

        //check if image was deleted from storage manually
        if (bitmap != null) {
            mChoiceImage.setVisibility(GONE);
            Bitmap newImage = UserHelper.cropImage(bitmap, width, height);
            Drawable drawable = new BitmapDrawable(getResources(), newImage);
            mHeaderView.setBackground(drawable);
        }
    }

    private void setImageToHeaderView() {
        String picturePath = mPreferences.getSharedPreferences().getString(IMAGE_HEADER_MENU, null);

        if (!TextUtils.isEmpty(picturePath)) {
            setImageInBackground(picturePath);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

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
        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_record:
                //      mNavigator.replace(this, VideoFragment.class, R.id.main_container, null, false);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_main_header:
                DialogTakePictureFragment dialog = new DialogTakePictureFragment();
                dialog.show(getSupportFragmentManager(), null);
                break;
        }
    }
}
