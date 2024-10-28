package box.bookstorebe.repository.customer.ex;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.customer.CustomerInfoDto;
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
public class CustomerExRepositoryImpl implements CustomerExRepository{
    private final MongoTemplate mongoTemplate;
    @Override
    public Page<CustomerDocument> getCustomers(String name, String phone, String address,Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();
        criteria = criteria.and("deleted_at").isNull();
        if (name != null) {
            criteria = criteria.and("name").is(name);
        }
        if(phone != null) {
            criteria = criteria.and("phone_number").regex(".*" + phone + ".*");
        }
        if(address != null) {
            criteria = criteria.and("address").regex(".*" + address + ".*");
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), AccountDocument.class);
        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, (int) totalElement);
        } else {
            pageRequest = PageRequest.of(page, size);
        }


        AggregationOperation matchOperations = match(criteria);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("_id")));

        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("id")
                .and("account_id").as("account_id")
                .and("name").as("name")
                .and("phone_number").as("phone_number")
                .and("address").as("address");

        Aggregation aggregation = newAggregation(
                matchOperations,
                sortOperation,
                skipOperation,
                limitOperation,
                projectionOperation
        );

        AggregationResults<CustomerDocument> result = mongoTemplate.aggregate(aggregation, "customers", CustomerDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
