package interactor;

import database.DataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import model.VideoDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseCreateVideoNote extends UseCase {
    private final VideoDomain mVideoDomain;
    private VideoDataBase mDataBase;

    public UseCaseCreateVideoNote(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, VideoDomain videoDomain) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoDataBase) dataBase;
        mVideoDomain = videoDomain;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.createVideo(mVideoDomain);
    }
}
