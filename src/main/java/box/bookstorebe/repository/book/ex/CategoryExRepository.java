package box.bookstorebe.repository.book.ex;

import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.dto.book.CategorySalesStat;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryExRepository {
    Page<CategoryDto> getCategories(String name, Integer page, Integer size);
    List<CategorySalesStat> getTopSellingCategories();

}
