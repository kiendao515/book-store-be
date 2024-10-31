package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.book.CollectionDocument;
import org.springframework.data.domain.Page;

public interface CollectionExRepository {
    Page<CollectionDocument> getCollections(String name, Integer page, Integer size);
}
