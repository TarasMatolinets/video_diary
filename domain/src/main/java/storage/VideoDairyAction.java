package storage;

import rx.Observable;

/**
 * Created by Terry on 11/22/2016.
 */

public interface VideoDairyAction {
    Observable<String> getSavedImagePath(String path);
}
