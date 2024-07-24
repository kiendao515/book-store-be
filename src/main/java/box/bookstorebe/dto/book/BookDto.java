package box.bookstorebe.dto.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookDto extends BookCommonDto{
    private String name;
    private List<Collection> collections;
    private List<Category> categories;
    private List<BookRealityDto> bookRealities;
    private Store store;

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
