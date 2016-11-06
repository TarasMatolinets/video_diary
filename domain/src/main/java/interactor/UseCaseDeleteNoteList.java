package interactor;

import database.DataBase;
import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseDeleteNoteList extends UseCase {
    private NoteDataBase mDataBase;

    public UseCaseDeleteNoteList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase noteDataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) noteDataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.deleteList();
    }
}
