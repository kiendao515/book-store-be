package box.bookstorebe.model.book.bookreality;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateBookRealityModel {
    @Field("price")
    private BigDecimal price;

    @Field("type")
    private BookType type;

    @Field("book_id")
    private String bookId;

    @Field("cover_image_id")
    private String coverImageId;

    @Field("quantity")
    private Integer quantity;
}
