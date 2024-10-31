package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.repository.book.ex.BookExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BookRepository extends MongoRepository<BookDocument, String>, BookExRepository {
    @Query("{ 'relatedPeople': { $elemMatch: { 'type': 'AUTHOR', 'relatedPersonId': ?0 } } }")
    List<BookDocument> findBooksByAuthorId(String authorId);
}
