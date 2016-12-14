package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.manager.NoteDataBaseFactory;
import com.mti.videodiary.data.storage.manager.VideoNoteDataBaseFactory;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;

import java.util.List;

import javax.inject.Inject;

import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseCreateVideoNote;
import interactor.UseCaseGetVideoNoteByPosition;
import model.NoteDomain;
import model.VideoDomain;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.application.VideoDiaryApplication.TAG;

/**
 * Created by Terry on 12/14/2016.
 * Presenter for communicate with data layer
 */

public class CreateVideoPresenter {
    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final VideoDataBase mDataBase;
    private CreateVideoNoteActivity mView;

    @Inject
    public CreateVideoPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoNoteDataBaseFactory dataBase) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mDataBase = dataBase;
    }

    public void setView(CreateVideoNoteActivity view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    public void getVideoNote(int id) {
        UseCase useCase = new UseCaseGetVideoNoteByPosition(mExecutor, mPostExecutorThread, mDataBase, id);
        GetVideoNoteSubscriber subscriber = new GetVideoNoteSubscriber();

        useCase.execute(subscriber);
        mComposeSubscriptionList.add(subscriber);
    }

    //region SUBSCRIBER
    private final class GetVideoNoteSubscriber extends DefaultSubscriber<VideoDomain> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(VideoDomain videoDomain) {
            mView.loadVideoNote(videoDomain);

        }
    }
    //endregion
}
