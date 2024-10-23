package box.bookstorebe.document.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("book_information")
public class BookDocument {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("category_id")
    private String categoryId;

    @Field("tags")
    private List<String> tags;

    @Field("number_of_page")
    private Long numberOfPage;

    @Field("description")
    private String description;

    @Field("publish_year")
    private Integer publishYear;

    @Field("isbn")
    private String isbn;

    @Field("publisher")
    private String publisher;

    @Field("author_name")
    private String authorName;

    @Field("cover_image_id")
    private String coverImageId;

    @Field("detail_image_id")
    private String detailImageId;

    @Field("demo_image_ids")
    private List<String> demoImageIds;

    @Field("demo_url")
    private String demoUrl;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;
}
