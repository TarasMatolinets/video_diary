package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.manager.VideoNoteIDataBaseFactory;
import com.mti.videodiary.di.annotation.PerFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.VideoNoteText;

import java.util.List;

import javax.inject.Inject;

import database.VideoIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseDeleteNoteId;
import interactor.UseCaseGetVideoNoteList;
import interactor.UseCaseGetVideoNotesByTitle;
import model.VideoDomain;
import mti.com.videodiary.R;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.data.Constants.TAG;

/**
 * Created by Terry on 12/15/2016.
 * Presenter for communicate with video data model
 */
@PerFragment
public class VideoFragmentPresenter {

    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final VideoIDataBase mDataBase;
    private VideoFragment mView;

    @Inject
    VideoFragmentPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoNoteIDataBaseFactory dataBase) {
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
        UseCase useCaseGetSearchList = new UseCaseGetVideoNotesByTitle(mExecutor, mPostExecutorThread, mDataBase, query);

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
    private class GetListVideoNotesSubscriber extends DefaultSubscriber<List<VideoDomain>> {

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(List<VideoDomain> list) {
            mView.updateRecycleView(list);

            if (list.isEmpty()) {
                mView.showEmptyView(true);
            } else {
                mView.showEmptyView(false);
            }
        }
    }

    private final class GetListNotesSearchSubscriber extends DefaultSubscriber<List<VideoDomain>> {

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
            VideoNoteText noteText = new VideoNoteText();
            noteText.setText(mView.getResources().getString(R.string.note_deleted_successfully));
            mView.showNoteAction(noteText);
            mView.removeNoteFromList(position);
        }
    }
    //endregion
}
