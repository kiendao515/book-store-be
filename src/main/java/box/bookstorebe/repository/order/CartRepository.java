package box.bookstorebe.repository.order;

import box.bookstorebe.document.order.CartDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<CartDocument, String> {
}
