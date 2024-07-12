package box.bookstorebe.service.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.order.BookOrder;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
        orderRepository.save(orderDocument);
    }

    public Page<OrderDto> getOrders(Integer page, Integer size){
        Page<OrderDocument> orderDocuments =orderRepository.getOrders(page,size);
        List<OrderDto> rs = new ArrayList<>();
        for (OrderDocument order:orderDocuments) {
            OrderDto orderDto = new OrderDto();
            orderDto.setAddress(order.getAddress());
            orderDto.setCustomerName(order.getCustomerName());
            orderDto.setCustomerPhone(order.getCustomerPhone());
            orderDto.setCreatedAt(order.getCreatedAt());
            orderDto.setStatus(order.getStatus());
            List<BookDto> bookDtos = new ArrayList<>();
            for(BookRealityDocument book: order.getItems()){
                BookDto bookDto = new BookDto();
                bookDto.setId(book.getId());
//                bookDto.set
//                bookDtos.add()
            }
        }
    }

}
