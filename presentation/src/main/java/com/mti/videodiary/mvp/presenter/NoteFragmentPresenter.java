package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.manager.NoteIDataBaseFactory;
import com.mti.videodiary.di.annotation.PerFragment;
import com.mti.videodiary.mvp.presenter.CreateNotePresenter.NoteText;
import com.mti.videodiary.mvp.view.fragment.NoteFragment;

import java.util.List;

import javax.inject.Inject;

import database.NoteIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseDeleteNoteId;
import interactor.UseCaseGetNotesByTitle;
import interactor.UseCaseGetNotesList;
import model.NoteDomain;
import mti.com.videodiary.R;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.data.Constants.TAG;

/**
 * Created by Terry on 11/6/2016.
 * Presenter for communicate with note data model
 */

@PerFragment
public class NoteFragmentPresenter {

    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final NoteIDataBase mDataBase;
    private NoteFragment mView;

    @Inject
    NoteFragmentPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, NoteIDataBaseFactory dataBase) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mDataBase = dataBase;
    }

    public void setView(NoteFragment view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    public void loadNoteList() {
        UseCase useCaseGetNotesList = new UseCaseGetNotesList(mExecutor, mPostExecutorThread, mDataBase);

        GetListNotesSubscriber subscriber = new GetListNotesSubscriber();
        useCaseGetNotesList.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    public void loadSearchedNotes(String query) {
        UseCase useCaseGetSearchList = new UseCaseGetNotesByTitle(mExecutor, mPostExecutorThread, mDataBase, query);

        GetListNotesSearchSubscriber subscriber = new GetListNotesSearchSubscriber();
        useCaseGetSearchList.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    public void deleteNoteItem(int id, int notePosition) {
        UseCase useCaseDeleteList = new UseCaseDeleteNoteId(mExecutor, mPostExecutorThread, mDataBase, id);

        DeleteNoteItemSubscriber subscriber = new DeleteNoteItemSubscriber(notePosition);
        useCaseDeleteList.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    //region SUBSCRIBER
    private final class GetListNotesSubscriber extends DefaultSubscriber<List<NoteDomain>> {

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(List<NoteDomain> list) {
            mView.setupRecycleView(list);

            if (list.isEmpty()) {
                mView.showEmptyView(true);
            } else {
                mView.showEmptyView(false);
            }
        }
    }

    private final class GetListNotesSearchSubscriber extends DefaultSubscriber<List<NoteDomain>> {
        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(List<NoteDomain> list) {
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
            loadNoteList();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(Void nothing) {
            NoteText noteText = new NoteText();
            noteText.setText(mView.getResources().getString(R.string.note_deleted_successfully));
            mView.showNoteAction(noteText);
            mView.removeNoteFromList(position);
        }
    }
    //endregion
}
