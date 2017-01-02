package interactor;

import database.IDataBase;
import database.NoteIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * UseCase for create note
 */

public class UseCaseCreateNote extends UseCase {
    private final String mDescription;
    private final String mTitle;

    private NoteIDataBase mDataBase;

    public UseCaseCreateNote(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, String description, String title) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteIDataBase) IDataBase;
        mDescription = description;
        mTitle = title;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.createNote(mDescription, mTitle);
    }
}
