package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.book.CollectionDocument;
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
public class CollectionExRepositoryImpl implements CollectionExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<CollectionDocument> getCollections(String name, Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();
        if (name != null) {
            criteria = criteria.and("name").is(name);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), CollectionDocument.class);

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

        AggregationResults<CollectionDocument> result = mongoTemplate.aggregate(aggregation, "collections", CollectionDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
