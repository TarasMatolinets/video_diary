package com.mti.videodiary.mvp.view.activity;

import android.os.Bundle;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mti.videodiary.data.Constants;
import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.navigator.Navigator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mti.com.videodiary.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.data.Constants.IS_TITLE_LOADED;
import static com.mti.videodiary.data.storage.VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.BOOLEAN;

/**
 * Created by Taras  Matolinets on 04.11.14.
 * Splash screen.
 */
public class SplashActivity extends BaseActivity implements OnPreDrawListener {

    public static final int DEFAULT_VALUE = 0;
    public static final int TIME_SLEEP = 1000;

    @BindView(R.id.tv_title) TextView mAppName;
    @BindView(R.id.tv_welcome) TextView mWelcome;
    @BindView(R.id.rl_splash) RelativeLayout mContainer;


    @Inject Navigator mNavigator;
    @Inject VideoDairySharePreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        boolean isTitleLoaded = mPreferences.getSharedPreferences().getBoolean(IS_TITLE_LOADED, false);

        if (!isTitleLoaded) {
            setListeners();
        } else {
            mNavigator.replaceActivity(SplashActivity.this, MenuActivity.class);
        }
    }

    @Override
    public void setComponent() {
        getActivityComponent().inject(this);
    }

    private void setListeners() {
        mContainer.getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    public boolean onPreDraw() {
        mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
        mContainer.setScaleX(DEFAULT_VALUE);
        mContainer.setScaleY(DEFAULT_VALUE);

        ViewPropertyAnimator animationProperty = mContainer.animate();
        animationProperty.scaleX(1).scaleY(1);
        animationProperty.setInterpolator(new OvershootInterpolator());

        YoYo.with(Techniques.ZoomIn).withListener(mSlideToNextAnimation).playOn(mWelcome);

        return true;
    }


    private AnimatorListenerAdapter mSlideToNextAnimation = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            UserHelper.sleep(TIME_SLEEP);
            mWelcome.setVisibility(GONE);
            mAppName.setVisibility(VISIBLE);
            YoYo.with(Techniques.ZoomIn).withListener(mRunNextScreen).playOn(mAppName);
        }
    };
    private AnimatorListenerAdapter mRunNextScreen = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mPreferences.setDataToSharePreferences(IS_TITLE_LOADED, true, BOOLEAN);

            mNavigator.replaceActivity(SplashActivity.this, MenuActivity.class);
        }
    };
}
