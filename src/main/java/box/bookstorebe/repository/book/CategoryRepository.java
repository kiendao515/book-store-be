package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.repository.book.ex.CategoryExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<CategoryDocument, String>, CategoryExRepository {
}
