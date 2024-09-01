package box.bookstorebe.model.book.book;

import box.bookstorebe.common.Const;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateMultipleBookRealityModel {
    private Const.BookRealityType type;
    private Long quantity;
    private Double price;
    private String coverImageId;
}
