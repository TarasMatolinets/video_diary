package com.mti.videodiary.mvp.presenter;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mti.videodiary.data.helper.UserHelper;
import com.mti.videodiary.data.storage.manager.VideoNoteDataBaseFactory;
import com.mti.videodiary.di.annotation.PerFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment;
import com.mti.videodiary.mvp.view.fragment.VideoFragment.VideoNoteText;

import java.io.File;
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
import interactor.UseCaseGetVideoNotesByTitle;
import model.VideoDomain;
import mti.com.videodiary.R;
import rx.subscriptions.CompositeSubscription;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.MEDIA_MOUNTED_READ_ONLY;
import static com.mti.videodiary.data.Constants.TAG;
import static com.mti.videodiary.data.Constants.VIDEO_DIR;
import static com.mti.videodiary.data.Constants.VIDEO_FILE_NAME;

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
    VideoFragmentPresenter(ThreadExecutor executor, PostExecutionThread postExecutionThread, VideoNoteDataBaseFactory dataBase) {
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

    public File saveFileInStorage() {
        String state = Environment.getExternalStorageState();
        File mediaFile = null;

        if (MEDIA_MOUNTED.equals(state)) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_DIR + VIDEO_FILE_NAME);
        } else if (MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mediaFile = new File(mView.getActivity().getFilesDir().getAbsolutePath() + VIDEO_DIR + VIDEO_FILE_NAME);
        }
        return mediaFile;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File createFileFromUri(Uri uri) {
        String state = Environment.getExternalStorageState();
        File mediaFile = null;
        String path = UserHelper.getRealPathFromURI(mView.getActivity(), uri);
        if (!TextUtils.isEmpty(path)) {
            File videoFile = new File(path);

            if (MEDIA_MOUNTED.equals(state)) {
                mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_DIR + VIDEO_FILE_NAME);
            } else if (MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                mediaFile = new File(mView.getActivity().getFilesDir().getAbsolutePath() + VIDEO_DIR + VIDEO_FILE_NAME)
                ;
            }
            UserHelper.copyFileUsingFileStreams(videoFile, mediaFile);

            if (videoFile.exists()) {
                videoFile.delete();
            }
        }

        return mediaFile;
    }

    //region SUBSCRIBER
    private final class GetListVideoNotesSubscriber extends DefaultSubscriber<List<VideoDomain>> {

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
