package box.bookstorebe.repository.order.ex;

import box.bookstorebe.document.order.OfflineOrderDocument;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class OfflineOrderExRepositoryImpl implements OfflineOrderExRepository {
    private MongoTemplate mongoTemplate;

    @Override
    public Page<OfflineOrderDocument> getOfflineOrders(ZonedDateTime startAt, ZonedDateTime endAt, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();

        if (startAt != null || endAt != null) {
            criteria = criteria.and("created_at");
        }

        if (startAt != null) {
            criteria = criteria.gte(startAt);
        }

        if (endAt != null) {
            criteria = criteria.lte(endAt);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), OrderDocument.class);

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, totalElement > 0 ? (int) totalElement : 10);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        AggregationOperation matchOperation = match(criteria);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("_id")));

        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());


        Aggregation aggregation = newAggregation(
                matchOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<OfflineOrderDocument> result = mongoTemplate.aggregate(aggregation, "offline_orders", OfflineOrderDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
