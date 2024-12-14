package box.bookstorebe.repository.order;

import box.bookstorebe.document.order.OfflineOrderDocument;
import box.bookstorebe.repository.order.ex.OfflineOrderExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfflineOrderRepository extends MongoRepository<OfflineOrderDocument, String>, OfflineOrderExRepository {
}
