import java.util.List;

import rx.Observable;

/**
 * Interface for transfer object from presenter to DB in data layer
 */
public interface DataBase {

    Observable<Void> saveProductInfo();
}
