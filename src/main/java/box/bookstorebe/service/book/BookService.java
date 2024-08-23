package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.*;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.document.common.CommonEntity;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.document.common.PersonDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.book.CreateBookModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.repository.book.*;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import box.bookstorebe.repository.common.person.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final BookStoreRepository bookStoreRepository;
    private final BookRealityRepository bookRealityRepository;
    private final PersonRepository personRepository;
    private final CommonEntityRepository commonEntityRepository;

    public Page<BookDto> getBooks(String name, List<String> categoryIds, List<String> collectionIds, List<String> relatedPersonIds, String storeId, Integer page, Integer size) throws BizException {
        Page<BookDocument> bookDocuments = bookRepository.getBooks(name, categoryIds, collectionIds, relatedPersonIds, storeId, page, size);

        List<String> resultCategoryIds = new ArrayList<>();
        List<String> resultCommonEntityIds = new ArrayList<>();
        List<String> resultPersonIds = new ArrayList<>();
        List<String> resultImageIds = new ArrayList<>();
        List<String> resultBookStoreIds = new ArrayList<>();
        List<String> bookIds = new ArrayList<>();

        for (BookDocument bookDocument : bookDocuments) {
            resultCategoryIds.addAll(bookDocument.getCategoryIds());
            resultCommonEntityIds.addAll(bookDocument.getTagIds());
            resultCommonEntityIds.add(bookDocument.getPublisherId());
            resultCommonEntityIds.add(bookDocument.getPublishingUnitId());
            resultPersonIds.add(bookDocument.getAuthorId());
            resultPersonIds.add(bookDocument.getEditorId());
            resultPersonIds.add(bookDocument.getTranslatorId());
            resultPersonIds.add(bookDocument.getCoverDrawerId());
            resultImageIds.add(bookDocument.getCoverImageId());
            resultImageIds.add(bookDocument.getDetailImageId());
            resultImageIds.addAll(bookDocument.getDemoImageIds());
            resultBookStoreIds.add(bookDocument.getStoreId());
            bookIds.add(bookDocument.getId());
        }

        List<CategoryDocument> categoryDocuments = categoryRepository.findAllById(resultCategoryIds);

        List<PersonDocument> personDocuments = personRepository.findAllById(resultPersonIds);
        Map<String, PersonDocument> personMap = personDocuments.stream().collect(Collectors.toMap(PersonDocument::getId, Function.identity()));

        List<ImageDocument> imageDocuments = imageRepository.findAllById(resultImageIds);
        Map<String, ImageDocument> imageDocumentMap = imageDocuments.stream().collect(Collectors.toMap(ImageDocument::getId, Function.identity()));

        List<CommonEntity> commonEntities = commonEntityRepository.findAllById(resultCommonEntityIds);
        Map<String, CommonEntity> commonEntityMap = commonEntities.stream().collect(Collectors.toMap(CommonEntity::getId, Function.identity()));

        List<BookStoreDocument> bookStoreDocuments = bookStoreRepository.findAllById(resultBookStoreIds);
        Map<String, BookStoreDocument> bookStoreMap = bookStoreDocuments.stream().collect(Collectors.toMap(BookStoreDocument::getId, Function.identity()));

        List<BookRealityDocument> bookRealityDocuments = bookRealityRepository.findAllByBookIdIn(bookIds);
        Map<String, List<BookRealityDocument>> bookRealityMap = bookRealityDocuments.stream().collect(Collectors.groupingBy(BookRealityDocument::getBookId));

        List<BookDto> content = new ArrayList<>();

        for (BookDocument bookDocument : bookDocuments) {
            List<BookRealityDocument> bookRealities = bookRealityMap.getOrDefault(bookDocument.getId(), null);
            List<BookRealityDto> bookRealityDtos = new ArrayList<>();
            for (BookRealityDocument bookRealityDocument : bookRealities) {
                ImageDocument imageDocument = imageRepository.findById(bookRealityDocument.getCoverImageId()).orElse(null);
                BookRealityDto bookRealityDto = BookRealityDto.builder()
                        .id(bookRealityDocument.getId())
                        .price(bookRealityDocument.getPrice())
                        .status(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()))
                        .type(Const.BookRealityType.valueOf(bookRealityDocument.getType()))
                        .coverImage(imageDocument)
                        .createdAt(bookRealityDocument.getCreatedAt())
                        .updatedAt(bookRealityDocument.getUpdatedAt())
                        .build();
                bookRealityDtos.add(bookRealityDto);
            }

            BookDto bookDto = BookDto.builder()
                    .id(bookDocument.getId())
                    .name(bookDocument.getName())
                    .description(bookDocument.getDescription())
                    .numberOfPage(bookDocument.getNumberOfPage())
                    .publishYear(bookDocument.getPublishYear())
                    .isbn(bookDocument.getIsbn())
                    .publishingUnit(commonEntityMap.getOrDefault(bookDocument.getPublishingUnitId(), null))
                    .publisher(commonEntityMap.getOrDefault(bookDocument.getPublisherId(), null))
                    .author(personMap.getOrDefault(bookDocument.getAuthorId(), null))
                    .editor(personMap.getOrDefault(bookDocument.getEditorId(), null))
                    .translator(personMap.getOrDefault(bookDocument.getTranslatorId(), null))
                    .coverDrawer(personMap.getOrDefault(bookDocument.getCoverDrawerId(), null))
                    .coverImage(imageDocumentMap.getOrDefault(bookDocument.getCoverImageId(), null))
                    .detailImage(imageDocumentMap.getOrDefault(bookDocument.getDetailImageId(), null))
                    .demoImages(imageDocumentMap.values().stream().filter(imageDocument -> bookDocument.getDemoImageIds().contains(imageDocument.getId())).collect(Collectors.toList()))
                    .tags(commonEntities.stream().filter(entity -> bookDocument.getTagIds().contains(entity.getId())).collect(Collectors.toList()))
                    .categories(categoryDocuments.stream().filter(categoryDocument -> bookDocument.getCategoryIds().contains(categoryDocument.getId())).collect(Collectors.toList()))
                    .bookRealities(bookRealityDtos)
                    .bookStore(bookStoreMap.getOrDefault(bookDocument.getStoreId(), null))
                    .createdAt(bookDocument.getCreatedAt())
                    .updatedAt(bookDocument.getUpdatedAt())
                    .build();
            content.add(bookDto);
        }

        return new PageImpl<>(content, bookDocuments.getPageable(), bookDocuments.getTotalElements());
    }

    public BookDto findById(String id) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(bookDocument.getStoreId()).orElseThrow(() -> new BizException("Invalid store id"));
        List<String> personIds = List.of(bookDocument.getAuthorId(), bookDocument.getEditorId(), bookDocument.getTranslatorId(), bookDocument.getCoverDrawerId());
        List<PersonDocument> personDocuments = personRepository.findAllById(personIds);
        Map<String, PersonDocument> personMap = personDocuments.stream().collect(Collectors.toMap(PersonDocument::getId, Function.identity()));

        List<String> imageIds = new ArrayList<>(List.of(bookDocument.getCoverImageId(), bookDocument.getDetailImageId()));
        imageIds.addAll(bookDocument.getDemoImageIds());
        List<ImageDocument> imageDocuments = imageRepository.findAllById(imageIds);
        Map<String, ImageDocument> imageMap = imageDocuments.stream().collect(Collectors.toMap(ImageDocument::getId, Function.identity()));

        List<String> commonEntityIds = new ArrayList<>(List.of(bookDocument.getPublisherId(), bookDocument.getPublishingUnitId()));
        commonEntityIds.addAll(bookDocument.getTagIds());
        List<CommonEntity> commonEntities = commonEntityRepository.findAllById(commonEntityIds);
        Map<String, CommonEntity> commonEntityMap = commonEntities.stream().collect(Collectors.toMap(CommonEntity::getId, Function.identity()));

        List<CategoryDocument> categoryDocuments = categoryRepository.findAllById(bookDocument.getCategoryIds());
        List<BookRealityDocument> bookRealityDocuments = bookRealityRepository.findAllByBookId(bookDocument.getId());

        List<BookRealityDto> bookRealityDtos = new ArrayList<>();
        for (BookRealityDocument bookRealityDocument : bookRealityDocuments) {
            ImageDocument imageDocument = imageRepository.findById(bookRealityDocument.getCoverImageId()).orElse(null);
            BookRealityDto bookRealityDto = BookRealityDto.builder()
                    .id(bookRealityDocument.getId())
                    .price(bookRealityDocument.getPrice())
                    .status(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()))
                    .type(Const.BookRealityType.valueOf(bookRealityDocument.getType()))
                    .coverImage(imageDocument)
                    .createdAt(bookRealityDocument.getCreatedAt())
                    .updatedAt(bookRealityDocument.getUpdatedAt())
                    .build();
            bookRealityDtos.add(bookRealityDto);
        }

        return BookDto.builder()
                .id(bookDocument.getId())
                .name(bookDocument.getName())
                .description(bookDocument.getDescription())
                .numberOfPage(bookDocument.getNumberOfPage())
                .publishYear(bookDocument.getPublishYear())
                .isbn(bookDocument.getIsbn())
                .publishingUnit(commonEntityMap.getOrDefault(bookDocument.getPublishingUnitId(), null))
                .publisher(commonEntityMap.getOrDefault(bookDocument.getPublisherId(), null))
                .author(personMap.getOrDefault(bookDocument.getAuthorId(), null))
                .editor(personMap.getOrDefault(bookDocument.getEditorId(), null))
                .translator(personMap.getOrDefault(bookDocument.getTranslatorId(), null))
                .coverDrawer(personMap.getOrDefault(bookDocument.getCoverDrawerId(), null))
                .coverImage(imageMap.getOrDefault(bookDocument.getCoverImageId(), null))
                .detailImage(imageMap.getOrDefault(bookDocument.getDetailImageId(), null))
                .demoImages(imageMap.values().stream().filter(imageDocument -> bookDocument.getDemoImageIds().contains(imageDocument.getId())).collect(Collectors.toList()))
                .tags(commonEntities.stream().filter(entity -> bookDocument.getTagIds().contains(entity.getId())).collect(Collectors.toList()))
                .categories(categoryDocuments)
                .bookRealities(bookRealityDtos)
                .bookStore(bookStoreDocument)
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
        bookDocument.setPublisherId(bookModel.getPublisherId());
        bookDocument.setPublishingUnitId(bookModel.getPublishingUnitId());
        bookDocument.setAuthorId(bookModel.getAuthorId());
        bookDocument.setEditorId(bookModel.getEditorId());
        bookDocument.setTranslatorId(bookModel.getTranslatorId());
        bookDocument.setCoverDrawerId(bookModel.getCoverDrawerId());
        bookDocument.setCoverImageId(bookModel.getCoverImageId());
        bookDocument.setDetailImageId(bookModel.getDetailImageId());
        bookDocument.setDemoImageIds(bookModel.getDemoImageIds());
        bookDocument.setTagIds(bookModel.getTagIds());
        bookDocument.setCategoryIds(bookModel.getCategoryIds());
        bookDocument.setStoreId(bookModel.getStoreId());
        bookDocument.setCreatedAt(ZonedDateTime.now());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        BookDocument newBook = bookRepository.save(bookDocument);

        List<BookRealityDocument> bookRealities = new ArrayList<>();
        for (CreateBookModel.BookReality bookReality : bookModel.getBookRealities()) {
            for (int i = 0; i < bookReality.getQuantity(); i++) {
                BookRealityDocument bookRealityDocument = new BookRealityDocument();
                bookRealityDocument.setBookId(newBook.getId());
                bookRealityDocument.setPrice(bookReality.getPrice());
                bookRealityDocument.setType(bookReality.getType().toString());
                bookRealityDocument.setStatus(Const.BookRealityStatus.AVAILABLE.name());
                bookRealityDocument.setCoverImageId(bookReality.getCoverImageId());
                bookRealityDocument.setCreatedAt(ZonedDateTime.now());
                bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
                bookRealities.add(bookRealityDocument);
            }
        }

        bookRealityRepository.saveAll(bookRealities);
    }

    public void updateBook(String id, UpdateBookModel bookModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        bookDocument.setName(bookModel.getName());
        bookDocument.setNumberOfPage(bookModel.getNumberOfPage());
        bookDocument.setDescription(bookModel.getDescription());
        bookDocument.setPublishYear(bookModel.getPublishYear());
        bookDocument.setIsbn(bookModel.getIsbn());
        bookDocument.setPublisherId(bookModel.getPublisherId());
        bookDocument.setAuthorId(bookModel.getAuthorId());
        bookDocument.setEditorId(bookModel.getEditorId());
        bookDocument.setTranslatorId(bookModel.getTranslatorId());
        bookDocument.setCoverImageId(bookModel.getCoverImageId());
        bookDocument.setDetailImageId(bookModel.getDetailImageId());
        bookDocument.setDemoImageIds(bookModel.getDemoImageIds());
        bookDocument.setTagIds(bookModel.getTagIds());
        bookDocument.setCategoryIds(bookModel.getCategoryIds());
        bookDocument.setStoreId(bookModel.getStoreId());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        bookRepository.save(bookDocument);
    }

    public void deleteBook(String id) throws BizException {
        bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }
}
