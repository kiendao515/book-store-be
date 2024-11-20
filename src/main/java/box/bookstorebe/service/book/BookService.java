package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.*;
import box.bookstorebe.document.common.SystemConfigDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookFavoriteDto;
import box.bookstorebe.dto.book.BookSettingDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.book.CreateBookModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.model.book.book.UpdateMultipleBookRealityModel;
import box.bookstorebe.model.book.common.BookSettingModel;
import box.bookstorebe.repository.book.*;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import box.bookstorebe.repository.common.person.PersonRepository;
import box.bookstorebe.repository.common.systemconfig.SystemConfigRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class BookService extends BaseService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookInventoryRepository bookRealityRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final AccountRepository accountRepository;
    private final BookFavoriteRepository bookFavoriteRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public Page<BookDto> getBooks(
            String name,
            String categoryId,
            String storeId,
            String createdAt,
            String updatedAt,
            Integer page,
            Integer size
    ) {
        ZonedDateTime created = null;
        ZonedDateTime updated = null;
        if (createdAt != null && updatedAt!=null && !createdAt.isBlank() && !updatedAt.isBlank()) {
            created = ZonedDateTime.parse(createdAt);
            updated = ZonedDateTime.parse(updatedAt);
        }

        Page<BookDocument> bookDocuments = bookRepository.getBooks(name, categoryId, storeId, created, updated, page, size);

        List<String> resultCategoryIds = new ArrayList<>();
        List<String> bookIds = new ArrayList<>();
        for (BookDocument bookDocument : bookDocuments) {
            resultCategoryIds.add(bookDocument.getCategoryId());
            bookIds.add(bookDocument.getId());
        }
        List<CategoryDocument> categoryDocuments = categoryRepository.findAllById(resultCategoryIds);
        Map<String, CategoryDocument> categoryDocumentMap = categoryDocuments.stream().collect(Collectors.toMap(CategoryDocument::getId, Function.identity()));
        List<BookInventory> bookRealityDocuments = bookRealityRepository.findAllByBookIdIn(bookIds);
        Map<String, List<BookInventory>> bookRealityMap = bookRealityDocuments.stream().collect(Collectors.groupingBy(BookInventory::getBookId));

        // Lấy thông tin số lượng sách đã bán từ order_item và orders
        List<OrderItemDocument> orderItems = orderItemRepository.findAllByBookInventoryIdIn(bookRealityDocuments.stream()
                .map(BookInventory::getId)
                .collect(Collectors.toList()));

        // Lọc các đơn hàng có trạng thái "done"
        List<String> doneOrderIds = orderRepository.findAllByStatus(Const.OrderStatus.DONE).stream()
                .map(OrderDocument::getId)
                .toList();

        Map<String, String> inventoryToBookMap = bookRealityDocuments.stream()
                .collect(Collectors.toMap(BookInventory::getId, BookInventory::getBookId));

        Map<String, Integer> bookSellMap = orderItems.stream()
                .filter(orderItem -> doneOrderIds.contains(orderItem.getOrderId())) // Chỉ lấy các đơn hàng "done"
                .collect(Collectors.groupingBy(
                        orderItem -> inventoryToBookMap.get(orderItem.getBookInventoryId()), // Map book_inventory_id -> book_id
                        Collectors.summingInt(OrderItemDocument::getQuantity) // Tổng số lượng đã bán
                ));

        List<BookDto> content = new ArrayList<>();
        for (BookDocument bookDocument : bookDocuments) {
            List<BookInventory> bookRealities = bookRealityMap.getOrDefault(bookDocument.getId(), new ArrayList<>());
            Integer totalBook = bookRealities.stream()
                    .mapToInt(BookInventory::getQuantity)
                    .sum();

            Integer bookSell = bookSellMap.getOrDefault(bookDocument.getId(), 0);


            BookDto bookDto = BookDto.builder()
                    .id(bookDocument.getId())
                    .name(bookDocument.getName())
                    .description(bookDocument.getDescription())
                    .numberOfPage(bookDocument.getNumberOfPage())
                    .publishYear(bookDocument.getPublishYear())
                    .isbn(bookDocument.getIsbn())
                    .publisher(bookDocument.getPublisher())
                    .authorName(bookDocument.getAuthorName())
                    .coverImage(bookDocument.getCoverImage())
                    .backImage(bookDocument.getBackImage())
                    .contentImage(bookDocument.getDemoImage())
                    .demoUrl(bookDocument.getDemoUrl())
                    .tags(bookDocument.getTags())
                    .category(categoryDocumentMap.getOrDefault(bookDocument.getCategoryId(), null))
                    .numberOfBooks(totalBook)
                    .createdAt(bookDocument.getCreatedAt())
                    .updatedAt(bookDocument.getUpdatedAt())
                    .soldQuantity(bookSell)
                    .bookInventories(bookRealities)
                    .build();
            content.add(bookDto);
        }

        return new PageImpl<>(content, bookDocuments.getPageable(), bookDocuments.getTotalElements());
    }

    public BookDto findById(String id) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id)
                .orElseThrow(() -> new BizException("Invalid book id"));
        CategoryDocument categoryDocument = categoryRepository.findById(bookDocument.getCategoryId())
                .orElseThrow(() -> new BizException("Invalid category"));

        List<BookInventory> bookRealityDocuments = bookRealityRepository.findAllByBookId(bookDocument.getId());

        List<OrderItemDocument> orderItems = orderItemRepository.findAllByBookInventoryIdIn(
                bookRealityDocuments.stream().map(BookInventory::getId).collect(Collectors.toList())
        );

        List<String> doneOrderIds = orderRepository.findAllByStatus(Const.OrderStatus.DONE).stream()
                .map(OrderDocument::getId)
                .toList();

        Map<String, String> inventoryToBookMap = bookRealityDocuments.stream()
                .collect(Collectors.toMap(BookInventory::getId, BookInventory::getBookId));

        Map<String, Integer> bookSellMap = orderItems.stream()
                .filter(orderItem -> doneOrderIds.contains(orderItem.getOrderId()))
                .collect(Collectors.groupingBy(
                        orderItem -> inventoryToBookMap.get(orderItem.getBookInventoryId()),
                        Collectors.summingInt(OrderItemDocument::getQuantity)
                ));

        Integer totalBook = bookRealityDocuments.stream()
                .mapToInt(BookInventory::getQuantity)
                .sum();
        Integer bookSell = bookSellMap.getOrDefault(bookDocument.getId(), 0);

        // Xây dựng BookDto
        return BookDto.builder()
                .id(bookDocument.getId())
                .name(bookDocument.getName())
                .description(bookDocument.getDescription())
                .numberOfPage(bookDocument.getNumberOfPage())
                .publishYear(bookDocument.getPublishYear())
                .isbn(bookDocument.getIsbn())
                .publisher(bookDocument.getPublisher())
                .authorName(bookDocument.getAuthorName())
                .coverImage(bookDocument.getCoverImage())
                .backImage(bookDocument.getBackImage())
                .contentImage(bookDocument.getDemoImage())
                .demoUrl(bookDocument.getDemoUrl())
                .tags(bookDocument.getTags())
                .category(categoryDocument)
                .numberOfBooks(totalBook)
                .soldQuantity(bookSell)
                .bookInventories(bookRealityDocuments)
                .createdAt(bookDocument.getCreatedAt())
                .updatedAt(bookDocument.getUpdatedAt())
                .build();

    }

    public BookDocument createNewBook(CreateBookModel bookModel) throws BizException {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setName(bookModel.getName());
        bookDocument.setNumberOfPage(bookModel.getNumberOfPage());
        bookDocument.setDescription(bookModel.getDescription());
        bookDocument.setPublisher(bookModel.getPublisher());
        bookDocument.setAuthorName(bookModel.getAuthorName());
        bookDocument.setPublishYear(bookModel.getPublishYear());
        bookDocument.setIsbn(bookModel.getIsbn());
        bookDocument.setCoverImage(bookModel.getCoverImage());
        bookDocument.setBackImage(bookModel.getBackImage());
        bookDocument.setDemoImage(bookModel.getContentImage());
        bookDocument.setDemoUrl(bookModel.getDemoUrl());
//        if (!bookModel.getTags().isBlank()) {
//            String[] arr = bookModel.getTags().split(";");
//            bookDocument.setTags(Arrays.stream(arr).toList());
//        }
        bookDocument.setTags(bookModel.getTags());
        bookDocument.setCategoryId(bookModel.getCategoryId());
        bookDocument.setCreatedAt(ZonedDateTime.now());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        return bookRepository.save(bookDocument);
    }

    public void updateBook(String id, UpdateBookModel bookModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        bookDocument.setName(bookModel.getName());
        bookDocument.setNumberOfPage(bookModel.getNumberOfPage());
        bookDocument.setDescription(bookModel.getDescription());
        bookDocument.setPublishYear(bookModel.getPublishYear());
        bookDocument.setIsbn(bookModel.getIsbn());
        bookDocument.setPublisher(bookModel.getPublisher());
        bookDocument.setAuthorName(bookModel.getAuthorName());
        bookDocument.setCoverImage(bookModel.getCoverImage());
        bookDocument.setBackImage(bookModel.getBackImage());
        bookDocument.setDemoImage(bookModel.getContentImage());
        bookDocument.setDemoUrl(bookModel.getDemoUrl());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        bookDocument.setCategoryId(bookModel.getCategoryId());
//        if (!bookModel.getTags().isBlank()) {
//            String[] arr = bookModel.getTags().split(",");
//            bookDocument.setTags(Arrays.stream(arr).toList());
//        }
        bookDocument.setTags(bookModel.getTags());
        bookRepository.save(bookDocument);
    }

    public void updateMultipleBookReality(String id, UpdateMultipleBookRealityModel bookRealityModel) throws BizException {
        if (bookRealityModel.getQuantity() < 0) throw new BizException("Số lượng quyển sách phải lớn hơn 0");
        if (bookRealityModel.getPrice().equals(BigDecimal.valueOf(0)))
            throw new BizException("Giá trị quyển sách phải lớn hơn 0");
        List<BookInventory> bookInventories = bookRealityRepository.findAllByBookIdAndStoreIdAndType(bookRealityModel.getStoreId(), id, bookRealityModel.getType().name());
        for (BookInventory bookReality : bookInventories) {
            bookReality.setPrice(bookRealityModel.getPrice());
            bookReality.setCoverImage(bookRealityModel.getCoverImage());
            bookReality.setUpdatedAt(ZonedDateTime.now());
            bookReality.setQuantity(bookReality.getQuantity());
            bookReality.setStoreId(bookRealityModel.getStoreId());
            bookRealityRepository.save(bookReality);
        }
    }

    public void deleteBook(String id) throws BizException {
        bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    public BookSettingDto getBookSetting() {
        BookSettingDto bookSettingDto = new BookSettingDto();
        SystemConfigDocument authorNationality = systemConfigRepository.findByKey(Const.SystemConfig.AUTHOR_NATIONALITY);
        bookSettingDto.setAuthorNationality(authorNationality != null ? Integer.parseInt(authorNationality.getValue()) : 1);
        SystemConfigDocument bookCategory = systemConfigRepository.findByKey(Const.SystemConfig.CREATE_BOOK_CATEGORY);
        bookSettingDto.setCategoryId(bookCategory != null ? bookCategory.getValue() : "");
        SystemConfigDocument bookStore = systemConfigRepository.findByKey(Const.SystemConfig.CREATE_BOOK_STORE);
        bookSettingDto.setBookStoreId(bookStore != null ? bookStore.getValue() : "");
        return bookSettingDto;
    }

    public BookFavoriteDto getBookFavorite() throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null || currentUser.getAccountId() == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid user"));
        List<BookFavoriteDocument> bookFavorites = bookFavoriteRepository.findAllByUserId(user.getId());
        List<String> bookIds = bookFavorites.stream().map(BookFavoriteDocument::getBookId).collect(Collectors.toList());
        BookFavoriteDto bookFavoriteDto = new BookFavoriteDto();
        bookFavoriteDto.setBookIds(bookIds);
        bookFavoriteDto.setUserId(user.getId());
        return bookFavoriteDto;
    }

    public void updateBookFavorite(String bookId) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid user"));
        BookFavoriteDocument bookFavoriteDocument = bookFavoriteRepository.findByUserIdAndBookId(user.getId(), bookId);
        if (bookFavoriteDocument == null) {
            bookRepository.findById(bookId).orElseThrow(() -> new BizException("Invalid book id"));
            BookFavoriteDocument newBookFavoriteDocument = new BookFavoriteDocument();
            newBookFavoriteDocument.setBookId(bookId);
            newBookFavoriteDocument.setUserId(user.getId());
            bookFavoriteRepository.save(newBookFavoriteDocument);
        } else {
            bookFavoriteRepository.delete(bookFavoriteDocument);
        }
    }

    public void createBookSetting(BookSettingModel model) {
        SystemConfigDocument authorNationality = systemConfigRepository.findByKey(Const.SystemConfig.AUTHOR_NATIONALITY);
        authorNationality.setValue(String.valueOf(model.getAuthorNationality()));
        systemConfigRepository.save(authorNationality);

        SystemConfigDocument bookCategory = systemConfigRepository.findByKey(Const.SystemConfig.CREATE_BOOK_CATEGORY);
        bookCategory.setValue(model.getCategoryId());
        systemConfigRepository.save(bookCategory);

        SystemConfigDocument bookStore = systemConfigRepository.findByKey(Const.SystemConfig.CREATE_BOOK_STORE);
        bookStore.setValue(model.getBookStoreId());
        systemConfigRepository.save(bookStore);

    }
}
