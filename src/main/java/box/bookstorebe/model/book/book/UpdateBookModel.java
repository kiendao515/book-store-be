package box.bookstorebe.model.book.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateBookModel {
    private String name;
    private Long numberOfPage;
    private String description;
    private Integer publishYear;
    private String isbn;
    private String publishingUnitId;
    private String publisher;
    private String author;
    private String editorId;
    private String translatorId;
    private String coverDrawerId;
    private String coverImage;
    private String backImage;
    private List<String> demoImage;
    private String demoUrl;
    private List<String> tagIds;
    private String categoryId;
    private String storeId;
}
