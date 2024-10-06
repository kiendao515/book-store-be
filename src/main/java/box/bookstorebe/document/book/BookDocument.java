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
@Document("books")
public class BookDocument {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("category_ids")
    private List<String> categoryIds;

    @Field("tag_ids")
    private List<String> tagIds;

    @Field("store_id")
    private String storeId;

    @Field("number_of_page")
    private Long numberOfPage;

    @Field("description")
    private String description;

    @Field("publish_year")
    private Integer publishYear;

    @Field("isbn")
    private String isbn;

    @Field("publishing_unit_id")
    private String publishingUnitId;

    @Field("publisher_id")
    private String publisherId;

    @Field("author_id")
    private String authorId;

    @Field("editor_id")
    private String editorId;

    @Field("translator_id")
    private String translatorId;

    @Field("cover_drawer_id")
    private String coverDrawerId;

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
