package interactor;

import database.IDataBase;
import database.NoteIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * Use case for update video note
 */

public class UseCaseUpdateNotesList extends UseCase {
    private final String mDescription;
    private final String mTitle;
    private final int mPosition;
    private NoteIDataBase mDataBase;

    public UseCaseUpdateNotesList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, String description, String title, int position) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteIDataBase) IDataBase;
        mDescription = description;
        mTitle = title;
        mPosition = position;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.updateNoteList(mDescription, mTitle, mPosition);
    }
}
