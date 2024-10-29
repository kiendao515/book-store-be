package box.bookstorebe.service.bookstore;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.bookstore.BookStoreMapper;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.CreateBookstoreAndAccount;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.service.account.AccountService;
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
    private final AccountService accountService;

    // route này cho admin thêm sửa xóa bkstore
    public Page<BookStoreDto> getBookStores(String name, Integer page, Integer size) {
        Page<StoreDocument> bookStoreDocuments = bookStoreRepository.getBookStores(name, page, size);

        List<BookStoreDto> content = new ArrayList<>();
        for (StoreDocument bookStoreDocument : bookStoreDocuments.getContent()) {
            BookStoreDto bookStoreDto = BookStoreMapper.INSTANCE.entityToDto(bookStoreDocument);
            content.add(bookStoreDto);
        }
        return new PageImpl<>(content, bookStoreDocuments.getPageable(), bookStoreDocuments.getTotalElements());
    }

    public BookStoreDto findById(String id) throws BizException {
        StoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        return BookStoreMapper.INSTANCE.entityToDto(bookStoreDocument);
    }

    public void createNewBookStore(CreateBookStoreModel bookStoreModel) throws BizException {
        StoreDocument bookStoreDocument = new StoreDocument();
        bookStoreDocument.setThumbnail(bookStoreModel.getThumbnail());
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreModel.getPhoneNumber());
        bookStoreDocument.setDescription(bookStoreModel.getDescription());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }
    public void createNewBookStoreAndAccount(CreateBookstoreAndAccount bookStoreModel) throws BizException {
        StoreDocument bookStoreDocument = new StoreDocument();
        bookStoreDocument.setThumbnail(bookStoreModel.getThumbnail());
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreModel.getPhoneNumber());
        bookStoreDocument.setDescription(bookStoreModel.getDescription());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        AccountDocument acc= accountService.createAccount(new UserModel(bookStoreModel.getEmail(), bookStoreModel.getPassword()), Role.STORE,1);
        bookStoreDocument.setAccountId(acc.getId());
        bookStoreRepository.save(bookStoreDocument);
    }

    public void updateBookStore(String id, UpdateBookStoreModel updateBookStoreModel) throws BizException {
        StoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        bookStoreDocument.setThumbnail(updateBookStoreModel.getThumbnail());
        bookStoreDocument.setName(updateBookStoreModel.getName());
        bookStoreDocument.setAddress(updateBookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(updateBookStoreModel.getPhoneNumber());
        bookStoreDocument.setDescription(updateBookStoreModel.getDescription());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }

    public void deleteBookStore(String id) throws BizException {
        StoreDocument bookStoreDocument = bookStoreRepository.findById(id).orElseThrow(() -> new BizException("Invalid book store id"));
        bookStoreDocument.setDeletedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }
}
