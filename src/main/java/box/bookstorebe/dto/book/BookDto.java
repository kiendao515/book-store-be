package box.bookstorebe.dto.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookDto {
    private String id;
    private String name;
    private List<Description> descriptions;
    private List<RelatedPerson> relatedPeople;
    private List<Collection> collections;
    private List<Category> categories;
    private List<RelatedImage> relatedImages;
    private Store store;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        private String type;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedPerson {
        private String type;
        private String id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedImage {
        private String id;
        private String type;
        private String link;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Collection {
        private String id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category {
        private String id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Store {
        private String id;
        private String name;
    }
}
