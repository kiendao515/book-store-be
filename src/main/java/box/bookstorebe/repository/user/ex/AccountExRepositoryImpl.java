package box.bookstorebe.repository.user.ex;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.dto.account.AccountDto;
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
public class AccountExRepositoryImpl implements AccountExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<AccountDto> getUsers(String email, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Criteria criteria = new Criteria();
        if (email != null) {
            criteria = criteria.and("email").is(email);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), AccountDocument.class);

        AggregationOperation matchOperations = match(criteria);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("_id")));

        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("id")
                .and("first_name").as("first_name")
                .and("last_name").as("last_name")
                .and("email").as("email")
                .and("role").as("role");

        Aggregation aggregation = newAggregation(
                matchOperations,
                sortOperation,
                skipOperation,
                limitOperation,
                projectionOperation
        );

        AggregationResults<AccountDto> result = mongoTemplate.aggregate(aggregation, "accounts", AccountDto.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
