package box.bookstorebe.model.book.bookreality;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookCommon;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.dto.book.BookDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateBookRealityModel {
    private Double price;
    private Const.BookRealityType type;
    private List<BookCommon.RelatedImage> relatedImages;
    private List<BookCommon.Description> descriptions;
    private List<BookCommon.RelatedPerson> relatedPeople;
    private String bookId;
}
