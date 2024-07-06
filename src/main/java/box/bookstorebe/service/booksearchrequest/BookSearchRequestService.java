package box.bookstorebe.service.booksearchrequest;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.dto.booksearchrequest.BookSearchRequestDto;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.booksearchrequest.BookSearchRequestMapper;
import box.bookstorebe.mapper.bookstore.BookStoreMapper;
import box.bookstorebe.model.booksearchrequest.booksearchrequest.CreateBookSearchRequestModel;
import box.bookstorebe.model.booksearchrequest.booksearchrequest.UpdateBookSearchRequestModel;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
import box.bookstorebe.repository.booksearchrequest.BookSearchRequestRepository;
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
public class BookSearchRequestService {
    private final BookSearchRequestRepository bookSearchRequestRepository;

    public Page<BookSearchRequestDto> getBookSearchRequests(String userId, String fullName, String phoneNumber, Integer page, Integer size) {
        Page<BookSearchRequestDocument> bookSearchRequestDocuments = bookSearchRequestRepository.getBookSearchRequests(userId, fullName, phoneNumber, page, size);
        List<BookSearchRequestDto> content = new ArrayList<>();
        for (BookSearchRequestDocument bookSearchRequestDocument : bookSearchRequestDocuments.getContent()) {
            content.add(BookSearchRequestMapper.INSTANCE.entityToDto(bookSearchRequestDocument));
        }
        return new PageImpl<>(content, bookSearchRequestDocuments.getPageable(), bookSearchRequestDocuments.getTotalElements());
    }

    public BookSearchRequestDto findById(String id) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = bookSearchRequestRepository.findById(id).orElseThrow(() -> new BizException("Invalid book search request id"));
        return BookSearchRequestMapper.INSTANCE.entityToDto(bookSearchRequestDocument);
    }

    public void createBookSearchRequest(CreateBookSearchRequestModel bookSearchRequestModel) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = new BookSearchRequestDocument();
        bookSearchRequestDocument.setUserId(bookSearchRequestDocument.getUserId());
        bookSearchRequestDocument.setFullName(bookSearchRequestModel.getFullName());
        bookSearchRequestDocument.setEmail(bookSearchRequestModel.getEmail());
        bookSearchRequestDocument.setPhoneNumber(bookSearchRequestModel.getPhoneNumber());
        bookSearchRequestDocument.setBookRequests(bookSearchRequestModel.getBookRequests());
        bookSearchRequestDocument.setCreatedAt(ZonedDateTime.now());
        bookSearchRequestDocument.setUpdatedAt(ZonedDateTime.now());
        bookSearchRequestRepository.save(bookSearchRequestDocument);
    }

    public void updateBookSearchRequest(String id, UpdateBookSearchRequestModel bookSearchRequestModel) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = bookSearchRequestRepository.findById(id).orElseThrow(() -> new BizException("Invalid book search request id"));
        bookSearchRequestDocument.setFullName(bookSearchRequestModel.getFullName());
        bookSearchRequestDocument.setEmail(bookSearchRequestModel.getEmail());
        bookSearchRequestDocument.setPhoneNumber(bookSearchRequestModel.getPhoneNumber());
        bookSearchRequestDocument.setBookRequests(bookSearchRequestModel.getBookRequests());
        bookSearchRequestDocument.setUpdatedAt(ZonedDateTime.now());
        bookSearchRequestRepository.save(bookSearchRequestDocument);
    }

    public void deleteBookSearchRequest(String id) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = bookSearchRequestRepository.findById(id).orElseThrow(() -> new BizException("Invalid book search request id"));
        bookSearchRequestRepository.delete(bookSearchRequestDocument);
    }
}
