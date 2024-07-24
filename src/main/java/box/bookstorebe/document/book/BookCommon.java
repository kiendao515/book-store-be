package box.bookstorebe.document.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BookCommon {
    @Field("descriptions")
    private List<Description> descriptions;

    @Field("related_people")
    private List<RelatedPerson> relatedPeople;

    @Field("related_images")
    private List<RelatedImage> relatedImages;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RelatedPerson {
        @Field("type")
        private String type;
        @Field("related_person_id")
        private String relatedPersonId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Description {
        @Field("type")
        private String type;
        @Field("value")
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RelatedImage {
        @Field("type")
        private String type;
        @Field("image_id")
        private String imageId;
    }
}
