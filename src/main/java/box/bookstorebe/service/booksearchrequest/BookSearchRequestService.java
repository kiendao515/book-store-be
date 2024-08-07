package box.bookstorebe.service.booksearchrequest;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.booksearchrequest.BookRequestDocument;
import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import box.bookstorebe.dto.booksearchrequest.BookSearchRequestDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.booksearchrequest.booksearchrequest.CreateBookSearchRequestModel;
import box.bookstorebe.model.booksearchrequest.booksearchrequest.UpdateBookSearchRequestModel;
import box.bookstorebe.repository.booksearchrequest.BookRequestRepository;
import box.bookstorebe.repository.booksearchrequest.BookSearchRequestRepository;
import box.bookstorebe.service.BaseService;
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
public class BookSearchRequestService extends BaseService {
    private final BookSearchRequestRepository bookSearchRequestRepository;
    private final BookRequestRepository bookRequestRepository;

    public Page<BookSearchRequestDto> getBookSearchRequests(String userId, String fullName, String phoneNumber, Integer page, Integer size) {
        Page<BookSearchRequestDocument> bookSearchRequestDocuments = bookSearchRequestRepository.getBookSearchRequests(userId, fullName, phoneNumber, page, size);
        List<BookSearchRequestDto> content = new ArrayList<>();
        List<String> bookSearchRequestIds = bookSearchRequestDocuments.getContent().stream().map(BookSearchRequestDocument::getId).toList();
        List<BookRequestDocument> bookRequestDocuments = bookRequestRepository.getAllByBookSearchRequestIdIn(bookSearchRequestIds);
        Map<String, List<BookRequestDocument>> bookRequestMap = bookRequestDocuments.stream().collect(Collectors.groupingBy(BookRequestDocument::getBookSearchRequestId));
        for (BookSearchRequestDocument bookSearchRequestDocument : bookSearchRequestDocuments.getContent()) {

            BookSearchRequestDto bookSearchRequestDto = new BookSearchRequestDto();
            bookSearchRequestDto.setId(bookSearchRequestDocument.getId());
            bookSearchRequestDto.setUserId(bookSearchRequestDocument.getUserId());
            bookSearchRequestDto.setFullName(bookSearchRequestDocument.getFullName());
            bookSearchRequestDto.setEmail(bookSearchRequestDocument.getEmail());
            bookSearchRequestDto.setPhoneNumber(bookSearchRequestDocument.getPhoneNumber());
            bookSearchRequestDto.setCreatedAt(bookSearchRequestDocument.getCreatedAt());
            bookSearchRequestDto.setUpdatedAt(bookSearchRequestDocument.getUpdatedAt());
            bookSearchRequestDto.setBookRequests(bookRequestDocuments);
            if (bookRequestMap.containsKey(bookSearchRequestDocument.getId())) {
                bookSearchRequestDto.setBookRequests(bookRequestMap.get(bookSearchRequestDocument.getId()));
            }
            content.add(bookSearchRequestDto);
        }
        return new PageImpl<>(content, bookSearchRequestDocuments.getPageable(), bookSearchRequestDocuments.getTotalElements());
    }

    public BookSearchRequestDto findById(String id) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = bookSearchRequestRepository.findById(id).orElseThrow(() -> new BizException("Invalid book search request id"));
        List<BookRequestDocument> bookRequestDocuments = bookRequestRepository.getAllByBookSearchRequestId(id);

        BookSearchRequestDto result = new BookSearchRequestDto();
        result.setId(bookSearchRequestDocument.getId());
        result.setUserId(bookSearchRequestDocument.getUserId());
        result.setFullName(bookSearchRequestDocument.getFullName());
        result.setEmail(bookSearchRequestDocument.getEmail());
        result.setPhoneNumber(bookSearchRequestDocument.getPhoneNumber());
        result.setCreatedAt(bookSearchRequestDocument.getCreatedAt());
        result.setUpdatedAt(bookSearchRequestDocument.getUpdatedAt());
        result.setBookRequests(bookRequestDocuments);
        return result;
    }

    public void createBookSearchRequest(CreateBookSearchRequestModel bookSearchRequestModel) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = new BookSearchRequestDocument();
        if (getCurrentUserInfo() != null) {
            bookSearchRequestDocument.setUserId(getCurrentUserInfo().getUserId());
        }
        bookSearchRequestDocument.setFullName(bookSearchRequestModel.getFullName());
        bookSearchRequestDocument.setEmail(bookSearchRequestModel.getEmail());
        bookSearchRequestDocument.setPhoneNumber(bookSearchRequestModel.getPhoneNumber());
        bookSearchRequestDocument.setCreatedAt(ZonedDateTime.now());
        bookSearchRequestDocument.setUpdatedAt(ZonedDateTime.now());
        BookSearchRequestDocument bookSearchRequest = bookSearchRequestRepository.save(bookSearchRequestDocument);
        List<BookRequestDocument> bookRequests = new ArrayList<>();
        for (CreateBookSearchRequestModel.BookRequest bookStoreDocument : bookSearchRequestModel.getBookRequests()) {
            BookRequestDocument bookRequest = new BookRequestDocument();
            bookRequest.setBookName(bookStoreDocument.getBookName());
            bookRequest.setAuthor(bookStoreDocument.getAuthor());
            bookRequest.setStatus(Const.BookSearchRequestStatus.NEW.name());
            bookRequest.setBookSearchRequestId(bookSearchRequest.getId());
            bookRequest.setCreatedAt(ZonedDateTime.now());
            bookRequest.setUpdatedAt(ZonedDateTime.now());
            bookRequests.add(bookRequest);
        }
        bookRequestRepository.saveAll(bookRequests);
    }

    public void updateBookSearchRequest(String id, UpdateBookSearchRequestModel bookSearchRequestModel) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = bookSearchRequestRepository.findById(id).orElseThrow(() -> new BizException("Invalid book search request id"));
        bookSearchRequestDocument.setFullName(bookSearchRequestModel.getFullName());
        bookSearchRequestDocument.setEmail(bookSearchRequestModel.getEmail());
        bookSearchRequestDocument.setPhoneNumber(bookSearchRequestModel.getPhoneNumber());
        bookSearchRequestDocument.setUpdatedAt(ZonedDateTime.now());
        List<BookRequestDocument> bookRequestDocuments = bookRequestRepository.getAllByBookSearchRequestId(bookSearchRequestDocument.getId());
        List<String> bookRequestIds = bookRequestDocuments.stream().map(BookRequestDocument::getId).toList();
        List<String> updatedBookRequestIds = bookSearchRequestModel.getBookRequests().stream().map(UpdateBookSearchRequestModel.BookRequest::getId).filter(bookRequestModelId -> bookRequestModelId != null).toList();
        List<String> removedBookRequestIds = bookRequestIds.stream().filter(bookRequestId -> !updatedBookRequestIds.contains(bookRequestId)).toList();
        bookRequestRepository.deleteAllById(removedBookRequestIds);
        List<BookRequestDocument> bookRequests = new ArrayList<>();
        for (UpdateBookSearchRequestModel.BookRequest bookRequestModel : bookSearchRequestModel.getBookRequests()) {
            BookRequestDocument bookRequest = new BookRequestDocument();
            bookRequest.setId(bookRequestModel.getId());
            bookRequest.setBookName(bookRequestModel.getBookName());
            bookRequest.setAuthor(bookRequestModel.getAuthorName());
            if (bookRequestModel.getStatus() != null) {
                bookRequest.setStatus(bookRequestModel.getStatus().name());
            } else {
                bookRequest.setStatus(Const.BookSearchRequestStatus.NEW.name());
            }
            bookRequest.setBookSearchRequestId(bookSearchRequestDocument.getId());
            bookRequests.add(bookRequest);
        }
        bookRequestRepository.saveAll(bookRequests);
        bookSearchRequestRepository.save(bookSearchRequestDocument);
    }

    public void deleteBookSearchRequest(String id) throws BizException {
        BookSearchRequestDocument bookSearchRequestDocument = bookSearchRequestRepository.findById(id).orElseThrow(() -> new BizException("Invalid book search request id"));
        bookRequestRepository.deleteAllByBookSearchRequestId(bookSearchRequestDocument.getId());
        bookSearchRequestRepository.delete(bookSearchRequestDocument);
    }
}
