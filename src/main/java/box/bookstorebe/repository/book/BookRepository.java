package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.repository.book.ex.BookExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<BookDocument, String>, BookExRepository {
}
