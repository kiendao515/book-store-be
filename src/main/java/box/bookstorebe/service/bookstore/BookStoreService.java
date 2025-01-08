package box.bookstorebe.service.bookstore;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.dto.account.DeleteAccountDto;
import box.bookstorebe.dto.book.BookInventoryDto;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.dto.bookstore.DetailBookRevenue;
import box.bookstorebe.dto.bookstore.StoreRevenueDto;
import box.bookstorebe.dto.order.OrderItemDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.bookstore.BookStoreMapper;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.CreateBookstoreAndAccount;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
import box.bookstorebe.model.order.OrderItem;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.account.AccountService;
import box.bookstorebe.service.common.MailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final OrderRepository orderRepository;
    private final MailService mailService;

    // route này cho admin thêm sửa xóa bkstore
    public Page<BookStoreDto> getBookStores(String name, Integer page, Integer size) {
        Page<StoreDocument> bookStoreDocuments = bookStoreRepository.getBookStores(name, page, size);
        List<String> accountIds = bookStoreDocuments.getContent()
                .stream()
                .map(StoreDocument::getAccountId)
                .collect(Collectors.toList());

        Map<String, AccountDocument> accountMap = accountRepository.findAllById(accountIds)
                .stream()
                .collect(Collectors.toMap(AccountDocument::getId, account -> account));

        List<BookStoreDto> content = bookStoreDocuments.getContent()
                .stream()
                .map(storeDocument -> {
                    BookStoreDto bookStoreDto = BookStoreMapper.INSTANCE.entityToDto(storeDocument);
                    AccountDocument accountDocument = accountMap.get(storeDocument.getAccountId());
                    if (accountDocument != null) {
                        bookStoreDto.setAccountId(accountDocument.getId());
                    }
                    return bookStoreDto;
                })
                .collect(Collectors.toList());

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

    public void createNewBookStoreAndAccount(CreateBookstoreAndAccount bookStoreModel) throws BizException, MessagingException {
        StoreDocument bookStoreDocument = new StoreDocument();
        bookStoreDocument.setThumbnail(bookStoreModel.getThumbnail());
        bookStoreDocument.setName(bookStoreModel.getName());
        bookStoreDocument.setAddress(bookStoreModel.getAddress());
        bookStoreDocument.setPhoneNumber(bookStoreModel.getPhone());
        bookStoreDocument.setDescription(bookStoreModel.getDescription());
        bookStoreDocument.setCommissionPercentage(bookStoreModel.getCommissionPercentage());
        bookStoreDocument.setCreatedAt(ZonedDateTime.now());
        bookStoreDocument.setUpdatedAt(ZonedDateTime.now());
        AccountDocument acc = accountService.createAccount(new UserModel(bookStoreModel.getEmail(), bookStoreModel.getPassword(),null), Role.STORE, 1);
        bookStoreDocument.setAccountId(acc.getId());
        bookStoreRepository.save(bookStoreDocument);
        mailService.sendMailStoreInfo(bookStoreModel.getEmail(), bookStoreModel.getPassword(), bookStoreDocument);
    }

    public StoreDocument createStoreInfo(UpdateBookStoreModel bookStoreModel) {
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
        if (updateBookStoreModel.getEnabled() != null) {
            if (!updateBookStoreModel.getEnabled().equals("0") && !updateBookStoreModel.getEnabled().equals("1")) {
                throw new BizException("invalid enabled param");
            }
            accountDocument.setEnabled(Integer.parseInt(updateBookStoreModel.getEnabled()));
            accountRepository.save(accountDocument);
        }
        if (bookStoreDocument == null) {
            bookStoreDocument = createStoreInfo(updateBookStoreModel);
            bookStoreRepository.save(bookStoreDocument);
        } else {
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
        for (String id : deleteAccountDto.getAccountIds()) {
            AccountDocument accountDocument = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid account id"));
            accountDocument.setDeletedAt(ZonedDateTime.now());
            accountRepository.save(accountDocument);
            StoreDocument store = bookStoreRepository.findByAccountId(id);
            if (store != null) {
                store.setDeletedAt(ZonedDateTime.now());
                bookStoreRepository.save(store);
            }
        }
    }

    public List<StoreRevenueDto> getStoreRevenue(String id, ZonedDateTime from, ZonedDateTime to) throws BizException {
        StoreDocument storeDocument = bookStoreRepository.findById(id)
                .orElseThrow(() -> new BizException("Invalid store id"));

        List<BookInventory> bookInventories = bookInventoryRepository.findAllByStoreIdAndCreatedAtBetween(id, from, to);

        if (bookInventories.isEmpty()) {
            throw new BizException("No books found for the store id: " + id);
        }

        List<String> bookIds = bookInventories.stream()
                .map(BookInventory::getBookId)
                .distinct()
                .toList();

        Map<String, BookDocument> bookMap = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(BookDocument::getId, book -> book));
        List<String> bookInventoryIds = bookInventories.stream()
                .map(BookInventory::getId)
                .toList();

        List<OrderItemDocument> orderItems = orderItemRepository.findAllByBookInventoryIdIn(bookInventoryIds);
        List<StoreRevenueDto> revenueDtos = new ArrayList<>();
        Map<String, List<BookInventory>> inventoriesGroupedByBookId = bookInventories.stream()
                .collect(Collectors.groupingBy(BookInventory::getBookId));

        for (Map.Entry<String, List<BookInventory>> entry : inventoriesGroupedByBookId.entrySet()) {
            String bookId = entry.getKey();
            List<BookInventory> inventoriesForBook = entry.getValue();
            int totalInventory = inventoriesForBook.stream().mapToInt(BookInventory::getQuantity).sum();
            int totalSold = 0;
            int totalSettled = 0;
            BigDecimal totalSettledAmount = BigDecimal.ZERO;
            BigDecimal totalNotSettledAmount = BigDecimal.ZERO;

            for (BookInventory inventory : inventoriesForBook) {
                List<OrderItemDocument> itemsForInventory = orderItems.stream()
                        .filter(order -> order.getBookInventoryId().equals(inventory.getId()))
                        .toList();
                int sold = itemsForInventory.stream().mapToInt(OrderItemDocument::getQuantity).sum();
                int settled = itemsForInventory.stream()
                        .filter(orderItem -> orderItem.getSettledStatus() == 1)
                        .mapToInt(OrderItemDocument::getQuantity)
                        .sum();
                BigDecimal price = inventory.getPrice();
                totalSettledAmount = totalSettledAmount.add(price.multiply(BigDecimal.valueOf(settled)));
                totalNotSettledAmount = totalNotSettledAmount.add(price.multiply(BigDecimal.valueOf(sold - settled)));
                totalSold += sold;
                totalSettled += settled;
            }

            int notSettled = totalSold - totalSettled;
            BookDocument book = bookMap.get(bookId);
            if (book == null) {
                throw new BizException("Book not found for id: " + bookId);
            }
            StoreRevenueDto dto = new StoreRevenueDto(
                    bookId,
                    book,
                    totalInventory,
                    totalSold,
                    totalSettled,
                    notSettled,
                    totalSettledAmount,
                    totalNotSettledAmount,
                    storeDocument.getCommissionPercentage()
            );
            revenueDtos.add(dto);
        }

        return revenueDtos;
    }

    public DetailBookRevenue getDetailBookRevenue(String bookId, String storeId) throws BizException {
        BookDocument book = bookRepository.findById(bookId).orElseThrow(() -> new BizException("invalid bookid"));
        DetailBookRevenue detailBookRevenue = new DetailBookRevenue();
        List<BookInventory> inventories = bookInventoryRepository.findAllByBookIdAndStoreId(bookId, storeId);
        if (inventories.isEmpty()) {
            throw new BizException("No inventory found for the given bookId: " + bookId + " and storeId: " + storeId);
        }

        List<BookInventoryDto> inventoryDtos = new ArrayList<>();
        BigDecimal totalAmountInventory = BigDecimal.ZERO;

        for (BookInventory inventory : inventories) {
            BookInventoryDto dto = new BookInventoryDto();
            dto.setId(inventory.getId());
            dto.setBook(book);
            dto.setBookId(inventory.getBookId());
            dto.setPrice(inventory.getPrice());
            dto.setLocation(inventory.getLocation());
            dto.setQuantity(inventory.getQuantity());
            dto.setType(inventory.getType());
            dto.setStoreId(inventory.getStoreId());
            dto.setCreatedAt(inventory.getCreatedAt());
            dto.setUpdatedAt(inventory.getUpdatedAt());

            inventoryDtos.add(dto);
            totalAmountInventory = totalAmountInventory.add(
                    inventory.getPrice().multiply(BigDecimal.valueOf(inventory.getQuantity()))
            );
        }

        List<String> bookInventoryIds = inventories.stream().map(BookInventory::getId).toList();
        List<OrderItemDocument> orderItems = orderItemRepository.findAllByBookInventoryIdIn(bookInventoryIds);

        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        BigDecimal totalAmountSold = BigDecimal.ZERO;

        for (OrderItemDocument item : orderItems) {

            BookInventory matchedInventory = inventories.stream()
                    .filter(inv -> inv.getId().equals(item.getBookInventoryId()))
                    .findFirst()
                    .orElse(null);

            if (matchedInventory != null) {
                OrderItemDto dto = new OrderItemDto();
                dto.setBookName(book.getName());
                dto.setQuantity(item.getQuantity());
                dto.setPrice(matchedInventory.getPrice());
                dto.setType(matchedInventory.getType());
                dto.setSettledStatus(item.getSettledStatus());
                OrderDocument orderDocument = orderRepository.findById(item.getOrderId()).orElseThrow(() -> new BizException("invalid orderid"));
                dto.setCreatedAt(orderDocument.getCreatedAt());
                dto.setOrderId(orderDocument.getOrderCode());
                orderItemDtos.add(dto);
                totalAmountSold = totalAmountSold.add(
                        matchedInventory.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
        }

        detailBookRevenue.setInventory(inventoryDtos);
        detailBookRevenue.setOrderItems(orderItemDtos);
        detailBookRevenue.setTotalAmountInventory(totalAmountInventory);
        detailBookRevenue.setTotalAmountSold(totalAmountSold);

        return detailBookRevenue;
    }

    public void confirmBookRevenue(List<String> ids, String storeId) throws BizException {
        StoreDocument storeDocument = bookStoreRepository.findById(storeId).orElseThrow(() -> new BizException("Invalid store id"));
        List<BookInventory> inventories = bookInventoryRepository.findAllByBookIdInAndStoreId(ids, storeDocument.getId());
        if (inventories.isEmpty()) {
            throw new BizException("No inventory found for the given bookId: " + ids + " and storeId: " + storeId);
        }
        List<String> bookInventoryIds = inventories.stream().map(BookInventory::getId).toList();
        List<OrderItemDocument> orderItems = orderItemRepository.findAllByBookInventoryIdIn(bookInventoryIds);
        for (OrderItemDocument orderItem : orderItems) {
            if (orderItem.getSettledStatus() == 0) {
                orderItem.setSettledStatus(1);
                orderItemRepository.save(orderItem);
            }
        }
    }


}
