package box.bookstorebe.repository.order;
import box.bookstorebe.document.order.OfflineOrderDetailDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OfflineOrderDetailRepository extends MongoRepository<OfflineOrderDetailDocument, String> {
    List<OfflineOrderDetailDocument> findAllByOfflineOrderId(String offlineOrderId);

    List<OfflineOrderDetailDocument> findAllByOfflineOrderIdIn(List<String> offlineOrderIds);
}
