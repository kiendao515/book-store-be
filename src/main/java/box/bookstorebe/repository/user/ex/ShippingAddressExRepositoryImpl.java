package box.bookstorebe.repository.user.ex;

import box.bookstorebe.document.account.ShippingAddressDocument;
import box.bookstorebe.dto.customer.ShippingAddressDto;
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
public class ShippingAddressExRepositoryImpl implements ShippingAddressExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ShippingAddressDto> getShippingAddresses(String userId, Integer page, Integer size) {
        PageRequest pageRequest;

        Criteria criteria = new Criteria();
        if (userId != null) {
            criteria = criteria.and("account_id").is(userId);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), ShippingAddressDocument.class);

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, totalElement == 0 ? 10 : (int) totalElement);
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
                .and("full_name").as("full_name")
                .and("phone_number").as("phone_number")
                .and("province").as("province")
                .and("district").as("district")
                .and("ward").as("ward")
                .and("street").as("street")
                .and("is_default").as("is_default");

        Aggregation aggregation = newAggregation(
                matchOperations,
                sortOperation,
                skipOperation,
                limitOperation,
                projectionOperation
        );

        AggregationResults<ShippingAddressDto> result = mongoTemplate.aggregate(aggregation, "shipping_addresses", ShippingAddressDto.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }
}
