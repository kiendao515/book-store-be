package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Service
@Slf4j
public class BookInventoryService {
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;
    private final ImageRepository imageRepository;
    private final BookService bookService;

    public BookInventory findById(String id) throws BizException {
        return bookInventoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
    }

    public void createBookInventory(CreateBookRealityModel bookRealityModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        BookInventory bookRealityDocument = new BookInventory();
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setCoverImageId(bookRealityModel.getCoverImageId());
        bookRealityDocument.setQuantity(bookRealityDocument.getQuantity());
        bookRealityDocument.setCreatedAt(ZonedDateTime.now());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookRealityDocument);
    }

    public void updateBookInventory(String id, UpdateBookRealityModel bookRealityModel) throws BizException {
        BookInventory bookRealityDocument = bookInventoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDocument bookDocument = bookRepository.findById(bookRealityDocument.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType());
        bookRealityDocument.setCoverImageId(bookRealityModel.getCoverImageId());
        bookRealityDocument.setQuantity(bookRealityDocument.getQuantity());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookRealityDocument);
    }

    public void deleteBookReality(String id) throws BizException {
        bookInventoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        bookInventoryRepository.deleteById(id);
    }
}
