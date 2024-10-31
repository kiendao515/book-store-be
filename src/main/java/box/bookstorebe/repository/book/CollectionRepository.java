package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.CollectionDocument;
import box.bookstorebe.repository.book.ex.CollectionExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollectionRepository extends MongoRepository<CollectionDocument, String>, CollectionExRepository {
}
