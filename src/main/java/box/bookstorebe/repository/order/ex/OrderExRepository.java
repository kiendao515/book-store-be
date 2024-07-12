package box.bookstorebe.repository.order.ex;

import box.bookstorebe.document.order.OrderDocument;
import org.springframework.data.domain.Page;


public interface OrderExRepository {
    Page<OrderDocument> getOrders(Integer page, Integer size);
}
