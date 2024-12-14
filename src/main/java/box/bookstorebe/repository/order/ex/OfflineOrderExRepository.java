package box.bookstorebe.repository.order.ex;

import box.bookstorebe.document.order.OfflineOrderDocument;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;


public interface OfflineOrderExRepository {
    Page<OfflineOrderDocument> getOfflineOrders(ZonedDateTime startAt, ZonedDateTime endAt, Integer page, Integer size);
}
