package box.bookstorebe.service.bookstore;

import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.bookstore.BookStoreMapper;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
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

@AllArgsConstructor
@Service
@Slf4j
public class BookStoreService {
    private final BookStoreRepository bookStoreRepository;
    private final ImageRepository imageRepository;

    public Page<BookStoreDto> getBookStores(String name, Integer page, Integer size) {
        Page<BookStoreDocument> bookStoreDocuments = bookStoreRepository.getBookStores(name, page, size);

        List<BookStoreDto> content = new ArrayList<>();
        for (BookStoreDocument bookStoreDocument : bookStoreDocuments.getContent()) {
            BookStoreDto bookStoreDto = BookStoreMapper.INSTANCE.entityToDto(bookStoreDocument);
            ImageDocument imageDocument = imageRepository.findById(bookStoreDocument.getImageId() != null ? bookStoreDocument.getImageId() : "").orElseGet(() -> null);
            if (imageDocument != null) {
                BookStoreDto.Image image = BookStoreDto.Image.builder().id(imageDocument.getId()).link(imageDocument.getLink()).build();
                bookStoreDto.setImage(image);
            }
            content.add(bookStoreDto);
        }
        return new PageImpl<>(content, bookStoreDocuments.getPageable(), bookStoreDocuments.getTotalElements());
    }

    public BookStoreDto findById(String id) throws BizException {
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        BookStoreDto bookStoreDto = BookStoreMapper.INSTANCE.entityToDto(bookStoreDocument);
        ImageDocument imageDocument = imageRepository.findById(bookStoreDocument.getImageId() != null ? bookStoreDocument.getImageId() : "").orElseGet(() -> null);
        if (imageDocument != null) {
            BookStoreDto.Image image = BookStoreDto.Image.builder().id(imageDocument.getId()).link(imageDocument.getLink()).build();
            bookStoreDto.setImage(image);
        }
        return bookStoreDto;
    }

    public void createNewBookStore(CreateBookStoreModel bookStoreModel) throws BizException {
        BookStoreDocument bookStoreDocument = new BookStoreDocument();
        ImageDocument imageDocument = imageRepository.findById(bookStoreModel.getImageId() != null ? bookStoreModel.getImageId() : "").orElseThrow(() -> new BizException("Invalid image id"));
        bookStoreDocument.setImageId(imageDocument.getId());
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreModel.getPhoneNumber());
//        bookStoreDocument.setDescription(bookStoreModel.getDescription());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }

    public void updateBookStore(String id, UpdateBookStoreModel updateBookStoreModel) throws BizException {
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        ImageDocument imageDocument = imageRepository.findById(updateBookStoreModel.getImageId() != null ? updateBookStoreModel.getImageId() : "").orElseThrow(() -> new BizException("Invalid image id"));
        bookStoreDocument.setImageId(imageDocument.getId());
        bookStoreDocument.setName(updateBookStoreModel.getName());
        bookStoreDocument.setAddress(updateBookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreDocument.getPhoneNumber());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }

    public void deleteBookStore(String id) throws BizException {
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        bookStoreRepository.delete(bookStoreDocument);
    }
}
