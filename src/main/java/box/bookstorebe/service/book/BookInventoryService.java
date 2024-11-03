package box.bookstorebe.service.book;

import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.dto.book.BookInventoryDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class BookInventoryService {
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;
    private final BookStoreRepository storeRepository;
    private final ImageRepository imageRepository;
    private final BookService bookService;

    public List<BookInventory> getDetailBookInventory(String bookId,String storeId) throws BizException {
        if(bookId.isBlank() || storeId.isBlank()) throw new BizException("invalid params");
        return bookInventoryRepository.findAllByBookIdAndStoreId(bookId, storeId);
    }

    public void createBookInventory(CreateBookRealityModel bookRealityModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        StoreDocument storeDocument = storeRepository.findById(bookRealityModel.getStoreId()).orElseThrow(()-> new BizException("invalid store id"));
        BookInventory bookInventory = new BookInventory();
        bookInventory.setBookId(bookDocument.getId());
        bookInventory.setType(bookRealityModel.getType());
        bookInventory.setPrice(bookRealityModel.getPrice());
        bookInventory.setCoverImage(bookRealityModel.getCoverImage());
        bookInventory.setQuantity(bookRealityModel.getQuantity());
        bookInventory.setStoreId(storeDocument.getId());
        bookInventory.setCreatedAt(ZonedDateTime.now());
        bookInventory.setUpdatedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookInventory);
    }

    public void updateBookInventory(String id, UpdateBookRealityModel bookRealityModel) throws BizException {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDocument bookDocument = bookRepository.findById(bookInventory.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        bookInventory.setBookId(bookDocument.getId());
        bookInventory.setType(bookRealityModel.getType());
        bookInventory.setCoverImage(bookRealityModel.getCoverImage());
        bookInventory.setQuantity(bookInventory.getQuantity());
        bookInventory.setPrice(bookRealityModel.getPrice());
        bookInventory.setUpdatedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookInventory);
    }

    public void deleteBookReality(String id) throws BizException {
        BookInventory bookInventory= bookInventoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        bookInventory.setDeletedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookInventory);
    }
}
