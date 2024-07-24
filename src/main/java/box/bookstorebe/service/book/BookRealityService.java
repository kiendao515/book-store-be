package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.book.BookCommonDto;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.book.BookRelatedPersonRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class BookRealityService {
    private final BookRealityRepository bookRealityRepository;
    private final BookRepository bookRepository;
    private final BookRelatedPersonRepository bookRelatedPersonRepository;
    private final ImageRepository imageRepository;

    public BookRealityDto findById(String id) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookRealityDto bookRealityDto = new BookRealityDto();
        bookRealityDto.setId(id);
        bookRealityDto.setStatus(Const.BookRealityStatus.valueOf(bookRealityDocument.getStatus()));
        List<BookCommonDto.Description> bookDescriptions = new ArrayList<>();
        bookRealityDocument.getDescriptions().forEach(description -> {
            bookDescriptions.add(new BookCommonDto.Description(Const.BookDescriptionType.valueOf(description.getType()), description.getValue()));
        });
        bookRealityDto.setDescriptions(bookDescriptions);
        List<BookCommonDto.RelatedPerson> relatedPeople = new ArrayList<>();
        bookRealityDocument.getRelatedPeople().forEach(relatedPerson -> {
            BookCommonDto.RelatedPerson bookRelatedPerson = new BookCommonDto.RelatedPerson();
            bookRelatedPersonRepository.findById(relatedPerson.getRelatedPersonId()).ifPresent(bookRelatedPersonDocument -> bookRelatedPerson.setName(bookRelatedPersonDocument.getName()));
            bookRelatedPerson.setType(Const.BookRelatedPersonType.valueOf(relatedPerson.getType()));
            bookRelatedPerson.setId(relatedPerson.getRelatedPersonId());
            relatedPeople.add(bookRelatedPerson);
        });
        bookRealityDto.setRelatedPeople(relatedPeople);
        List<BookCommonDto.RelatedImage> relatedImages = new ArrayList<>();
        bookRealityDocument.getRelatedImages().forEach(relatedImage -> {
            BookCommonDto.RelatedImage bookRelatedImage = new BookCommonDto.RelatedImage();
            bookRelatedImage.setType(Const.BookImageType.valueOf(relatedImage.getType()));
            bookRelatedImage.setId(relatedImage.getImageId());
            imageRepository.findById(relatedImage.getImageId()).ifPresent(imageDocument -> bookRelatedImage.setLink(imageDocument.getLink()));
            relatedImages.add(bookRelatedImage);
        });
        bookRealityDto.setRelatedImages(relatedImages);

        bookRealityDto.setPrice(bookRealityDocument.getPrice());
        return bookRealityDto;
    }

    public void createBookReality(CreateBookRealityModel bookRealityModel) throws BizException {
        BookDocument bookDocument = bookRepository.findById(bookRealityModel.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        BookRealityDocument bookRealityDocument = new BookRealityDocument();
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType().toString());
        bookRealityDocument.setDescriptions(bookRealityModel.getDescriptions());
        bookRealityDocument.setRelatedPeople(bookRealityModel.getRelatedPeople());
        bookRealityDocument.setRelatedImages(bookRealityModel.getRelatedImages());
        bookRealityDocument.setStatus(Const.BookRealityStatus.AVAILABLE.name());
        bookRealityDocument.setPrice(bookRealityModel.getPrice());
        bookRealityDocument.setCreatedAt(ZonedDateTime.now());
        bookRealityDocument.setUpdatedAt(ZonedDateTime.now());
        bookRealityRepository.save(bookRealityDocument);
    }

    public void updateBookReality(String id, UpdateBookRealityModel bookRealityModel) throws BizException {
        BookRealityDocument bookRealityDocument = bookRealityRepository.findById(id).orElseThrow(() -> new BizException("Invalid book reality id"));
        BookDocument bookDocument = bookRepository.findById(bookRealityDocument.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
        bookRealityDocument.setBookId(bookDocument.getId());
        bookRealityDocument.setType(bookRealityModel.getType().toString());
        bookRealityDocument.setDescriptions(bookRealityModel.getDescriptions());
        bookRealityDocument.setRelatedPeople(bookRealityModel.getRelatedPeople());
        bookRealityDocument.setRelatedImages(bookRealityModel.getRelatedImages());
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
