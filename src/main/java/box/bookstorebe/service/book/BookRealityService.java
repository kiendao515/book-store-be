package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class BookRealityService {
    private final BookRealityRepository bookRealityRepository;
    private final BookRepository bookRepository;
    private final ImageRepository imageRepository;
    private final BookService bookService;

    public BookRealityDto findById(String id) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDto bookDto = bookService.findById(bookRealityDocument.getBookId());
        ImageDocument imageDocument = imageRepository.findById(bookRealityDocument.getCoverImageId()).orElse(null);
        BookRealityDto bookRealityDto = new BookRealityDto();
        bookRealityDto.setId(id);
        bookRealityDto.setPrice(bookRealityDocument.getPrice());
        bookRealityDto.setStatus(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()));
        bookRealityDto.setType(Const.BookRealityType.valueOf(bookRealityDocument.getType()));
        bookRealityDto.setCoverImage(imageDocument);
        bookRealityDto.setBook(bookDto);
        return bookRealityDto;
    }
    public BookRealityDto findEntityById(String id) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDocument bookDocument = bookRepository.findById(bookRealityDocument.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        BookDto bookDto= BookDto.builder()
                .id(bookDocument.getId())
                .name(bookDocument.getName())
                .description(bookDocument.getDescription())
                .numberOfPage(bookDocument.getNumberOfPage())
                .createdAt(bookDocument.getCreatedAt())
                .updatedAt(bookDocument.getUpdatedAt())
                .build();
        ImageDocument imageDocument = imageRepository.findById(bookRealityDocument.getCoverImageId()).orElse(null);
        BookRealityDto bookRealityDto = new BookRealityDto();
        bookRealityDto.setId(id);
        bookRealityDto.setPrice(bookRealityDocument.getPrice());
        bookRealityDto.setStatus(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()));
        bookRealityDto.setType(Const.BookRealityType.valueOf(bookRealityDocument.getType()));
        bookRealityDto.setCoverImage(imageDocument);
        bookRealityDto.setBook(bookDto);
        return bookRealityDto;
    }

    public void createBookReality(CreateBookRealityModel bookRealityModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        BookRealityDocument bookRealityDocument = new BookRealityDocument();
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType().toString());
        bookRealityDocument.setStatus(Const.BookRealityStatus.AVAILABLE.name());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setCoverImageId(bookRealityModel.getCoverImageId());
        bookRealityDocument.setCreatedAt(ZonedDateTime.now());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookRealityRepository.save(bookRealityDocument);
    }

    public void updateBookReality(String id, UpdateBookRealityModel bookRealityModel) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDocument bookDocument = bookRepository.findById(bookRealityDocument.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType().toString());
        bookRealityDocument.setStatus(bookRealityModel.getStatus());
        bookRealityDocument.setCoverImageId(bookRealityModel.getCoverImageId());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookRealityRepository.save(bookRealityDocument);
    }

    public void deleteBookReality(String id) throws BizException {
        bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        bookRealityRepository.deleteById(id);
    }
}
