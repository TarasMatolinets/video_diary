package com.mti.videodiary.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.annotation.PerActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import mti.com.videodiary.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mti.videodiary.application.VideoDiaryApplication.TAG;
import static com.mti.videodiary.data.storage.VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING;
import static com.mti.videodiary.utils.Constants.IMAGE_HEADER_MENU;

/**
 * Created by Terry on 11/20/2016.
 */

@PerActivity
public class MenuPresenter {
    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private MenuActivity mView;

    private VideoDairySharePreferences mPreferences;

    @Inject
    public MenuPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoDairySharePreferences sharePreferences) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mPreferences = sharePreferences;
    }

    public void setView(MenuActivity view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    public void loadImage(String path) {
        GetSavedImagePathSubscriber subscriber = new GetSavedImagePathSubscriber();
        getLoadedImagePathObserver(mView, path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    private Observable<Bitmap> getLoadedImagePathObserver(final Context context, final String imagePath) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Uri uri = Uri.parse(imagePath);
                    InputStream is = context.getContentResolver().openInputStream(uri);
                    if (is != null) {
                        Bitmap pictureBitmap = BitmapFactory.decodeStream(is);

                        subscriber.onNext(pictureBitmap);
                        subscriber.onCompleted();

                        String imageUrl = UserHelper.saveBitmapToSD(pictureBitmap);
                        mPreferences.setDataToSharePreferences(IMAGE_HEADER_MENU, imageUrl, STRING);
                    }
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    //region SUBSCRIBER
    private final class GetSavedImagePathSubscriber extends DefaultSubscriber<Bitmap> {
        @Override
        public void onCompleted() {
            mView.setChoiceImageVisibility(GONE);
            mView.setProgressImageVisibility(GONE);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
            mView.showSnackView(mView.getString(R.string.error_picture));
            mView.setChoiceImageVisibility(VISIBLE);
            mView.setProgressImageVisibility(GONE);
        }

        @Override
        public void onNext(Bitmap image) {
            mView.setChoiceImageVisibility(GONE);
            mView.setImageInBackground(image);
        }
    }
    //endregion
}
