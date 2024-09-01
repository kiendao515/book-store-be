package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;

public interface BookExRepository {
    Page<BookDocument> getBooks(String name, String categoryId, String authorId, String storeId, ZonedDateTime startAt, ZonedDateTime endAt, Integer page, Integer size);
}
