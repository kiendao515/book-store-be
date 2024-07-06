package box.bookstorebe.service.book;

import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.dto.book.BookRelatedPersonDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.book.BookRelatedPersonMapper;
import box.bookstorebe.model.book.bookrelatedperson.CreateBookRelatedPersonModel;
import box.bookstorebe.model.book.bookrelatedperson.UpdateBookRelatedPersonModel;
import box.bookstorebe.repository.book.BookRelatedPersonRepository;
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
public class BookRelatedPersonService {
    private final BookRelatedPersonRepository bookRelatedPersonRepository;

    public Page<BookRelatedPersonDto> getBookRelatedPersons(String name, String type, int page, int size) {
        Page<BookRelatedPersonDocument> bookRelatedPersonDocuments = bookRelatedPersonRepository.getBookRelatedPersons(name, type, page, size);

        List<BookRelatedPersonDto> content = new ArrayList<>();
        for (BookRelatedPersonDocument bookRelatedPersonDocument : bookRelatedPersonDocuments.getContent()) {
            content.add(BookRelatedPersonMapper.INSTANCE.entityToDto(bookRelatedPersonDocument));
        }
        return new PageImpl<>(content, bookRelatedPersonDocuments.getPageable(), bookRelatedPersonDocuments.getTotalElements());
    }

    public BookRelatedPersonDto findById(String id) throws BizException {
        BookRelatedPersonDocument bookRelatedPersonDocument = bookRelatedPersonRepository.findById(id).orElseThrow(() -> new BizException("Invalid book related person id"));
        return BookRelatedPersonMapper.INSTANCE.entityToDto(bookRelatedPersonDocument);
    }

    public void createNewBookRelatedPerson(CreateBookRelatedPersonModel relatedPersonModel) {
        BookRelatedPersonDocument bookRelatedPersonDocument = new BookRelatedPersonDocument();
        bookRelatedPersonDocument.setName(relatedPersonModel.getName());
        bookRelatedPersonDocument.setDescriptions(relatedPersonModel.getDescriptions());
        bookRelatedPersonDocument.setType(relatedPersonModel.getType());
        bookRelatedPersonDocument.setCreatedAt(ZonedDateTime.now());
        bookRelatedPersonDocument.setUpdatedAt(ZonedDateTime.now());
        bookRelatedPersonRepository.save(bookRelatedPersonDocument);
    }

    public void updateBookRelatedPerson(String id, UpdateBookRelatedPersonModel relatedPersonModel) throws BizException {
        BookRelatedPersonDocument bookRelatedPersonDocument = bookRelatedPersonRepository.findById(id).orElseThrow(() -> new BizException("Invalid book related user id"));
        bookRelatedPersonDocument.setName(relatedPersonModel.getName());
        bookRelatedPersonDocument.setType(relatedPersonModel.getType());
        bookRelatedPersonDocument.setDescriptions(relatedPersonModel.getDescriptions());
        bookRelatedPersonDocument.setUpdatedAt(ZonedDateTime.now());
        bookRelatedPersonRepository.save(bookRelatedPersonDocument);
    }

    public void deleteBookRelatedPerson(String id) throws BizException {
        bookRelatedPersonRepository.findById(id).orElseThrow(() -> new BizException("Invalid book related person id"));
        bookRelatedPersonRepository.deleteById(id);
    }
}
