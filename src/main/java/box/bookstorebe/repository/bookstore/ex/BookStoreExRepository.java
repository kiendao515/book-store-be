package box.bookstorebe.repository.bookstore.ex;

import box.bookstorebe.document.book.CollectionDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import org.springframework.data.domain.Page;

public interface BookStoreExRepository {
    Page<BookStoreDocument> getBookStores(String name, Integer page, Integer size);
}
