package box.bookstorebe.model.book.bookreality;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookCommon;
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
public class UpdateBookRealityModel {
    private Double price;
    private Const.BookRealityType type;
    private Const.BookRealityStatus status;
    private List<BookCommon.RelatedImage> relatedImages;
    private List<BookCommon.Description> descriptions;
    private List<BookCommon.RelatedPerson> relatedPeople;
    private String bookId;
}
