package interactor;

import database.DataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseGetNotes  extends UseCase{

    private final DataBase mDataBase;

    public UseCaseGetNotes(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase  = dataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getListNotes();
    }
}
