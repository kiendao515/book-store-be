package box.bookstorebe.repository.order.ex;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.order.OrderDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
@Repository
@AllArgsConstructor
public class OrderExRepositoryImpl implements OrderExRepository{
    private MongoTemplate mongoTemplate;
    @Override
    public Page<OrderDocument> getOrders(String customerPhone, String id, String paymentType, String status, ZonedDateTime startAt, ZonedDateTime endAt,
                                         Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();
        if (customerPhone != null) {
            criteria = criteria.and("customer_phone").regex(".*" + customerPhone + ".*");
        }

        if (id != null) {
            criteria = criteria.and("_id").is(id);
        }

        if (paymentType != null) {
            if(paymentType.equals("COD")){
                criteria = criteria.and("payment_type").is(false);
            }else if(paymentType.equals("CK")){
                criteria = criteria.and("payment_type").is(true);
            }
        }

        if (status!=null && !status.isEmpty()) {
            criteria = criteria.and("status").is(status);
        }

        if (startAt != null || endAt != null) {
            criteria = criteria.and("created_at");
            if (startAt != null) {
                criteria = criteria.gte(startAt);
            }

            if (endAt != null) {
                criteria = criteria.lte(endAt);
            }
        }
        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), OrderDocument.class);


        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, (int) totalElement);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        AggregationOperation matchOperations = match(criteria);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("_id")));
        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());

        Aggregation aggregation = newAggregation(
                matchOperations,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<OrderDocument> result = mongoTemplate.aggregate(aggregation, "orders", OrderDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
