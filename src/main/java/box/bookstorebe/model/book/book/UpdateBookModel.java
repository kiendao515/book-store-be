package box.bookstorebe.model.book.book;

import box.bookstorebe.document.book.BookCommon;
import box.bookstorebe.document.book.BookDocument;
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
    private List<BookCommon.Description> descriptions;
    private List<BookCommon.RelatedPerson> relatedPeople;
    private List<String> collectionIds;
    private List<String> categoryIds;
    private List<BookCommon.RelatedImage> relatedImages;
    private String storeId;
}
