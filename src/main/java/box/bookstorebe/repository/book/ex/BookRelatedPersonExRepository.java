package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookRelatedPersonExRepository {
    Page<BookRelatedPersonDocument> getBookRelatedPersons(String name, String type, int page, int size);
}
