package box.bookstorebe.repository.common.province.ex;

import box.bookstorebe.document.common.ProvinceDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class ProvinceExRepositoryImpl implements ProvinceExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<ProvinceDocument> getAllProvinces(String provinceCode, String districtCode, String wardCode) {
        Criteria criteria = new Criteria();
        if (provinceCode != null) {
            criteria = criteria.and("Code").is(provinceCode);
        }

        if (districtCode != null) {
            criteria = criteria.and("District.Code").is(districtCode);
        }

        if (wardCode != null) {
            criteria = criteria.and("District.Ward.Code").is(wardCode);
        }


        Aggregation aggregation = newAggregation(
                match(criteria)
        );

        AggregationResults<ProvinceDocument> result = mongoTemplate.aggregate(aggregation, "provinces", ProvinceDocument.class);
        return result.getMappedResults();
    }

}
