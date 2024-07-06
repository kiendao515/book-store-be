package box.bookstorebe.repository.bookstore;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.repository.bookstore.ex.BookStoreExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookStoreRepository extends MongoRepository<BookStoreDocument, String>, BookStoreExRepository {
}
