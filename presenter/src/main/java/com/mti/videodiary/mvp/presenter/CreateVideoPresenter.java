package com.mti.videodiary.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;

import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.data.storage.manager.NoteDataBaseFactory;
import com.mti.videodiary.data.storage.manager.VideoNoteDataBaseFactory;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.utils.Constants;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseCreateVideoNote;
import interactor.UseCaseDeleteVideoNoteId;
import interactor.UseCaseGetVideoNoteByPosition;
import interactor.UseCaseUpdateVideoNoteList;
import model.NoteDomain;
import model.VideoDomain;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.application.VideoDiaryApplication.TAG;
import static com.mti.videodiary.data.Constants.FILE_FORMAT;
import static com.mti.videodiary.data.Constants.VIDEO_DIR;
import static java.io.File.separator;

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

    public void updateVideoNote(String videoPath, String imagePath, String title, String description, int videoId) {
        VideoDomain videoDomain = new VideoDomain();

        videoDomain.setVideoUrl(videoPath);
        videoDomain.setImageUrl(imagePath);
        videoDomain.setTitle(title);
        videoDomain.setId(videoId);
        videoDomain.setDescription(description);

        UseCase useCase = new UseCaseUpdateVideoNoteList(mExecutor, mPostExecutorThread, videoDomain, mDataBase);
        SaveUpdateVideoNoteSubscriber subscriber = new SaveUpdateVideoNoteSubscriber();
        useCase.execute(subscriber);
    }

    public void deleteVideoNote(int id) {
        UseCase useCase = new UseCaseDeleteVideoNoteId(mExecutor, mPostExecutorThread, mDataBase, id);
        DeleteVideoNoteSubscriber subscriber = new DeleteVideoNoteSubscriber();

        useCase.execute(subscriber);
        mComposeSubscriptionList.add(subscriber);
    }

    public void createNewVideoDaily(String videoPath, String imagePath, String title, String description) {
        VideoDomain videoDomain = new VideoDomain();

        videoDomain.setVideoUrl(videoPath);
        videoDomain.setImageUrl(imagePath);
        videoDomain.setTitle(title);
        videoDomain.setDescription(description);

        UseCase useCase = new UseCaseCreateVideoNote(mExecutor, mPostExecutorThread, mDataBase, videoDomain);
        SaveUpdateVideoNoteSubscriber subscriber = new SaveUpdateVideoNoteSubscriber();
        useCase.execute(subscriber);
    }

    //region SUBSCRIBER
    private final class SaveUpdateVideoNoteSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onCompleted() {
            mView.finish();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }
    }

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

    private final class DeleteVideoNoteSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.toString());
        }
    }
    //endregion
}
