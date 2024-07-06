package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.CategoryDocument;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryExRepository {
    Page<CategoryDocument> getCategories(String name, Integer page, Integer size);
}
