package interactor;

import database.DataBase;
import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseGetNotesList extends UseCase{

    private final NoteDataBase mDataBase;

    public UseCaseGetNotesList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase  = (NoteDataBase) dataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getListNotes();
    }
}
