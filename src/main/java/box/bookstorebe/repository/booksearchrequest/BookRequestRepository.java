package box.bookstorebe.repository.booksearchrequest;

import box.bookstorebe.document.booksearchrequest.BookRequestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRequestRepository extends MongoRepository<BookRequestDocument, String> {
    List<BookRequestDocument> getAllByBookSearchRequestId(String bookSearchRequestId);
    List<BookRequestDocument> getAllByBookSearchRequestIdIn(List<String> bookSearchRequestIds);

    void deleteAllByBookSearchRequestId(String bookSearchRequestId);
}
