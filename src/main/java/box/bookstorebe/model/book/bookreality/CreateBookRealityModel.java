package box.bookstorebe.model.book.bookreality;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookType;
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
public class CreateBookRealityModel {
    private BigDecimal price;
    private BookType type;
    private String coverImageId;
    private String bookId;
    private Integer quantity;
    private String storeId;
}
