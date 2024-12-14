package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.dto.book.BookInventoryDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookAndInventory;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import box.bookstorebe.repository.user.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class BookInventoryService {
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;
    private final BookStoreRepository storeRepository;
    private final ImageRepository imageRepository;
    private final BookService bookService;
    private final AccountRepository accountRepository;

    public List<BookInventory> getDetailBookInventory(String bookId, String storeId) throws BizException {
        if (bookId.isBlank() || storeId.isBlank()) throw new BizException("invalid params");
        return bookInventoryRepository.findAllByBookIdAndStoreId(bookId, storeId);
    }

    public void createBookInventory(CreateBookRealityModel bookRealityModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        StoreDocument storeDocument = storeRepository.findById(bookRealityModel.getStoreId()).orElseThrow(() -> new BizException("invalid store id"));
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

    public void updateBookInventory(UpdateBookRealityModel bookRealityModel) throws BizException {
        // kiểm tra xem có inventory id k, nếu có thì là update còn k có thì là thêm mới
        Optional<BookInventory> bookInventoryOptional = bookInventoryRepository.findById(bookRealityModel.getId());
        BookInventory bookInventory;
        if (bookRealityModel.getId() == null || !bookInventoryOptional.isPresent()) {
            bookInventory = new BookInventory();
            bookInventory.setCreatedAt(ZonedDateTime.now());
        } else {
            bookInventory = bookInventoryOptional.get();
        }
        Optional<AccountDocument> acc = accountRepository.findByEmail(Const.ADMIN_EMAIL);
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        StoreDocument store = storeRepository.findById(bookRealityModel.getStoreId()).orElseThrow(() -> new BizException("invalid store id"));
        if (acc.isEmpty()) throw new BizException("có lỗi xảy ra");
        StoreDocument storeDocument = storeRepository.findByAccountId(acc.get().getId());
        BookInventory bookInventory1 = bookInventoryRepository.findByBookIdAndStoreIdAndType(bookDocument.getId(), storeDocument.getId(), bookRealityModel.getType());
        if (bookInventory1 != null) {
            if (!bookInventory1.getId().equals(bookInventory.getId())) {
                bookInventory.setRelatedBookId(bookInventory1.getId());
            }
        }
        bookInventory.setBookId(bookDocument.getId());
        bookInventory.setType(bookRealityModel.getType());
        bookInventory.setCoverImage(bookRealityModel.getCoverImage());
        bookInventory.setQuantity(bookRealityModel.getQuantity());
        bookInventory.setPrice(bookRealityModel.getPrice());
        bookInventory.setStoreId(store.getId());
        bookInventory.setLocation(bookRealityModel.getLocation());
        bookInventory.setUpdatedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookInventory);
    }

    public void createBookAndInventory(CreateBookAndInventory createBookAndInventory) throws BizException {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setName(createBookAndInventory.getName());
        bookDocument.setCollectionId(createBookAndInventory.getCollectionId());
        bookDocument.setNumberOfPage(createBookAndInventory.getNumberOfPage());
        bookDocument.setDescription(createBookAndInventory.getDescription());
        bookDocument.setPublisher(createBookAndInventory.getPublisher());
        bookDocument.setAuthorName(createBookAndInventory.getAuthorName());
        bookDocument.setPublishYear(createBookAndInventory.getPublishYear());
        bookDocument.setIsbn(createBookAndInventory.getIsbn());
        bookDocument.setCoverImage(createBookAndInventory.getCoverImage());
        bookDocument.setBackImage(createBookAndInventory.getBackImage());
        bookDocument.setDemoImage(createBookAndInventory.getContentImage());
        bookDocument.setDemoUrl(createBookAndInventory.getDemoUrl());
        if (!createBookAndInventory.getTags().isBlank()) {
            String[] arr = createBookAndInventory.getTags().split("[,;]");
            bookDocument.setTags(Arrays.stream(arr).map(String::trim).toList());
        }

        bookDocument.setCategoryId(createBookAndInventory.getCategoryId());
        bookDocument.setCreatedAt(ZonedDateTime.now());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        BookDocument bookDocument1 = bookRepository.save(bookDocument);
        StoreDocument storeDocument = storeRepository.findById(createBookAndInventory.getStoreId()).orElseThrow(() -> new BizException("invalid store id"));
        for (CreateBookAndInventory.BookInventory bookInventory : createBookAndInventory.getBookInventory()) {
            BookInventory newBookInventory = new BookInventory();
            newBookInventory.setBookId(bookDocument1.getId());
            newBookInventory.setType(bookInventory.getType());
            newBookInventory.setPrice(bookInventory.getPrice());
            newBookInventory.setCoverImage(bookInventory.getCoverImage());
            newBookInventory.setQuantity(bookInventory.getQuantity());
            newBookInventory.setLocation(bookInventory.getLocation());
            newBookInventory.setStoreId(storeDocument.getId());
            newBookInventory.setCreatedAt(ZonedDateTime.now());
            newBookInventory.setUpdatedAt(ZonedDateTime.now());
            bookInventoryRepository.save(newBookInventory);
        }
    }

    public void deleteBookReality(String id) throws BizException {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        bookInventory.setDeletedAt(ZonedDateTime.now());
        bookInventoryRepository.save(bookInventory);
    }
}
