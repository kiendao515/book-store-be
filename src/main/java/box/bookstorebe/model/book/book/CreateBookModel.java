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
    private String publisher;
    private String authorName;
    private String coverImage;
    private String backImage;
    private List<String> contentImage;
    private String demoUrl;
    private String tags;
    private String categoryId;
    private String storeId;
}
