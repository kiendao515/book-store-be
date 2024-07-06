package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.user.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookExRepository {
    Page<BookDocument> getBooks(String name, List<String> categoryIds, List<String> collectionIds, List<String> relatedPersonIds, String storeId, Integer page, Integer size);
}
