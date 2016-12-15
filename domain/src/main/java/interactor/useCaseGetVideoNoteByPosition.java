package interactor;

import database.DataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseGetVideoNoteByPosition extends UseCase {

    private final VideoDataBase mDataBase;
    private final int mId;

    public UseCaseGetVideoNoteByPosition(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoDataBase) dataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getVideoById(mId);
    }
}
