package interactor;

import database.IDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 *  UseCase for create by id
 */

public class UseCaseDeleteNoteId extends UseCase {
    private final int mId;
    private IDataBase mIDataBase;

    public UseCaseDeleteNoteId(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mIDataBase = IDataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mIDataBase.deleteItemById(mId);
    }
}
