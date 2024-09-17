package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.dto.book.CategoryDto;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class CategoryExRepositoryImpl implements CategoryExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<CategoryDto> getCategories(String name, Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();
        if (name != null) {
            criteria = criteria.and("name").is(name);
        }

        long totalElement = mongoTemplate.count(new Query().addCriteria(criteria), CategoryDocument.class);

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, (int) totalElement);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        // Match operation
        AggregationOperation matchOperations = match(criteria);

        AggregationOperation customLookupOperation = context -> new Document("$lookup",
                new Document("from", "books")
                        .append("let", new Document("categoryId", new Document("$toString", "$_id")))
                        .append("pipeline", List.of(
                                new Document("$match",
                                        new Document("$expr",
                                                new Document("$in", Arrays.asList("$$categoryId", "$category_ids"))
                                        )
                                )
                        ))
                        .append("as", "books")
        );

        AggregationOperation addFieldsOperation = Aggregation.addFields()
                .addField("num_of_books").withValueOf(ArrayOperators.Size.lengthOfArray("books")).build();

        ProjectionOperation projectOperation = Aggregation.project()
                .and("_id").as("_id")
                .and("name").as("name")
                .and("description").as("description")
                .and("num_of_books").as("numOfBooks")
                .and("created_at").as("createdAt")
                .and("updated_at").as("updatedAt");

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("_id")));
        SkipOperation skipOperation = Aggregation.skip(pageRequest.getOffset());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());

        Aggregation aggregation = newAggregation(
                matchOperations,
                customLookupOperation,
                addFieldsOperation,
                projectOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<CategoryDto> result = mongoTemplate.aggregate(aggregation, "categories", CategoryDto.class);
        return new PageImpl<>(result.getMappedResults(), pageRequest, totalElement);
    }

}
