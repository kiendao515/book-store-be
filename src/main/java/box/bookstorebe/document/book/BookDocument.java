package box.bookstorebe.document.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("books")
public class BookDocument {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("descriptions")
    private List<Description> descriptions;

    @Field("related_people")
    private List<RelatedPerson> relatedPeople;

    @Field("collection_ids")
    private List<String> collectionIds;

    @Field("category_ids")
    private List<String> categoryIds;

    @Field("related_images")
    private List<RelatedImage> relatedImages;

    @Field("store_id")
    private String storeId;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedPerson {
        @Field("type")
        private String type;
        @Field("related_person_id")
        private String relatedPersonId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        @Field("type")
        private String type;
        @Field("content")
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedImage {
        @Field("type")
        private String type;
        @Field("image_id")
        private String imageId;
    }
}
