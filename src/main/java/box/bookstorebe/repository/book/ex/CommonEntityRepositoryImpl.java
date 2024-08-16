package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.common.CommonEntity;
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
public class CommonEntityRepositoryImpl implements CommonEntityExRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public Page<CommonEntity> getCommonEntity(String type, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Criteria criteria = new Criteria();
        if (type != null) {
            criteria = criteria.and("type").is(type);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), CommonEntity.class);

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

        AggregationResults<CommonEntity> result = mongoTemplate.aggregate(aggregation, "common_entity", CommonEntity.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
