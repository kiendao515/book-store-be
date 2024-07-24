package box.bookstorebe.dto.book;

import box.bookstorebe.common.Const;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class BookCommonDto {
    private String id;
    private List<BookDto.Description> descriptions;
    private List<BookDto.RelatedPerson> relatedPeople;
    private List<BookDto.RelatedImage> relatedImages;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Description {
        private Const.BookDescriptionType type;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedPerson {
        private Const.BookRelatedPersonType type;
        private String id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedImage {
        private String id;
        private Const.BookImageType type;
        private String link;
    }
}
