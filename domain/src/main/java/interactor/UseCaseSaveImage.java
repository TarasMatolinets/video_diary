package interactor;

import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;
import storage.VideoDairyAction;

/**
 * Created by Terry on 11/22/2016.
 */

public class UseCaseSaveImage extends UseCase {
    private VideoDairyAction mAction;
    private String mImagePath;

    public UseCaseSaveImage(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, VideoDairyAction action, String imagePath) {
        super(threadExecutor, postExecutionThread);
        mAction = action;
        mImagePath = imagePath;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mAction.getSavedImagePath(mImagePath);
    }
}
