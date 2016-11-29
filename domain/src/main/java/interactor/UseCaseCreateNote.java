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
    private final String mDescription;
    private final String mTitle;

    private NoteDataBase mDataBase;

    public UseCaseCreateNote(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, String description, String title) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        mDescription = description;
        mTitle = title;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.createNote(mDescription, mTitle);
    }
}
