package box.bookstorebe.repository.order;

import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.repository.order.ex.OrderExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrderRepository extends MongoRepository<OrderDocument, String>, OrderExRepository {
    OrderDocument findByOrderCode(String orderId);

}
