package box.bookstorebe.service.order;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.order.OfflineOrderDetailDocument;
import box.bookstorebe.document.order.OfflineOrderDocument;
import box.bookstorebe.dto.order.OfflineBookDetailDto;
import box.bookstorebe.dto.order.OfflineOrderDetailDto;
import box.bookstorebe.dto.order.OfflineOrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOfflineOrderModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.order.OfflineOrderDetailRepository;
import box.bookstorebe.repository.order.OfflineOrderRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class OfflineOrderService extends BaseService {
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;
    private final OfflineOrderRepository offlineOrderRepository;
    private final OfflineOrderDetailRepository offlineOrderDetailRepository;
    private final AccountRepository accountRepository;

    public void createOfflineOrder(CreateOfflineOrderModel orderModel) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null || currentUser.getAccountId() == null) { // Logged in user
            throw new BizException("Permission denied");
        }

        List<String> barcodes = orderModel.getBookOrders().stream().map(CreateOfflineOrderModel.BookOfflineDetail::getBarcode).toList();

        List<BookInventory> bookInventories = bookInventoryRepository.findAllByBarcodeIn(barcodes);

        OfflineOrderDocument offlineOrderDocument = new OfflineOrderDocument();
        offlineOrderDocument.setBillDiscount(orderModel.getBillDiscount());
        offlineOrderDocument.setAccountId(currentUser.getAccountId());
        List<OfflineOrderDetailDocument> offlineOrderDetailDocuments = new ArrayList<>();
        List<BookInventory> boughtBookInventories = new ArrayList<>();
        BigDecimal totalBookPrice = new BigDecimal(0);
        BigDecimal totalBookDiscount = new BigDecimal(0);

        for (CreateOfflineOrderModel.BookOfflineDetail bookOfflineDetail : orderModel.getBookOrders()) {
            String barcode = bookOfflineDetail.getBarcode();
            BookInventory bookInventory = bookInventories.stream().filter(b -> Objects.equals(b.getBarcode(), barcode)).toList().stream().findFirst().orElseGet(() -> null);
            if (bookInventory == null) {
                throw new BizException("Invalid params");
            }

            if (bookInventory.getQuantity() < bookOfflineDetail.getQuantity()) {
                throw new BizException("Invalid params");
            }

            OfflineOrderDetailDocument offlineOrderDetailDocument = new OfflineOrderDetailDocument();
            offlineOrderDetailDocument.setBookInventoryId(bookInventory.getId());
            offlineOrderDetailDocument.setDiscount(bookOfflineDetail.getDiscount());
            offlineOrderDetailDocument.setQuantity(bookOfflineDetail.getQuantity());
            offlineOrderDetailDocument.setNote(bookOfflineDetail.getNote());
            offlineOrderDetailDocument.setCreatedAt(ZonedDateTime.now());
            offlineOrderDetailDocument.setUpdatedAt(ZonedDateTime.now());
            offlineOrderDetailDocuments.add(offlineOrderDetailDocument);
            totalBookPrice = totalBookPrice.add(bookInventory.getPrice().multiply(BigDecimal.valueOf(bookOfflineDetail.getQuantity())));
            totalBookDiscount = totalBookDiscount.add(bookInventory.getPrice().multiply(BigDecimal.valueOf(bookOfflineDetail.getQuantity())).multiply(BigDecimal.valueOf(bookOfflineDetail.getDiscount() / 100.0)));
            bookInventory.setQuantity(bookInventory.getQuantity() - bookOfflineDetail.getQuantity());
            boughtBookInventories.add(bookInventory);
        }

        offlineOrderDocument.setBookDiscount(totalBookDiscount);
        offlineOrderDocument.setTotalBookPrice(totalBookPrice);
        offlineOrderDocument.setBillDiscount(orderModel.getBillDiscount());
        offlineOrderDocument.setTotalPrice((totalBookPrice.min(totalBookDiscount)).multiply(BigDecimal.valueOf(1 - (orderModel.getBillDiscount() / 100.0))));
        offlineOrderDocument.setCreatedAt(ZonedDateTime.now());
        offlineOrderDocument.setUpdatedAt(ZonedDateTime.now());
        OfflineOrderDocument newOfflineOrder = offlineOrderRepository.save(offlineOrderDocument);
        for (OfflineOrderDetailDocument offlineOrderDetailDocument : offlineOrderDetailDocuments) {
            offlineOrderDetailDocument.setOfflineOrderId(newOfflineOrder.getId());
        }
        offlineOrderDetailRepository.saveAll(offlineOrderDetailDocuments);
        bookInventoryRepository.saveAll(boughtBookInventories);
    }

    public Page<OfflineOrderDto> getOfflineOrders(ZonedDateTime startAt, ZonedDateTime endAt, int page, int size) {
        Page<OfflineOrderDocument> offlineOrders = offlineOrderRepository.getOfflineOrders(startAt, endAt, page, size);
        List<String> accountIds = offlineOrders.getContent().stream().map(OfflineOrderDocument::getAccountId).toList();
        List<String> offlineOrderIds = offlineOrders.getContent().stream().map(OfflineOrderDocument::getId).toList();

        List<OfflineOrderDetailDocument> offlineOrderDetails = offlineOrderDetailRepository.findAllByOfflineOrderIdIn(offlineOrderIds);
        // group by offline order id
        Map<String, List<OfflineOrderDetailDocument>> offlineOrderDetailMap = offlineOrderDetails.stream().collect(Collectors.groupingBy(OfflineOrderDetailDocument::getOfflineOrderId));

        List<AccountDocument> accountDocuments = accountRepository.findAllById(accountIds);

        List<OfflineOrderDto> result = new ArrayList<>();
        for (OfflineOrderDocument offlineOrder : offlineOrders) {
            OfflineOrderDto offlineOrderDto = new OfflineOrderDto();
            offlineOrderDto.setId(offlineOrder.getId());
            offlineOrderDto.setBillDiscount(offlineOrder.getBillDiscount());
            offlineOrderDto.setTotalBookPrice(offlineOrder.getTotalBookPrice());
            offlineOrderDto.setBookDiscount(offlineOrder.getBookDiscount());
            offlineOrderDto.setTotalPrice(offlineOrder.getTotalPrice());
            offlineOrderDto.setCreatedAt(offlineOrder.getCreatedAt());
            offlineOrderDto.setUpdatedAt(offlineOrder.getUpdatedAt());

            List<OfflineOrderDetailDocument> offlineOrderDetailDocuments = offlineOrderDetailMap.getOrDefault(offlineOrder.getId(), new ArrayList<>());
            offlineOrderDto.setQuantity(offlineOrderDetailDocuments.size());

            OfflineOrderDto.Seller seller = new OfflineOrderDto.Seller();
            AccountDocument accountDocument = accountDocuments.stream().filter(u -> u.getId().equals(offlineOrder.getAccountId())).findFirst().orElseGet(() -> null);
            if (accountDocument != null) {
                seller.setId(accountDocument.getId());
                seller.setName(accountDocument.getEmail());
                seller.setRole(accountDocument.getRole().toString());
                offlineOrderDto.setSeller(seller);
            }

            result.add(offlineOrderDto);
        }
        return new PageImpl<>(result, offlineOrders.getPageable(), offlineOrders.getTotalElements());
    }

    public OfflineOrderDetailDto findById(String id) throws BizException {
        OfflineOrderDocument offlineOrderDocument = offlineOrderRepository.findById(id).orElseThrow(() -> new BizException("Invalid params"));

        List<OfflineOrderDetailDocument> offlineOrderDetailDocuments = offlineOrderDetailRepository.findAllByOfflineOrderId(id);
        List<String> bookInventoryIds = offlineOrderDetailDocuments.stream().map(OfflineOrderDetailDocument::getBookInventoryId).toList();

        List<BookInventory> bookInventories = bookInventoryRepository.findAllById(bookInventoryIds);
        List<String> bookIds = bookInventories.stream().map(BookInventory::getBookId).toList();
        List<BookDocument> books = bookRepository.findAllById(bookIds);

        AccountDocument accountDocument = accountRepository.findById(offlineOrderDocument.getAccountId()).orElseThrow(() -> new BizException("Invalid params"));
        OfflineOrderDetailDto.Seller seller = new OfflineOrderDetailDto.Seller();
        seller.setId(accountDocument.getId());
        seller.setName(accountDocument.getEmail());
        seller.setRole(accountDocument.getRole().toString());

        OfflineOrderDetailDto offlineOrderDto = new OfflineOrderDetailDto();
        offlineOrderDto.setId(offlineOrderDocument.getId());
        offlineOrderDto.setBillDiscount(offlineOrderDocument.getBillDiscount());
        offlineOrderDto.setTotalBookPrice(offlineOrderDocument.getTotalBookPrice());
        offlineOrderDto.setBookDiscount(offlineOrderDocument.getBookDiscount());
        offlineOrderDto.setTotalPrice(offlineOrderDocument.getTotalPrice());
        offlineOrderDto.setCreatedAt(offlineOrderDocument.getCreatedAt());
        offlineOrderDto.setUpdatedAt(offlineOrderDocument.getUpdatedAt());
        offlineOrderDto.setSeller(seller);

        List<OfflineOrderDetailDto.OfflineOrderDetail> details = new ArrayList<>();

        for (BookInventory bookInventory : bookInventories) {
            OfflineOrderDetailDto.OfflineOrderDetail detail = new OfflineOrderDetailDto.OfflineOrderDetail();

            detail.setBookId(bookInventory.getBookId());
            detail.setBarcode(bookInventory.getBarcode());

            BookDocument bookDocument = books.stream().filter(b -> b.getId().equals(bookInventory.getBookId())).findFirst().orElseGet(() -> null);
            if (bookDocument == null) continue;

            detail.setBookName(bookDocument.getName());
            detail.setAuthor(bookDocument.getAuthorName());
            detail.setType(bookInventory.getType().name());
            detail.setQuantity(bookInventory.getQuantity());
            detail.setPrice(bookInventory.getPrice());
            OfflineOrderDetailDocument offlineOrderDetailDocument = offlineOrderDetailDocuments.stream().filter(d -> d.getBookInventoryId().equals(bookInventory.getId())).findFirst().orElseGet(() -> null);
            detail.setCreatedAt(bookInventory.getCreatedAt());
            detail.setUpdatedAt(bookInventory.getUpdatedAt());
            if (offlineOrderDetailDocument != null) {
                detail.setDiscount(offlineOrderDetailDocument.getDiscount());
                detail.setNote(offlineOrderDetailDocument.getNote());
                detail.setTotalPrice(bookInventory.getPrice().multiply(BigDecimal.valueOf(bookInventory.getQuantity())).multiply(BigDecimal.valueOf((100 - offlineOrderDetailDocument.getDiscount()) / 100)));
            }
            details.add(detail);
        }

        offlineOrderDto.setDetails(details);
        return offlineOrderDto;
    }

    public OfflineBookDetailDto getOfflineBookDetail(String barcode, Integer quantity) throws BizException {
        if (barcode == null) {
            throw new BizException("Invalid params");
        }

        BookInventory bookInventory = bookInventoryRepository.findByBarcode(barcode);
        if (bookInventory == null) {
            throw new BizException("Không tồn tại sách với mã barcode: " + barcode + " trong hệ thống");
        }

        if (quantity == null) {
            throw new BizException("Invalid params");
        }

        if (bookInventory.getQuantity() < quantity) {
            throw new BizException(String.format("Số lượng sách với mã %s còn lại trong hệ thống không đủ - Còn %s quyển", barcode, bookInventory.getQuantity()));
        }

        BookDocument bookDocument = bookRepository.findById(bookInventory.getBookId()).orElseThrow(() -> new BizException("Invalid params"));

        OfflineBookDetailDto result = new OfflineBookDetailDto();
        result.setBookInventoryId(bookInventory.getId());
        result.setBarcode(bookInventory.getBarcode());
        result.setBookName(bookDocument.getName());
        result.setAuthor(bookDocument.getName());
        result.setPrice(bookInventory.getPrice());
        result.setType(bookInventory.getType().name());
        result.setCreatedAt(bookInventory.getCreatedAt());
        result.setUpdatedAt(bookInventory.getUpdatedAt());
        return result;
    }

}
