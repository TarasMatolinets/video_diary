package com.mti.videodiary.mvp.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.mvp.presenter.CreateVideoPresenter;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.UpdateVideoNoteList;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.VideoDomain;
import mti.com.videodiary.R;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;
import static android.provider.MediaStore.ACTION_VIDEO_CAPTURE;
import static android.provider.MediaStore.EXTRA_VIDEO_QUALITY;
import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.data.Constants.HEIGHT;
import static com.mti.videodiary.data.Constants.KEY_POSITION;
import static com.mti.videodiary.data.Constants.KEY_VIDEO_PATH;
import static com.mti.videodiary.data.Constants.ORIENTATION;
import static com.mti.videodiary.data.Constants.WIDTH;

/**
 * Created by Taras Matolinets on 01.03.15.
 * Activity for create or edit video note.
 */
public class CreateVideoNoteActivity extends BaseActivity implements TextWatcher, IHasComponent<ActivityComponent> {
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    public static final String ALPHA = "alpha";
    public static final String VIDEO_MP4 = "video/mp4";
    private static float sAnimatorScale = 1;

    private static final int ANIM_DURATION = 500;
    private static final int DURATION = 1000;
    public static final int DEFAULT_VALUE = -1;
    private static final int DEFAULT_ITEM_POSITION = -1;
    private static final int DURATION_FADE_IN = 600;
    private static final int REQUEST_VIDEO_CAPTURE = 433;

    @BindView(R.id.iv_play) ImageView mIvPlay;
    @BindView(R.id.et_title) EditText mEtTitle;
    @BindView(R.id.tv_add_video) TextView mTvAddVideoNote;
    @BindView(R.id.et_description) EditText mEtDescription;
    @BindView(R.id.iv_video_thumbnail) ImageView mIvThumbnail;
    @BindView(R.id.iv_cancel) ImageView mIvCancel;
    @BindView(R.id.scroll_card) ScrollView mScrollCard;
    @BindView(R.id.cv_create_video) CardView mCardView;

    @Inject CreateVideoPresenter mPresenter;

    private boolean isShowSave;
    private boolean isEditVideoDaily;
    private int mOriginalOrientation;
    private float mWidthScale;
    private float mHeightScale;

    private ColorDrawable mBackground;
    private ActivityComponent mComponent;
    private VideoDomain mVideoNote;
    private String mRecordedVideoFilePath;
    private boolean isDeletedVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video_note);
        ButterKnife.bind(this);

        setComponent();
        mPresenter.setView(this);
        initListeners();
        initActionBar();
        setDataToView();
        animateImageView(savedInstanceState);
    }

    @Override
    public void setComponent() {
        mComponent = getActivityComponent();
        mComponent.inject(this);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();

        int position = getIntent().getIntExtra(KEY_POSITION, DEFAULT_VALUE);

        if (actionBar != null) {
            if (position == DEFAULT_ITEM_POSITION) {
                actionBar.setTitle(R.string.create_video_note);
            } else {
                actionBar.setTitle(R.string.edit_video_note);
            }
            actionBar.show();
        }
    }

    private void initListeners() {
        mEtTitle.addTextChangedListener(this);
        mEtDescription.addTextChangedListener(this);
    }

    private void setDataToView() {
        int position = getIntent().getIntExtra(KEY_POSITION, DEFAULT_VALUE);

        mBackground = new ColorDrawable(WHITE);

        mCardView.setCardBackgroundColor(TRANSPARENT);
        mScrollCard.setBackground(mBackground);

        if (position != DEFAULT_VALUE) {
            isEditVideoDaily = true;
            mPresenter.getVideoNote(position);
        } else {
            mRecordedVideoFilePath = getIntent().getStringExtra(KEY_VIDEO_PATH);
            setImageToView(mRecordedVideoFilePath);
        }
    }

    private void setImageToView(String path) {
        final File file = new File(path);
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MINI_KIND);
        mIvThumbnail.setImageBitmap(bMap);
        mIvPlay.setVisibility(VISIBLE);
        mIvCancel.setVisibility(VISIBLE);
    }

    public void loadVideoNote(VideoDomain videoDomain) {
        mRecordedVideoFilePath = videoDomain.getVideoPath();
        mVideoNote = videoDomain;
        mEtTitle.setText(videoDomain.getTitle());
        mEtDescription.setText(videoDomain.getDescription());

        if (!TextUtils.isEmpty(videoDomain.getImageUrl())) {
            setImageToView(videoDomain.getImageUrl());
            mIvPlay.setVisibility(VISIBLE);
            mIvCancel.setVisibility(VISIBLE);
        } else {
            mIvCancel.setVisibility(GONE);
            mIvPlay.setVisibility(GONE);
            mIvThumbnail.setVisibility(GONE);
            mTvAddVideoNote.setVisibility(VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_video_note, menu);

        int defaultValue = 0;
        MenuItem item = menu.getItem(defaultValue);
        item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        int defaultValue = 0;

        MenuItem item = menu.getItem(defaultValue);
        if (isShowSave) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!isEditVideoDaily) {
                    mPresenter.createNewVideoDaily(mRecordedVideoFilePath, mEtTitle.getText().toString(), mEtDescription.getText().toString());
                } else {
                    mPresenter.updateVideoNote(mVideoNote.getVideoPath(), mEtTitle.getText().toString(), mEtDescription.getText().toString(), mVideoNote.getId(), isDeletedVideo);
                }
                break;
            case android.R.id.home:
                runExitAnimation();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onBackPressed() {
        runExitAnimation();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int position = getIntent().getIntExtra(KEY_POSITION, DEFAULT_VALUE);
        boolean title = false;
        boolean description = false;

        if (position != DEFAULT_VALUE) {
            title = mEtTitle.getText().toString().equals(mVideoNote.getTitle());
            description = mEtDescription.getText().toString().equals(mVideoNote.getDescription());
        }

        isShowSave = !title || !description;
        invalidateOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_VIDEO_CAPTURE:
                    final Uri videoUri = data.getData();
                    String path = UserHelper.getRealPathFromURI(this, videoUri);

                    final File file = new File(path);
                    mRecordedVideoFilePath = file.getAbsolutePath();

                    if (isEditVideoDaily) {
                        mVideoNote.setVideoUrl(mRecordedVideoFilePath);
                    }

                    isDeletedVideo = false;
                    mTvAddVideoNote.setVisibility(GONE);
                    mIvThumbnail.setVisibility(VISIBLE);
                    mIvPlay.setVisibility(VISIBLE);
                    mIvCancel.setVisibility(VISIBLE);

                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MINI_KIND);
                    mIvThumbnail.setImageBitmap(bMap);

                    isShowSave = !TextUtils.isEmpty(mEtTitle.getText());

                    invalidateOptionsMenu();
                    break;
            }
        }
    }

    @OnClick(R.id.iv_play)
    public void playVideoNote() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRecordedVideoFilePath));
        intent.setDataAndType(Uri.parse(mRecordedVideoFilePath), VIDEO_MP4);
        startActivity(intent);
    }

    @OnClick(R.id.iv_cancel)
    public void deleteVideoNote() {
        mIvThumbnail.setImageBitmap(null);
        isDeletedVideo = true;

        mIvCancel.setVisibility(GONE);
        mIvPlay.setVisibility(GONE);
        mIvThumbnail.setVisibility(GONE);
        mTvAddVideoNote.setVisibility(VISIBLE);

        isShowSave = true;
        invalidateOptionsMenu();
    }

    @OnClick(R.id.tv_add_video)
    public void addVideoNote() {
        Intent intentVideo = new Intent(ACTION_VIDEO_CAPTURE);
        intentVideo.putExtra(EXTRA_VIDEO_QUALITY, 1);

        startActivityForResult(intentVideo, REQUEST_VIDEO_CAPTURE);
    }

    @Override
    public ActivityComponent getComponent() {
        return mComponent;
    }

    //region ANIMATION
    private void animateImageView(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();

        final int thumbnailWidth = bundle.getInt(WIDTH);
        final int thumbnailHeight = bundle.getInt(HEIGHT);
        mOriginalOrientation = bundle.getInt(ORIENTATION);

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        if (savedInstanceState == null) {
            ViewTreeObserver observer = mIvThumbnail.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mIvThumbnail.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mIvThumbnail.getLocationOnScreen(screenLocation);

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mIvThumbnail.getWidth();
                    mHeightScale = (float) thumbnailHeight / mIvThumbnail.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the picture is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * sAnimatorScale);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mIvThumbnail.setScaleX(mWidthScale);
        mIvThumbnail.setScaleY(mHeightScale);

        mEtTitle.setAlpha(0);
        mEtDescription.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        ViewPropertyAnimator anim = mIvThumbnail.animate();
        anim.setDuration(DURATION)
                .scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator);

        anim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateText(duration);
            }
        });

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, ALPHA, 0, 255);
        bgAnim.setDuration(DURATION_FADE_IN);
        bgAnim.addListener(new AnimatorListenerAdapter() {
                               @Override
                               public void onAnimationEnd(Animator animation) {
                                   super.onAnimationEnd(animation);
                                   mCardView.setCardBackgroundColor(WHITE);

                               }
                           }

        );
        bgAnim.start();
    }

    private void animateText(final long duration) {
        // Animate the description in after the image animation
        // is done. Slide and fade the text in from underneath
        // the picture.
        mEtTitle.setTranslationY(-mEtTitle.getHeight());
        ViewPropertyAnimator anim = mEtTitle.animate();
        anim.setDuration(duration / 2).translationY(0).alpha(1).setInterpolator(sDecelerator);
        anim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateDescription(duration);
            }
        });
    }

    private void animateDescription(long duration) {
        mEtDescription.setTranslationY(0);
        mEtDescription.animate().setDuration(duration / 2).alpha(1).setInterpolator(sDecelerator);
    }

    public void runExitAnimation() {
        UpdateVideoNoteList updateVideoNoteList = new UpdateVideoNoteList();
        EventBus.getDefault().post(updateVideoNoteList);

        final long duration = (long) (ANIM_DURATION * sAnimatorScale);

        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier

        // Caveat: configuration change invalidates thumbnail positions; just animate
        // the scale around the center. Also, fade it out since it won't match up with
        // whatever's actually in the center
        if (getResources().getConfiguration().orientation != mOriginalOrientation) {
            mIvThumbnail.setPivotX(mIvThumbnail.getWidth() / 2);
            mIvThumbnail.setPivotY(mIvThumbnail.getHeight() / 2);
        }

        //show transparent effect
        mCardView.setCardBackgroundColor(TRANSPARENT);
        mIvPlay.setVisibility(GONE);
        mIvCancel.setVisibility(GONE);
        mTvAddVideoNote.setVisibility(GONE);

        ViewPropertyAnimator anim = mEtDescription.animate();

        anim.translationY(-mEtDescription.getHeight()).alpha(0).setDuration(duration / 2).setInterpolator(sAccelerator);
        anim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateTextView(duration);
            }
        });
    }

    private void animateTextView(long duration) {
        ViewPropertyAnimator anim = mEtTitle.animate();

        anim.translationY(-mEtTitle.getHeight()).alpha(0).setDuration(duration / 2).setInterpolator(sAccelerator);
        anim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateThumbnail();
            }
        });
    }

    private void animateThumbnail() {
        ViewPropertyAnimator anim = mIvThumbnail.animate();
        anim.setDuration(DURATION).scaleX(mWidthScale).scaleY(mHeightScale).alpha(0);
        anim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateThumbnail();
                finish();
            }
        });

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, ALPHA, 0);
        bgAnim.setDuration(DURATION);
        bgAnim.start();
    }
    //endregion

}
