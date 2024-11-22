package box.bookstorebe.repository.order;
import box.bookstorebe.document.order.OrderItemDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderItemRepository extends MongoRepository<OrderItemDocument, String> {
    List<OrderItemDocument> findAllByBookInventoryIdIn(List<String> ids);
    List<OrderItemDocument> findAllByOrderId(String id);
}
