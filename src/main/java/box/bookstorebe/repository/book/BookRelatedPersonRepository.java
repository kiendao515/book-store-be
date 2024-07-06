package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.repository.book.ex.BookRelatedPersonExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRelatedPersonRepository extends MongoRepository<BookRelatedPersonDocument, String>, BookRelatedPersonExRepository {
}
