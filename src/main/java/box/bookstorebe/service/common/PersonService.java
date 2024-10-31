package box.bookstorebe.service.common;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.common.PersonDocument;
import box.bookstorebe.dto.book.AuthorDto;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.common.PersonDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.common.PersonMapper;
import box.bookstorebe.model.book.bookrelatedperson.CreateBookRelatedPersonModel;
import box.bookstorebe.model.book.bookrelatedperson.UpdateBookRelatedPersonModel;
import box.bookstorebe.repository.common.person.PersonRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.service.book.BookService;
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
public class PersonService {
    private final PersonRepository personRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public Page<PersonDto> getBookRelatedPersons(String name, String type, Integer page, Integer size) {
        Page<PersonDocument> personDocuments = personRepository.getPeople(name, type, page, size);

        List<PersonDto> content = new ArrayList<>();
        for (PersonDocument personDocument : personDocuments.getContent()) {
            content.add(PersonMapper.INSTANCE.entityToDto(personDocument));
        }
        return new PageImpl<>(content, personDocuments.getPageable(), personDocuments.getTotalElements());
    }

    public List<AuthorDto> getAuthorWithLetter(String letter) throws BizException {
        List<AuthorDto> authors = new ArrayList<>();
        List<PersonDocument> list = personRepository.findByNameStartingWithAndType(letter, "AUTHOR");
        for (PersonDocument author : list) {
            authors.add(new AuthorDto(author.getId(), author.getName(), null));
        }
        if (!list.isEmpty()) {
            List<BookDocument> bookDocumentList = bookRepository.findBooksByAuthorId(list.get(0).getId());
            List<BookDto> bookDtoList = new ArrayList<>();
            for (BookDocument b : bookDocumentList) {
                bookDtoList.add(bookService.findById(b.getId()));
            }
            authors.get(0).setBooks(bookDtoList);
        }
        return authors;
    }

    public List<BookDto> getBookOfAuthor(String authorId) throws BizException {
        List<BookDto> list = new ArrayList<>();
        PersonDocument personDocument = personRepository.findById(authorId).
                orElseThrow(() -> new BizException("authorId invalid!"));
        List<BookDocument> bookDocumentList = bookRepository.findBooksByAuthorId(personDocument.getId());
        for (BookDocument b : bookDocumentList) {
            list.add(bookService.findById(b.getId()));
        }
        return list;
    }

    public PersonDto findById(String id) throws BizException {
        PersonDocument personDocument = personRepository.findById(id).orElseThrow(() -> new BizException("Invalid book related person id"));
        return PersonMapper.INSTANCE.entityToDto(personDocument);
    }

    public PersonDocument createNewBookRelatedPerson(CreateBookRelatedPersonModel relatedPersonModel) {
        System.out.println(relatedPersonModel.isNationality());
        PersonDocument personDocument = new PersonDocument();
        personDocument.setName(relatedPersonModel.getName());
        personDocument.setDescriptions(relatedPersonModel.getDescriptions());
        personDocument.setType(relatedPersonModel.getType());
        personDocument.setCreatedAt(ZonedDateTime.now());
        personDocument.setUpdatedAt(ZonedDateTime.now());
        personDocument.setNationality(relatedPersonModel.isNationality());
        return personRepository.save(personDocument);
    }

    public void updateBookRelatedPerson(String id, UpdateBookRelatedPersonModel relatedPersonModel) throws BizException {
        PersonDocument personDocument = personRepository.findById(id).orElseThrow(() -> new BizException("Invalid book related user id"));
        personDocument.setName(relatedPersonModel.getName());
        personDocument.setType(relatedPersonModel.getType());
        personDocument.setDescriptions(relatedPersonModel.getDescriptions());
        personDocument.setUpdatedAt(ZonedDateTime.now());
        personDocument.setNationality(relatedPersonModel.isNationality());
        personRepository.save(personDocument);
    }

    public void deleteBookRelatedPerson(String id) throws BizException {
        personRepository.findById(id).orElseThrow(() -> new BizException("Invalid book related person id"));
        personRepository.deleteById(id);
    }
}
