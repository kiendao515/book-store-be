package box.bookstorebe.repository.book.ex;

import box.bookstorebe.dto.book.CategoryDto;
import org.springframework.data.domain.Page;

public interface CategoryExRepository {
    Page<CategoryDto> getCategories(String name, Integer page, Integer size);
}
