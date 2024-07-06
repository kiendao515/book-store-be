package box.bookstorebe.service.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.book.CollectionDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.book.CreateBookModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.repository.book.*;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;
    private final BookRelatedPersonRepository bookRelatedPersonDocumentRepository;
    private final ImageRepository imageRepository;
    private final BookStoreRepository bookStoreRepository;
    private final BookRealityRepository bookRealityRepository;

    public Page<BookDto> getBooks(String name, List<String> categoryIds, List<String> collectionIds, List<String> relatedPersonIds, String storeId, Integer page, Integer size) {
        Page<BookDocument> bookDocuments = bookRepository.getBooks(name, categoryIds, collectionIds, relatedPersonIds, storeId, page, size);

        List<String> resultCategoryIds = new ArrayList<>();
        List<String> resultCollectionIds = new ArrayList<>();
        List<String> resultRelatedPersonIds = new ArrayList<>();
        List<String> resultRelatedImageIds = new ArrayList<>();

        for (BookDocument bookDocument : bookDocuments) {
            resultCategoryIds.addAll(bookDocument.getCategoryIds());
            resultCollectionIds.addAll(bookDocument.getCollectionIds());
            resultRelatedPersonIds.addAll(bookDocument.getRelatedPeople().stream().map(BookDocument.RelatedPerson::getRelatedPersonId).toList());
            resultRelatedImageIds.addAll(bookDocument.getRelatedImages().stream().map(BookDocument.RelatedImage::getImageId).toList());
        }

        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(storeId).orElse(new BookStoreDocument());
        List<CategoryDocument> categoryDocuments = categoryRepository.findAllById(resultCategoryIds);
        Map<String, CategoryDocument> categoryMap = categoryDocuments.stream().collect(Collectors.toMap(CategoryDocument::getId, categoryDocument -> categoryDocument));

        List<CollectionDocument> collectionDocuments = collectionRepository.findAllById(resultCollectionIds);
        Map<String, CollectionDocument> collectionMap = collectionDocuments.stream().collect(Collectors.toMap(CollectionDocument::getId, collectionDocument -> collectionDocument));

        List<BookRelatedPersonDocument> relatedPersonDocuments = bookRelatedPersonDocumentRepository.findAllById(resultRelatedPersonIds);
        Map<String, BookRelatedPersonDocument> relatedPersonMap = relatedPersonDocuments.stream().collect(Collectors.toMap(BookRelatedPersonDocument::getId, relatedPersonDocument -> relatedPersonDocument));

        List<ImageDocument> relatedImageDocuments = imageRepository.findAllById(resultRelatedImageIds);
        Map<String, ImageDocument> relatedImageMap = relatedImageDocuments.stream().collect(Collectors.toMap(ImageDocument::getId, relatedImageDocument -> relatedImageDocument));

        List<BookDto> content = new ArrayList<>();

        for (BookDocument bookDocument : bookDocuments) {
            BookDto bookDto = new BookDto();
            bookDto.setId(bookDocument.getId());
            bookDto.setName(bookDocument.getName());
            List<BookDto.Description> descriptions = new ArrayList<>();
            bookDocument.getDescriptions().forEach(description -> {
                if (description.getType().equals("description")) {
                    descriptions.add(new BookDto.Description(description.getType(), description.getContent()));
                }
            });
            bookDto.setDescriptions(descriptions);
            List<BookDto.Category> categories = new ArrayList<>();
            bookDocument.getCategoryIds().forEach(categoryId -> {
                CategoryDocument categoryDocument = categoryMap.getOrDefault(categoryId, new CategoryDocument());
                categories.add(new BookDto.Category(categoryId, categoryDocument.getName()));
            });
            bookDto.setCategories(categories);

            List<BookDto.Collection> collections = new ArrayList<>();
            bookDocument.getCollectionIds().forEach(collectionId -> {
                CollectionDocument collectionDocument = collectionMap.getOrDefault(collectionId, new CollectionDocument());
                collections.add(new BookDto.Collection(collectionId, collectionDocument.getName()));
            });
            bookDto.setCollections(collections);

            List<BookDto.RelatedPerson> relatedPeople = new ArrayList<>();
            bookDocument.getRelatedPeople().forEach(relatedPerson -> {
                BookDto.RelatedPerson relatedPersonDto = new BookDto.RelatedPerson();
                BookRelatedPersonDocument bookRelatedPersonDocument = relatedPersonMap.getOrDefault(relatedPerson.getRelatedPersonId(), new BookRelatedPersonDocument());
                relatedPersonDto.setId(bookRelatedPersonDocument.getId());
                relatedPersonDto.setName(bookRelatedPersonDocument.getName());
                relatedPersonDto.setType(relatedPerson.getType());
                relatedPeople.add(relatedPersonDto);
            });
            bookDto.setRelatedPeople(relatedPeople);

            List<BookDto.RelatedImage> relatedImages = new ArrayList<>();
            bookDocument.getRelatedImages().forEach(relatedImage -> {
                BookDto.RelatedImage relatedImageDto = new BookDto.RelatedImage();
                ImageDocument imageDocument = relatedImageMap.getOrDefault(relatedImage.getImageId(), new ImageDocument());
                relatedImageDto.setId(imageDocument.getId());
                relatedImageDto.setType(relatedImage.getType());
                relatedImageDto.setLink(imageDocument.getLink());
                relatedImages.add(relatedImageDto);
            });
            bookDto.setRelatedImages(relatedImages);

            BookDto.Store store = new BookDto.Store();
            store.setId(bookStoreDocument.getId());
            store.setName(bookStoreDocument.getName());
            bookDto.setStore(store);
            bookDto.setCreatedAt(bookDocument.getCreatedAt());
            bookDto.setUpdatedAt(bookDocument.getUpdatedAt());
            content.add(bookDto);
        }

        return new PageImpl<>(content, bookDocuments.getPageable(), bookDocuments.getTotalElements());
    }

    public BookDto findById(String id) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        List<String> categoryIds = bookDocument.getCategoryIds();
        List<String> collectionIds = bookDocument.getCollectionIds();
        List<String> relatedPersonIds = bookDocument.getRelatedPeople().stream().map(BookDocument.RelatedPerson::getRelatedPersonId).toList();
        List<String> relatedImageIds = bookDocument.getRelatedImages().stream().map(BookDocument.RelatedImage::getImageId).toList();

        List<CategoryDocument> categoryDocuments = categoryRepository.findAllById(categoryIds);
        Map<String, CategoryDocument> categoryMap = categoryDocuments.stream().collect(Collectors.toMap(CategoryDocument::getId, categoryDocument -> categoryDocument));

        List<CollectionDocument> collectionDocuments = collectionRepository.findAllById(collectionIds);
        Map<String, CollectionDocument> collectionMap = collectionDocuments.stream().collect(Collectors.toMap(CollectionDocument::getId, collectionDocument -> collectionDocument));

        List<BookRelatedPersonDocument> bookRelatedPersonDocuments = bookRelatedPersonDocumentRepository.findAllById(relatedPersonIds);
        Map<String, BookRelatedPersonDocument> relatedPersonMap = bookRelatedPersonDocuments.stream().collect(Collectors.toMap(BookRelatedPersonDocument::getId, relatedPersonDocument -> relatedPersonDocument));

        List<ImageDocument> relatedImageDocuments = imageRepository.findAllById(relatedImageIds);
        Map<String, ImageDocument> relatedImageMap = relatedImageDocuments.stream().collect(Collectors.toMap(ImageDocument::getId, relatedImageDocument -> relatedImageDocument));

        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(bookDocument.getStoreId()).orElseGet(BookStoreDocument::new);

        BookDto bookDto = new BookDto();
        bookDto.setId(bookDocument.getId());
        bookDto.setName(bookDocument.getName());
        List<BookDto.Description> descriptions = new ArrayList<>();
        bookDocument.getDescriptions().forEach(description -> {
            descriptions.add(new BookDto.Description(description.getType(), description.getContent()));
        });
        bookDto.setDescriptions(descriptions);

        List<BookDto.Category> categories = new ArrayList<>();
        bookDocument.getCategoryIds().forEach(categoryId -> {
            CategoryDocument categoryDocument = categoryMap.getOrDefault(categoryId, new CategoryDocument());
            categories.add(new BookDto.Category(categoryId, categoryDocument.getName()));
        });
        bookDto.setCategories(categories);

        List<BookDto.Collection> collections = new ArrayList<>();
        bookDocument.getCollectionIds().forEach(collectionId -> {
            CollectionDocument collectionDocument = collectionMap.getOrDefault(collectionId, new CollectionDocument());
            collections.add(new BookDto.Collection(collectionId, collectionDocument.getName()));
        });
        bookDto.setCollections(collections);

        List<BookDto.RelatedPerson> relatedPeople = new ArrayList<>();
        bookDocument.getRelatedPeople().forEach(relatedPerson -> {
            BookDto.RelatedPerson relatedPersonDto = new BookDto.RelatedPerson();
            BookRelatedPersonDocument bookRelatedPersonDocument = relatedPersonMap.getOrDefault(relatedPerson.getRelatedPersonId(), new BookRelatedPersonDocument());
            relatedPersonDto.setId(bookRelatedPersonDocument.getId());
            relatedPersonDto.setName(bookRelatedPersonDocument.getName());
            relatedPersonDto.setType(relatedPerson.getType());
            relatedPeople.add(relatedPersonDto);
        });
        bookDto.setRelatedPeople(relatedPeople);

        List<BookDto.RelatedImage> relatedImages = new ArrayList<>();
        bookDocument.getRelatedImages().forEach(relatedImage -> {
            BookDto.RelatedImage relatedImageDto = new BookDto.RelatedImage();
            ImageDocument imageDocument = relatedImageMap.getOrDefault(relatedImage.getImageId(), new ImageDocument());
            relatedImageDto.setId(imageDocument.getId());
            relatedImageDto.setType(relatedImage.getType());
            relatedImageDto.setLink(imageDocument.getLink());
            relatedImages.add(relatedImageDto);
        });
        bookDto.setRelatedImages(relatedImages);

        BookDto.Store store = new BookDto.Store();
        store.setId(bookStoreDocument.getId());
        store.setName(bookStoreDocument.getName());
        bookDto.setStore(store);
        bookDto.setCreatedAt(bookDocument.getCreatedAt());
        bookDto.setUpdatedAt(bookDocument.getUpdatedAt());
        return bookDto;
    }

    public void createNewBook(CreateBookModel bookModel) throws BizException {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setName(bookModel.getName());
        // Validate data
        List<String> validCategoryIds = categoryRepository.findAllById(bookModel.getCategoryIds()).stream().map(CategoryDocument::getId).toList();
        List<String> validCollectionIds = collectionRepository.findAllById(bookModel.getCollectionIds()).stream().map(CollectionDocument::getId).toList();
        List<BookRelatedPersonDocument> bookRelatedPersonDocuments = bookRelatedPersonDocumentRepository.findAllById(bookModel.getRelatedPeople().stream().map(BookDocument.RelatedPerson::getRelatedPersonId).toList());
        List<ImageDocument> imageDocuments = imageRepository.findAllById(bookModel.getRelatedImages().stream().map(BookDocument.RelatedImage::getImageId).toList());
        List<BookDocument.RelatedPerson> relatedPeople = new ArrayList<>();
        bookRelatedPersonDocuments.forEach(relatedPerson -> {
            relatedPeople.add(new BookDocument.RelatedPerson(relatedPerson.getId(), relatedPerson.getType()));
        });
        List<BookDocument.RelatedImage> relatedImages = new ArrayList<>();
        bookModel.getRelatedImages().forEach(relatedImage -> {
            if (imageDocuments.stream().anyMatch(imageDocument -> imageDocument.getId().equals(relatedImage.getImageId()))) {
                relatedImages.add(new BookDocument.RelatedImage(relatedImage.getImageId(), relatedImage.getType()));
            }
        });

        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(bookModel.getStoreId()).orElseThrow(() -> new BizException("Invalid store id"));

        bookDocument.setDescriptions(bookModel.getDescriptions());
        bookDocument.setCategoryIds(validCategoryIds);
        bookDocument.setCollectionIds(validCollectionIds);
        bookDocument.setRelatedPeople(relatedPeople);
        bookDocument.setRelatedImages(relatedImages);
        bookDocument.setStoreId(bookStoreDocument.getId());
        bookDocument.setCreatedAt(ZonedDateTime.now());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        bookRepository.save(bookDocument);
    }

    public void updateBook(String id, UpdateBookModel bookModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        List<String> validCategoryIds = categoryRepository.findAllById(bookModel.getCategoryIds()).stream().map(CategoryDocument::getId).toList();
        List<String> validCollectionIds = collectionRepository.findAllById(bookModel.getCollectionIds()).stream().map(CollectionDocument::getId).toList();
        List<BookRelatedPersonDocument> bookRelatedPersonDocuments = bookRelatedPersonDocumentRepository.findAllById(bookModel.getRelatedPeople().stream().map(BookDocument.RelatedPerson::getRelatedPersonId).toList());
        List<ImageDocument> imageDocuments = imageRepository.findAllById(bookModel.getRelatedImages().stream().map(BookDocument.RelatedImage::getImageId).toList());
        List<BookDocument.RelatedPerson> relatedPeople = new ArrayList<>();
        bookRelatedPersonDocuments.forEach(relatedPerson -> {
            relatedPeople.add(new BookDocument.RelatedPerson(relatedPerson.getId(), relatedPerson.getType()));
        });
        List<BookDocument.RelatedImage> relatedImages = new ArrayList<>();
        bookModel.getRelatedImages().forEach(relatedImage -> {
            if (imageDocuments.stream().anyMatch(imageDocument -> imageDocument.getId().equals(relatedImage.getImageId()))) {
                relatedImages.add(new BookDocument.RelatedImage(relatedImage.getImageId(), relatedImage.getType()));
            }
        });

        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(bookModel.getStoreId()).orElseThrow(() -> new BizException("Invalid store id"));
        bookDocument.setName(bookModel.getName());
        bookDocument.setDescriptions(bookModel.getDescriptions());
        bookDocument.setCategoryIds(validCategoryIds);
        bookDocument.setCollectionIds(validCollectionIds);
        bookDocument.setRelatedPeople(relatedPeople);
        bookDocument.setRelatedImages(relatedImages);
        bookDocument.setStoreId(bookStoreDocument.getId());
        bookDocument.setUpdatedAt(ZonedDateTime.now());
        bookRepository.save(bookDocument);
    }

    public void deleteBook(String id) throws BizException {
        bookRepository.findById(id).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }
}
