package box.bookstorebe.repository.customer.ex;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
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
public class CustomerExRepositoryImpl implements CustomerExRepository{
    private final MongoTemplate mongoTemplate;
    @Override
    public Page<CustomerDocument> getCustomers(String role,String name, String phone, String address,Integer page, Integer size) {
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

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), CustomerDocument.class);
        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, (int) totalElement);
        } else {
            pageRequest = PageRequest.of(page, size);
        }


        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("accounts")
                .localField("account_id")
                .foreignField("_id")
                .as("account");


        // Aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                lookupOperation,
                Aggregation.unwind("account"),
                Aggregation.skip((long) pageRequest.getOffset()),
                Aggregation.limit(pageRequest.getPageSize())
        );

        // Execute aggregation
        AggregationResults<CustomerDocument> results = mongoTemplate.aggregate(aggregation, "customers", CustomerDocument.class);
        List<CustomerDocument> customerList = results.getMappedResults();

        // Return as a Page object
        return new PageImpl<>(customerList, pageRequest, customerList.size());
    }
}
