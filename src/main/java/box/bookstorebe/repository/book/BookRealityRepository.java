package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookRealityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRealityRepository extends MongoRepository<BookRealityDocument, String> {
    void deleteByBookId(String bookId);
}
