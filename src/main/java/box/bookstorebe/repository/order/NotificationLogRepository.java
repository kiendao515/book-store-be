package box.bookstorebe.repository.order;

import box.bookstorebe.document.order.NotificationLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationLogRepository extends MongoRepository<NotificationLogDocument,String> {
}
