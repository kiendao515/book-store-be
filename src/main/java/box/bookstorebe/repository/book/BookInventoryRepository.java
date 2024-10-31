package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookInventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookInventoryRepository extends MongoRepository<BookInventory, String> {
    void deleteByBookId(String bookId);

    List<BookInventory> findAllByBookId(String bookId);
    BookInventory findAllByBookIdAndStoreId(String storeId,String bookId);

    List<BookInventory> findAllByBookIdAndStoreIdAndType(String storeId,String bookId, String type);

    List<BookInventory> findAllByBookIdIn(List<String> bookIds);
}
