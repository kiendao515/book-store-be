package box.bookstorebe.service.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.order.BookOrder;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.UpdateOrderModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.service.book.BookRealityService;
import box.bookstorebe.service.book.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookRealityRepository bookRealityRepository;
    private final BookService bookService;
    private final BookRealityService bookRealityService;
    @Transactional
    public void createOrder(CreateOrderModel order) throws BizException {
        OrderDocument orderDocument = new OrderDocument();
        List<String> bookIds = order.getBooks().stream()
                .map(BookOrder::getBookId)
                .collect(Collectors.toList());

        List<BookRealityDocument> listBook = bookRealityRepository.findAllById(bookIds);
        if (listBook.size() < bookIds.size()) {
            throw new BizException("bookId is invalid!");
        }
        List<BookRealityDocument> availableBooks = listBook.stream()
                .filter(book -> Const.BookRealityStatus.AVAILABLE.toString().equals(book.getStatus()))
                .collect(Collectors.toList());
        if (availableBooks.size() < listBook.size()) {
            throw new BizException("Book is unavailable!");
        }

        orderDocument.setCreatedAt(ZonedDateTime.now());
        orderDocument.setAddress(order.getAddress());
        orderDocument.setCustomerName(order.getCustomerName());
        orderDocument.setCustomerPhone(order.getCustomerPhone());
        orderDocument.setEmail(order.getEmail());
        orderDocument.setItems(availableBooks);
        orderDocument.setStatus(Const.OrderStatus.CREATED);
        for(BookRealityDocument bookRealityDocument:availableBooks){
            bookRealityDocument.setStatus(Const.BookRealityStatus.UNAVAILABLE.toString());
            bookRealityRepository.save(bookRealityDocument);
        }
        orderRepository.save(orderDocument);
    }

    public Page<OrderDto> getOrders(Integer page, Integer size) throws BizException {
        Page<OrderDocument> orderDocuments =orderRepository.getOrders(page,size);
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
            List<BookRealityDto> bookDtos = new ArrayList<>();
            for(BookRealityDocument book: order.getItems()){
                BookRealityDto bookDto = new BookRealityDto();
                bookDto.setId(book.getId());
                bookDto.setType(book.getType());
                bookDto.setStatus(book.getStatus());
                bookDto.setPrice(book.getPrice());
                bookDto.setImageLinks(book.getImageIds());
                bookDto.setCreatedAt(book.getCreatedAt());
                BookDto b = bookService.findById(book.getBookId());
                bookDto.setBookDetail(b);
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
                stream().map(bookRealityDocument -> bookRealityDocument.getId()).collect(Collectors.toList()));
        List<BookRealityDto> bookRealityDtoList = new ArrayList<>();
        for(BookRealityDocument bookRealityDocument: list){
            BookRealityDto bookDto = bookRealityService.findById(bookRealityDocument.getId());
            bookRealityDtoList.add(bookDto);
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
                .build();
    }

    @Transactional
    public void updateOrder(String id, UpdateOrderModel order) throws BizException {
        OrderDocument orderDocument = orderRepository.findById(id).orElseThrow(()-> new BizException("orderId is invalid"));
        orderDocument.setAddress(order.getAddress());
        orderDocument.setEmail(order.getEmail());
        orderDocument.setCustomerName(order.getCustomerName());
        orderDocument.setCustomerPhone(order.getCustomerPhone());
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
            case Const.OrderStatus.CONFIRM:
                handleOrderStatus(orderDocument, Const.OrderStatus.CREATED, order.getStatus(), "can't confirm order now!");
                break;
            case Const.OrderStatus.SHIPPING:
                handleOrderStatus(orderDocument, Const.OrderStatus.CONFIRM, order.getStatus(), "can't change order status to shipping now!");
                break;
            case Const.OrderStatus.DONE:
                handleOrderStatus(orderDocument, Const.OrderStatus.SHIPPING, order.getStatus(), "can't change order status to done now!");
                break;
            default:
                throw new BizException("status order is invalid");
        }
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
