package com.mti.videodiary.mvp.presenter;

import android.util.Log;

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

    @Inject
    public MenuPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoDairyAction action) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mAction = action;
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
        }
    }
    //endregion
}
