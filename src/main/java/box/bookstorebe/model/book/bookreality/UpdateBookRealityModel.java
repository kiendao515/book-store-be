package box.bookstorebe.model.book.bookreality;

import box.bookstorebe.common.Const;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateBookRealityModel {
    @Field("price")
    private Double price;

    @Field("status")
    private String status;

    @Field("type")
    private Const.BookRealityType type;

    @Field("book_id")
    private String bookId;

    @Field("cover_image_id")
    private String coverImageId;
}
