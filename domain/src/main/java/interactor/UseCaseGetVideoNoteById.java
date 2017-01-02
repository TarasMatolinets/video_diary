package interactor;

import database.IDataBase;
import database.VideoIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * Use case for get video note by id
 */

public class UseCaseGetVideoNoteById extends UseCase {

    private final VideoIDataBase mDataBase;
    private final int mId;

    public UseCaseGetVideoNoteById(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoIDataBase) IDataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getVideoById(mId);
    }
}
