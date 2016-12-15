package com.mti.videodiary.mvp.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mti.videodiary.application.VideoDiaryApplication;
import com.mti.videodiary.data.storage.dao.Video;
import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.mvp.presenter.CreateVideoPresenter;
import com.mti.videodiary.mvp.view.BaseActivity;
import com.mti.videodiary.utils.Constants;
import com.mti.videodiary.data.helper.UserHelper;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import model.VideoDomain;
import mti.com.videodiary.R;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;
import static com.mti.videodiary.data.Constants.*;
import static java.io.File.separator;

/**
 * Created by Taras Matolinets on 01.03.15.
 * Video Activity for create or edit video.
 */
public class CreateVideoNoteActivity extends BaseActivity implements TextWatcher, View.OnClickListener, IHasComponent<ActivityComponent> {
    private static final String FILE_PLAY_VIDEO = "file://";
    private static float sAnimatorScale = 1;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;
    private static final int DURATION = 1000;
    public static final int DEFAULT_VALUE = -1;
    private static final int DEFAULT_ITEM_POSITION = -1;
    private static final int DURATION_FADE_IN = 600;
    private static final int REQUEST_VIDEO_CAPTURE = 433;

    @BindView(R.id.ivPlay) ImageView mIvPlay;
    @BindView(R.id.et_title) EditText mEtTitle;
    @BindView(R.id.tvAddVideo) TextView mEtDescription;
    @BindView(R.id.ivVideoThumbnail) ImageView mIvThumbnail;
    @BindView(R.id.ivCancel) ImageView mIvCancel;
    @BindView(R.id.scrollCard) ScrollView mScrollCard;
    @BindView(R.id.cardViewCreateVideo) CardView mCardView;

    @Inject CreateVideoPresenter mPresenter;

    private boolean isShowSave;
    private ActionBar mActionBar;
    private boolean isEditVideoDaily;
    private int mOriginalOrientation;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private ColorDrawable mBackground;
    private TextView mTvAddVideoNote;
    private String mVideoFilePath;
    private boolean isDeleteVideo = true;
    private ActivityComponent mComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video_note);

        setComponent();
        initListeners();
        setDataToView(savedInstanceState);
        initActionBar();
    }

    @Override
    public void setComponent() {
        mComponent = getActivityComponent();
        mComponent.inject(this);
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();

        int position = getIntent().getIntExtra(Constants.KEY_POSITION, -1);

        if (position == DEFAULT_ITEM_POSITION) {
            mActionBar.setTitle(R.string.create_video_note);
        } else {
            mActionBar.setTitle(R.string.edit_video_note);
        }

        mActionBar.show();
    }

    private void initListeners() {
        mEtTitle.addTextChangedListener(this);
        mEtDescription.addTextChangedListener(this);
        mIvPlay.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mTvAddVideoNote.setOnClickListener(this);
    }

    private void setDataToView(Bundle savedInstanceState) {
        mCardView.setCardBackgroundColor(TRANSPARENT);
        int position = getIntent().getIntExtra(Constants.KEY_POSITION, DEFAULT_VALUE);

        mBackground = new ColorDrawable(WHITE);
        mScrollCard.setBackground(mBackground);

        if (position != DEFAULT_VALUE) {
            isEditVideoDaily = true;
            mPresenter.getVideoNote(position);
        } else {
            mVideoFilePath = getIntent().getStringExtra(Constants.KEY_VIDEO_PATH);
        }

        setImageToView();
        animateImageView(savedInstanceState);
    }

    private void animateImageView(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();

        final int thumbnailTop = bundle.getInt(VideoDiaryApplication.TAG + ".top");
        final int thumbnailLeft = bundle.getInt(VideoDiaryApplication.TAG + ".left");
        final int thumbnailWidth = bundle.getInt(VideoDiaryApplication.TAG + ".width");
        final int thumbnailHeight = bundle.getInt(VideoDiaryApplication.TAG + ".height");
        mOriginalOrientation = bundle.getInt(VideoDiaryApplication.TAG + ".orientation");

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
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mIvThumbnail.getWidth();
                    mHeightScale = (float) thumbnailHeight / mIvThumbnail.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    private void setImageToView() {
        final File file = new File(mVideoFilePath);
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

    public void loadVideoNote(VideoDomain videoDomain) {
        mVideoFilePath = videoDomain.getVideoName();

        mEtTitle.setText(videoDomain.getTitle());
        mEtDescription.setText(videoDomain.getDescription());
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

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(DURATION_FADE_IN);
        bgAnim.addListener(new AnimatorListenerAdapter() {
                               @Override
                               public void onAnimationEnd(Animator animation) {
                                   super.onAnimationEnd(animation);
                                   mCardView.setCardBackgroundColor(WHITE);
                                   mIvPlay.setVisibility(View.VISIBLE);
                                   mIvCancel.setVisibility(View.VISIBLE);
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

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     *                  when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {
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
            mLeftDelta = 0;
            mTopDelta = 0;
        }

        //show transparent effect
        mCardView.setCardBackgroundColor(TRANSPARENT);
        mIvPlay.setVisibility(View.GONE);
        mIvCancel.setVisibility(View.GONE);
        mTvAddVideoNote.setVisibility(View.GONE);

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
            }
        });

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(DURATION);
        bgAnim.start();
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
                if (!isEditVideoDaily) {
         //           updateVideoDairy();
                } else {
                    saveVideoNote();
                }
                break;
            case android.R.id.home:
                break;
        }

        runExitAnimation(new Runnable() {
            public void run() {
                finish();
            }
        });

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    private void deleteVideoNote() {
        int id = getIntent().getIntExtra(Constants.KEY_POSITION, DEFAULT_ITEM_POSITION);

        if (id != DEFAULT_ITEM_POSITION) {
            mPresenter.deleteVideoNote(id);
        }
    }

    private void saveVideoNote() {
        if (!isEditVideoDaily) {
      //      createNewVideoDaily();
        } else {
    //        updateVideoDairy();
        }
        setResult(RESULT_OK, null);
    }

//    private void updateVideoDairy() {
//        File oldFileName = new File(mVideoFilePath);
//        File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + separator + APPLICATION_DIRECTORY + separator + VIDEO_DIR + separator + mEtTitle.getText().toString() + FILE_FORMAT);
//
//        boolean success = oldFileName.renameTo(newFileName);
//
//        if (success) {
//            Bitmap bitmap = ((BitmapDrawable) mIvThumbnail.getDrawable()).getBitmap();
//            String tempBitmapPath = UserHelper.saveBitmapToSD(bitmap);
//            //we can use decodeSampledBitmapFromResource when we have stored bitmap in sd otherwise bitmap  will be null
//            Bitmap finalBitmap = UserHelper.decodeSampledBitmapFromResource(tempBitmapPath);
//
//            String finalPathBitmap = UserHelper.saveBitmapToSD(finalBitmap);
//
//            File file = new File(tempBitmapPath);
//
//            if (file.exists())
//                file.delete();
//
//            int position = getIntent().getIntExtra(Constants.KEY_POSITION, DEFAULT_ITEM_POSITION);
//            VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//
//            Video video = videoDataManager.getVideoByPosition(position);
//            video.setDescription(mEtDescription.getText().toString());
//            video.setTitle(mEtTitle.getText().toString());
//
//            if (!mVideoFilePath.equals(video.getVideoName())) {
//                File videoOld = new File(video.getVideoName());
//
//                if (videoOld.exists())
//                    videoOld.delete();
//            }
//
//            video.setVideoUrl(newFileName.getAbsolutePath());
//
//            File imageOld = new File(video.getImageUrl());
//
//            if (imageOld.exists())
//                imageOld.delete();
//
//            video.setImageUrl(finalPathBitmap);
//
//
//            videoDataManager.updateVideoList(video);
//        }
//    }

//    private void createNewVideoDaily() {
//        Bitmap bitmap = ((BitmapDrawable) mIvThumbnail.getDrawable()).getBitmap();
//
//        File oldFileName = new File(mVideoFilePath);
//        File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + separator + APPLICATION_DIRECTORY + separator + VIDEO_DIR + separator + mEtTitle.getText().toString() + FILE_FORMAT);
//
//        boolean success = oldFileName.renameTo(newFileName);
//
//        if (success) {
//            Video video = new Video();
//
//            video.setVideoUrl(newFileName.getAbsolutePath());
//            video.setTitle(mEtTitle.getText().toString());
//            video.setDescription(mEtDescription.getText().toString());
//
//            String tempBitmapPath = UserHelper.saveBitmapToSD(bitmap);
//            //we can use decodeSampledBitmapFromResource when we have stored bitmap in sd otherwise bitmap  will be null
//            Bitmap finalBitmap = UserHelper.decodeSampledBitmapFromResource(tempBitmapPath);
//
//            String finalPathBitmap = UserHelper.saveBitmapToSD(finalBitmap);
//
//            File file = new File(tempBitmapPath);
//
//            if (file.exists())
//                file.delete();
//
//            video.setImageUrl(finalPathBitmap);
//            VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//            videoDataManager.createVideo(video);
//        }
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        int position = getIntent().getIntExtra(Constants.KEY_POSITION, DEFAULT_ITEM_POSITION);
//        boolean title = false;
//        boolean description = false;
//
//        if (position != DEFAULT_ITEM_POSITION) {
//            VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//
//            Video video = videoDataManager.getVideoByPosition(position);
//
//            title = mEtTitle.getText().toString().equals(video.getTitle());
//            description = mEtDescription.getText().toString().equals(video.getDescription());
//        }
//        if (mEtTitle.getText().length() > 0 && !title && mVideoFilePath != null || !description && mVideoFilePath != null)
//            isShowSave = true;
//        else
//            isShowSave = false;

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
                    isDeleteVideo = false;
                    final Uri videoUri = data.getData();
                    mVideoFilePath = videoUri.getPath();

                    final File file = new File(mVideoFilePath);

                    mIvThumbnail.setVisibility(View.VISIBLE);
                    mTvAddVideoNote.setVisibility(View.GONE);
                    mIvPlay.setVisibility(View.VISIBLE);
                    mIvCancel.setVisibility(View.VISIBLE);

                    mIvThumbnail.post(new Runnable() {
                        @Override
                        public void run() {
                            int width = mIvThumbnail.getWidth();
                            int height = mIvThumbnail.getHeight();

                            if (width > 0 && height > 0) {

                                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                                Bitmap newImage = UserHelper.cropImage(bMap, width, height);

                                mIvThumbnail.setImageBitmap(newImage);

                                if (mEtTitle.getText().length() > 0)
                                    isShowSave = true;
                                else
                                    isShowSave = false;

                                invalidateOptionsMenu();
                            }
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPlay:
//                String videoFilePath;
//                if (!isEditVideoDaily)
//                    videoFilePath = FILE_PLAY_VIDEO + getIntent().getStringExtra(Constants.KEY_VIDEO_PATH);
//                else {
//                    int position = getIntent().getIntExtra(Constants.KEY_POSITION, DEFAULT_ITEM_POSITION);
//                    VideoDataManager videoDataManager = (VideoDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.VIDEO_MANAGER);
//                    Video video = videoDataManager.getVideoByPosition(position);
//                    videoFilePath = FILE_PLAY_VIDEO + video.getVideoName();
//                }
//
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoFilePath));
//                intent.setDataAndType(Uri.parse(videoFilePath), "video/mp4");
//                startActivity(intent);

                break;
            case R.id.ivCancel:
                isDeleteVideo = true;
                mVideoFilePath = null;

                mIvCancel.setVisibility(View.GONE);
                mIvPlay.setVisibility(View.GONE);
                mIvThumbnail.setVisibility(View.GONE);
                mTvAddVideoNote.setVisibility(View.VISIBLE);

                isShowSave = false;
                invalidateOptionsMenu();
                break;
            case R.id.tvAddVideo:
//                final File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + separator + VideoDiaryApplication.APPLICATION_DIRECTORY + separator + BaseActivity.VIDEO_DIR + Constants.VIDEO_FILE_NAME);
//
//                Uri fileUri = Uri.fromFile(mediaFile);
//
//                Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//
//                intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                intentVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//
//                startActivityForResult(intentVideo, REQUEST_VIDEO_CAPTURE);
                break;
        }
    }

    @Override
    public ActivityComponent getComponent() {
        return mComponent;
    }
}
