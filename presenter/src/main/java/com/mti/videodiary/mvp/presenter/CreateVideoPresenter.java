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

    public void updateVideoNote(String videoPath, Bitmap bitmap, String title, String description, int videoId) {
        File oldFileName = new File(videoPath);
        File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_DIR + separator + title + FILE_FORMAT);

        boolean success = oldFileName.renameTo(newFileName);

        if (success) {
            String tempBitmapPath = UserHelper.saveBitmapToSD(bitmap);
            Bitmap finalBitmap = UserHelper.decodeSampledBitmapFromResource(tempBitmapPath);
            String finalPathBitmap = UserHelper.saveBitmapToSD(finalBitmap);

            if (tempBitmapPath != null) {
                File file = new File(tempBitmapPath);

                if(file.exists()) {
                    boolean deleted = file.delete();
                }

//
//                Video video = videoDataManager.getVideoByPosition(position);
//                video.setDescription(mEtDescription.getText().toString());
//                video.setTitle(mEtTitle.getText().toString());
//
//                if (!mVideoFilePath.equals(video.getVideoName())) {
//                    File videoOld = new File(video.getVideoName());
//
//                    if (videoOld.exists())
//                        videoOld.delete();
//                }
//
//                video.setVideoUrl(newFileName.getAbsolutePath());
//
//                File imageOld = new File(video.getImageUrl());
//
//                if (imageOld.exists())
//                    imageOld.delete();
//
//                video.setImageUrl(finalPathBitmap);
//
//
//                videoDataManager.updateVideoList(video);
            }
        }
    }

    public void deleteVideoNote(int id) {
        UseCase useCase = new UseCaseDeleteVideoNoteId(mExecutor, mPostExecutorThread, mDataBase, id);
        DeleteVideoNoteSubscriber subscriber = new DeleteVideoNoteSubscriber();

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

    private final class DeleteVideoNoteSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onCompleted() {

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
}
