package box.bookstorebe.repository.bookstore;

import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.repository.bookstore.ex.BookStoreExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookStoreRepository extends MongoRepository<StoreDocument, String>, BookStoreExRepository {
    List<StoreDocument> findAllByAccountIdInAndDeletedAtIsNull(List<String> accountIds);
    StoreDocument findByAccountId(String accountId);
}
