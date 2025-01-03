package box.bookstorebe.service.order;

import box.bookstorebe.client.CommonClient;
import box.bookstorebe.common.Const;
import box.bookstorebe.configuration.security.RequestScope;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.document.payment.PaymentDocument;
import box.bookstorebe.dto.common.AddressDto;
import box.bookstorebe.dto.order.CombinedOrderDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.dto.order.OrderItemDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.OrderItem;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.model.order.UpdateOrderModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.account.AccountService;
import box.bookstorebe.service.common.AddressService;
import box.bookstorebe.service.common.MailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService extends BaseService {
    private final OrderRepository orderRepository;
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;
    private final PaymentService paymentService;
    private final MailService mailService;
    private final OrderItemRepository orderItemRepository;
    private final AddressService addressService;
    private final CommonClient commonClient;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private SimpMessagingTemplate messagingTemplate;

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

        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid account id"));
        CustomerDocument customer = customerRepository.findByAccountId(user.getId());
        if (customer == null) {
            throw new BizException("Invalid customer");
        }

        if (order.getDiscountPoint() == null || order.getDiscountPoint().compareTo(BigDecimal.valueOf(customer.getPoint())) > 0) {
            throw new BizException("Invalid discount point");
        }

        BigDecimal totalAmount = new BigDecimal(0);
        List<String> bookInventoryIds = order.getOrderItems().stream()
                .map(OrderItem::getBookInventoryId).collect(Collectors.toList());
        List<BookInventory> bookInventories = bookInventoryRepository.findAllByIdIn(bookInventoryIds);
        if (bookInventories.size() != bookInventoryIds.size()) {
            throw new BizException("Some books are not available in inventory.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            BookInventory inventory = bookInventories.stream()
                    .filter(book -> book.getId().equals(orderItem.getBookInventoryId()))
                    .findFirst()
                    .orElseThrow(() -> new BizException("Book inventory ID " + orderItem.getBookInventoryId() + " not found"));

            List<BookInventory> relatedInventories = bookInventoryRepository.findAllByRelatedBookId(inventory.getId());

            if (inventory.getRelatedBookId() != null) {
                relatedInventories.add(inventory);
                relatedInventories.add(0, inventory);
            }

            int totalAvailableQuantityForAnotherStore = !relatedInventories.isEmpty() ? relatedInventories.stream()
                    .mapToInt(BookInventory::getQuantity)
                    .sum() : inventory.getQuantity();
            int totalAvailable = totalAvailableQuantityForAnotherStore + inventory.getQuantity();

            if (totalAvailable < orderItem.getQuantity()) {
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

            List<BookInventory> relatedInventories = bookInventoryRepository.findAllByRelatedBookId(inventory.getId());

            if (inventory.getRelatedBookId() != null) {
                relatedInventories.add(inventory);
            } else {
                relatedInventories.add(0, inventory);
            }

            int remainingQuantity = orderItem.getQuantity();

            for (BookInventory relatedInventory : relatedInventories) {
                if (remainingQuantity <= 0) break;

                if (relatedInventory.getQuantity() > 0) {
                    int quantityToSubtract = Math.min(remainingQuantity, relatedInventory.getQuantity());
                    relatedInventory.setQuantity(relatedInventory.getQuantity() - quantityToSubtract);
                    remainingQuantity -= quantityToSubtract;

                    bookInventoryRepository.save(relatedInventory);
                    OrderItemDocument orderItemDocument = new OrderItemDocument();
                    orderItemDocument.setOrderId(savedOrder.getId());
                    orderItemDocument.setBookInventoryId(relatedInventory.getId());
                    orderItemDocument.setQuantity(quantityToSubtract);
                    orderItemDocuments.add(orderItemDocument);
                    totalAmount = totalAmount.add(relatedInventory.getPrice().multiply(BigDecimal.valueOf(quantityToSubtract)));
                }
            }
        }

        if (order.getDiscountPoint() != null) {
            totalAmount = totalAmount.subtract(order.getDiscountPoint());
            savedOrder.setDiscountPoint(order.getDiscountPoint());
            customer.setPoint(customer.getPoint() - order.getDiscountPoint().intValue());
            customerRepository.save(customer);
        }

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        orderItemRepository.saveAll(orderItemDocuments);
        messagingTemplate.convertAndSend("/topic/order", savedOrder);

        if (order.isPaymentMethod()) {
            return paymentService.createOrder(request, totalAmount, savedOrder.getOrderCode(), returnUrl);
        } else {
            mailService.sendEmailOrderDetail(order.getEmail(), savedOrder, orderItemDocuments);
        }

        return savedOrder;
    }

    @Transactional
    public Object createOrderV2(HttpServletRequest request, CreateOrderModel order, String returnUrl) throws BizException, MessagingException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }

        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid account id"));
        CustomerDocument customer = customerRepository.findByAccountId(user.getId());
        if (customer == null) {
            throw new BizException("Invalid customer");
        }

        if (order.getDiscountPoint() == null || order.getDiscountPoint().compareTo(BigDecimal.valueOf(customer.getPoint())) > 0) {
            throw new BizException("Invalid discount point");
        }

        BigDecimal totalAmount = new BigDecimal(0);
        List<String> bookInventoryIds = order.getOrderItems().stream()
                .map(OrderItem::getBookInventoryId).collect(Collectors.toList());
        List<BookInventory> bookInventories = bookInventoryRepository.findAllByIdIn(bookInventoryIds);
        if (bookInventories.size() != bookInventoryIds.size()) {
            throw new BizException("Some books are not available in inventory.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            BookInventory inventory = bookInventories.stream()
                    .filter(book -> book.getId().equals(orderItem.getBookInventoryId()))
                    .findFirst()
                    .orElseThrow(() -> new BizException("Book inventory ID " + orderItem.getBookInventoryId() + " not found"));

            List<BookInventory> relatedInventories = bookInventoryRepository.findAllByRelatedBookId(inventory.getId());

            if (inventory.getRelatedBookId() != null) {
                relatedInventories.add(inventory);
                relatedInventories.add(0, inventory);
            }

            int totalAvailableQuantityForAnotherStore = !relatedInventories.isEmpty() ? relatedInventories.stream()
                    .mapToInt(BookInventory::getQuantity)
                    .sum() : inventory.getQuantity();
            int totalAvailable = totalAvailableQuantityForAnotherStore + inventory.getQuantity();

            if (totalAvailable < orderItem.getQuantity()) {
                throw new BizException("Not enough quantity for book inventory ID " + orderItem.getBookInventoryId());
            }
        }

        OrderDocument orderDocument = new OrderDocument();

        orderDocument.setCreatedAt(ZonedDateTime.now());
        orderDocument.setStreet(order.getStreet());
        orderDocument.setReceiverName(order.getCustomerName());
        orderDocument.setReceiverPhone(order.getCustomerPhone());
        orderDocument.setAccountId(currentUser.getAccountId());
        orderDocument.setPaymentType(order.isPaymentMethod());
        orderDocument.setOrderCode(getSaltString());
        orderDocument.setStatus(Const.OrderStatus.CREATED);
        orderDocument.setNote(order.getNote());
        orderDocument.setShippingCompany("GIAO HÀNG TIẾT KIỆM");
        OrderDocument savedOrder = orderRepository.save(orderDocument);
        if (order.getDistrictCode() != null && order.getWardCode() != null && order.getProvinceCode() != null) {
            AddressDto addressDto = addressService.getAddress(order.getProvinceCode(), order.getDistrictCode(), order.getWardCode());
            orderDocument.setProvince(addressDto.getProvince());
            orderDocument.setDistrict(addressDto.getDistrict());
            orderDocument.setWard(addressDto.getWard());
            // cho gom don
            if (order.isPaymentMethod() && order.getCombinedOrder() == 1) {
                if (!order.getRelatedOrderId().isEmpty()) {
                    OrderDocument rootOrder = orderRepository.findByOrderCode(order.getRelatedOrderId());
                    if(rootOrder == null) {
                        return new BizException("Bạn chưa chọn đơn gom gốc");
                    }
                    orderDocument.setRelatedOrderId(rootOrder.getOrderCode());
                } else {
                    orderDocument.setRelatedOrderId(savedOrder.getOrderCode());
                }
            } else if (order.isPaymentMethod() && order.getCombinedOrder() == 2) {
                // da gom xong va thanh toan
                OrderDocument rootOrder = orderRepository.findByOrderCode(order.getRelatedOrderId());
                orderDocument.setRelatedOrderId(rootOrder.getOrderCode());
            }
            orderDocument.setShippingStatus(order.getCombinedOrder());
            BigDecimal fee = calculateCombinedOrderFee(order);
            orderDocument.setShippingFee(fee);
            totalAmount = totalAmount.add(fee);
        }
        List<OrderItemDocument> orderItemDocuments = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            BookInventory inventory = bookInventories.stream()
                    .filter(book -> book.getId().equals(orderItem.getBookInventoryId()))
                    .findFirst()
                    .orElseThrow(() -> new BizException("Book inventory ID " + orderItem.getBookInventoryId() + " not found"));

            List<BookInventory> relatedInventories = bookInventoryRepository.findAllByRelatedBookId(inventory.getId());

            if (inventory.getRelatedBookId() != null) {
                relatedInventories.add(inventory);
            } else {
                relatedInventories.add(0, inventory);
            }

            int remainingQuantity = orderItem.getQuantity();

            for (BookInventory relatedInventory : relatedInventories) {
                if (remainingQuantity <= 0) break;

                if (relatedInventory.getQuantity() > 0) {
                    int quantityToSubtract = Math.min(remainingQuantity, relatedInventory.getQuantity());
                    relatedInventory.setQuantity(relatedInventory.getQuantity() - quantityToSubtract);
                    remainingQuantity -= quantityToSubtract;

                    bookInventoryRepository.save(relatedInventory);
                    OrderItemDocument orderItemDocument = new OrderItemDocument();
                    orderItemDocument.setOrderId(savedOrder.getId());
                    orderItemDocument.setBookInventoryId(relatedInventory.getId());
                    orderItemDocument.setQuantity(quantityToSubtract);
                    orderItemDocuments.add(orderItemDocument);
                    totalAmount = totalAmount.add(relatedInventory.getPrice().multiply(BigDecimal.valueOf(quantityToSubtract)));
                }
            }
        }

        if (order.getDiscountPoint() != null) {
            totalAmount = totalAmount.subtract(order.getDiscountPoint());
            savedOrder.setDiscountPoint(order.getDiscountPoint());
            customer.setPoint(customer.getPoint() - order.getDiscountPoint().intValue());
            customerRepository.save(customer);
        }

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        orderItemRepository.saveAll(orderItemDocuments);
        messagingTemplate.convertAndSend("/topic/order", savedOrder);

        if (order.isPaymentMethod()) {
            return paymentService.createOrder(request, totalAmount, savedOrder.getOrderCode(), returnUrl);
        } else {
            mailService.sendEmailOrderDetail(order.getEmail(), savedOrder, orderItemDocuments);
        }

        return savedOrder;
    }

    public List<CombinedOrderDto> getListCombinedOrder() throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }

        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid account id"));
        CustomerDocument customer = customerRepository.findByAccountId(user.getId());
        if (customer == null) {
            throw new BizException("Invalid customer");
        }
        List<OrderDocument> orderDocuments = orderRepository.findAllByAccountIdAndStatusAndRelatedOrderIdIsNotNull(user.getId(), Const.OrderStatus.COMBINED_ORDER);
        List<String> uniqueRelatedOrderIds = orderDocuments.stream()
                .map(OrderDocument::getRelatedOrderId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<OrderDocument> combinedOrder = orderRepository.findAllByOrderCodeIn(uniqueRelatedOrderIds);
        List<CombinedOrderDto> combinedOrderDtos = new ArrayList<>();
        for (OrderDocument orderDocument : combinedOrder) {
            CombinedOrderDto combinedOrderDto = new CombinedOrderDto();
            combinedOrderDto.setOrderCode(orderDocument.getOrderCode());
            combinedOrderDto.setTotalAmount(orderDocument.getTotalAmount());
            combinedOrderDtos.add(combinedOrderDto);
        }
        return combinedOrderDtos;
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

    public Page<OrderDto> getOrders(Integer type, String customerPhone, String id, String paymentType, String status, String startAt, String endAt,
                                    Integer page, Integer size) throws BizException {
        ZonedDateTime created = null;
        ZonedDateTime updated = null;
        if (startAt != null && endAt != null && !startAt.isBlank() && !endAt.isBlank()) {
            created = ZonedDateTime.parse(startAt);
            updated = ZonedDateTime.parse(endAt);
        }
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }

        AccountDocument account = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid account id"));
        String accountId = null;
        if (account.getRole().equals(Role.USER)) {
            accountId = account.getId();
        }
        Page<OrderDocument> orderDocuments = orderRepository.getOrders(type,accountId, customerPhone, id, paymentType, status, created, updated, page, size);
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
                    orderDto.setDiscountPoint(order.getDiscountPoint());
                    orderDto.setPaid(order.getTransactionId() != null);
                    orderDto.setRelatedOrderId(order.getRelatedOrderId());
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
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(() -> new BizException("invalid order id"));
        boolean isPaid = false;
        if (orderDocument.getTransactionId() != null) {
            isPaid = true;
        }
        AccountDocument accountDocument = accountRepository.findById(orderDocument.getAccountId()).orElseThrow(() -> new BizException("account id invalid"));
        List<OrderItemDocument> orderItemDocuments = orderItemRepository.findAllByOrderId(orderDocument.getId());
        List<String> bookInventoryIds = orderItemDocuments.stream()
                .map(OrderItemDocument::getBookInventoryId)
                .collect(Collectors.toList());

        Map<String, BookInventory> bookInventoryMap = bookInventoryRepository.findAllById(bookInventoryIds).stream()
                .collect(Collectors.toMap(BookInventory::getId, inventory -> inventory));

        List<String> bookIds = bookInventoryMap.values().stream()
                .map(BookInventory::getBookId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, BookDocument> bookMap = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(BookDocument::getId, book -> book));

        List<OrderItemDto> orderItems = orderItemDocuments.stream()
                .map(orderItem -> {
                    BookInventory bookInventory = bookInventoryMap.get(orderItem.getBookInventoryId());
                    BookDocument bookDocument = bookMap.get(bookInventory.getBookId());
                    return OrderItemDto.builder()
                            .bookName(bookDocument.getName())
                            .quantity(orderItem.getQuantity())
                            .price(bookInventory.getPrice())
                            .type(bookInventory.getType())
                            .build();
                })
                .collect(Collectors.toList());
        return OrderDto.builder()
                .id(orderDocument.getId())
                .address(orderDocument.getStreet() + "," + orderDocument.getWard().getFullName() + "," + orderDocument.getDistrict().getFullName() + "," + orderDocument.getProvince().getFullName())
                .email(accountDocument.getEmail())
                .customerName(orderDocument.getReceiverName())
                .customerPhone(orderDocument.getReceiverPhone())
                .status(orderDocument.getStatus())
                .createdAt(orderDocument.getCreatedAt())
                .orderItems(orderItems)
                .isPaid(isPaid)
                .paymentType(orderDocument.isPaymentType())
                .note(orderDocument.getNote())
                .orderCode(orderDocument.getOrderCode())
                .totalAmount(orderDocument.getTotalAmount())
                .shippingFee(orderDocument.getShippingFee())
                .shippingCode(orderDocument.getShippingCode())
                .discountPoint(orderDocument.getDiscountPoint())
                .shippingCompany(orderDocument.getShippingCompany())
                .build();
    }

    public OrderDto findByOrderCode(String code) throws BizException {
        OrderDocument orderDocument = orderRepository.findByOrderCode(code);
        if (orderDocument == null) {
            throw new BizException("order code is invalid");
        }
        boolean isPaid = false;
        if (orderDocument.getTransactionId() != null) {
            isPaid = true;
        }
        return OrderDto.builder()
                .id(orderDocument.getId())
                .address(orderDocument.getStreet() + "," + orderDocument.getWard().getFullName() + "," + orderDocument.getDistrict().getFullName() + "," + orderDocument.getProvince().getFullName())
                .status(orderDocument.getStatus())
                .createdAt(orderDocument.getCreatedAt())
                .discountPoint(orderDocument.getDiscountPoint())
                .isPaid(isPaid)
                .paymentType(orderDocument.isPaymentType())
                .note(orderDocument.getNote())
                .shippingCode(orderDocument.getShippingCode())
                .shippingCompany(orderDocument.getShippingCompany())
                .build();
    }

    @Transactional
    public void updateOrder(String id, UpdateOrderModel order) throws BizException {
        OrderDocument orderDocument = orderRepository.findByOrderCode(id);
        if (orderDocument == null) throw new BizException("order code is invalid");
        if (!orderDocument.getStatus().equalsIgnoreCase(order.getStatus())) {
            switch (order.getStatus()) {
                case Const.OrderStatus.CANCEL:
                    if (orderDocument.getStatus().equals(Const.OrderStatus.CREATED)) {
                        handleOrderStatus(orderDocument, Const.OrderStatus.CREATED, order.getStatus(), "can't cancel order now!");
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
                    saveSettleDetail(orderDocument);
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

    private void saveSettleDetail(OrderDocument orderDocument) {
        List<OrderItemDocument> listOrderItems = orderItemRepository.findAllByOrderId(orderDocument.getId());
        listOrderItems.forEach(orderItem -> {
            orderItem.setSettledStatus(0);
        });
        orderItemRepository.saveAll(listOrderItems);
    }
    public BigDecimal calculateCombinedOrderFee(CreateOrderModel order) throws BizException {
        if (order.getDistrictCode() != null && order.getWardCode() != null && order.getProvinceCode() != null) {
            ShippingFeeRequest shippingFeeRequest = new ShippingFeeRequest();
            AddressDto addressDto = addressService.getAddress(order.getProvinceCode(), order.getDistrictCode(), order.getWardCode());
            shippingFeeRequest.setProvince(addressDto.getProvince().getFullName());
            shippingFeeRequest.setDistrict(addressDto.getDistrict().getFullName());
            shippingFeeRequest.setPickDistrict(Const.PICK_ADDRESS_DISTRICT);
            shippingFeeRequest.setPickProvince(Const.PICK_ADDRESS_CITY);
            float totalWeight =0.0f;
            List<BookInventory> bookInventories = bookInventoryRepository.findAllByIdIn(order.getOrderItems().stream().map(OrderItem::getBookInventoryId).toList());
            List<String> bookIds = bookInventories.stream()
                    .map(BookInventory::getBookId)
                    .toList();


            List<BookDocument> books = bookRepository.findAllById(bookIds);
            for (OrderItem item : order.getOrderItems()) {
                BookInventory inventory = bookInventories.stream()
                        .filter(bi -> bi.getId().equals(item.getBookInventoryId()))
                        .findFirst()
                        .orElse(null);

                if (inventory != null) {
                    BookDocument book = books.stream()
                            .filter(b -> b.getId().equals(inventory.getBookId()))
                            .findFirst()
                            .orElse(null);

                    if (book != null) {
                        Long pages = book.getNumberOfPage();
                        int quantity = item.getQuantity();
                        totalWeight += pages * quantity;
                    }
                }
            }
            float relatedWeight=0.0f;
             relatedWeight = calculateWeight(order.getRelatedOrderId(), 2);
             totalWeight += relatedWeight;
            shippingFeeRequest.setWeight(Float.toString(totalWeight));

            return commonClient.calculateShippingFee(shippingFeeRequest);
        }else {
            throw new BizException("missing code");
        }
    }
    public Float calculateWeight(String orderId, Integer combined) {
        float totalWeight = 0.0f;

        if (combined == 2) {
            List<OrderDocument> orderDocuments = orderRepository.findAllByRelatedOrderId(orderId);
            List<String> ids = orderDocuments.stream()
                    .map(OrderDocument::getId)
                    .toList();

            List<OrderItemDocument> orderItemDocuments = orderItemRepository.findALlByOrderIdIn(ids);
            List<String> orderItemIds = orderItemDocuments.stream()
                    .map(OrderItemDocument::getBookInventoryId)
                    .toList();

            List<BookInventory> bookInventories = bookInventoryRepository.findAllByIdIn(orderItemIds);
            List<String> bookIds = bookInventories.stream()
                    .map(BookInventory::getBookId)
                    .toList();


            List<BookDocument> books = bookRepository.findAllById(bookIds);

            // Tính trọng lượng
            for (OrderItemDocument item : orderItemDocuments) {
                BookInventory inventory = bookInventories.stream()
                        .filter(bi -> bi.getId().equals(item.getBookInventoryId()))
                        .findFirst()
                        .orElse(null);

                if (inventory != null) {
                    BookDocument book = books.stream()
                            .filter(b -> b.getId().equals(inventory.getBookId()))
                            .findFirst()
                            .orElse(null);

                    if (book != null) {
                        Long pages = book.getNumberOfPage();
                        int quantity = item.getQuantity();
                        totalWeight += pages * quantity;
                    }
                }
            }
        } else {
            OrderDocument orderDocument = orderRepository.findByOrderCode(orderId);
            List<OrderItemDocument> orderItems = orderItemRepository.findAllByOrderId(orderDocument.getId());
            List<String> orderItemIds = orderItems.stream()
                    .map(OrderItemDocument::getId)
                    .toList();

            List<BookInventory> bookInventories = bookInventoryRepository.findAllByIdIn(orderItemIds);
            List<String> bookIds = bookInventories.stream()
                    .map(BookInventory::getBookId)
                    .toList();

            List<BookDocument> books = bookRepository.findAllById(bookIds);

            for (OrderItemDocument item : orderItems) {
                BookInventory inventory = bookInventories.stream()
                        .filter(bi -> bi.getId().equals(item.getBookInventoryId()))
                        .findFirst()
                        .orElse(null);

                if (inventory != null) {
                    BookDocument book = books.stream()
                            .filter(b -> b.getId().equals(inventory.getBookId()))
                            .findFirst()
                            .orElse(null);

                    if (book != null) {
                        Long pages = book.getNumberOfPage();
                        int quantity = item.getQuantity();
                        totalWeight += pages * quantity;
                    }
                }
            }
        }

        return totalWeight;
    }


}
