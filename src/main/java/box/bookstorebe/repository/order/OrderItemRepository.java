package box.bookstorebe.repository.order;
import box.bookstorebe.document.order.OrderItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderItemRepository extends MongoRepository<OrderItem, String> {
    List<OrderItem> findAllByBookInventoryIdIn(List<String> ids);
}
