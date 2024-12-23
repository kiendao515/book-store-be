package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookFavoriteDocument;
import box.bookstorebe.repository.book.ex.BookFavoriteExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookFavoriteRepository extends MongoRepository<BookFavoriteDocument, String>, BookFavoriteExRepository {
    List<BookFavoriteDocument> findAllByAccountId(String id);
    BookFavoriteDocument findByAccountIdAndBookId(String userId, String bookId);
    List<BookFavoriteDocument> findAllByBookId(String bookId);
}
