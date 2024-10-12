package box.bookstorebe.model.book.book;

import box.bookstorebe.common.Const;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateBookModel {
    private String name;
    private Long numberOfPage;
    private String description;
    private Integer publishYear;
    private String isbn;
    private String publishingUnitId;
    private String publisherId;
    private String authorId;
    private String editorId;
    private String translatorId;
    private String coverDrawerId;
    private String coverImageId;
    private String detailImageId;
    private List<String> demoImageIds;
    private String demoUrl;
    private List<String> tagIds;
    private List<String> categoryIds;
    private String storeId;
    private List<BookReality> bookRealities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BookReality {
        private Const.BookRealityType type;
        private Long quantity;
        private Double price;
        private String coverImageId;
    }
}
