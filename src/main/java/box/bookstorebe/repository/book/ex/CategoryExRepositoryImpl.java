package box.bookstorebe.repository.book.ex;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.dto.book.CategorySalesStat;
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
import java.util.Objects;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@AllArgsConstructor
public class CategoryExRepositoryImpl implements CategoryExRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<CategoryDto> getCategories(String name,String sortBy, Const.SortDirection orderBy, Integer page, Integer size) {
        PageRequest pageRequest;
        Criteria criteria = new Criteria();
        criteria = criteria.and("deleted_at").isNull();
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
                new Document("from", "book_information")
                        .append("let", new Document("categoryId", new Document("$toString", "$_id")))
                        .append("pipeline", List.of(
                                new Document("$match",
                                        new Document("$expr",
                                                new Document("$eq", Arrays.asList("$$categoryId", "$category_id"))
                                        )
                                )
                        ))
                        .append("as", "book_information")
        );

        AggregationOperation addFieldsOperation = Aggregation.addFields()
                .addField("num_of_books").withValueOf(ArrayOperators.Size.lengthOfArray("book_information")).build();

        ProjectionOperation projectOperation = Aggregation.project()
                .and("_id").as("_id")
                .and("name").as("name")
                .and("description").as("description")
                .and("num_of_books").as("numOfBooks")
                .and("created_at").as("createdAt")
                .and("updated_at").as("updatedAt");

        SortOperation sortOperation = Aggregation.sort(Sort.by(Objects.equals(orderBy, Const.SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
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


    public List<CategorySalesStat> getTopSellingCategories() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("book_information", "category_id", "_id", "book_info"),
                Aggregation.lookup("book_inventory", "book_info._id", "book_id", "book_inventory"),
                Aggregation.lookup("order_items", "book_inventory._id", "book_inventory_id", "order_items"),
                Aggregation.lookup("orders", "order_items.order_id", "_id", "orders"),
                Aggregation.match(Criteria.where("orders.status").is("DONE")),
                Aggregation.group("book_info.category_id")
                        .sum("order_items.quantity").as("totalSold")
                        .first("book_info.category_id").as("categoryId"),
                Aggregation.lookup("categories", "categoryId", "_id", "category"),
                Aggregation.project("category._id", "category.name", "totalSold")
                        .andExclude("_id"),
                Aggregation.sort(Sort.by(Sort.Order.desc("totalSold"))),
                Aggregation.limit(10)
        );

        // Thực hiện aggregation và trả kết quả
        AggregationResults<CategorySalesStat> result = mongoTemplate.aggregate(aggregation, "categories", CategorySalesStat.class);
        return result.getMappedResults();
    }



}
