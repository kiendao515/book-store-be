package box.bookstorebe.service.order;

import box.bookstorebe.client.CommonClient;
import box.bookstorebe.common.Const;
import box.bookstorebe.configuration.security.RequestScope;

import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.document.payment.PaymentDocument;
import box.bookstorebe.dto.common.AddressDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.OrderItem;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.model.order.UpdateOrderModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.account.AccountService;
import box.bookstorebe.service.book.BookInventoryService;
import box.bookstorebe.service.book.BookService;
import box.bookstorebe.service.common.AddressService;
import box.bookstorebe.service.common.MailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService extends BaseService {
    private final OrderRepository orderRepository;
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookInventoryService bookInventoryService;
    private final PaymentService paymentService;
    private static final AtomicLong counter = new AtomicLong();
    private final MongoTemplate mongoTemplate;
    private final MailService mailService;
    private final OrderItemRepository orderItemRepository;
    private final AddressService addressService;
    private final CommonClient commonClient;
    private final AccountService accountService;

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }

    @Transactional
    public Object createOrder(HttpServletRequest request, CreateOrderModel order, String returnUrl) throws BizException, MessagingException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        BigDecimal totalAmount = new BigDecimal(0);
        List<String> bookInventoryIds = order.getOrderItems().stream()
                .map(OrderItem::getBookInventoryId).toList();
        List<BookInventory> bookInventories = bookInventoryRepository.findAllByIdIn(bookInventoryIds);
        if (bookInventories.size() != bookInventoryIds.size()) {
            throw new BizException("Some books are not available in inventory.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            BookInventory inventory = bookInventories.stream()
                    .filter(book -> book.getId().equals(orderItem.getBookInventoryId()))
                    .findFirst()
                    .orElseThrow(() -> new BizException("Book inventory ID " + orderItem.getBookInventoryId() + " not found"));
            if (inventory.getQuantity() < orderItem.getQuantity()) {
                throw new BizException("Not enough quantity for book inventory ID " + orderItem.getBookInventoryId());
            }
        }


        OrderDocument orderDocument = new OrderDocument();
        if (order.getDistrictCode() != null && order.getWardCode() != null && order.getProvinceCode() != null) {
            AddressDto addressDto = addressService.getAddress(order.getProvinceCode(), order.getDistrictCode(), order.getWardCode());
            orderDocument.setProvince(addressDto.getProvince());
            orderDocument.setDistrict(addressDto.getDistrict());
            orderDocument.setWard(addressDto.getWard());
            ShippingFeeRequest shippingFeeRequest = new ShippingFeeRequest();
            shippingFeeRequest.setProvince(addressDto.getProvince().getFullName());
            shippingFeeRequest.setDistrict(addressDto.getDistrict().getFullName());
            shippingFeeRequest.setPickDistrict(Const.PICK_ADDRESS_DISTRICT);
            shippingFeeRequest.setPickProvince(Const.PICK_ADDRESS_CITY);
            shippingFeeRequest.setWeight("2000");
            BigDecimal fee = commonClient.calculateShippingFee(shippingFeeRequest);
            orderDocument.setShippingFee(fee);
            totalAmount = totalAmount.add(fee);
        }
        orderDocument.setCreatedAt(ZonedDateTime.now());
        orderDocument.setStreet(order.getStreet());
//        orderDocument.setAddress(o);
        orderDocument.setReceiverName(order.getCustomerName());
        orderDocument.setReceiverPhone(order.getCustomerPhone());
        orderDocument.setAccountId(currentUser.getAccountId());
        orderDocument.setPaymentType(order.isPaymentMethod());
        orderDocument.setOrderCode(getSaltString());
        orderDocument.setStatus(Const.OrderStatus.CREATED);
        orderDocument.setNote(order.getNote());
        orderDocument.setShippingCompany("GIAO HÀNG TIẾT KIỆM");
        OrderDocument savedOrder = orderRepository.save(orderDocument);

        List<OrderItemDocument> orderItemDocuments = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            BookInventory inventory = bookInventories.stream()
                    .filter(book -> book.getId().equals(orderItem.getBookInventoryId()))
                    .findFirst()
                    .orElseThrow(() -> new BizException("Book inventory ID " + orderItem.getBookInventoryId() + " not found"));
            totalAmount = totalAmount.add(inventory.getPrice());
            // không trừ số lượng ở đây
//            inventory.setQuantity(inventory.getQuantity() - orderItem.getQuantity());
//            bookInventoryRepository.save(inventory);

            OrderItemDocument orderItemDocument = new OrderItemDocument();
            orderItemDocument.setOrderId(savedOrder.getId());
            orderItemDocument.setBookInventoryId(inventory.getId());
            orderItemDocument.setQuantity(orderItem.getQuantity());
            orderItemDocuments.add(orderItemDocument);
        }
        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);
        orderItemRepository.saveAll(orderItemDocuments);
        if (order.isPaymentMethod()) {
            // Redirect to payment service
//            BigDecimal total = calculateTotal(orderItemDocuments, bookInventories);
//            return paymentService.createOrder(request, total, savedOrder.getId(), returnUrl);
        } else {
            mailService.sendEmailOrderDetail(order.getEmail(), savedOrder, orderItemDocuments);
        }

        return savedOrder;
    }

    public String retryPayment(String id, String returnUrl, HttpServletRequest request) throws BizException {
        BigDecimal total = new BigDecimal(0);
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(() -> new BizException("orderId is invalid"));
        PaymentDocument paymentDocument = paymentService.getPaymentByOrderId(id);
//        if(paymentDocument == null && orderDocument.isPaymentType()){
//            for (BookRealityDocument b:orderDocument.getItems()) {
//                total.add(new BigDecimal(b.getPrice()));
//            }
//            if(total.compareTo(Const.AMOUNT_CAN_FREESHIP) < 0){
//                total.add(Const.SHIPPING_FEE);
//            }
//            String url = paymentService.createOrder(request,total,id,returnUrl);
//            return url;
//        }
        return "create link payment success!";
    }

    public Page<OrderDto> getOrders(String customerPhone, String id, String paymentType, String status, String startAt, String endAt,
                                    Integer page, Integer size) throws BizException {
        ZonedDateTime created = null;
        ZonedDateTime updated = null;
        if (startAt != null && endAt!=null && !startAt.isBlank() && !endAt.isBlank()) {
            created = ZonedDateTime.parse(startAt);
            updated = ZonedDateTime.parse(endAt);
        }
        Page<OrderDocument> orderDocuments = orderRepository.getOrders(customerPhone, id, paymentType, status, created, updated, page, size);
        List<OrderDto> orderDtos = orderDocuments.getContent().stream()
                .map(order -> {
                    OrderDto orderDto = new OrderDto();
                    orderDto.setId(order.getId());
                    orderDto.setAddress(buildAddress(order));
                    orderDto.setCustomerName(order.getReceiverName());
                    orderDto.setCustomerPhone(order.getReceiverPhone());
                    orderDto.setCreatedAt(order.getCreatedAt());
                    orderDto.setUpdatedAt(order.getUpdatedAt());
                    orderDto.setStatus(order.getStatus());
                    orderDto.setOrderCode(order.getOrderCode());
                    try {
                        orderDto.setAccount(accountService.getAccountDetail(order.getAccountId()));
                    } catch (BizException e) {
                        throw new RuntimeException(e);
                    }
                    orderDto.setNote(order.getNote());
                    orderDto.setPaymentType(order.isPaymentType());
                    orderDto.setShippingFee(order.getShippingFee());
                    orderDto.setTotalAmount(order.getTotalAmount());
                    orderDto.setTransactionId(order.getTransactionId());
                    orderDto.setOrderItems(fetchOrderItems(order.getId()));
                    return orderDto;
                }).collect(Collectors.toList());
        return new PageImpl<>(orderDtos, orderDocuments.getPageable(), orderDocuments.getTotalElements());
    }

    private List<OrderItemDocument> fetchOrderItems(String orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    private String buildAddress(OrderDocument order) {
        return String.join(", ",
                order.getStreet(),
                order.getWard().getFullName(),
                order.getDistrict().getFullName(),
                order.getProvince().getFullName());
    }

    public OrderDto findById(String id) throws BizException {
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(() -> new BizException("orderId is invalid"));
//        List<BookRealityDocument> list= bookRealityRepository.findAllById(orderDocument.getItems().
//                stream().map(BookRealityDocument::getId).collect(Collectors.toList()));
//        List<BookRealityDto> bookRealityDtoList = new ArrayList<>();
//        for(BookRealityDocument bookRealityDocument: list){
//            BookRealityDto bookDto = bookRealityService.findEntityById(bookRealityDocument.getId());
//            bookRealityDtoList.add(bookDto);
//        }
        PaymentDocument paymentDocument = paymentService.getPaymentByOrderId(id);
        boolean isPaid = false;
        if (paymentDocument != null) {
            isPaid = true;
        }
        return OrderDto.builder()
                .id(orderDocument.getId())
                .address(orderDocument.getStreet() + "," + orderDocument.getWard().getFullName() + "," + orderDocument.getDistrict().getFullName() + "," + orderDocument.getProvince().getFullName())
//                .email(orderDocument.getEmail())
//                .customerName(orderDocument.getCustomerName())
//                .customerPhone(orderDocument.getCustomerPhone())
                .status(orderDocument.getStatus())
                .createdAt(orderDocument.getCreatedAt())
//                .books(bookRealityDtoList)
                .isPaid(isPaid)
                .paymentType(orderDocument.isPaymentType())
                .note(orderDocument.getNote())
//                .orderId(orderDocument.getOrderId())
                .shippingCode(orderDocument.getShippingCode())
                .shippingCompany(orderDocument.getShippingCompany())
                .build();
    }

    @Transactional
    public void updateOrder(String id, UpdateOrderModel order) throws BizException {
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(() -> new BizException("orderId is invalid"));
//        orderDocument.setAddress(order.getAddress());
//        orderDocument.setEmail(order.getEmail());
//        orderDocument.setCustomerName(order.getCustomerName());
//        orderDocument.setCustomerPhone(order.getCustomerPhone());
        orderDocument.setShippingCode(order.getShippingCode());
        orderDocument.setNote(order.getNote());
        orderDocument.setShippingCompany(order.getShippingCompany());
        if (!orderDocument.getStatus().equalsIgnoreCase(order.getStatus())) {
            switch (order.getStatus()) {
                case Const.OrderStatus.CANCEL:
                    if (orderDocument.getStatus().equals(Const.OrderStatus.CREATED)) {
                        handleOrderStatus(orderDocument, Const.OrderStatus.CREATED, order.getStatus(), "can't cancel order now!");
//                        orderDocument.getItems().forEach(bookRealityDocument -> {
//                            bookRealityDocument.setStatus(Const.BookRealityStatus.AVAILABLE.toString());
//                            bookRealityRepository.save(bookRealityDocument);
//                        });
                    }
                    break;
                case Const.OrderStatus.READY_TO_PACKAGE:
                    handleOrderStatus(orderDocument, Const.OrderStatus.CREATED, order.getStatus(), "can't confirm order now!");
                    break;
                case Const.OrderStatus.READY_TO_SHIP:
                    handleOrderStatus(orderDocument, Const.OrderStatus.READY_TO_PACKAGE, order.getStatus(), "can't set status ready_to_ship");
                case Const.OrderStatus.SHIPPING:
                    handleOrderStatus(orderDocument, Const.OrderStatus.READY_TO_SHIP, order.getStatus(), "can't change order status to shipping now!");
                    break;
                case Const.OrderStatus.DONE:
                    handleOrderStatus(orderDocument, Const.OrderStatus.SHIPPING, order.getStatus(), "can't change order status to done now!");
                    break;
                default:
                    throw new BizException("Can't update order status!");
            }
        }
        orderDocument.setUpdatedAt(ZonedDateTime.now());
        orderRepository.save(orderDocument);
    }

    private void handleOrderStatus(OrderDocument orderDocument, String currentStatus, String newStatus, String errorMessage) throws BizException {
        if (orderDocument.getStatus().equals(currentStatus)) {
            orderDocument.setStatus(newStatus);
        } else {
            throw new BizException(errorMessage);
        }
    }

}
