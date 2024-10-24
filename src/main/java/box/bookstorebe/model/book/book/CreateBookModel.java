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
    private String author;
    private String coverImageId;
    private String detailImageId;
    private List<String> demoImageIds;
    private String demoUrl;
    private List<String> tags;
    private String categoryId;
    private String storeId;
}
