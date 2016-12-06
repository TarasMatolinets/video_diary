package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.storage.manager.NoteDataBaseFactory;
import com.mti.videodiary.di.annotation.PerActivity;
import com.mti.videodiary.mvp.view.activity.CreateNoteActivity;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseCreateNote;
import interactor.UseCaseGetNoteByPosition;
import interactor.UseCaseUpdateNotesList;
import model.NoteDomain;
import mti.com.videodiary.R;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.application.VideoDiaryApplication.TAG;

/**
 * Created by Terry on 11/5/2016.
 * Presenter for communicate with data model
 */

@PerActivity
public class CreateNotePresenter {

    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final NoteDataBaseFactory mDataBase;
    private CreateNoteActivity mView;

    @Inject
    public CreateNotePresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, NoteDataBaseFactory dataBase) {
        mExecutor = executor;
        mPostExecutorThread = postExecutionThread;
        mComposeSubscriptionList = new CompositeSubscription();
        mDataBase = dataBase;
    }

    public void setView(CreateNoteActivity view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
        mComposeSubscriptionList.unsubscribe();
    }

    public void getNoteByPosition(int position) {
        UseCase getNoteByPosition = new UseCaseGetNoteByPosition(mExecutor, mPostExecutorThread, mDataBase, position);

        GetNoteByPositionSubscriber subscriber = new GetNoteByPositionSubscriber();
        getNoteByPosition.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    public void updateNoteList(String description, String title, int position) {
        UseCase updateNoteList = new UseCaseUpdateNotesList(mExecutor, mPostExecutorThread, mDataBase, description, title, position);

        UpdateNoteListSubscriber subscriber = new UpdateNoteListSubscriber();
        updateNoteList.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    public void createNotePresenter(String description, String title) {
        UseCase createNote = new UseCaseCreateNote(mExecutor, mPostExecutorThread, mDataBase, description, title);

        CreateNoteSubscriber subscriber = new CreateNoteSubscriber();
        createNote.execute(subscriber);

        mComposeSubscriptionList.add(subscriber);
    }

    //region SUBSCRIBER
    private final class GetNoteByPositionSubscriber extends DefaultSubscriber<NoteDomain> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(NoteDomain note) {
            mView.fillNoteByPosition(note);
        }
    }

    private final class UpdateNoteListSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onCompleted() {
            SaveEditNoteText saveEditNoteText = new SaveEditNoteText();
            saveEditNoteText.setText(mView.getString(R.string.note_edited_successfully));

            EventBus.getDefault().post(saveEditNoteText);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(Void nothing) {
        }
    }

    private final class CreateNoteSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onCompleted() {
            SaveEditNoteText saveEditNoteText = new SaveEditNoteText();
            saveEditNoteText.setText(mView.getString(R.string.note_saved_successfully));

            EventBus.getDefault().post(saveEditNoteText);
            mView.finish();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(Void nothing) {
        }
    }
    //endregion

    public static class SaveEditNoteText {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
