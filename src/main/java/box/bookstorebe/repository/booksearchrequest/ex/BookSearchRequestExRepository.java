package box.bookstorebe.repository.booksearchrequest.ex;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import org.springframework.data.domain.Page;

public interface BookSearchRequestExRepository {
    Page<BookSearchRequestDocument> getBookSearchRequests(String name, String fullName, String phoneNumber, Integer page, Integer size);
}
