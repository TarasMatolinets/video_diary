import java.util.List;

import co.otenti.domain.model.ProductInfoDomain;
import rx.Observable;

/**
 * Interface for transfer object from presenter to DB in data layer
 */
public interface DataBase {

    Observable<List<ProductInfoDomain>> getProductInfo();
    Observable<Void> saveProductInfo(ProductInfoDomain tagProductDB);
}
