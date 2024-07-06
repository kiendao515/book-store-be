package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.user.UserDto;
import box.bookstorebe.repository.user.ex.UserExRepository;
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
public class BoxExRepositoryImpl implements BookExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<BookDocument> getBooks(String name, List<String> categoryIds, List<String> collectionIds, List<String> relatedPersonIds, String storeId, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();
        if (name != null) {
            criteria = criteria.and("name").regex(".*" + name + ".*");
        }

        if (categoryIds != null) {
            criteria = criteria.and("category_id").in(categoryIds);
        }

        if (collectionIds != null) {
            criteria = criteria.and("collection_id").in(collectionIds);
        }

        if (storeId != null) {
            criteria = criteria.and("store_id").is(storeId);
        }

        if (relatedPersonIds != null) {
            criteria = criteria.and("related_person.related_person_id").in(relatedPersonIds);
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

        AggregationResults<BookDocument> result = mongoTemplate.aggregate(aggregation, "books", BookDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
