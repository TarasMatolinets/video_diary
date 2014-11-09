package com.mti.videodiary.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mti.videodiary.animation.SkewableTextView;

import mti.com.videodiary.R;

/**
 * Created by Taras  Matolinets on 04.11.14.
 */
public class SplashActivity extends Activity implements ViewTreeObserver.OnPreDrawListener, TextWatcher {

    public static final long MEDIUM_DURATION = 1000;
    private static final long SMALL_DURATION = 500;

    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();

    private static final TimeInterpolator mOvershooter = new OvershootInterpolator();
    private static final DecelerateInterpolator mDecelerator = new DecelerateInterpolator();

    private SkewableTextView mName;
    private SkewableTextView mWelcome;
    private RelativeLayout mContainer;
    private EditText mPersonalName;
    private ImageButton mClickNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        getActionBar().hide();

        initViews();
        setListeners();
    }

    private void initViews() {
        mContainer = (RelativeLayout) findViewById(R.id.fl_splash);
        mName = (SkewableTextView) findViewById(R.id.tv_title);
        mWelcome = (SkewableTextView) findViewById(R.id.tv_welcome);
        mPersonalName = (EditText) findViewById(R.id.et_name);
        mClickNext = (ImageButton) findViewById(R.id.splash_bt_click_next);
    }

    private void setListeners() {
        mContainer.getViewTreeObserver().addOnPreDrawListener(this);
        mPersonalName.addTextChangedListener(this);
    }

    @Override
    public boolean onPreDraw() {
        mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
        mContainer.setScaleX(0);
        mContainer.setScaleY(0);

        ViewPropertyAnimator animationProperty = mContainer.animate();
        animationProperty.scaleX(1).scaleY(1);
        animationProperty.setInterpolator(new OvershootInterpolator());

        animateView();

        return true;
    }

    private void animateView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
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
        PropertyValuesHolder pvhTX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0);
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(mContainer, pvhTX, pvhTY);
        moveAnim.setDuration(600);
        moveAnim.start();

        moveAnim.addListener(mSlideToNextAnimation);
    }

    private AnimatorListenerAdapter mSlideToNextAnimation = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            //  super.onAnimationEnd(animation);
            slideToNext(mWelcome, mName);
        }
    };

    private void slideToNext(final SkewableTextView currentView, final SkewableTextView nextView) {
        ObjectAnimator currentSkewer = ObjectAnimator.ofFloat(currentView, "skewX", -.5f);
        currentSkewer.setInterpolator(mDecelerator);

        ObjectAnimator currentMover = ObjectAnimator.ofFloat(currentView, View.TRANSLATION_X, -mContainer.getWidth());
        currentMover.setInterpolator(sLinearInterpolator);
        currentMover.setDuration(MEDIUM_DURATION);

        // set next view visible, translate off to right, skew,
        // slide on in parallel, overshoot/wobble, unskew
        nextView.setVisibility(View.VISIBLE);
        nextView.setSkewX(-.5f);
        nextView.setTranslationX(mContainer.getWidth());

        ObjectAnimator nextMover = ObjectAnimator.ofFloat(nextView, View.TRANSLATION_X, 0);
        nextMover.setInterpolator(sAccelerator);
        nextMover.setDuration(MEDIUM_DURATION);

        ObjectAnimator nextSkewer = ObjectAnimator.ofFloat(nextView, "skewX", 0);
        nextSkewer.setInterpolator(mOvershooter);

        AnimatorSet moverSet = new AnimatorSet();
        moverSet.playTogether(currentMover, nextMover);

        AnimatorSet fullSet = new AnimatorSet();
        fullSet.playSequentially(currentSkewer, moverSet, nextSkewer);

        fullSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sleep(1000);

                moveViewToScreenCenter(nextView, -nextView.getHeight() * 4,true);
            }
        });
        fullSet.start();
    }

    private void moveViewToScreenCenter(View view, int height,boolean isLisenerEnable) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, height);
        anim.setDuration(1000);
        anim.setFillAfter(true);

        if(isLisenerEnable)
        anim.setAnimationListener(mMoveOnScreenListener);

        view.startAnimation(anim);
    }

    private Animation.AnimationListener mMoveOnScreenListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mPersonalName.setVisibility(View.VISIBLE);

            YoYo.AnimationComposer composer = YoYo.with(Techniques.RollIn);
            composer.duration(700);
            composer.playOn(mPersonalName);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (count > 0) {

            if(mClickNext.getVisibility() != View.VISIBLE) {
                mClickNext.setVisibility(View.VISIBLE);
            }

        }
            else {

            mClickNext.setVisibility(View.GONE);

        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
