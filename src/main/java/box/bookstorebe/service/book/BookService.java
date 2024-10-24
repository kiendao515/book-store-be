package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.*;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.document.common.CommonEntity;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.document.common.PersonDocument;
import box.bookstorebe.document.common.SystemConfigDocument;
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
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class BookService extends BaseService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final BookStoreRepository bookStoreRepository;
    private final BookInventoryRepository bookRealityRepository;
    private final PersonRepository personRepository;
    private final CommonEntityRepository commonEntityRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final AccountRepository accountRepository;
    private final BookFavoriteRepository bookFavoriteRepository;

    public Page<BookDto> getBooks(
            String name,
            String categoryId,
            String authorId,
            String storeId,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt,
            Integer page,
            Integer size
    ) {
        Page<BookDocument> bookDocuments = bookRepository.getBooks(name, categoryId, authorId, storeId, createdAt, updatedAt, page, size);

//        List<String> resultCategoryIds = new ArrayList<>();
//        List<String> resultCommonEntityIds = new ArrayList<>();
//        List<String> resultPersonIds = new ArrayList<>();
//        List<String> resultImageIds = new ArrayList<>();
//        List<String> resultBookStoreIds = new ArrayList<>();
//        List<String> bookIds = new ArrayList<>();
//
//        for (BookDocument bookDocument : bookDocuments) {
//            resultCategoryIds.addAll(bookDocument.getCategoryIds());
//            resultCommonEntityIds.addAll(bookDocument.getTagIds());
//            resultCommonEntityIds.add(bookDocument.getPublisherId());
//            resultCommonEntityIds.add(bookDocument.getPublishingUnitId());
//            resultPersonIds.add(bookDocument.getAuthorId());
//            resultPersonIds.add(bookDocument.getEditorId());
//            resultPersonIds.add(bookDocument.getTranslatorId());
//            resultPersonIds.add(bookDocument.getCoverDrawerId());
//            resultImageIds.add(bookDocument.getCoverImageId());
//            resultImageIds.add(bookDocument.getDetailImageId());
//            resultImageIds.addAll(bookDocument.getDemoImageIds());
//            resultBookStoreIds.add(bookDocument.getStoreId());
//            bookIds.add(bookDocument.getId());
//        }
//
//        // get running time of this method
//        List<CategoryDocument> categoryDocuments = categoryRepository.findAllById(resultCategoryIds);
//
//        List<PersonDocument> personDocuments = personRepository.findAllById(resultPersonIds);
//        Map<String, PersonDocument> personMap = personDocuments.stream().collect(Collectors.toMap(PersonDocument::getId, Function.identity()));
//
//        List<CommonEntity> commonEntities = commonEntityRepository.findAllById(resultCommonEntityIds);
//        Map<String, CommonEntity> commonEntityMap = commonEntities.stream().collect(Collectors.toMap(CommonEntity::getId, Function.identity()));
//
//        List<BookStoreDocument> bookStoreDocuments = bookStoreRepository.findAllById(resultBookStoreIds);
//        Map<String, BookStoreDocument> bookStoreMap = bookStoreDocuments.stream().collect(Collectors.toMap(BookStoreDocument::getId, Function.identity()));
//
//        List<BookInventory> bookRealityDocuments = bookRealityRepository.findAllByBookIdIn(bookIds);
//        Map<String, List<BookRealityDocument>> bookRealityMap = bookRealityDocuments.stream().collect(Collectors.groupingBy(BookRealityDocument::getBookId));
//        for (BookRealityDocument bookRealityDocument : bookRealityDocuments) {
//            if (bookRealityDocument.getCoverImageId() != null)
//                resultImageIds.add(bookRealityDocument.getCoverImageId());
//        }
//
//        List<ImageDocument> imageDocuments = imageRepository.findAllById(resultImageIds);
//        Map<String, ImageDocument> imageDocumentMap = imageDocuments.stream().collect(Collectors.toMap(ImageDocument::getId, Function.identity()));
//
        List<BookDto> content = new ArrayList<>();
//
//        for (BookDocument bookDocument : bookDocuments) {
//            List<BookRealityDocument> bookRealities = bookRealityMap.getOrDefault(bookDocument.getId(), new ArrayList<>());
//            List<BookRealityDto> bookRealityDtos = new ArrayList<>();
//            for (BookRealityDocument bookRealityDocument : bookRealities) {
//                ImageDocument imageDocument = imageDocumentMap.getOrDefault(bookRealityDocument.getCoverImageId(), null);
//
//                BookRealityDto bookRealityDto = BookRealityDto.builder()
//                        .id(bookRealityDocument.getId())
//                        .price(bookRealityDocument.getPrice())
//                        .status(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()))
//                        .type(Const.BookRealityType.valueOf(bookRealityDocument.getType()))
//                        .coverImage(imageDocument)
//                        .createdAt(bookRealityDocument.getCreatedAt())
//                        .updatedAt(bookRealityDocument.getUpdatedAt())
//                        .build();
//                bookRealityDtos.add(bookRealityDto);
//            }
//
//            BookDto bookDto = BookDto.builder()
//                    .id(bookDocument.getId())
//                    .name(bookDocument.getName())
//                    .description(bookDocument.getDescription())
//                    .numberOfPage(bookDocument.getNumberOfPage())
//                    .publishYear(bookDocument.getPublishYear())
//                    .isbn(bookDocument.getIsbn())
//                    .publishingUnit(commonEntityMap.getOrDefault(bookDocument.getPublishingUnitId(), null))
//                    .publisher(commonEntityMap.getOrDefault(bookDocument.getPublisherId(), null))
//                    .author(personMap.getOrDefault(bookDocument.getAuthorId(), null))
//                    .editor(personMap.getOrDefault(bookDocument.getEditorId(), null))
//                    .translator(personMap.getOrDefault(bookDocument.getTranslatorId(), null))
//                    .coverDrawer(personMap.getOrDefault(bookDocument.getCoverDrawerId(), null))
//                    .coverImage(imageDocumentMap.getOrDefault(bookDocument.getCoverImageId(), null))
//                    .detailImage(imageDocumentMap.getOrDefault(bookDocument.getDetailImageId(), null))
//                    .demoImages(imageDocumentMap.values().stream().filter(imageDocument -> bookDocument.getDemoImageIds().contains(imageDocument.getId())).collect(Collectors.toList()))
//                    .demoUrl(bookDocument.getDemoUrl())
//                    .tags(commonEntities.stream().filter(entity -> bookDocument.getTagIds().contains(entity.getId())).collect(Collectors.toList()))
//                    .categories(categoryDocuments.stream().filter(categoryDocument -> bookDocument.getCategoryIds().contains(categoryDocument.getId())).collect(Collectors.toList()))
//                    .bookRealities(bookRealityDtos)
//                    .bookStore(bookStoreMap.getOrDefault(bookDocument.getStoreId(), null))
//                    .createdAt(bookDocument.getCreatedAt())
//                    .updatedAt(bookDocument.getUpdatedAt())
//                    .build();
//            content.add(bookDto);
//        }

        return new PageImpl<>(content, bookDocuments.getPageable(), bookDocuments.getTotalElements());
    }

    public BookDto findById(String id) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        List<String> imageIds = new ArrayList<>();
        if (bookDocument.getCoverImageId() != null) imageIds.add(bookDocument.getCoverImageId());
        if (bookDocument.getDetailImageId() != null) imageIds.add(bookDocument.getDetailImageId());
        imageIds.addAll(bookDocument.getDemoImageIds());

        CategoryDocument categoryDocument = categoryRepository.findById(bookDocument.getCategoryId()).orElseThrow(()-> new BizException("invalid category"));
        List<BookInventory> bookRealityDocuments = bookRealityRepository.findAllByBookId(bookDocument.getId());

        for (BookInventory bookRealityDocument : bookRealityDocuments) {
            if (bookRealityDocument.getCoverImageId() != null) imageIds.add(bookRealityDocument.getCoverImageId());
        }

        List<ImageDocument> imageDocuments = imageRepository.findAllById(imageIds);
        Map<String, ImageDocument> imageMap = imageDocuments.stream().collect(Collectors.toMap(ImageDocument::getId, Function.identity()));

//        List<BookRealityDto> bookRealityDtos = new ArrayList<>();
//        for (BookRealityDocument bookRealityDocument : bookRealityDocuments) {
//            ImageDocument imageDocument = imageMap.getOrDefault(bookRealityDocument.getCoverImageId(), null);
//            BookRealityDto bookRealityDto = BookRealityDto.builder()
//                    .id(bookRealityDocument.getId())
//                    .price(bookRealityDocument.getPrice())
//                    .status(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()))
//                    .type(Const.BookRealityType.valueOf(bookRealityDocument.getType()))
//                    .coverImage(imageDocument)
//                    .createdAt(bookRealityDocument.getCreatedAt())
//                    .updatedAt(bookRealityDocument.getUpdatedAt())
//                    .build();
//            bookRealityDtos.add(bookRealityDto);
//        }

        return BookDto.builder()
                .id(bookDocument.getId())
                .name(bookDocument.getName())
                .description(bookDocument.getDescription())
                .numberOfPage(bookDocument.getNumberOfPage())
                .category(categoryDocument)
                .publisher(bookDocument.getPublisher())
                .authorName(bookDocument.getAuthorName())
                .publishYear(bookDocument.getPublishYear())
                .isbn(bookDocument.getIsbn())
                .coverImage(imageMap.getOrDefault(bookDocument.getCoverImageId(), null))
                .detailImage(imageMap.getOrDefault(bookDocument.getDetailImageId(), null))
                .demoImages(imageMap.values().stream().filter(imageDocument -> bookDocument.getDemoImageIds().contains(imageDocument.getId())).collect(Collectors.toList()))
                .demoUrl(bookDocument.getDemoUrl())
                .createdAt(bookDocument.getCreatedAt())
                .updatedAt(bookDocument.getUpdatedAt())
                .build();

    }

    public void createNewBook(CreateBookModel bookModel) throws BizException {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setName(bookModel.getName());
        bookDocument.setNumberOfPage(bookModel.getNumberOfPage());
        bookDocument.setDescription(bookModel.getDescription());
        bookDocument.setPublishYear(bookModel.getPublishYear());
        bookDocument.setIsbn(bookModel.getIsbn());
        bookDocument.setCoverImageId(bookModel.getCoverImageId());
        bookDocument.setDetailImageId(bookModel.getDetailImageId());
        bookDocument.setDemoImageIds(bookModel.getDemoImageIds());
        bookDocument.setDemoUrl(bookModel.getDemoUrl());
        bookDocument.setTags(bookModel.getTags());
        bookDocument.setCategoryId(bookModel.getCategoryId());
        bookDocument.setCreatedAt(ZonedDateTime.now());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        bookRepository.save(bookDocument);
    }

    public void updateBook(String id, UpdateBookModel bookModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        bookDocument.setName(bookModel.getName());
        bookDocument.setNumberOfPage(bookModel.getNumberOfPage());
        bookDocument.setDescription(bookModel.getDescription());
        bookDocument.setPublishYear(bookModel.getPublishYear());
        bookDocument.setIsbn(bookModel.getIsbn());
        bookDocument.setCoverImageId(bookModel.getCoverImageId());
        bookDocument.setDetailImageId(bookModel.getDetailImageId());
        bookDocument.setDemoImageIds(bookModel.getDemoImageIds());
        bookDocument.setDemoUrl(bookModel.getDemoUrl());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        bookRepository.save(bookDocument);
    }

    public void updateMultipleBookReality(String id, UpdateMultipleBookRealityModel bookRealityModel) throws BizException {
        if(bookRealityModel.getQuantity() <0) throw new BizException("Số lượng quyển sách phải lớn hơn 0");
        if(bookRealityModel.getPrice().equals(BigDecimal.valueOf(0))) throw new BizException("Giá trị quyển sách phải lớn hơn 0");
        List<BookInventory> bookInventories = bookRealityRepository.findAllByBookIdAndStoreIdAndType(bookRealityModel.getStoreId(), id, bookRealityModel.getType().name());
        for (BookInventory bookReality : bookInventories) {
            bookReality.setPrice(bookRealityModel.getPrice());
            bookReality.setCoverImageId(bookRealityModel.getCoverImageId());
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
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument user = accountRepository.findById(currentUser.getUserId()).orElseThrow(() -> new BizException("Invalid user"));
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
        AccountDocument user = accountRepository.findById(currentUser.getUserId()).orElseThrow(() -> new BizException("Invalid user"));
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
