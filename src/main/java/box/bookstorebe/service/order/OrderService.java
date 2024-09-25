package box.bookstorebe.service.order;

import box.bookstorebe.api.order.OrderController;
import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.payment.PaymentDocument;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.BookOrder;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.UpdateOrderModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.service.book.BookRealityService;
import box.bookstorebe.service.book.BookService;
import box.bookstorebe.service.common.MailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookRealityRepository bookRealityRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookRealityService bookRealityService;
    private final PaymentService paymentService;
    private static final AtomicLong counter = new AtomicLong();
    private final MongoTemplate mongoTemplate;
    private final MailService mailService;

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
    public String createOrder(CreateOrderModel order,String returnUrl) throws BizException, MessagingException {
        OrderDocument orderDocument = new OrderDocument();
        List<String> bookIds = order.getBooks().stream()
                .map(BookOrder::getId)
                .collect(Collectors.toList());
        int total=0;

        List<BookDocument> listBook = bookRepository.findAllById(bookIds);
        Set<String> set = new HashSet<>(bookIds);
        List<String> distinctList = new ArrayList<>(set);
        List<BookRealityDocument> orderBooks = new ArrayList<>();
        if (listBook.size() < distinctList.size()) {
            throw new BizException("bookId is invalid!");
        }
        for (int i = 0; i <order.getBooks().size(); i++) {
            List<BookRealityDocument> bookRealityDocuments= bookRealityRepository.findAllByBookId(order.getBooks().get(i).getId());
            int finalI = i;
            if(bookRealityDocuments.stream().filter(bookRealityDocument ->
                    ( bookRealityDocument.getType().equals(order.getBooks().get(finalI).getType()) &&
                            bookRealityDocument.getStatus().equals(Const.BookRealityStatus.AVAILABLE.toString()))).count()
                    < order.getBooks().get(finalI).getQuantity()){
                throw new BizException("số lượng sách "+order.getBooks().get(finalI).getName()+
                        "có tình trạng " +order.getBooks().get(finalI).getType() +" không đủ!");
            }
            List<BookRealityDocument> availableBooks = bookRealityDocuments.stream().filter(bookRealityDocument ->
                    bookRealityDocument.getType().equals(order.getBooks().get(finalI).getType()) &&
                            bookRealityDocument.getStatus().equals(Const.BookRealityStatus.AVAILABLE.toString())).limit(order.getBooks().get(finalI).getQuantity()).toList();
            for(BookRealityDocument bookRealityDocument:availableBooks){
                bookRealityDocument.setStatus(Const.BookRealityStatus.UNAVAILABLE.toString());
                bookRealityRepository.save(bookRealityDocument);
                orderBooks.add(bookRealityDocument);
                total+=bookRealityDocument.getPrice();
            }

        }
        orderDocument.setCreatedAt(ZonedDateTime.now());
        orderDocument.setAddress(order.getAddress());
        orderDocument.setCustomerName(order.getCustomerName());
        orderDocument.setCustomerPhone(order.getCustomerPhone());
        orderDocument.setEmail(order.getEmail());
        orderDocument.setItems(orderBooks);
        orderDocument.setPaymentType(order.isPaymentMethod());
        orderDocument.setOrderId(getSaltString());
        orderDocument.setStatus(Const.OrderStatus.CREATED);
        orderDocument.setNote(order.getNote());
        if(total < 500000){
            total += Const.SHIPPING_FEE;
        }
        OrderDocument savedOrder= orderRepository.save(orderDocument);
        if(order.isPaymentMethod()){
            return paymentService.createOrder(total, savedOrder.getId(), returnUrl);
        }else{
            mailService.sendEmailOrderDetail(orderDocument.getEmail(),orderDocument);
        }
        return "order successfully!";
    }

    public String retryPayment(String id,String returnUrl) throws BizException {
        int total=0;
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(()-> new BizException("orderId is invalid"));
        PaymentDocument paymentDocument = paymentService.getPaymentByOrderId(id);
        if(paymentDocument == null && orderDocument.isPaymentType()){
            for (BookRealityDocument b:orderDocument.getItems()) {
                total += b.getPrice();
            }
            if(total < 500000){
                total += Const.SHIPPING_FEE;
            }
            String url = paymentService.createOrder(total,id,returnUrl);
            return url;
        }
        return "create link payment success!";
    }

    public Page<OrderDto> getOrders(String customerPhone,String id,String paymentType,String status,ZonedDateTime startAt,ZonedDateTime endAt ,
                                    Integer page, Integer size) throws BizException {
        Page<OrderDocument> orderDocuments =orderRepository.getOrders(customerPhone,id,paymentType,status,startAt,endAt,page,size);
        List<OrderDto> rs = new ArrayList<>();
        for (OrderDocument order:orderDocuments) {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setAddress(order.getAddress());
            orderDto.setCustomerName(order.getCustomerName());
            orderDto.setCustomerPhone(order.getCustomerPhone());
            orderDto.setCreatedAt(order.getCreatedAt());
            orderDto.setStatus(order.getStatus());
            orderDto.setEmail(order.getEmail());
            orderDto.setNote(order.getNote());
            orderDto.setPaymentType(order.isPaymentType());
            List<BookRealityDto> bookDtos = new ArrayList<>();
            for(BookRealityDocument book: order.getItems()){
                BookRealityDto bookDto = new BookRealityDto();
                bookDto.setId(book.getId());
                bookDto.setPrice(book.getPrice());
                bookDto.setCreatedAt(book.getCreatedAt());
                bookDto.setNumberOfPage(bookRepository.findById(book.getBookId()).get().getNumberOfPage());
                bookDtos.add(bookDto);
            }
            orderDto.setBooks(bookDtos);
            rs.add(orderDto);
        }
        return new PageImpl<>(rs, orderDocuments.getPageable(), orderDocuments.getTotalElements());
    }
    public OrderDto findById(String id) throws BizException {
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(()-> new BizException("orderId is invalid"));
        List<BookRealityDocument> list= bookRealityRepository.findAllById(orderDocument.getItems().
                stream().map(BookRealityDocument::getId).collect(Collectors.toList()));
        List<BookRealityDto> bookRealityDtoList = new ArrayList<>();
        for(BookRealityDocument bookRealityDocument: list){
            BookRealityDto bookDto = bookRealityService.findEntityById(bookRealityDocument.getId());
            bookRealityDtoList.add(bookDto);
        }
        PaymentDocument paymentDocument = paymentService.getPaymentByOrderId(id);
        boolean isPaid=false;
        if(paymentDocument != null){
            isPaid= true;
        }
        return OrderDto.builder()
                .id(orderDocument.getId())
                .address(orderDocument.getAddress())
                .email(orderDocument.getEmail())
                .customerName(orderDocument.getCustomerName())
                .customerPhone(orderDocument.getCustomerPhone())
                .status(orderDocument.getStatus())
                .createdAt(orderDocument.getCreatedAt())
                .books(bookRealityDtoList)
                .isPaid(isPaid)
                .paymentType(orderDocument.isPaymentType())
                .note(orderDocument.getNote())
                .orderId(orderDocument.getOrderId())
                .shippingCode(orderDocument.getShippingCode())
                .shippingCompany(orderDocument.getShippingCompany())
                .build();
    }

    @Transactional
    public void updateOrder(String id, UpdateOrderModel order) throws BizException {
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(()-> new BizException("orderId is invalid"));
        orderDocument.setAddress(order.getAddress());
        orderDocument.setEmail(order.getEmail());
        orderDocument.setCustomerName(order.getCustomerName());
        orderDocument.setCustomerPhone(order.getCustomerPhone());
        orderDocument.setShippingCode(order.getShippingCode());
        orderDocument.setNote(order.getNote());
        orderDocument.setShippingCompany(order.getShippingCompany());
        if(!orderDocument.getStatus().equalsIgnoreCase(order.getStatus())){
            switch (order.getStatus()){
                case Const.OrderStatus.CANCEL:
                    if(orderDocument.getStatus().equals(Const.OrderStatus.CREATED)){
                        handleOrderStatus(orderDocument, Const.OrderStatus.CREATED, order.getStatus(), "can't cancel order now!");
                        orderDocument.getItems().forEach(bookRealityDocument -> {
                            bookRealityDocument.setStatus(Const.BookRealityStatus.AVAILABLE.toString());
                            bookRealityRepository.save(bookRealityDocument);
                        });
                    }
                    break;
                case Const.OrderStatus.READY_TO_PACKAGE:
                    handleOrderStatus(orderDocument, Const.OrderStatus.CREATED, order.getStatus(), "can't confirm order now!");
                    break;
                case Const.OrderStatus.READY_TO_SHIP:
                    handleOrderStatus(orderDocument,Const.OrderStatus.READY_TO_PACKAGE,order.getStatus(), "can't set status ready_to_ship");
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
