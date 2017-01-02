package interactor;

import database.IDataBase;
import database.VideoIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import model.VideoDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * UseCase for create video note
 */

public class UseCaseCreateVideoNote extends UseCase {
    private final VideoDomain mVideoDomain;
    private VideoIDataBase mDataBase;

    public UseCaseCreateVideoNote(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, VideoDomain videoDomain) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoIDataBase) IDataBase;
        mVideoDomain = videoDomain;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.createVideo(mVideoDomain);
    }
}
