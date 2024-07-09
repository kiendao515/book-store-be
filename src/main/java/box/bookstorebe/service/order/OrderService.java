package box.bookstorebe.service.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.repository.book.BookRealityRepository;
import box.bookstorebe.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookRealityRepository bookRealityRepository;
    public void createOrder(CreateOrderModel order) throws BizException {
        OrderDocument orderDocument = new OrderDocument();
        // validate book
        List<String> validBookIds = bookRealityRepository.findAllById(order.getBooks()).stream().map(BookRealityDocument::getId).toList();
        System.out.println(validBookIds);

    }

}
