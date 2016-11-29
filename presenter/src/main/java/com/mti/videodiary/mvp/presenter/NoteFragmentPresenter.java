package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.di.annotation.PerActivity;

import javax.inject.Inject;

import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseGetNoteByPosition;
import model.NoteDomain;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.application.VideoDiaryApplication.TAG;

/**
 * Created by Terry on 11/6/2016.
 * Presenter for collaborate between view and model
 */

@PerActivity
public class NoteFragmentPresenter {

    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final NoteDataBase mDataBase;
    private NoteFragmentPresenter mView;

    @Inject
    NoteFragmentPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, NoteDataBase dataBase) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mDataBase = dataBase;
    }

    public void setView(NoteFragmentPresenter view) {
        mView = view;
    }

    public void retriveNotesList() {

    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    //region SUBSCRIBER
    private final class GetListNotes extends DefaultSubscriber<String> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(String latestAppVersion) {

        }
    }

    //endregion
}
