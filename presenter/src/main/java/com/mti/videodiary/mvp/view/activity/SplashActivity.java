package com.mti.videodiary.mvp.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mti.videodiary.animation.SkewableTextView;
import com.mti.videodiary.mvp.view.BaseActivity;
import com.mti.videodiary.navigator.Navigator;
import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.data.storage.VideoDairySharePreferences;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mti.com.videodiary.R;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.view.View.GONE;
import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.utils.Constants.KEY_PERSON_NAME;
import static com.mti.videodiary.data.storage.VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING;

/**
 * Created by Taras  Matolinets on 04.11.14.
 * Splash screen for user.
 */
public class SplashActivity extends BaseActivity implements OnPreDrawListener, TextWatcher {

    public static final long MEDIUM_DURATION = 1000;
    private static final long SMALL_DURATION = 500;
    public static final int DURATION = 600;
    public static final int DEFAULT_VALUE = 0;
    public static final int TIME_SLEEP = 1000;

    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();

    private static final TimeInterpolator mOverShooter = new OvershootInterpolator();
    private static final DecelerateInterpolator mDecelerator = new DecelerateInterpolator();

    public static final String SKEW_X = "skewX";
    public static final float VALUE = -.5f;

    @BindView(R.id.tvTitle) SkewableTextView mName;
    @BindView(R.id.tvWelcome) SkewableTextView mWelcome;
    @BindView(R.id.rl_splash) RelativeLayout mContainer;
    @BindView(R.id.et_title) EditText mPersonalName;
    @BindView(R.id.tv_splash_click_next) ImageButton mClickNext;

    @Inject Navigator mNavigator;
    @Inject VideoDairySharePreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        setListeners();
    }

    @Override
    public void setComponent() {
        getActivityComponent().inject(this);
    }

    private void setListeners() {
        mContainer.getViewTreeObserver().addOnPreDrawListener(this);
        mPersonalName.addTextChangedListener(this);
    }

    @Override
    public boolean onPreDraw() {
        mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
        mContainer.setScaleX(DEFAULT_VALUE);
        mContainer.setScaleY(DEFAULT_VALUE);

        ViewPropertyAnimator animationProperty = mContainer.animate();
        animationProperty.scaleX(1).scaleY(1);
        animationProperty.setInterpolator(new OvershootInterpolator());

        YoYo.with(Techniques.ZoomIn).playOn(mWelcome);
        animateView();

        return true;
    }

    private void animateView() {
        if (SDK_INT < JELLY_BEAN) {
            mContainer.animate().setListener(mAnimViewListener);
        } else
            mContainer.animate().setDuration(SMALL_DURATION).withEndAction(mAnimListenerRunnable);
    }

    private AnimatorListenerAdapter mAnimViewListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            moveView();
        }
    };

    private Runnable mAnimListenerRunnable = new Runnable() {
        @Override
        public void run() {
            moveView();
        }
    };

    private void moveView() {
        PropertyValuesHolder pvhTX = PropertyValuesHolder.ofFloat(TRANSLATION_X, DEFAULT_VALUE);
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, DEFAULT_VALUE);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(mContainer, pvhTX, pvhTY);
        moveAnim.setDuration(DURATION);
        moveAnim.start();

        moveAnim.addListener(mSlideToNextAnimation);
    }

    private AnimatorListenerAdapter mSlideToNextAnimation = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            slideToNext(mWelcome, mName);
        }
    };

    private void slideToNext(final SkewableTextView currentView, final SkewableTextView nextView) {
        ObjectAnimator currentSkewer = getCurrentAnimator(currentView);
        ObjectAnimator currentMover = getCurrentMoverAnimator(currentView);
        ObjectAnimator nextMover = getNextMoverAnimator(nextView);

        // set next viewDivider visible, translate off to right, skew,
        // slide on in parallel, overshoot/wobble, unskew
        nextView.setVisibility(VISIBLE);
        nextView.setSkewX(VALUE);
        nextView.setTranslationX(mContainer.getWidth());

        ObjectAnimator nextSkewer = ObjectAnimator.ofFloat(nextView, SKEW_X, DEFAULT_VALUE);
        nextSkewer.setInterpolator(mOverShooter);

        AnimatorSet moverSet = new AnimatorSet();
        moverSet.playTogether(currentMover, nextMover);

        AnimatorSet fullSet = new AnimatorSet();
        fullSet.playSequentially(currentSkewer, moverSet, nextSkewer);

        fullSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                UserHelper.sleep(TIME_SLEEP);
                mNavigator.replaceActivity(SplashActivity.this, MenuActivity.class);
            }
        });
        fullSet.start();
    }

    @NonNull
    private ObjectAnimator getNextMoverAnimator(SkewableTextView nextView) {
        ObjectAnimator nextMover = ObjectAnimator.ofFloat(nextView, TRANSLATION_X, DEFAULT_VALUE);
        nextMover.setInterpolator(sAccelerator);
        nextMover.setDuration(MEDIUM_DURATION);
        return nextMover;
    }

    @NonNull
    private ObjectAnimator getCurrentAnimator(SkewableTextView currentView) {
        ObjectAnimator currentSkewer = ObjectAnimator.ofFloat(currentView, SKEW_X, VALUE);
        currentSkewer.setInterpolator(mDecelerator);
        return currentSkewer;
    }

    @NonNull
    private ObjectAnimator getCurrentMoverAnimator(SkewableTextView currentView) {
        ObjectAnimator currentMover = ObjectAnimator.ofFloat(currentView, TRANSLATION_X, -mContainer.getWidth());
        currentMover.setInterpolator(sLinearInterpolator);
        currentMover.setDuration(MEDIUM_DURATION);
        return currentMover;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (count > DEFAULT_VALUE && mClickNext.getVisibility() != VISIBLE) {
            mClickNext.setVisibility(VISIBLE);
        } else {
            mClickNext.setVisibility(GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @OnClick(R.id.tv_splash_click_next)
    public void splashButtonClick() {
        String name = mPreferences.getSharedPreferences().getString(KEY_PERSON_NAME, null);

        if (!TextUtils.isEmpty(name)) {
            mPreferences.setDataToSharePreferences(KEY_PERSON_NAME, mPersonalName.getText().toString(), STRING);
        }

        splashClickNext();
    }

    private void splashClickNext() {
        mClickNext.setVisibility(GONE);
        UserHelper.hideKeyboard(this, mClickNext);

        YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.RollOut);
        personalAnim.duration(DURATION);
        personalAnim.withListener(mPersonalNameAnimation);
        personalAnim.playOn(mPersonalName);
    }

    private com.nineoldandroids.animation.Animator.AnimatorListener mPersonalNameAnimation = new com.nineoldandroids.animation.Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
        }

        @Override
        public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
            showMainMenu();
        }

        @Override
        public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {
        }

        @Override
        public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {
        }
    };

    private void showMainMenu() {
        mNavigator.replaceActivity(this, MenuActivity.class);
    }
}
