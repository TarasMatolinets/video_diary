package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.di.annotation.PerFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment;

import java.util.List;

import javax.inject.Inject;

import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseDeleteNoteId;
import interactor.UseCaseGetNotesByTitle;
import interactor.UseCaseGetVideoNoteList;
import model.VideoDomain;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.application.VideoDiaryApplication.TAG;

/**
 * Created by Terry on 12/15/2016.
 * VideoFragment presenter for communicate with data layer
 */
@PerFragment
public class VideoFragmentPresenter {

    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final VideoDataBase mDataBase;
    private VideoFragment mView;

    @Inject
    VideoFragmentPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoDataBase dataBase) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mDataBase = dataBase;
    }

    public void setView(VideoFragment view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    public void loadVideoNoteList() {
        UseCase useCase = new UseCaseGetVideoNoteList(mExecutor, mPostExecutorThread, mDataBase);

        GetListVideoNotesSubscriber subscriber = new GetListVideoNotesSubscriber();
        useCase.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    public void loadSearchedVideoNotes(String query) {
        UseCase useCaseGetSearchList = new UseCaseGetNotesByTitle(mExecutor, mPostExecutorThread, mDataBase, query);

        GetListNotesSearchSubscriber subscriber = new GetListNotesSearchSubscriber();
        useCaseGetSearchList.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    public void deleteVideoNoteItem(int id, int notePosition) {
        UseCase useCaseDeleteList = new UseCaseDeleteNoteId(mExecutor, mPostExecutorThread, mDataBase, id);

        DeleteNoteItemSubscriber subscriber = new DeleteNoteItemSubscriber(notePosition);
        useCaseDeleteList.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }


    //region SUBSCRIBER
    private final class GetListVideoNotesSubscriber extends DefaultSubscriber<List<VideoDomain>> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(List<VideoDomain> list) {
            mView.setupRecycleView(list);

            if (list.isEmpty()) {
                mView.showEmptyView(true);
            } else {
                mView.showEmptyView(false);
            }
        }
    }

    private final class GetListNotesSearchSubscriber extends DefaultSubscriber<List<VideoDomain>> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(List<VideoDomain> list) {
            mView.loadQueryNotes(list);
        }
    }

    private final class DeleteNoteItemSubscriber extends DefaultSubscriber<Void> {

        private final int position;

        public DeleteNoteItemSubscriber(int notePosition) {
            position = notePosition;
        }

        @Override
        public void onCompleted() {
            loadVideoNoteList();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(Void nothing) {
//            CreateNotePresenter.NoteText noteText = new CreateNotePresenter.NoteText();
//            noteText.setText(mView.getResources().getString(R.string.note_deleted_successfully));
//            mView.showNoteAction(noteText);
//
//            mView.removeNoteFromList(position);
        }
    }
    //endregion
}
