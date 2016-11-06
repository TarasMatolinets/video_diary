package interactor;

import database.DataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class USeCaseDeleteVideoNotesList extends UseCase {
    private final VideoDataBase mDataBase;

    public USeCaseDeleteVideoNotesList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoDataBase) dataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.deleteList();
    }
}
