package box.bookstorebe.repository.common.webcontent.ex;

import box.bookstorebe.document.common.WebContentDocument;
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
public class WebContentExRepositoryImpl implements WebContentExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<WebContentDocument> getWebContents(Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), WebContentDocument.class);
        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, totalElement > 0 ? (int) totalElement : 10);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("_id")));

        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());


        Aggregation aggregation = newAggregation(
                match(criteria),
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<WebContentDocument> result = mongoTemplate.aggregate(aggregation, "web_contents", WebContentDocument.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }

}
