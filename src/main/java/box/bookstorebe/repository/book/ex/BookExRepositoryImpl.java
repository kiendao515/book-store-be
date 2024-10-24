package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
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
public class BookExRepositoryImpl implements BookExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<BookDocument> getBooks(String name, String categoryId, String storeId, ZonedDateTime startAt, ZonedDateTime endAt, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();
        if (name != null) {
            criteria = criteria.and("name").regex(".*" + name + ".*");
        }

        if (categoryId != null) {
            criteria = criteria.and("category_id").in(List.of(categoryId));
        }

        if (storeId != null) {
            criteria = criteria.and("store_id").is(storeId);
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

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), BookDocument.class);

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

        AggregationResults<BookDocument> result = mongoTemplate.aggregate(aggregation, "book_information", BookDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
