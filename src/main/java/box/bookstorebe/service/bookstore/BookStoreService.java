package box.bookstorebe.service.bookstore;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.dto.account.DeleteAccountDto;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.dto.bookstore.StoreRevenueDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.bookstore.BookStoreMapper;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.CreateBookstoreAndAccount;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.account.AccountService;
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
public class BookStoreService {
    private final BookStoreRepository bookStoreRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final BookInventoryRepository bookInventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;

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
        bookStoreDocument.setCommissionPercentage(bookStoreModel.getCommissionPercentage());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        bookStoreRepository.save(bookStoreDocument);
    }
    public void createNewBookStoreAndAccount(CreateBookstoreAndAccount bookStoreModel) throws BizException {
        StoreDocument bookStoreDocument = new StoreDocument();
        bookStoreDocument.setThumbnail(bookStoreModel.getThumbnail());
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreModel.getPhone());
        bookStoreDocument.setDescription(bookStoreModel.getDescription());
        bookStoreDocument.setCommissionPercentage(bookStoreDocument.getCommissionPercentage());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        AccountDocument acc= accountService.createAccount(new UserModel(bookStoreModel.getEmail(), bookStoreModel.getPassword()), Role.STORE,1);
        bookStoreDocument.setAccountId(acc.getId());
        bookStoreRepository.save(bookStoreDocument);
    }
    public StoreDocument createStoreInfo(UpdateBookStoreModel bookStoreModel){
        StoreDocument bookStoreDocument = new StoreDocument();
        bookStoreDocument.setThumbnail(bookStoreModel.getAvatar());
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreModel.getPhone());
        bookStoreDocument.setDescription(bookStoreModel.getDescription());
        bookStoreDocument.setCommissionPercentage(bookStoreModel.getCommissionPercentage());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        return bookStoreDocument;

    }

    public void updateBookStore(String accountId, UpdateBookStoreModel updateBookStoreModel) throws BizException {
        AccountDocument accountDocument = accountRepository.findById(accountId).orElseThrow(() -> new BizException("Invalid account id"));
        StoreDocument bookStoreDocument = bookStoreRepository.findByAccountId(accountId);
        if(updateBookStoreModel.getEnabled() != null){
            if(!updateBookStoreModel.getEnabled().equals("0") && !updateBookStoreModel.getEnabled().equals("1")){
                throw new BizException("invalid enabled param");
            }
            accountDocument.setEnabled(Integer.parseInt(updateBookStoreModel.getEnabled()));
            accountRepository.save(accountDocument);
        }
        if(bookStoreDocument == null){
            bookStoreDocument = createStoreInfo(updateBookStoreModel);
            bookStoreRepository.save(bookStoreDocument);
        }else{
            bookStoreDocument.setThumbnail(updateBookStoreModel.getAvatar());
            bookStoreDocument.setName(updateBookStoreModel.getName());
            bookStoreDocument.setAddress(updateBookStoreModel.getAddress());
            bookStoreDocument.setPhoneNumber(updateBookStoreModel.getPhone());
            bookStoreDocument.setDescription(updateBookStoreModel.getDescription());
            bookStoreDocument.setCommissionPercentage(updateBookStoreModel.getCommissionPercentage());
            bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        }
        bookStoreRepository.save(bookStoreDocument);
    }

    public void deleteBookStore(DeleteAccountDto deleteAccountDto) throws BizException {
        for(String id : deleteAccountDto.getAccountIds()){
            AccountDocument accountDocument = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid account id"));
            accountDocument.setDeletedAt(ZonedDateTime.now());
            accountRepository.save(accountDocument);
            StoreDocument store = bookStoreRepository.findByAccountId(id);
            if(store != null){
                store.setDeletedAt(ZonedDateTime.now());
                bookStoreRepository.save(store);
            }
        }
    }

    public List<StoreRevenueDto> getStoreRevenue(String id) throws BizException {
        StoreDocument storeDocument = bookStoreRepository.findById(id).orElseThrow(()-> new BizException("Invalid store id"));
        List<BookInventory> bookInventories = bookInventoryRepository.findAllByStoreId(id);

        if (bookInventories.isEmpty()) {
            throw new BizException("No books found for the store id: " + id);
        }
        List<StoreRevenueDto> revenueDtos = new ArrayList<>();
        List<String> bookInventoryIds = bookInventories.stream()
                .map(BookInventory::getId)
                .toList();

        List<OrderItemDocument> orderItems = orderItemRepository.findAllByBookInventoryIdIn(bookInventoryIds);
        Map<String, List<OrderItemDocument>> orderItemsGroupedByInventory = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDocument::getBookInventoryId));

        for (BookInventory inventory : bookInventories) {
            List<OrderItemDocument> itemsForInventory = orderItemsGroupedByInventory.getOrDefault(inventory.getId(), List.of());

            int sold = itemsForInventory.stream().mapToInt(OrderItemDocument::getQuantity).sum();
            int settled = itemsForInventory.stream()
                    .filter(orderItem -> orderItem.getSettledStatus() == 1)
                    .mapToInt(OrderItemDocument::getQuantity)
                    .sum();
            int notSettled = sold - settled;

            BookDocument book = bookRepository.findById(inventory.getBookId())
                    .orElseThrow(() -> new BizException("Book not found for id: " + inventory.getBookId()));
            float commissionRate = storeDocument.getCommissionPercentage();
            float commissionPercentage = commissionRate * 100;
            StoreRevenueDto dto = new StoreRevenueDto(
                    inventory.getId(),
                    book,
                    inventory.getQuantity(),
                    sold,
                    settled,
                    notSettled,
                    commissionPercentage
            );
            revenueDtos.add(dto);
        }
        return revenueDtos;
    }
}
