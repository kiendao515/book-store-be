package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookRelatedPersonDocument;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
public class BookRelatedPersonExRepositoryImpl implements BookRelatedPersonExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<BookRelatedPersonDocument> getBookRelatedPersons(String name, String type, Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();
        if (name != null) {
            criteria = criteria.and("name").regex(".*" + name + ".*");
        }

        if (type != null) {
            criteria = criteria.and("type").is(type);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), BookRelatedPersonDocument.class);
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

        AggregationResults<BookRelatedPersonDocument> result = mongoTemplate.aggregate(aggregation, "book_related_people", BookRelatedPersonDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
