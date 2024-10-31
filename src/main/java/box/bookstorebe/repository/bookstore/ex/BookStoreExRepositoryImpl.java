package box.bookstorebe.repository.bookstore.ex;

import box.bookstorebe.document.bookstore.StoreDocument;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class BookStoreExRepositoryImpl implements BookStoreExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<StoreDocument> getBookStores(String name, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();
        criteria = criteria.and("deleted_at").is(null);
        if (name != null) {
            criteria = criteria.and("name").regex(".*" + name + ".*");
        }

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, 10);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), StoreDocument.class);

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

        AggregationResults<StoreDocument> result = mongoTemplate.aggregate(aggregation, "stores", StoreDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
