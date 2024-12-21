package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import ch.qos.logback.core.util.StringUtil;
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
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class BookExRepositoryImpl implements BookExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<BookDocument> getBooks(String name, String authorName, String categoryId, String storeId, String collectionId, ZonedDateTime startAt, ZonedDateTime endAt, List<String> bookSearchIds, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();
        if (!StringUtil.isNullOrEmpty(name)) {
            criteria = criteria.and("name").regex(".*" + name + ".*", "i");
        }

        if(!StringUtil.isNullOrEmpty(authorName)) {
            criteria = criteria.and("author_name").regex(".*" + authorName + ".*", "i");
        }

        if (!StringUtil.isNullOrEmpty(categoryId)) {
            criteria = criteria.and("category_id").is(categoryId);
        }

        if (!StringUtil.isNullOrEmpty(storeId)) {
            criteria = criteria.and("store_id").is(storeId);
        }
        if (!StringUtil.isNullOrEmpty(collectionId)) {
            criteria = criteria.and("collection_id").is(collectionId);
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
        if (bookSearchIds != null) {
            criteria = criteria.and("_id").in(bookSearchIds);
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
