package box.bookstorebe.service.report;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.book.CategorySalesStat;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.dto.report.BuyerStat;
import box.bookstorebe.dto.report.OrderReportDto;
import box.bookstorebe.dto.report.RevenueStatDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.book.CategoryRepository;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class ReportService extends BaseService {
    private final OrderRepository orderRepository;
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookInformationRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;


    public OrderReportDto getOrderStatisticByMonth(ZonedDateTime fromDateTime, ZonedDateTime toDateTime) throws BizException {
        List<OrderDocument> orderDocuments = orderRepository.findAll();
        List<OrderDocument> filteredOrders = orderDocuments.stream()
                .filter(order ->
                        !order.getCreatedAt().isBefore(fromDateTime) &&
                                !order.getCreatedAt().isAfter(toDateTime))
                .collect(Collectors.toList());

        OrderReportDto orderReportDto = new OrderReportDto();

        orderReportDto.setReadyToConfirm(calculateStatistic(filteredOrders, Const.OrderStatus.CREATED));
        orderReportDto.setReadyToPackage(calculateStatistic(filteredOrders, Const.OrderStatus.READY_TO_PACKAGE));
        orderReportDto.setReadyToShip(calculateStatistic(filteredOrders, Const.OrderStatus.READY_TO_SHIP));
        orderReportDto.setShipping(calculateStatistic(filteredOrders, Const.OrderStatus.SHIPPING));
        orderReportDto.setDone(calculateStatistic(filteredOrders, Const.OrderStatus.DONE));
        orderReportDto.setCancel(calculateStatistic(filteredOrders, Const.OrderStatus.CANCEL));

        return orderReportDto;
    }

    private OrderReportDto.Statistic calculateStatistic(List<OrderDocument> orderDocuments, String status) {
        OrderReportDto.Statistic statistic = new OrderReportDto.Statistic();
        List<OrderDocument> filteredOrders = orderDocuments.stream()
                .filter(order -> order.getStatus().equals(status))
                .toList();
        BigDecimal totalAmount = filteredOrders.stream()
                .map(OrderDocument::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistic.setQuantity(filteredOrders.size());
        statistic.setAmount(totalAmount);
        return statistic;
    }

    public List<CategorySalesStat> getTopSellingCategories(ZonedDateTime fromDateTime, ZonedDateTime toDateTime) {
        List<BookDocument> bookInformations = bookInformationRepository.findAll();
        List<BookInventory> bookInventories = bookInventoryRepository.findAll();
        List<OrderItemDocument> orderItems = orderItemRepository.findAll();
        List<OrderDocument> orders = orderRepository.findAll();
        List<CategoryDocument> categories = categoryRepository.findAll();

        Map<String, Integer> categorySales = new HashMap<>();

        for (OrderItemDocument orderItem : orderItems) {
            BookInventory bookInventory = bookInventories.stream()
                    .filter(bi -> bi.getId().equals(orderItem.getBookInventoryId()))
                    .findFirst()
                    .orElse(null);

            if (bookInventory != null) {
                BookDocument bookInformation = bookInformations.stream()
                        .filter(bi -> bi.getId().equals(bookInventory.getBookId()))
                        .findFirst()
                        .orElse(null);

                if (bookInformation != null) {
                    OrderDocument order = orders.stream()
                            .filter(o -> o.getId().equals(orderItem.getOrderId()))
                            .findFirst()
                            .orElse(null);

                    // Thêm điều kiện lọc thời gian với ZonedDateTime
                    if (order != null
                            && "DONE".equals(order.getStatus())
                            && !order.getCreatedAt().isBefore(fromDateTime)
                            && !order.getCreatedAt().isAfter(toDateTime)) {
                        String categoryId = bookInformation.getCategoryId();
                        categorySales.put(categoryId, categorySales.getOrDefault(categoryId, 0) + orderItem.getQuantity());
                    }
                }
            }
        }

        // Tạo danh sách CategorySalesStat
        List<CategorySalesStat> categorySalesStats = categorySales.entrySet().stream()
                .map(entry -> {
                    String categoryId = entry.getKey();
                    int totalSold = entry.getValue();
                    CategoryDocument category = categories.stream()
                            .filter(c -> c.getId().equals(categoryId))
                            .findFirst()
                            .orElse(null);
                    if (category != null) {
                        CategorySalesStat stat = new CategorySalesStat();
                        stat.setId(categoryId);
                        stat.setName(category.getName());
                        stat.setTotalSold(totalSold);
                        return stat;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Integer.compare(b.getTotalSold(), a.getTotalSold()))
                .limit(10)
                .collect(Collectors.toList());

        return categorySalesStats;
    }


    public List<AccountDto> getTopBuyer(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<OrderDocument> orders = orderRepository.findAll()
                .stream()
                .filter(order -> {
                    LocalDateTime orderDateTime = order.getCreatedAt().toLocalDateTime();
                    return (orderDateTime.isEqual(fromDateTime) || orderDateTime.isAfter(fromDateTime)) &&
                            (orderDateTime.isEqual(toDateTime) || orderDateTime.isBefore(toDateTime));
                })
                .toList();

        // Tạo map của tài khoản và khách hàng
        Map<String, AccountDocument> accountMap = accountRepository.findAll()
                .stream()
                .collect(Collectors.toMap(AccountDocument::getId, account -> account));
        Map<String, CustomerDocument> customerMap = customerRepository.findAll()
                .stream()
                .collect(Collectors.toMap(CustomerDocument::getAccountId, customer -> customer));

        // Tính tổng số tiền theo tài khoản
        Map<String, BigDecimal> customerStats = new HashMap<>();
        for (OrderDocument order : orders) {
            customerStats.put(
                    order.getAccountId(),
                    customerStats.getOrDefault(order.getAccountId(), BigDecimal.ZERO)
                            .add(order.getTotalAmount())
            );
        }

        // Tạo danh sách AccountDto
        return customerStats.entrySet().stream()
                .map(entry -> {
                    String accountId = entry.getKey();
                    BigDecimal totalAmount = entry.getValue();

                    AccountDocument account = accountMap.get(accountId);
                    CustomerDocument customer = customerMap.get(accountId);

                    if (account == null) {
                        return null;
                    }

                    AccountDto acc = new AccountDto();
                    acc.setId(accountId);
                    acc.setEmail(account.getEmail());
                    acc.setName(customer != null ? customer.getName() : "Chưa có tên");
                    acc.setPhone(customer != null ? customer.getPhoneNumber() : "Chưa có số điện thoại");
                    acc.setTotalAmount(totalAmount);

                    return acc;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<RevenueStatDto> getRevenueByDateRange(ZonedDateTime fromDateTime, ZonedDateTime toDateTime) {
        List<OrderDocument> orders = orderRepository.findAll().stream()
                .filter(order ->
                        !order.getCreatedAt().isBefore(fromDateTime) &&
                                !order.getCreatedAt().isAfter(toDateTime) &&
                                "DONE".equals(order.getStatus()))
                .toList();


        Map<LocalDate, BigDecimal> revenueByDate = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().toLocalDate(),
                        Collectors.mapping(OrderDocument::getTotalAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));


        return revenueByDate.entrySet().stream()
                .map(entry -> new RevenueStatDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(RevenueStatDto::getDate)) // Sắp xếp theo ngày
                .collect(Collectors.toList());
    }


}
