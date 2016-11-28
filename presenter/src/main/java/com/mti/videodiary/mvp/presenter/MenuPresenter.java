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
import interactor.UseCase;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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

    public void storeImage(String path) {
        String[] splitArray = path.split("/");
        String imageName = splitArray[splitArray.length - 1];

        GetSavedImagePathSubscriber subscriber = new GetSavedImagePathSubscriber();
        getSavedImagePathObserver(mView, path, imageName).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    private Observable<String> getSavedImagePathObserver(final Context context, final String imagePath, final String imageName) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Uri uri = Uri.parse(imagePath);
                    InputStream is = context.getContentResolver().openInputStream(uri);
                    if (is != null) {
                        Bitmap pictureBitmap = BitmapFactory.decodeStream(is);

                        String url = UserHelper.saveBitmapToSD(imageName,pictureBitmap);
                        subscriber.onNext(url);
                        subscriber.onCompleted();
                    }
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    //region SUBSCRIBER
    private final class GetSavedImagePathSubscriber extends DefaultSubscriber<String> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(String imagePath) {
            mView.setImageInBackground(imagePath);
            mPreferences.setDataToSharePreferences(IMAGE_HEADER_MENU, imagePath, STRING);
        }
    }
    //endregion
}
