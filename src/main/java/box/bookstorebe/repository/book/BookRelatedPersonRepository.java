package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.repository.book.ex.BookRelatedPersonExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BookRelatedPersonRepository extends MongoRepository<BookRelatedPersonDocument, String>, BookRelatedPersonExRepository {
    List<BookRelatedPersonDocument> findByNameStartingWithAndType(String nameStart, String type);
}
