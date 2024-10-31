package box.bookstorebe.repository.booksearchrequest;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import box.bookstorebe.repository.booksearchrequest.ex.BookSearchRequestExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookSearchRequestRepository extends MongoRepository<BookSearchRequestDocument, String>, BookSearchRequestExRepository {
}
