package interactor;

import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseDeleteVideoNoteId extends UseCase {
    private VideoDataBase mDataBase;
    private final int mId;

    public UseCaseDeleteVideoNoteId(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, VideoDataBase videoDataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mDataBase = videoDataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.deleteItemById(mId);
    }
}
