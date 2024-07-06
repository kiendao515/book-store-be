package box.bookstorebe.service.bookstore;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.book.CategoryMapper;
import box.bookstorebe.mapper.bookstore.BookStoreMapper;
import box.bookstorebe.model.book.category.CreateCategoryModel;
import box.bookstorebe.model.book.category.UpdateCategoryModel;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
import box.bookstorebe.repository.book.CategoryRepository;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
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

    public Page<BookStoreDto> getBookStores(String name, Integer page, Integer size) {
        Page<BookStoreDocument> bookStoreDocuments = bookStoreRepository.getBookStores(name, page, size);

        List<BookStoreDto> content = new ArrayList<>();
        for (BookStoreDocument bookStoreDocument : bookStoreDocuments.getContent()) {
            content.add(BookStoreMapper.INSTANCE.entityToDto(bookStoreDocument));
        }
        return new PageImpl<>(content, bookStoreDocuments.getPageable(), bookStoreDocuments.getTotalElements());
    }

    public BookStoreDto findById(String id) throws BizException {
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        return BookStoreMapper.INSTANCE.entityToDto(bookStoreDocument);
    }

    public void createNewBookStore(CreateBookStoreModel bookStoreModel) {
        BookStoreDocument bookStoreDocument = new BookStoreDocument();
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setOtherInformation(bookStoreModel.getOtherInformation());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }

    public void updateBookStore(String id, UpdateBookStoreModel updateBookStoreModel) throws BizException {
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        bookStoreDocument.setName(updateBookStoreModel.getName());
        bookStoreDocument.setAddress(updateBookStoreModel.getAddress());
        bookStoreDocument.setOtherInformation(updateBookStoreModel.getOtherInformation());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }

    public void deleteBookStore(String id) throws BizException {
        BookStoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        bookStoreRepository.delete(bookStoreDocument);
    }
}
