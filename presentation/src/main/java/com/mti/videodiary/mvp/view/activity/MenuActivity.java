package com.mti.videodiary.mvp.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.mvp.presenter.MenuPresenter;
import com.mti.videodiary.mvp.view.fragment.NoteFragment;
import com.mti.videodiary.mvp.view.fragment.InfoFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment;
import com.mti.videodiary.navigator.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mti.com.videodiary.R;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.data.Constants.IMAGE_HEADER_MENU;
import static com.mti.videodiary.data.Constants.RESULT_LOAD_IMAGE;
import static com.mti.videodiary.data.storage.VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING;

/**
 * Created by Taras Matolinets on 23.02.15.
 * Main activity for navigate through the screens
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
    private ImageView mImageBackground;
    private ProgressBar mProgressLoadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setComponent();
        setSupportActionBar(mToolBar);

        mPresenter.setView(this);

        View headerView = mNavigationView.getHeaderView(DEFAULT_VALUE);
        mChoiceImage = (TextView) headerView.findViewById(R.id.tv_choice_image);
        mImageBackground = (ImageView) headerView.findViewById(R.id.image_background);
        mProgressLoadImage = (ProgressBar) headerView.findViewById(R.id.progress_load_image);
        headerView.setOnClickListener(this);

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().performIdentifierAction(R.id.action_record, DEFAULT_VALUE);

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
                        mPresenter.loadImage(selectedImage.toString());
                    } else {
                        getImageFromStorage(selectedImage);
                    }
                    break;
            }
        } else {
            String picturePath = mPreferences.getSharedPreferences().getString(IMAGE_HEADER_MENU, null);

            if (TextUtils.isEmpty(picturePath)) {
                setChoiceImageVisibility(VISIBLE);
            }

            setProgressImageVisibility(GONE);
        }
    }

    public void setProgressImageVisibility(int visibility) {
        mProgressLoadImage.setVisibility(visibility);
    }

    public void setChoiceImageVisibility(int visibility) {
        mChoiceImage.setVisibility(visibility);
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
                Bitmap image = BitmapFactory.decodeFile(picturePath);
                setProgressImageVisibility(GONE);
                setImageInBackground(image);
                mPreferences.setDataToSharePreferences(IMAGE_HEADER_MENU, picturePath, STRING);
            } else {
                showSnackView(getString(R.string.error_picture));
            }
        }
    }

    public void showSnackView(String message) {
        Snackbar snackbar = Snackbar.make(mNavigationView, message, LENGTH_SHORT);
        snackbar.show();
    }

    public void setImageInBackground(final Bitmap picture) {
        mChoiceImage.setVisibility(GONE);
        mImageBackground.setImageBitmap(picture);
    }

    private void setImageToHeaderView() {
        String picturePath = mPreferences.getSharedPreferences().getString(IMAGE_HEADER_MENU, null);

        if (!TextUtils.isEmpty(picturePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            setImageInBackground(bitmap);
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
        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_record:
                mNavigator.replace(this, VideoFragment.class, R.id.main_container, null, false);
                break;
            case R.id.action_notes:
                mNavigator.replace(this, NoteFragment.class, R.id.main_container, null, false);
                break;
            case R.id.action_contact:
                mNavigator.replace(this, InfoFragment.class, R.id.main_container, null, false);
                break;
        }
        mDrawerLayout.closeDrawers();

        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_main_header:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.select_image_gallery)
                        .setMessage(R.string.select_image_gallery_description)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                                setProgressImageVisibility(VISIBLE);
                                setChoiceImageVisibility(GONE);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }
}
