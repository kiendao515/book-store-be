package box.bookstorebe.repository.bookstore;

import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.repository.bookstore.ex.BookStoreExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookStoreRepository extends MongoRepository<StoreDocument, String>, BookStoreExRepository {
}
