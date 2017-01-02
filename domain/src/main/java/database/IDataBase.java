package database;

import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 *
 */

public interface IDataBase {
    Observable<Void> deleteItemById(int id);

    Observable<Void> deleteList();
}
