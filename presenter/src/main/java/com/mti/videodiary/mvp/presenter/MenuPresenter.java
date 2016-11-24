package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.VideoDairySharePreferences;
import com.mti.videodiary.di.annotation.PerActivity;
import com.mti.videodiary.mvp.view.activity.MenuActivity;

import javax.inject.Inject;

import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseSaveImage;
import rx.subscriptions.CompositeSubscription;
import storage.VideoDairyAction;

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
    private final VideoDairyAction mAction;
    private MenuActivity mView;

    private VideoDairySharePreferences mPreferences;

    @Inject
    public MenuPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoDairyAction action, VideoDairySharePreferences sharePreferences) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mAction = action;
        mPreferences = sharePreferences;
    }

    public void setView(MenuActivity view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    public void storeImage(String imagePath) {
        UseCase useCaseStoreImage = new UseCaseSaveImage(mExecutor, mPostExecutorThread, mAction, imagePath);
        GetSavedImagePathSubscriber subscriber = new GetSavedImagePathSubscriber();
        useCaseStoreImage.execute(subscriber);
        mComposeSubscriptionList.add(subscriber);
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
