package box.bookstorebe.repository.booksearchrequest.ex;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
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
public class BookSearchRequestExRepositoryImpl implements BookSearchRequestExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<BookSearchRequestDocument> getBookSearchRequests(String userId, String fullName, String phoneNumber, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();
        if (userId != null) {
            criteria = criteria.and("user_id").is(userId);
        }

        if (fullName != null) {
            criteria = criteria.and("full_name").is(fullName);
        }

        if (phoneNumber != null) {
            criteria = criteria.and("phone_number").is(phoneNumber);
        }

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, 10);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), BookSearchRequestDocument.class);

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

        AggregationResults<BookSearchRequestDocument> result = mongoTemplate.aggregate(aggregation, "book_search_requests", BookSearchRequestDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
