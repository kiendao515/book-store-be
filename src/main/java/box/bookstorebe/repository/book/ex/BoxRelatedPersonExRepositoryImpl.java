package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
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

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class BoxRelatedPersonExRepositoryImpl implements BookRelatedPersonExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<BookRelatedPersonDocument> getBookRelatedPersons(String name, String type, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Criteria criteria = new Criteria();
        if (name != null) {
            criteria = criteria.and("name").regex(".*" + name + ".*");
        }

        if (type != null) {
            criteria = criteria.and("type").is(type);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), BookRelatedPersonDocument.class);

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

        AggregationResults<BookRelatedPersonDocument> result = mongoTemplate.aggregate(aggregation, "book_related_persons", BookRelatedPersonDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
