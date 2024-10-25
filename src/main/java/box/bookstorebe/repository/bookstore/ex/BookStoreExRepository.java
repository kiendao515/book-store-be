package box.bookstorebe.repository.bookstore.ex;

import box.bookstorebe.document.bookstore.StoreDocument;
import org.springframework.data.domain.Page;

public interface BookStoreExRepository {
    Page<StoreDocument> getBookStores(String name, Integer page, Integer size);
}
