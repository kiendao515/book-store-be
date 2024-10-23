package box.bookstorebe.model.book.book;
import box.bookstorebe.document.book.BookType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateMultipleBookRealityModel {
    private String storeId;
    private BookType type;
    private int quantity;
    private BigDecimal price;
    private String coverImageId;
}
