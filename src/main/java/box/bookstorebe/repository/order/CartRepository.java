package box.bookstorebe.repository.order;

import box.bookstorebe.document.order.CartDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<CartDocument, String> {
     List<CartDocument> findAllByAccountId(String accountId);
     CartDocument findByAccountIdAndBookInventoryId(String accountId, String bookInventoryId);
}
