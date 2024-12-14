package box.bookstorebe.repository.book;

import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.book.BookType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookInventoryRepository extends MongoRepository<BookInventory, String> {
    void deleteByBookId(String bookId);

    List<BookInventory> findAllByBookId(String bookId);

    List<BookInventory> findAllByBookIdAndStoreId(String storeId, String bookId);

    List<BookInventory> findAllByBookIdAndStoreIdAndType(String storeId, String bookId, String type);

    List<BookInventory> findAllByBookIdIn(List<String> bookIds);

    BookInventory findByBookIdAndStoreIdAndType(String bookId, String storeId, BookType type);
    List<BookInventory> findAllByRelatedBookId(String relatedId);

    List<BookInventory> findAllByIdIn(List<String> bookIds);

    List<BookInventory> findAllByBarcodeIn(List<String> barcodes);

    BookInventory findByBarcode(String barcode);
}
