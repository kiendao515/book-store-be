package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.book.BookType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookInventoryRepository extends MongoRepository<BookInventory, String> {
    void deleteByBookId(String bookId);

    List<BookInventory> findAllByBookId(String bookId);

    List<BookInventory> findAllByBookIdAndStoreId(String bookId, String storeId);
    List<BookInventory> findAllByBookIdInAndStoreId(List<String> bookId, String storeId);

    List<BookInventory> findAllByBookIdAndStoreIdAndType(String bookId, String storeId, BookType type);

    List<BookInventory> findAllByBookIdIn(List<String> bookIds);

    BookInventory findByBookIdAndStoreIdAndType(String bookId, String storeId, BookType type);
    List<BookInventory> findAllByRelatedBookId(String relatedId);

    List<BookInventory> findAllByIdIn(List<String> bookIds);
    List<BookInventory> findAllByStoreIdAndCreatedAtBetween(String storeId, ZonedDateTime start, ZonedDateTime end);
    List<BookInventory> findAllByStoreId(String id);

    List<BookInventory> findAllByBarcodeIn(List<String> barcodes);

    BookInventory findByBarcode(String barcode);
}
