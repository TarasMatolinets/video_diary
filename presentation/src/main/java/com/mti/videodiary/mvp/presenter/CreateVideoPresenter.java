package com.mti.videodiary.mvp.presenter;

import android.util.Log;

import com.mti.videodiary.data.storage.manager.VideoNoteIDataBaseFactory;
import com.mti.videodiary.mvp.view.activity.CreateVideoNoteActivity;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.VideoNoteText;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import database.VideoIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import interactor.DefaultSubscriber;
import interactor.UseCase;
import interactor.UseCaseCreateVideoNote;
import interactor.UseCaseGetVideoNoteById;
import interactor.UseCaseUpdateVideoNoteList;
import model.VideoDomain;
import mti.com.videodiary.R;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.mti.videodiary.data.Constants.FILE_FORMAT;
import static com.mti.videodiary.data.Constants.TAG;

/**
 * Created by Terry on 12/14/2016.
 * Presenter for communicate with data layer
 */

public class CreateVideoPresenter {
    private final ThreadExecutor mExecutor;
    private final PostExecutionThread mPostExecutorThread;
    private final CompositeSubscription mComposeSubscriptionList;
    private final VideoIDataBase mDataBase;
    private CreateVideoNoteActivity mView;

    @Inject
    public CreateVideoPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoNoteIDataBaseFactory dataBase) {
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
        UseCase useCase = new UseCaseGetVideoNoteById(mExecutor, mPostExecutorThread, mDataBase, id);
        GetVideoNoteSubscriber subscriber = new GetVideoNoteSubscriber();

        useCase.execute(subscriber);
        mComposeSubscriptionList.add(subscriber);
    }

    public void updateVideoNote(String videoPath, String title, String description, int videoId, boolean isDeletedVideo) {
        VideoDomain videoDomain = new VideoDomain();

        videoPath = renameFile(videoPath, title);

        videoDomain.setVideoUrl(videoPath);
        videoDomain.setImageUrl(videoPath);
        videoDomain.setTitle(title);
        videoDomain.setId(videoId);
        videoDomain.setDescription(description);
        videoDomain.setDeletedVideo(isDeletedVideo);

        UseCase useCase = new UseCaseUpdateVideoNoteList(mExecutor, mPostExecutorThread, videoDomain, mDataBase);
        SaveUpdateVideoNoteSubscriber subscriber = new SaveUpdateVideoNoteSubscriber(mView.getString(R.string.video_note_edited_successfully));
        useCase.execute(subscriber);
    }

    private String renameFile(String videoPath, String title) {
        String[] splitArray = videoPath.split("/");
        splitArray[splitArray.length - 1] = title + FILE_FORMAT;

        StringBuilder builder = new StringBuilder();

        for (String s : splitArray) {
            builder.append(s).append("/");
        }

        File oldFileName = new File(videoPath);
        File newFileName = new File(builder.toString());

        boolean success = oldFileName.renameTo(newFileName);

        if (success) {
            Log.i(TAG, "video file renamed successfully");
        }
        return newFileName.getAbsolutePath();
    }

    public void createNewVideoDaily(String videoPath, String title, String description) {
        VideoDomain videoDomain = new VideoDomain();

        videoPath = renameFile(videoPath, title);
        videoDomain.setVideoUrl(videoPath);
        videoDomain.setImageUrl(videoPath);
        videoDomain.setTitle(title);
        videoDomain.setDescription(description);

        UseCase useCase = new UseCaseCreateVideoNote(mExecutor, mPostExecutorThread, mDataBase, videoDomain);
        SaveUpdateVideoNoteSubscriber subscriber = new SaveUpdateVideoNoteSubscriber(mView.getString(R.string.video_note_saved_successfully));
        useCase.execute(subscriber);
    }

    //region SUBSCRIBER
    private final class SaveUpdateVideoNoteSubscriber extends DefaultSubscriber<Void> {
        private String message;

        public SaveUpdateVideoNoteSubscriber(String text) {
            message = text;
        }

        @Override
        public void onCompleted() {
            VideoNoteText videoNoteText = new VideoNoteText();
            videoNoteText.setText(message);

            EventBus.getDefault().post(videoNoteText);
            mView.runExitAnimation();
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
    //endregion
}
