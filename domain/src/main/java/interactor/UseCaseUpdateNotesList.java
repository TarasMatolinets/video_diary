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

public class UseCaseUpdateNotesList extends UseCase {
    private final String mDescription;
    private final String mTitle;
    private final int mPosition;
    private NoteDataBase mDataBase;

    public UseCaseUpdateNotesList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, String description, String title, int position) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        mDescription = description;
        mTitle = title;
        mPosition = position;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.updateNoteList(mDescription, mTitle, mPosition);
    }
}
