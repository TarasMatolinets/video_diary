package interactor;

import database.DataBase;
import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import model.NoteDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseCreateNote extends UseCase {
    private final NoteDomain mNoteDomain;
    private NoteDataBase mDataBase;

    public UseCaseCreateNote(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, NoteDomain noteDomain) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        mNoteDomain = noteDomain;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.createNote(mNoteDomain);
    }
}
