package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.book.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Service
@Slf4j
public class BookRealityService {
    private final BookRealityRepository bookRealityRepository;
    private final BookService bookService;
    private final BookRepository bookRepository;

    public BookRealityDto findById(String id) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDto bookDetail = bookService.findById(bookRealityDocument.getBookId());
        BookRealityDto bookRealityDto = new BookRealityDto();
        bookRealityDto.setId(id);
        bookRealityDto.setBookDetail(bookDetail);
        bookRealityDto.setType(bookRealityDocument.getType());
        bookRealityDto.setStatus(bookRealityDocument.getStatus());
        bookRealityDto.setPrice(bookRealityDocument.getPrice());
        return bookRealityDto;
    }

    public void createBookReality(CreateBookRealityModel bookRealityModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        BookRealityDocument bookRealityDocument = new BookRealityDocument();
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType());
        bookRealityDocument.setStatus(Const.BookRealityStatus.AVAILABLE.name());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setCreatedAt(ZonedDateTime.now());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookRealityRepository.save(bookRealityDocument);
    }

    public void updateBookReality(String id, UpdateBookRealityModel bookRealityModel) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDocument bookDocument = bookRepository.findById(bookRealityDocument.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityDocument.setType(bookRealityModel.getType());
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setStatus(bookRealityModel.getStatus().name());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookRealityRepository.save(bookRealityDocument);
    }

    public void deleteBookReality(String id) throws BizException {
        bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        bookRealityRepository.deleteById(id);
    }
}
