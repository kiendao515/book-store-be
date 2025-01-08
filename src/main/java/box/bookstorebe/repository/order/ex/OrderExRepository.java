package box.bookstorebe.repository.order.ex;

import box.bookstorebe.document.order.OrderDocument;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;


public interface OrderExRepository {
    Page<OrderDocument> getOrders(Integer type, String accountId,String search, String id, String paymentType, String status, ZonedDateTime startAt,
                                  ZonedDateTime endAt,Integer page, Integer size);
}
