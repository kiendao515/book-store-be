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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
@Repository
@AllArgsConstructor
public class OrderExRepositoryImpl implements OrderExRepository{
    private MongoTemplate mongoTemplate;
    @Override
    public Page<OrderDocument> getOrders(Integer page, Integer size) {
        PageRequest pageRequest;
        long count = mongoTemplate.count(new Query(), OrderDocument.class);
        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, (int) count);
        } else {
            pageRequest = PageRequest.of(page, size);
        }
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("created_at")));
        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());
        Aggregation aggregation = newAggregation(
                sortOperation,
                skipOperation,
                limitOperation
        );
        AggregationResults<OrderDocument> result = mongoTemplate.aggregate(aggregation, "orders", OrderDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest,count);
    }
}
