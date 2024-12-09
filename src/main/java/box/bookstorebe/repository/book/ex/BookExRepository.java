package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookExRepository {
    Page<BookDocument> getBooks(String name, String categoryId, String storeId, String collectionId, ZonedDateTime startAt, ZonedDateTime endAt, List<String> bookSearchIds, Integer page, Integer size);
}
